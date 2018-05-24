package cole.matthew.vivace;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    public static final String APPLICATION_TAG = "Vivace_Tag";
    private AudioRecord _recorder;
    private ImageButton _recordButton;
    private TextView _noteTextView;
    private WebView _scoreUI;
    public static volatile boolean IsRecording;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        _scoreUI = findViewById(R.id.scoreUI);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET))
            {
                Snackbar.make(findViewById(R.id.scoreUI), R.string.internet_permissions_explanation, Snackbar.LENGTH_LONG).show();
            }
            else
            {
                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.INTERNET }, 15800);
            }
        }
        else if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED)
        {
            Log.d(APPLICATION_TAG, "Internet permission granted.");
        }

        _scoreUI.getSettings().setJavaScriptEnabled(true);
        _scoreUI.loadData(                                       //https://npmcdn.com/vexflow@1.2.84/releases/vexflow-debug.js
                "<html><script type=\"text/javascript\" src=\"https://unpkg.com/vexflow/releases/vexflow-min.js\"></script><body><div id=\"start\"></div></body></html>",
                "text/html", null);
        _scoreUI.evaluateJavascript("VF = Vex.Flow;\n" + "\n" +
                                    "// Create an SVG renderer and attach it to the DIV element named \"start\".\n" +
                                    "var div = document.getElementById(\"start\")\n" +
                                    "var renderer = new VF.Renderer(div, VF.Renderer.Backends.SVG);\n" +
                                    "\n" + "// Configure the rendering context.\n" +
                                    "renderer.resize(500, 500);\n" +
                                    "var context = renderer.getContext();\n" +
                                    "context.setFont(\"Arial\", 10, \"\").setBackgroundFillStyle(\"#eed\");\n" +
                                    "\n" +
                                    "// Create a stave of width 400 at position 10, 40 on the canvas.\n" +
                                    "var stave = new VF.Stave(10, 40, 400);\n" + "\n" +
                                    "// Add a clef and time signature.\n" +
                                    "stave.addClef(\"treble\").addTimeSignature(\"4/4\");\n" +
                                    "\n" + "// Connect it to the rendering context and draw!\n" +
                                    "stave.setContext(context).draw();", new ValueCallback<String>()
        {
            @Override
            public void onReceiveValue(String value)
            {
                Log.d(APPLICATION_TAG, value);
            }
        });

        _noteTextView = findViewById(R.id.note_name);
        _recordButton = findViewById(R.id.recordButton);
        _recordButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.RECORD_AUDIO) !=
                    PackageManager.PERMISSION_GRANTED)
                {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)v.getContext(), Manifest.permission.RECORD_AUDIO))
                    {
                        // Show an explanation to the user *asynchronously* -- don't block this thread
                        // waiting for the user's response! After the user sees the explanation, try
                        // again to request the permission.
                    }
                    else
                    {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions((Activity)v.getContext(), new String[] { Manifest.permission.RECORD_AUDIO }, 3461);
                    }
                }
                else
                {
                    // permission is already granted
                    analyzeAudio();
                }
                //                if (_recorder == null)
//                    _recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100, AudioFormat.CHANNEL_IN_MONO,
//                                                AudioFormat.ENCODING_PCM_8BIT, getMinBufferSize());
//
//                if (_recorder.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING)
//                {
//                    if (_recorder.getState() != AudioRecord.STATE_UNINITIALIZED)
//                    {
//                        Thread.currentThread().interrupt();
//                    }
//
//                    _recorder.startRecording();
//
//                    Thread recordingThread = new Thread(new Runnable()
//                    {
//                        @Override
//                        public void run()
//                        {
//                            Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
//                            analyzeAudio();
//                        }
//                    }, "AudioRecorder Thread");
//                    recordingThread.start();
//                }
//                else
//                {
//                    _recorder.stop();
//                    _recorder.release();
//                }
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults)
    {
        switch (requestCode)
        {
            case 3461:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // permission was granted
                    analyzeAudio();
                }
                else
                {
                    // permission denied
                    _recordButton.setEnabled(false);
                }

                break;
            case 1580:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // permission granted
                }
                else
                {
                    // permission denied
                    _scoreUI.setEnabled(false);
                }

                break;
            default:
                Log.d(APPLICATION_TAG, String.format("Got an unrecognized request code from asking for permissions: %d", requestCode));
                break;
        }
    }

    private void analyzeAudio()
    {
        if (!IsRecording)
        {
//            _recordButton.setImageDrawable(getDrawable(R.drawable.ic_info_black_24dp));

            Thread thread = new Thread(new Runnable()
            {
                private static final String TAG = "FrequencyThreadTAG";
                private int channel_config = AudioFormat.CHANNEL_IN_MONO;
                private int format = AudioFormat.ENCODING_PCM_16BIT;
                private int sampleSize = 44100;
                //private int bufferSize = AudioRecord.getMinBufferSize(sampleSize, channel_config, format);
                private int bufferSize = 1024;  // hardcode this so that can get power of 2
                private AudioRecord audioInput = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleSize, channel_config, format, bufferSize);

                @Override
                public void run()
                {
                    Log.i(TAG, "bufferSize: " + String.valueOf(bufferSize));

                    //Read audio
                    short[] audioBuffer = new short[bufferSize]; //short
                    audioInput.startRecording();
                    MainActivity.IsRecording = true;
                    int bytesRecorded = 0;

                    while (MainActivity.IsRecording)
                    {
                        bytesRecorded += audioInput.read(audioBuffer, 0, bufferSize);
                        Log.i(TAG, "bytesRecorded: " + String.valueOf(bytesRecorded));

                        double[] buffer = new double[audioBuffer.length];
                        int idx = 0;
                        for (int i = 0; i < audioBuffer.length && idx < audioBuffer.length; i += 2)
                        {
                            short bLow = audioBuffer[i];
                            short bHigh = audioBuffer[i + 1];

                            buffer[idx++] = (bLow & 0xFF | bHigh << 8) / 32767;
                            if (channel_config == AudioFormat.CHANNEL_IN_STEREO)
                                i += 2;
                        }

                        FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
                        Complex[] resultC = fft.transform(buffer, TransformType.FORWARD);

                        double[] results = new double[resultC.length];
                        for (int i = 0; i < resultC.length; ++i)
                        {
                            double real = resultC[i].getReal();
                            double imag = resultC[i].getImaginary();

                            results[i] = Math.sqrt(real * real + imag * imag);
                        }

                        List<Float> found = DFT.process(results, sampleSize, resultC.length, 7);
                        HashMap<String, Float> keys = new HashMap<>();
                        for (float freq : found)
                            keys.put(closestKey(freq), freq);

                        for (final String note : keys.keySet())
                        {
                            Log.d(TAG, String.format("Found: %s at freq=\"%f\"", note, keys.get(note)));
                            _noteTextView.post(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    _noteTextView.setText(note);
                                }
                            });
                        }
                    }

                    audioInput.stop();
                    audioInput.release();
                }

                /** A list of human-readable musical notes. */
                private String[] notes = { "A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#" };

                /**
                 * Converts the frequency into a human-readable string representing the note on a piano.
                 * @param freq The frequency to convert to a string
                 * @return A string representing the note on a piano.
                 */
                private String closestKey(double freq)
                {
                    String result = null;
                    int key = closestKeyIndex(freq);

                    if (key > 0)
                    {
                        int range = 1 + (key - 1) / notes.length;
                        result = notes[(key - 1) % notes.length] + range;
                    }

                    return result;
                }

                /**
                 * Takes a frequency and returns the corresponding key number on a piano in the range 1-88. This
                 * formula is derived from the logarithmic nature of the frequency.
                 * @param freq The frequency to get the key number for.
                 * @return A number between 1 and 88 identifying the key number of the note.
                 */
                private int closestKeyIndex(double freq)
                {
                    return 1 + (int)((12 * Math.log(freq / 440) / Math.log(2) + 49) - 0.5);
                }
            });
            thread.start();
        }
        else
        {
            IsRecording = false;
            //_recordButton.setImageDrawable(getDrawable(R.drawable.baseline_mic_black_48));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        boolean result = true;

        switch (item.getItemId())
        {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.action_search:

                break;
            case R.id.action_share:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "This is my text to send." });
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, getResources().getText(R.string.action_search)));
                break;
            default:
                result = super.onOptionsItemSelected(item);
                break;
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_actions, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView)searchItem.getActionView();

        // Configure the search info and add any event listeners...
        // Define the listener
        MenuItem.OnActionExpandListener expandListener = new MenuItem.OnActionExpandListener()
        {
            /**
             * {@inheritDoc}
             */
            @Contract(pure = true)
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Do something when action item collapses
                return true;  // Return true to collapse action view
            }

            /**
             * {@inheritDoc}
             */
            @Contract(pure = true)
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Do something when expanded
                return true;  // Return true to expand action view
            }
        };

        // Get the MenuItem for the action item
        MenuItem actionMenuItem = menu.findItem(R.id.action_search);

        // Assign the listener to that action item
        actionMenuItem.setOnActionExpandListener(expandListener);

        // Any other things you have to do when creating the options menu...

        return super.onCreateOptionsMenu(menu);
    }
}
