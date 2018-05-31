package cole.matthew.vivace;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.webkit.WebView;
import android.widget.AdapterView;
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

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, TempoPickerFragment.NoticeTempoDialogListener, TimeSignPickerFragment.NoticeTimeSignDialogListener
{
    public static final String APPLICATION_TAG = "Vivace_Tag";
    private ImageButton _recordButton;
    private TextView _timerTextView;
    private TextView _tempoTextView;
    private TextView _timeSignatureTextView;
    private WebView _scoreUI;
    public static volatile boolean IsRecording;
    private String _timeSignature;
    private int _bpm;
    private long startTime = 0;
//    private Handler timerHandler = new Handler();
//    private Runnable timerRunnable = new Runnable()
//    {
//        /**
//         * <p>This provides the functionality of Vivace's recording timer.</p><br/>
//         * {@inheritDoc}
//         */
//        @SuppressLint("DefaultLocale")
//        @Override
//        public void run()
//        {
//            long millis = System.currentTimeMillis() - startTime;
//            int seconds = (int)(millis / 1000) % 60;
//            int minutes = seconds / 60;
//            _timerTextView.setText(String.format("%02d:%02d", minutes, seconds));
//
//            timerHandler.postDelayed(this, 500);
//        }
//    };

    /** {@inheritDoc} */
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RequestAllPermissions();

        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        _tempoTextView = findViewById(R.id.tempo);
        _tempoTextView.setText("120 BPM");
        _tempoTextView.setOnClickListener(new View.OnClickListener()
        {
            /** {@inheritDoc} */
            @Override
            public void onClick(View v)
            {
                Bundle arguments = new Bundle();
                arguments.putInt("TempoValue", _bpm);
                TempoPickerFragment tempoPickerFragment = new TempoPickerFragment();
                tempoPickerFragment.setArguments(arguments);
                tempoPickerFragment.show(getFragmentManager(), "TempoPicker");
            }
        });

//        Spinner _timeSignature = findViewById(R.id.timeSignSelector);
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.timeSignatures, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        _timeSignature.setAdapter(adapter);
//        _timeSignature.setOnItemSelectedListener(this);
        _timeSignatureTextView = findViewById(R.id.timeSignSelector);
        _timeSignatureTextView.setText("4/4");
        _timeSignatureTextView.setOnClickListener(new View.OnClickListener()
        {
            /** {@inheritDoc} */
            @Override
            public void onClick(View v)
            {
                Bundle arguments = new Bundle();
                arguments.putString("TimeSignValue", _timeSignature);
                TimeSignPickerFragment timeSignPickerFragment = new TimeSignPickerFragment();
                timeSignPickerFragment.setArguments(arguments);
                timeSignPickerFragment.show(getFragmentManager(), "TempoPicker");
            }
        });

        _scoreUI = findViewById(R.id.scoreUI);

        if (BuildConfig.DEBUG)
            WebView.setWebContentsDebuggingEnabled(true);

        _scoreUI.getSettings().setJavaScriptEnabled(true);
        String html = "<html>" + "<head>" +
                      "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">" +
                      "<meta name=\"robots\" content=\"noindex, nofollow\">" +
                      "<meta name=\"googlebot\" content=\"noindex, nofollow\">" +
                      "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">" +
                      "<link rel=\"stylesheet\" type=\"text/css\" href=\"/css/result-light.css\">" +
                      "<script type=\"text/javascript\" src=\"https://npmcdn.com/vexflow/releases/vexflow-debug.js\"></script>" +
                      "<style type=\"text/css\"></style>" + "<title>VexFlow Sandbox</title>" +
                      "<script type=\"text/javascript\">var alreadyrunflag = 0; if (document.addEventListener) document.addEventListener(\"DOMContentLoaded\", function() { alreadyrunflag=1; }, false); else if (document.all && !window.opera) { document.write('<script type=\"text/javascript\" id=\"contentloadtag\" defer=\"defer\" src=\"javascript:void(0)\"><\\/script>'); var contentloadtag = document.getElementById(\"contentloadtag\"); contentloadtag.onreadystatechange=function() { if (this.readyState==\"complete\") { alreadyrunflag=1; } } } window.onload = function() { setTimeout(\"if (!alreadyrunflag){ }\", 0); }</script>" +
                      "</head>" + "<body>" + "<div id=\"boo\"></div>" +
                      "<script>if (window.parent && window.parent.parent) { window.parent.parent.postMessage([\"resultsFrame\", { height: document.body.getBoundingClientRect().height, slug: \"None\"}], \"*\")}</script>" +
                      "</body>" + "</html>";
        _scoreUI.loadData(html, "text/html", null);

        _timerTextView = findViewById(R.id.recordingTimer);
        _recordButton = findViewById(R.id.recordButton);
        _recordButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
//                String javaScript = "(function() { VF = Vex.Flow; var div = document.getElementById(\"boo\"); var renderer = new VF.Renderer(div, VF.Renderer.Backends.SVG); renderer.resize(500,500); var context = renderer.getContext(); context.setFont(\"Arial\", 10, \"\").setBackgroundFillStyle(\"#eed\"); var stave = new VF.Stave(10, 40, 40); stave.addClef(\"treble\").addTimeSignature(\"4/4\"); stave.setContext(context).draw(); return \"test\"; })();";
                String javaScript = "const VF = Vex.Flow;" + "var vf = new VF.Factory({" +
                                    "renderer: { elementId: 'boo', width: 500, height: 300 }" +
                                    "});" + "var score = vf.EasyScore();" +
                                    "var system = vf.System();" + "system.addStave({" +
                                    "voices: [" +
                                    "score.voice(score.notes('C#5/q, B4, A4, G#4', {stem: 'up'}))," +
                                    "score.voice(score.notes('C#4/h, C#4', {stem: 'down'}))" + "]" +
                                    "}).addClef('treble').addTimeSignature('4/4');" +
                                    "system.addStave({" + "voices: [" +
                                    "score.voice(score.notes('C4/q, B4, Eb5, G5'))" + "]" +
                                    "}).addClef('treble').addTimeSignature('4/4');" + "vf.draw();";
                _scoreUI.evaluateJavascript(javaScript, null);

                analyzeAudio();
            }
        });
    }

//    @Override
//    protected void onPause()
//    {
//        super.onPause();
//        timerHandler.removeCallbacks(timerRunnable);
//    }

    /** Requests all the permissions required by the application. */
    public void RequestAllPermissions()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
        {
            // Show an explanation to the user *asynchronously* -- don't block this thread waiting
            // for the user's response! After the user sees the explanation, try again to request
            // the permission.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO))
                Snackbar.make(findViewById(R.id.scoreUI), R.string.audio_permissions_explanation, Snackbar.LENGTH_LONG).show();
            else                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.RECORD_AUDIO }, VivacePermissionCodes.RECORD_AUDIO);
        }
        else        // permission is already granted
            Log.d(APPLICATION_TAG, "Audio Recording permission granted.");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
        {
            // Show an explanation to the user *asynchronously* -- don't block this thread waiting
            // for the user's response! After the user sees the explanation, try again to request
            // the permission.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET))
                Snackbar.make(findViewById(R.id.scoreUI), R.string.internet_permissions_explanation, Snackbar.LENGTH_LONG).show();
            else                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.INTERNET }, VivacePermissionCodes.INTERNET);
        }
        else        // else permission is already granted
            Log.d(APPLICATION_TAG, "Internet permission granted.");
    }

    /** {@inheritDoc} */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults)
    {
        switch (requestCode)
        {
            case VivacePermissionCodes.RECORD_AUDIO:
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
            case VivacePermissionCodes.INTERNET:
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
            startTime = System.currentTimeMillis();
            //timerHandler.postDelayed(timerRunnable, 0);

            Thread thread = new Thread(new Runnable()
            {
                private static final String TAG = "FrequencyThreadTAG";
                private int channel_config = AudioFormat.CHANNEL_IN_MONO;
                private int format = AudioFormat.ENCODING_PCM_16BIT;
                private int sampleSize = 44100;
                //private int bufferSize = AudioRecord.getMinBufferSize(sampleSize, channel_config, format);
                private int bufferSize = 32768;  // hardcode this so that can get power of 2
                private AudioRecord audioInput = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleSize, channel_config, format, bufferSize);

                /** {@inheritDoc} */
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
                        bytesRecorded += audioInput.read(audioBuffer, 0, 22050);
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
                        //if (!found.isEmpty())
                        //keys.put(closestKey(found.get(0)), found.get(0));
                        for (float freq : found)
                            keys.put(closestKey(freq), freq);

                        if (keys.keySet().isEmpty())
                        {
                            _timerTextView.post(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    _timerTextView.setText("");
                                }
                            });
                        }
                        else
                        {
                            for (final String note : keys.keySet())
                            {
                                Log.d(TAG, String.format("Found: %s at freq=\"%f\"", note,
                                                         keys.get(note)));
                                _timerTextView.post(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        _timerTextView.setText(note);
                                    }
                                });
                            }
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
                    return 1 + (int) ((12 * Math.log(freq / 440) / Math.log(2) + 49) - 0.5);
                }
            });
            thread.start();
        }
        else
        {
            IsRecording = false;
            //timerHandler.removeCallbacks(timerRunnable);
            startTime = 0;
            //_recordButton.setImageDrawable(getDrawable(R.drawable.baseline_mic_black_48));
        }
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_actions, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.getQuery();

        // Configure the search info and add any event listeners...
        // Define the listener
        MenuItem.OnActionExpandListener expandListener = new MenuItem.OnActionExpandListener()
        {
            /** {@inheritDoc} */
            @Contract(pure = true)
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item)
            {
                // Do something when action item collapses
                return true;  // Return true to collapse action view
            }

            /** {@inheritDoc} */
            @Contract(pure = true)
            @Override
            public boolean onMenuItemActionExpand(MenuItem item)
            {
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

    /** {@inheritDoc} */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        _timeSignature = parent.getItemAtPosition(position).toString();
    }

    /** {@inheritDoc} */
    @Override
    public void onNothingSelected(AdapterView<?> parent)
    { }

    /** {@inheritDoc} */
    @SuppressLint("DefaultLocale")
    @Override
    public void onTempoDialogPositiveClick(int tempo)
    {
        _bpm = tempo;
        _tempoTextView.setText(String.format("%d BPM", _bpm));
    }

    /** {@inheritDoc} */
    @Override
    public void onTempoDialogNegativeClick(int tempo)
    { }

    /** {@inheritDoc} */
    @Override
    public void onTimeSignDialogPositiveClick(String timeSign)
    {
        _timeSignature = timeSign;
        _timeSignatureTextView.setText(timeSign);
    }

    /** {@inheritDoc} */
    @Override
    public void onTimeSignDialogNegativeClick(String timeSign)
    { }
}