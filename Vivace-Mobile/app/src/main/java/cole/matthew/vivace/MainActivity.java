package cole.matthew.vivace;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity
        implements TempoPickerFragment.NoticeTempoDialogListener,
                   TimeSignPickerFragment.NoticeTimeSignDialogListener,
                   ToolbarFragment.OnFragmentInteractionListener
{
    public static final String APPLICATION_TAG = "Vivace_Tag";
    public final String IS_RECORDING_TAG = "IsRecording_Tag";
    public final String TIME_SIGNATURE_TAG = "TimeSignature_Tag";
    public final String BPM_TAG = "BPM_Tag";
    public final String STARTTIME_TAG = "StartTime_Tag";

    private TextView _tempoTextView;
    private TextView _timeSignatureTextView;
    private WebView _scoreUI;
    private ImageButton _recordButton;
    private LinearLayout _playbackLayout;
    private TextView _recordingTimer;
    private ImageButton _pauseButton;
    private ImageButton _stopButton;

    public static volatile boolean IsRecording;
    private String _timeSignature;
    private int _bpm;
    private long startTime;
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
//            _recordingTimer.setText(String.format("%02d:%02d", minutes, seconds));
//
//            timerHandler.postDelayed(this, 500);
//        }
//    };

    /** {@inheritDoc} */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        VivacePermissions.RequestAllPermissions(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        IsRecording = preferences.getBoolean(IS_RECORDING_TAG, false);
        _timeSignature = preferences.getString(TIME_SIGNATURE_TAG, "4/4");
        _bpm = preferences.getInt(BPM_TAG, 120);
        startTime = preferences.getLong(STARTTIME_TAG, 0);

        ToolbarFragment toolbarFragment = (ToolbarFragment)getSupportFragmentManager().findFragmentById(R.id.toolbarFragment);
        Toolbar toolbar = (Toolbar)toolbarFragment.getView();
        setSupportActionBar(toolbar);

        _tempoTextView = findViewById(R.id.tempo);
        _tempoTextView.setText(String.valueOf(_bpm));
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

        _timeSignatureTextView = findViewById(R.id.timeSignSelector);
        _timeSignatureTextView.setText(_timeSignature);
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
        _recordButton = findViewById(R.id.recordButton);
        _recordButton.setOnClickListener(new View.OnClickListener()
        {
            /** {@inheritDoc} */
            @Override
            public void onClick(View v)
            {
                _recordButton.setVisibility(View.GONE);
                _playbackLayout.setVisibility(View.VISIBLE);

//                String javaScript = "(function() { VF = Vex.Flow; let div = document.getElementById(\"boo\"); let renderer = new VF.Renderer(div, VF.Renderer.Backends.SVG); renderer.resize(500,500); let context = renderer.getContext(); context.setFont(\"Arial\", 10, \"\").setBackgroundFillStyle(\"#eed\"); let stave = new VF.Stave(10, 40, 40); stave.addClef(\"treble\").addTimeSignature(\"4/4\"); stave.setContext(context).draw(); return \"test\"; })();";
                String javaScript = "(function() {" +
                                        "const VF = Vex.Flow;" +
                                        "let vf = new VF.Factory({" +
                                            "renderer: { elementId: 'boo', width: 550, height: 300 }" +
                                        "});" +
                                        "let score = vf.EasyScore();" +
                                        "let system = vf.System();" +
                                        "system.addStave({" +
                                            "voices: [" +
                                                "score.voice(score.notes('C#5/q, B4, A4, G#4', {stem: 'up'}))," +
                                                "score.voice(score.notes('C#4/h, C#4', {stem: 'down'}))" +
                                            "]" +
                                        "}).addClef('treble').addTimeSignature('4/4');" +
                                        "system.addStave({" +
                                            "voices: [" +
                                                "score.voice(score.notes('C4/q, B4, Eb5, G5'))" +
                                            "]" +
                                        "}).addClef('treble').addTimeSignature('4/4');" +
                                        "vf.draw();" +
                                    "})();";
                _scoreUI.evaluateJavascript(javaScript, null);

                if (VivacePermissions.RequestPermission((Activity)v.getContext(), VivacePermissionCodes.RECORD_AUDIO))
                    analyzeAudio();
            }
        });

        _playbackLayout = findViewById(R.id.playbackLayout);
        _recordingTimer = findViewById(R.id.recordingTimer);
        _pauseButton = findViewById(R.id.pauseButton);
        _pauseButton.setOnClickListener(new View.OnClickListener()
        {
            /** {@inheritDoc} */
            @Override
            public void onClick(View v)
            {
                _recordButton.setVisibility(View.VISIBLE);
                _playbackLayout.setVisibility(View.GONE);

                IsRecording = false;
                //timerHandler.removeCallbacks(timerRunnable);
                startTime = 0;
            }
        });

        _stopButton = findViewById(R.id.stopButton);
        _stopButton.setOnClickListener(new View.OnClickListener()
        {
            /** {@inheritDoc} */
            @Override
            public void onClick(View v)
            {
                _recordButton.setVisibility(View.VISIBLE);
                _playbackLayout.setVisibility(View.GONE);

                IsRecording = false;
                //timerHandler.removeCallbacks(timerRunnable);
                startTime = 0;
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    protected void onPause()
    {
        Log.d(APPLICATION_TAG, "MainActivity - OnPause");
        super.onPause();

//        timerHandler.removeCallbacks(timerRunnable);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putBoolean(IS_RECORDING_TAG, IsRecording);
        editor.putString(TIME_SIGNATURE_TAG, _timeSignature);
        editor.putInt(BPM_TAG, _bpm);
        editor.putLong(STARTTIME_TAG, startTime);
        editor.apply();
    }

    /** {@inheritDoc} */
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        Log.d(APPLICATION_TAG, String.format("Configuration Change - %s", newConfig.toString()));
        super.onConfigurationChanged(newConfig);
    }

//    /** {@inheritDoc} */
//    @Override
//    protected void onSaveInstanceState(Bundle outState)
//    {
//        Log.d(APPLICATION_TAG, "MainActivity - OnSaveInstanceState");
//
//        super.onSaveInstanceState(outState);
//        outState.putBoolean(IS_RECORDING_TAG, IsRecording);
//        outState.putString(TIME_SIGNATURE_TAG, _timeSignature);
//        outState.putInt(BPM_TAG, _bpm);
//        outState.putLong(STARTTIME_TAG, startTime);
//    }

    /** {@inheritDoc} */
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        Log.d(APPLICATION_TAG, "MainActivity - OnRestoreInstanceState");

        try
        {
            InputStream inputStream = getAssets().open("minifiedHTML.html");
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            StringBuilder stringBuilder = new StringBuilder();

            for (String string; (string = in.readLine()) != null; )
                stringBuilder.append(string);

            in.close();
            String minifiedHTML = stringBuilder.toString();
            _scoreUI.loadData(minifiedHTML, "text/html", null);
            _scoreUI.getSettings().setJavaScriptEnabled(true);

            if (BuildConfig.DEBUG)
                WebView.setWebContentsDebuggingEnabled(true);
        }
        catch (IOException e)
        {
            Log.e(APPLICATION_TAG, e.getMessage());
            _recordButton.setEnabled(false);
            new AlertDialog.Builder(this).setTitle("Error").setMessage(
                    "An issue was encountered displaying the Music Sheet. Please " +
                    "quit and restart the application to continue. If this issue " +
                    "persists, please submit a report to https://github.com/colematthew4/Vivace/issues/new " +
                    "and describe your issue in detail.").setPositiveButton("", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                }
            }).create().show();
        }
    }

    /** {@inheritDoc} */
    @PermissionChecker.PermissionResult
    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults)
    {
        ToolbarFragment toolbarFragment = (ToolbarFragment)getSupportFragmentManager().findFragmentById(R.id.toolbarFragment);
        Toolbar toolbar = (Toolbar)toolbarFragment.getView();

        switch (requestCode)
        {
            case VivacePermissionCodes.RECORD_AUDIO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    _recordButton.setEnabled(true);     // permission granted
                else
                    _recordButton.setEnabled(false);    // permission denied

                break;
            case VivacePermissionCodes.INTERNET:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    _scoreUI.setEnabled(true);      // permission granted
                else
                    _scoreUI.setEnabled(false);     // permission denied

                break;
            case VivacePermissionCodes.READ_EXTERNAL_STORAGE:
                assert toolbar != null;

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    toolbar.findViewById(R.id.action_share).setEnabled(true);  // permission granted
                else
                    toolbar.findViewById(R.id.action_share).setEnabled(false);  // permission denied
            case VivacePermissionCodes.WRITE_EXTERNAL_STORAGE:
                assert toolbar != null;

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    toolbar.findViewById(R.id.action_share).setEnabled(true);  // permission granted
                else
                    toolbar.findViewById(R.id.action_share).setEnabled(false);  // permission denied

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
            startTime = System.currentTimeMillis();
            //timerHandler.postDelayed(timerRunnable, 0);

            Thread thread = new Thread(new Runnable()
            {
                private static final String TAG = "FrequencyThreadTAG";
                private int channel_config = AudioFormat.CHANNEL_IN_MONO;
                private int format = AudioFormat.ENCODING_PCM_16BIT;
                private int sampleSize = 44100;
                //private int bufferSize = AudioRecord.getMinBufferSize(sampleSize, channel_config, format);
                // must be a power of 2 for the FFT transform to work
                private int bufferSize = closestPowerOf2(sampleSize / (_bpm * 4 / 60));
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
                        //if (!found.isEmpty())
                        //  keys.put(closestKey(found.get(0)), found.get(0));
                        for (float freq : found)
                            keys.put(closestKey(freq), freq);

                        if (keys.keySet().isEmpty())
                        {
                            _recordingTimer.post(new Runnable()
                            {
                                /** {@inheritDoc} */
                                @Override
                                public void run()
                                {
                                    _recordingTimer.setText("");
                                }
                            });
                        }
                        else
                        {
                            for (final String note : keys.keySet())
                            {
                                Log.d(TAG, String.format("Found: %s at freq=\"%f\"", note, keys.get(note)));
                                _recordingTimer.post(new Runnable()
                                {
                                    /** {@inheritDoc} */
                                    @Override
                                    public void run()
                                    {
                                        _recordingTimer.setText(note);
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

                @Contract(pure = true)
                private int closestPowerOf2(final double sixteenthsPerSecond)
                {
                    double[] powers_of_2 = { 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096,
                                             8192, 16384, 32768 };
                    int result = -1;

                    for (int index = 0; result == -1 && index < powers_of_2.length; ++index)
                    {
                        if (powers_of_2[index] < sixteenthsPerSecond &&
                            powers_of_2[index + 1] > sixteenthsPerSecond)
                            result = (int)powers_of_2[index];
                    }

                    return result;
                }

                /**
                 * Converts the frequency into a human-readable string representing the note on a piano.
                 *
                 * @param freq The frequency to convert to a string
                 *
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
                 *
                 * @param freq The frequency to get the key number for.
                 *
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
            //timerHandler.removeCallbacks(timerRunnable);
            startTime = 0;
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

    /** {@inheritDoc} */
    @Override
    public void onFragmentInteraction(Uri uri)
    { }
}