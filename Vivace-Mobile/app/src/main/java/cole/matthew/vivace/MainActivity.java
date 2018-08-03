package cole.matthew.vivace;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import android.widget.Toast;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jfugue.Instrument;
import org.jfugue.MusicStringParser;
import org.jfugue.MusicXmlRenderer;
import org.jfugue.Pattern;
import org.jfugue.Tempo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import cole.matthew.vivace.Exceptions.StorageNotReadableException;
import cole.matthew.vivace.Helpers.DiscreteFourierTransform;
import cole.matthew.vivace.Helpers.FileStore;
import cole.matthew.vivace.Helpers.VexFlowScriptGenerator;
import cole.matthew.vivace.Helpers.VivacePermissionCodes;
import cole.matthew.vivace.Helpers.VivacePermissions;
import cole.matthew.vivace.Models.Measure;
import cole.matthew.vivace.Models.Note;
import cole.matthew.vivace.Models.ScorePartWise;
import cole.matthew.vivace.Models.TimeSignature;
import nu.xom.Serializer;

public class MainActivity extends AppCompatActivity
        implements TempoPickerFragment.NoticeTempoDialogListener,
                   TimeSignaturePickerFragment.NoticeTimeSignDialogListener,
                   ToolbarFragment.OnFragmentInteractionListener,
                   ScorePartWise.OnNewMeasureListener
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

    public static Pattern _score;
    public static ScorePartWise _scorePartWise;
    public static volatile boolean IsRecording;
    private TimeSignature _timeSignature;
    private int _bpm;
    private long _startTime;
    private File _tempFile;      // used to handle temporary recording storage for sharing files
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
            long millis = System.currentTimeMillis() - _startTime;
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
        Log.d(APPLICATION_TAG, "MainActivity - onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ToolbarFragment toolbarFragment = (ToolbarFragment)getSupportFragmentManager().findFragmentById(R.id.toolbarFragment);
        Toolbar toolbar = (Toolbar)toolbarFragment.getView();
        setSupportActionBar(toolbar);

        if (savedInstanceState != null)
            IsRecording = savedInstanceState.getBoolean(IS_RECORDING_TAG, false);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        _timeSignature = new TimeSignature(preferences.getString(TIME_SIGNATURE_TAG, "4/4"));
        _bpm = preferences.getInt(BPM_TAG, 120);
        _startTime = preferences.getLong(STARTTIME_TAG, 0);
//        _score = new Pattern();
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
                    _startTime = System.currentTimeMillis();
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
                    _pauseButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_white_24dp, null));
                    IsRecording = false;
                    timerHandler.removeCallbacks(timerRunnable);
                }
                else
                {
                    _pauseButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_white_24dp, null));
                    analyzeAudio();
                }
            }
        });

        _stopButton = findViewById(R.id.stopButton);
        _stopButton.setOnClickListener(new View.OnClickListener()
        {
            /** {@inheritDoc} */
            @Override
            public void onClick(View v)
            {
                final Context context = v.getContext();
                IsRecording = false;
                timerHandler.removeCallbacks(timerRunnable);

                new AlertDialog.Builder(v.getContext())
                        .setTitle("Save")
                        .setMessage("Do you wish to save this recording?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                                final String filename = sharedPreferences.getString(SettingsActivity.KEY_FILE_STORAGE_NAME, "recording_");
                                final boolean usePublicStorage = sharedPreferences.getBoolean(SettingsActivity.KEY_FILE_STORAGE_LOCATION, false);
                                int fileExtIndex = Integer.valueOf(sharedPreferences.getString(SettingsActivity.KEY_FILE_STORAGE_TYPE, "-1")) + 1;
                                String[] fileExts = getResources().getStringArray(R.array.pref_file_types);
                                final String fileExt = fileExts[fileExtIndex].split("\\(")[1].replace(")", "");
                                final FileStore fileStore = new FileStore((Activity)context);

                                new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        try
                                        {
                                            if (fileStore.isExternalStorageWritable())
                                            {
                                                File storageLocation = usePublicStorage ? fileStore.getPublicStorageDir() : fileStore.getPrivateStorageDir();
                                                if (!storageLocation.exists())
                                                    throw new StorageNotReadableException("Couldn't gain access to your external storage.");

                                                _tempFile = new File(storageLocation, String.format("%s_%d%s", filename, storageLocation.listFiles().length + 1, fileExt));
                                                FileOutputStream file = new FileOutputStream(
                                                        _tempFile);
                                                _score = new Pattern();
                                                _score.addElement(new Tempo(_bpm));
                                                _score.addElement(new Instrument(Instrument.PIANO));
                                                _score.add(_scorePartWise.toJFuguePatternString());
                                                MusicXmlRenderer renderer = new MusicXmlRenderer();
                                                MusicStringParser parser = new MusicStringParser();
                                                parser.addParserListener(renderer);
                                                parser.parse(_score);

                                                Serializer serializer = new Serializer(file, "UTF-8");
                                                serializer.setIndent(4);
                                                serializer.write(renderer.getMusicXMLDoc());

                                                file.flush();
                                                file.close();
                                                Toast.makeText(context, String.format("Saved as %s", _tempFile
                                                        .getName()), Toast.LENGTH_LONG).show();
                                                _tempFile = null;
                                            }
                                        }
                                        catch (IOException | StorageNotReadableException e)
                                        {
                                            new AlertDialog.Builder(context)
                                                    .setTitle("Failed to Save Recording")
                                                    .setMessage(e.getMessage())
                                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                                                    {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which)
                                                        {
                                                            dialog.dismiss();
                                                        }
                                                    }).create().show();
                                        }
                                    }
                                }.run();

                                _recordButton.setVisibility(View.VISIBLE);
                                _playbackLayout.setVisibility(View.GONE);

                                _startTime = 0;
                                _scorePartWise.clear();
                                _scoreUI.evaluateJavascript(VexFlowScriptGenerator.getInstance().clearScore(), new ValueCallback<String>()
                                {
                                    @Override
                                    public void onReceiveValue(String value)
                                    {
                                        Log.d(APPLICATION_TAG, "StopButton OnClickListener: Cleared WebView score.");
                                    }
                                });

                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                _recordButton.setVisibility(View.VISIBLE);
                                _playbackLayout.setVisibility(View.GONE);

                                _startTime = 0;
                                _scorePartWise.clear();
                                _scoreUI.evaluateJavascript(VexFlowScriptGenerator.getInstance().clearScore(), new ValueCallback<String>()
                                {
                                    @Override
                                    public void onReceiveValue(String value)
                                    {
                                        Log.d(APPLICATION_TAG, "StopButton OnClickListener: Cleared WebView score.");
                                    }
                                });

                                dialog.cancel();
                            }
                        }).create().show();
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
        editor.putString(TIME_SIGNATURE_TAG, _timeSignature.toString());
        editor.putInt(BPM_TAG, _bpm);
        editor.putLong(STARTTIME_TAG, _startTime);
        editor.apply();
    }

    /** {@inheritDoc} */
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        Log.d(APPLICATION_TAG, String.format("Configuration Change - %s", newConfig.toString()));
        super.onConfigurationChanged(newConfig);
    }

    /** {@inheritDoc} */
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        Log.d(APPLICATION_TAG, "MainActivity - OnSaveInstanceState");

        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_RECORDING_TAG, IsRecording);
    }

    /** {@inheritDoc} */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 3461 && _tempFile != null && _tempFile.exists())
        {
            _tempFile.delete();
            _tempFile = null;
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
            case R.id.action_share:
                try
                {
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                    String temp_filename = sharedPreferences.getString(SettingsActivity.KEY_FILE_STORAGE_NAME, "vivace_temp_recording.xml");

                    FileStore fileStore = new FileStore(this);
//                    if (_tempFile == null)
//                    {
                        _tempFile = new File(fileStore.getPrivateStorageDir(), temp_filename);
                        FileOutputStream file = new FileOutputStream(_tempFile);
                        _score = new Pattern();
                        _score.addElement(new Tempo(_bpm));
                        _score.addElement(new Instrument(Instrument.PIANO));
                        _score.add(_scorePartWise.toJFuguePatternString());
                        MusicXmlRenderer renderer = new MusicXmlRenderer();
                        MusicStringParser parser = new MusicStringParser();
                        parser.addParserListener(renderer);
                        parser.parse(_score);

                        Serializer serializer = new Serializer(file, "UTF-8");
                        serializer.setIndent(4);
                        serializer.write(renderer.getMusicXMLDoc());

                        file.flush();
                        file.close();
//                    }

                    Intent shareIntent = new Intent()
                            .setAction(Intent.ACTION_SEND).setType("text/xml")
                            .putExtra(Intent.EXTRA_EMAIL, "Hello World")
                            .putExtra(Intent.EXTRA_STREAM, Uri.fromFile(_tempFile))
                            .putExtra(Intent.EXTRA_TEXT, "Sharing a file...")
                            .putExtra(Intent.EXTRA_SUBJECT, "Subject");
                    startActivityForResult(Intent.createChooser(shareIntent, "Share Your Recording"), 3461);
                }
                catch (IOException | StorageNotReadableException e)
                {
                    new AlertDialog.Builder(this)
                            .setTitle("Error Encountered")
                            .setMessage("Vivace was unable to share your recording.")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    dialog.dismiss();
                                }
                            }).create().show();
                }

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

//        MenuItem searchItem = menu.findItem(R.id.action_search);
//        SearchView searchView = (SearchView) searchItem.getActionView();
//        searchView.getQuery();

        // Configure the search info and add any event listeners...
        // Define the listener
//        MenuItem.OnActionExpandListener expandListener = new MenuItem.OnActionExpandListener()
//        {
//            /** {@inheritDoc} */
//            @Contract(pure = true)
//            @Override
//            public boolean onMenuItemActionCollapse(MenuItem item)
//            {
//                // Do something when action item collapses
//                return true;  // Return true to collapse action view
//            }
//
//            /** {@inheritDoc} */
//            @Contract(pure = true)
//            @Override
//            public boolean onMenuItemActionExpand(MenuItem item)
//            {
//                // Do something when expanded
//                return true;  // Return true to expand action view
//            }
//        };

        // Get the MenuItem for the action item
//        MenuItem actionMenuItem = menu.findItem(R.id.action_search);

        // Assign the listener to that action item
//        actionMenuItem.setOnActionExpandListener(expandListener);

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
        Log.d(APPLICATION_TAG, "MainActivity - onTempoDialogPositiveClick: tempo = " + tempo);

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
        Log.d(APPLICATION_TAG, "MainActivity - onTimeSignDialogPositiveClick: time signature = " + timeSign);

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
        Log.d(APPLICATION_TAG, "MainActivity - onNewMeasure: Getting script to display notes.");

        try
        {
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
                            Log.d(APPLICATION_TAG, String.format("MainActivity - onNewMeasure: %s", value));
                        }
                    });
                }
            });
        }
        catch (NullPointerException e)
        {
            Log.e(APPLICATION_TAG, String.format("MainActivity - onNewMeasure: %s", e.getMessage()));
        }
    }

    private void analyzeAudio()
    {
        if (!IsRecording)
        {
//            _startTime = System.currentTimeMillis();
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

                        HashMap<String, Float> keys = DiscreteFourierTransform.processAudio(results, sampleSize, resultC.length, 24);
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