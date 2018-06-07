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
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
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
import org.jfugue.pattern.Pattern;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import cole.matthew.vivace.Helpers.DFT;
import cole.matthew.vivace.Helpers.VexFlowScriptGenerator;
import cole.matthew.vivace.Helpers.VivacePermissionCodes;
import cole.matthew.vivace.Helpers.VivacePermissions;
import cole.matthew.vivace.Models.Measure;
import cole.matthew.vivace.Models.Note;
import cole.matthew.vivace.Models.ScorePartWise;
import cole.matthew.vivace.Models.TimeSignature;

public class MainActivity extends AppCompatActivity
        implements TempoPickerFragment.NoticeTempoDialogListener,
                   TimeSignaturePickerFragment.NoticeTimeSignDialogListener,
                   ToolbarFragment.OnFragmentInteractionListener,
                   ScorePartWise.OnNewMeasureListener
{
    public static final String APPLICATION_TAG = "Vivace_Tag";
//    public final String IS_RECORDING_TAG = "IsRecording_Tag";
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

    public static Pattern _score;
    public static ScorePartWise _scorePartWise;
    public static volatile boolean IsRecording;
    private TimeSignature _timeSignature;
    private int _bpm;
    private long startTime;
    private File tempFile;      // used to handle temporary recording storage for sharing files
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = new Runnable()
    {
        /**
         * <p>This provides the functionality of Vivace's recording timer.</p><br/>
         * {@inheritDoc}
         */
        @SuppressLint("DefaultLocale")
        @Override
        public void run()
        {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int)(millis / 1000) % 60;
            int minutes = seconds / 60;
            _recordingTimer.setText(String.format("%02d:%02d", minutes, seconds));

            timerHandler.postDelayed(this, 500);
        }
    };

    /** {@inheritDoc} */
    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ToolbarFragment toolbarFragment = (ToolbarFragment)getSupportFragmentManager().findFragmentById(R.id.toolbarFragment);
        Toolbar toolbar = (Toolbar)toolbarFragment.getView();
        setSupportActionBar(toolbar);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //IsRecording = preferences.getBoolean(IS_RECORDING_TAG, false);
        _timeSignature = new TimeSignature(preferences.getString(TIME_SIGNATURE_TAG, "4/4"));
        _bpm = preferences.getInt(BPM_TAG, 120);
        startTime = preferences.getLong(STARTTIME_TAG, 0);
        _score = new Pattern().setTempo(_bpm);
        _scorePartWise = ScorePartWise.createInstance(this, _timeSignature, _bpm);

        _tempoTextView = findViewById(R.id.tempo);
        _tempoTextView.setText(String.format("%d BPM", _bpm));
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
        _timeSignatureTextView.setText(_timeSignature.toString());
        _timeSignatureTextView.setOnClickListener(new View.OnClickListener()
        {
            /** {@inheritDoc} */
            @Override
            public void onClick(View v)
            {
                Bundle arguments = new Bundle();
                arguments.putString("TimeSignValue", _timeSignature.toString());
                TimeSignaturePickerFragment timeSignPickerFragment = new TimeSignaturePickerFragment();
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

                if (VivacePermissions.requestPermission((Activity)v.getContext(), VivacePermissionCodes.RECORD_AUDIO))
                {
                    startTime = System.currentTimeMillis();
                    analyzeAudio();
                }
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
                if (IsRecording)
                {
                    IsRecording = false;
                    timerHandler.removeCallbacks(timerRunnable);
                }
                else
                    analyzeAudio();
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
                timerHandler.removeCallbacks(timerRunnable);
                startTime = 0;
                _scorePartWise.clear();
                _scoreUI.evaluateJavascript(VexFlowScriptGenerator.getInstance().clearScore(), new ValueCallback<String>()
                {
                    @Override
                    public void onReceiveValue(String value)
                    {
                        Log.d(APPLICATION_TAG, "StopButton OnClickListener: Cleared WebView score.");
                    }
                });
            }
        });

        initializeWebView();
    }

    /** {@inheritDoc} */
    @Override
    protected void onPause()
    {
        Log.d(APPLICATION_TAG, "MainActivity - OnPause");
        super.onPause();

        timerHandler.removeCallbacks(timerRunnable);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
//        editor.putBoolean(IS_RECORDING_TAG, IsRecording);
        editor.putString(TIME_SIGNATURE_TAG, _timeSignature.toString());
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 3461 && tempFile != null && tempFile.exists())
        {
            tempFile.delete();
            tempFile = null;
        }
    }

    /** {@inheritDoc} */
    @SuppressLint("RestrictedApi")
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        boolean result = true;

        switch (item.getItemId())
        {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));

                break;
//            case R.id.action_search:
//
//                break;
            case R.id.action_share:
//                try
//                {
//                    FileStore fileStore = new FileStore(this);
//                    tempFile = new File(fileStore.getPrivateStorageDir(), "music.xml");
//                    FileOutputStream file = new FileOutputStream(tempFile);
////                    FileOutputStream file = openFileOutput("music.xml", MODE_PRIVATE);
////                    MusicXmlRenderer renderer = new MusicXmlRenderer();
////                    MusicStringParser parser = new MusicStringParser();
////                    parser.addParserListener(renderer);
////                    parser.parse(_score);
//                    _score = new Pattern(_scorePartWise.toJFuguePatternString()).setVoice(0).setInstrument("Piano");
//                    MidiParser parser = new MidiParser();
//
//
////                    MusicXmlParserListener renderer = new MusicXmlParserListener();
////                    StaccatoParser parser = new StaccatoParser();
////                    parser.addParserListener(renderer);
////                    parser.parse(_score);
////                    Log.d(APPLICATION_TAG, renderer.getMusicXMLString());
//
////                    Serializer serializer = new Serializer(file, "UTF-8");
////                    serializer.setIndent(4);
////                    serializer.write(renderer.getMusicXMLDoc());
//
//                    file.flush();
//                    file.close();
//
//                    Intent shareIntent = new Intent()
//                            .setAction(Intent.ACTION_SEND).setType("text/xml")
//                            .putExtra(Intent.EXTRA_EMAIL, "Hello World")
//                            .putExtra(Intent.EXTRA_STREAM, Uri.fromFile(tempFile))
//                            .putExtra(Intent.EXTRA_TEXT, "Sharing a file...")
//                            .putExtra(Intent.EXTRA_SUBJECT, "Subject");
//                    startActivityForResult(Intent.createChooser(shareIntent, "Share Your Recording"), 3461);
//                }
//                catch (IOException | StorageNotReadableException e)
//                {
//                    new AlertDialog.Builder(this)
//                            .setTitle("Error Encountered")
//                            .setMessage("Vivace was unable to share your recording.")
//                            .setPositiveButton("Ok", new DialogInterface.OnClickListener()
//                            {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which)
//                                {
//                                    dialog.dismiss();
//                                }
//                            }).create().show();
//                }
//
//                break;
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

        VivacePermissions.requestAllPermissions(this);
        return super.onCreateOptionsMenu(menu);
    }

    /** {@inheritDoc} */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults)
    {
        ToolbarFragment toolbarFragment = (ToolbarFragment)getSupportFragmentManager().findFragmentById(R.id.toolbarFragment);
        Toolbar toolbar = (Toolbar)toolbarFragment.getView();

        switch (requestCode)
        {
            case VivacePermissionCodes.RECORD_AUDIO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    _recordButton.setVisibility(View.VISIBLE);                 // permission granted
                else
                    _recordButton.setVisibility(View.INVISIBLE);                // permission denied

                break;
            case VivacePermissionCodes.INTERNET:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    _scoreUI.setVisibility(View.VISIBLE);                      // permission granted
                else
                    _scoreUI.setVisibility(View.INVISIBLE);                     // permission denied

                break;
            case VivacePermissionCodes.READ_EXTERNAL_STORAGE:
                assert toolbar != null;

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    toolbar.getMenu().findItem(R.id.action_share).setVisible(true);     // permission granted
                else
                    toolbar.getMenu().findItem(R.id.action_share).setVisible(false);    // permission denied

                break;
            case VivacePermissionCodes.WRITE_EXTERNAL_STORAGE:
                assert toolbar != null;

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    toolbar.getMenu().findItem(R.id.action_share).setVisible(true);     // permission granted
                else
                    toolbar.getMenu().findItem(R.id.action_share).setVisible(false);    // permission denied

                break;
            default:
                Log.d(APPLICATION_TAG, String.format("Got an unrecognized request code from asking for permissions: %d", requestCode));
                break;
        }
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
        _timeSignature = new TimeSignature(timeSign);
        _timeSignatureTextView.setText(_timeSignature.toString());
    }

    /** {@inheritDoc} */
    @Override
    public void onTimeSignDialogNegativeClick(String timeSign)
    { }

    /** {@inheritDoc} */
    @Override
    public void onFragmentInteraction(Uri uri)
    { }

    /** {@inheritDoc} */
    @Override
    public void onNewMeasure(Measure measure)
    {
        try
        {
            Log.d(APPLICATION_TAG, "onNewMeasure: Getting script to display notes.");
            final String script = VexFlowScriptGenerator.getInstance().addMeasureStave(measure);
            _scoreUI.getHandler().post(new Runnable()
            {
                @Override
                public void run()
                {
                    _scoreUI.evaluateJavascript(script, new ValueCallback<String>()
                    {
                        @Override
                        public void onReceiveValue(String value)
                        {
                            Log.d(APPLICATION_TAG, String.format("onNewMeasure: %s", value));
                        }
                    });
                }
            });
        }
        catch (NullPointerException e)
        {
            Log.e(APPLICATION_TAG, String.format("onNewMeasure: %s", e.getMessage()));
        }
    }

    private void analyzeAudio()
    {
        if (!IsRecording)
        {
//            startTime = System.currentTimeMillis();
            timerHandler.postDelayed(timerRunnable, 0);

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

                        HashMap<String, Float> keys = DFT.process(results, sampleSize, resultC.length, 7);
                        if (keys.keySet().isEmpty())
                        {
                            Log.d(TAG, "Found: Rest");
                            _scorePartWise.addNote(new Note("B4", 0.25, true));
//                            _recordingTimer.post(new Runnable()
//                            {
//                                /** {@inheritDoc} */
//                                @Override
//                                public void run()
//                                {
//                                    _recordingTimer.setText("");
//                                }
//                            });
                        }
                        else
                        {
                            Log.d(TAG, String.format("Found: %s.", keys.toString()));
                            //_scorePartWise.addNote(keys.keySet(), 0.25);
                            String pitch = (String)keys.keySet().toArray()[0];
                            _scorePartWise.addNote(new Note(pitch, 0.25));
//                            for (final String note : keys.keySet())
//                            {
//                                Log.d(TAG, String.format("Found: %s at freq=\"%f\"", note, keys.get(note)));
//                                _scorePartWise.addNote(new Note(note, 0.25));
////                                _recordingTimer.post(new Runnable()
////                                {
////                                    /** {@inheritDoc} */
////                                    @Override
////                                    public void run()
////                                    {
////                                        _recordingTimer.setText(note);
////                                    }
////                                });
//                            }
                        }
                    }

                    audioInput.stop();
                    audioInput.release();
                }

                /**
                 * Finds the closest power of 2 to the number of sixteenth notes that can be played
                 * per second (via the BPM). This is so the Fast Fourier Transform can alwasy perform
                 * the correct translation without failing.
                 *
                 * @param sixteenthsPerSecond The number of sixteenth notes that can be played per
                 *                            second, calculated from the BPM.
                 * @return The closest power of 2 to the number of sixteenth notes that can be played
                 * per second.
                 */
                @Contract(pure = true)
                private int closestPowerOf2(final double sixteenthsPerSecond)
                {
                    double[] powers_of_2 = { 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384, 32768 };
                    int result = -1;

                    for (int index = powers_of_2.length - 1; result == -1 && index > 0; index--)
                    {
                        if (powers_of_2[index] > sixteenthsPerSecond && powers_of_2[index - 1] < sixteenthsPerSecond)
                            result = (int)powers_of_2[index - 1];
                    }

                    return result;
                }
            });
            thread.start();
        }
        else
        {
            IsRecording = false;
            timerHandler.removeCallbacks(timerRunnable);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initializeWebView()
    {
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
            WebSettings scoreUISettings = _scoreUI.getSettings();
            scoreUISettings.setJavaScriptEnabled(true);
            scoreUISettings.setBuiltInZoomControls(true);
            scoreUISettings.setDisplayZoomControls(false);

            if (BuildConfig.DEBUG)
                WebView.setWebContentsDebuggingEnabled(true);
        }
        catch (IOException e)
        {
            Log.e(APPLICATION_TAG, e.getMessage());
            _recordButton.setVisibility(View.INVISIBLE);
            new AlertDialog.Builder(this).setTitle("Error").setMessage(
                    "An issue was encountered displaying the Music Sheet. Please " +
                    "quit and restart the application to continue. If this issue " +
                    "persists, please submit a report to https://github.com/colematthew4/Vivace/issues/new " +
                    "and describe your issue in detail.").setPositiveButton("Ok", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                }
            }).create().show();
        }
    }
}