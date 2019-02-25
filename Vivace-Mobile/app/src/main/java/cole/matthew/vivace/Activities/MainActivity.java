package cole.matthew.vivace.Activities;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
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
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;

import cole.matthew.vivace.BuildConfig;
import cole.matthew.vivace.Fragments.TempoPickerFragment;
import cole.matthew.vivace.Fragments.TimeSignaturePickerFragment;
import cole.matthew.vivace.Fragments.ToolbarFragment;
import cole.matthew.vivace.Helpers.FileStore;
import cole.matthew.vivace.Helpers.VexFlowScriptGenerator;
import cole.matthew.vivace.Helpers.VivacePermissionCodes;
import cole.matthew.vivace.Helpers.VivacePermissions;
import cole.matthew.vivace.Math.ComplexNumber;
import cole.matthew.vivace.Math.DFTNormalization;
import cole.matthew.vivace.Math.DiscreteFourierTransform;
import cole.matthew.vivace.Math.FastFourierTransform;
import cole.matthew.vivace.Math.TransformType;
import cole.matthew.vivace.Models.Exceptions.StorageNotReadableException;
import cole.matthew.vivace.Models.Measure;
import cole.matthew.vivace.Models.Note;
import cole.matthew.vivace.Models.ScorePartWise;
import cole.matthew.vivace.Models.TimeSignature;
import cole.matthew.vivace.R;
import nu.xom.Serializer;

//import org.apache.commons.math3.complex.Complex;
//import org.apache.commons.math3.transform.DftNormalization;
//import org.apache.commons.math3.transform.FastFourierTransformer;
//import org.apache.commons.math3.transform.TransformType;

public class MainActivity extends BaseVivaceActivity
        implements TempoPickerFragment.NoticeTempoDialogListener,
                   TimeSignaturePickerFragment.NoticeTimeSignDialogListener,
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
    private DrawerLayout _activityLayout;

    public static Pattern _score;
    public static ScorePartWise _scorePartWise;
    public static volatile boolean IsRecording;
    private TimeSignature _timeSignature;
    private int _bpm;
    private long _startTime;
    private File _tempFile;      // used to handle temporary recording storage for sharing files
    private Handler timerHandler = new Handler();

    /**
     * This provides the functionality of Vivace's recording timer.
     */
    private final Runnable timerRunnable = () -> {
        long millis = System.currentTimeMillis() - _startTime;
        int seconds = (int)(millis / 1000) % 60;
        int minutes = seconds / 60;
        _recordingTimer.setText(String.format(Locale.US, "%02d:%02d", minutes, seconds));
        //        timerHandler.postDelayed(, 500);
    };

    private ValueAnimator _valueAnimator;
    private int _scrollY;
    private int _oldScrollY;

    private final View.OnScrollChangeListener _scrollChangeListener = new View.OnScrollChangeListener() {
        /** {@inheritDoc} */
        @Override
        public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            _scrollY = scrollY;
            _oldScrollY = oldScrollY;
//            Log.d(APPLICATION_TAG, "new scroll = " + _scrollY + "; old scroll = " + _oldScrollY);
            if (_scrollY > _oldScrollY && _scrollY - _oldScrollY <= 150) {
//                _valueAnimator.setIntValues(255, 0);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE |
                                                                 View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                                                                 View.SYSTEM_UI_FLAG_FULLSCREEN);
            } else if (_scrollY < _oldScrollY && _scrollY - _oldScrollY < 150) {
//                _valueAnimator.setIntValues(0, 255);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }

//            if (!_valueAnimator.isRunning()) {
//                _valueAnimator.cancel();
//            } else {
//                _valueAnimator.start();
//            }
        }
    };

    /** {@inheritDoc} */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(APPLICATION_TAG, "MainActivity - onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ToolbarFragment toolbarFragment = (ToolbarFragment)getFragmentManager().findFragmentById(R.id.toolbarFragment);
        Toolbar toolbar = (Toolbar)toolbarFragment.getView();
        setSupportActionBar(toolbar);

        if (savedInstanceState != null) {
            IsRecording = savedInstanceState.getBoolean(IS_RECORDING_TAG, false);
        }

        _activityLayout = findViewById(R.id.activity_layout);

        //        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //        _timeSignature = new TimeSignature(preferences.getString(TIME_SIGNATURE_TAG, "4/4"));
        //        _bpm = preferences.getInt(BPM_TAG, 120);
        //        _startTime = preferences.getLong(STARTTIME_TAG, 0);
        //        _score = new Pattern();
        //        _scorePartWise = ScorePartWise.createInstance(this, _timeSignature, _bpm);

        //        _tempoTextView = findViewById(R.id.tempo);
        //        _tempoTextView.setText(_bpm + " BPM");
        //        _tempoTextView.setOnClickListener(view -> {
        //              Bundle arguments = new Bundle();
        //              arguments.putInt("TempoValue", _bpm);
        //              TempoPickerFragment tempoPickerFragment = new TempoPickerFragment();
        //              tempoPickerFragment.setArguments(arguments);
        //              tempoPickerFragment.show(getFragmentManager(), "TempoPicker");
        //          });
        //
        //        _timeSignatureTextView = findViewById(R.id.timeSignSelector);
        //        _timeSignatureTextView.setText(_timeSignature.toString());
        //        _timeSignatureTextView.setOnClickListener(view -> {
        //            Bundle arguments = new Bundle();
        //            arguments.putString("TimeSignValue", _timeSignature.toString());
        //            TimeSignaturePickerFragment timeSignPickerFragment = new TimeSignaturePickerFragment();
        //            timeSignPickerFragment.setArguments(arguments);
        //            timeSignPickerFragment.show(getFragmentManager(), "TempoPicker");
        //        });

        NestedScrollView contentScroller = findViewById(R.id.contentScroller);
        if (contentScroller != null) {
            contentScroller.setOnScrollChangeListener(_scrollChangeListener);
        }
        //        _recordButton = findViewById(R.id.recordButton);
        //        _recordButton.setOnClickListener(new View.OnClickListener()
        //        {
        //            /** {@inheritDoc} */
        //            @Override
        //            public void onClick(View v)
        //            {
        //                _recordButton.setVisibility(View.GONE);
        //                _playbackLayout.setVisibility(View.VISIBLE);
        //
        //                if (VivacePermissions.requestPermission((Activity)v.getContext(), VivacePermissionCodes.RECORD_AUDIO))
        //                {
        //                    _startTime = System.currentTimeMillis();
        //                    analyzeAudio();
        //                }
        //            }
        //        });
        //
        //        _playbackLayout = findViewById(R.id.playbackLayout);
        //        _recordingTimer = findViewById(R.id.recordingTimer);
        //        _pauseButton = findViewById(R.id.pauseButton);
        //        _pauseButton.setOnClickListener(new View.OnClickListener()
        //        {
        //            /** {@inheritDoc} */
        //            @Override
        //            public void onClick(View v)
        //            {
        //                if (IsRecording)
        //                {
        //                    _pauseButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_white_24dp, null));
        //                    IsRecording = false;
        //                    timerHandler.removeCallbacks(timerRunnable);
        //                }
        //                else
        //                {
        //                    _pauseButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_white_24dp, null));
        //                    analyzeAudio();
        //                }
        //            }
        //        });
        //
        //        _stopButton = findViewById(R.id.stopButton);
        //        _stopButton.setOnClickListener(new View.OnClickListener()
        //        {
        //            /** {@inheritDoc} */
        //            @Override
        //            public void onClick(View v)
        //            {
        //                final Context context = v.getContext();
        //                IsRecording = false;
        //                timerHandler.removeCallbacks(timerRunnable);
        //
        //                new AlertDialog.Builder(v.getContext())
        //                        .setTitle("Save")
        //                        .setMessage("Do you wish to save this recording?")
        //                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
        //                        {
        //                            @Override
        //                            public void onClick(DialogInterface dialog, int which)
        //                            {
        //                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        //                                final String filename = sharedPreferences.getString(SettingsActivity.KEY_FILE_STORAGE_NAME, "recording_");
        //                                final boolean usePublicStorage = sharedPreferences.getBoolean(SettingsActivity.KEY_FILE_STORAGE_LOCATION, false);
        //                                int fileExtIndex = Integer.valueOf(sharedPreferences.getString(SettingsActivity.KEY_FILE_STORAGE_TYPE, "-1")) + 1;
        //                                String[] fileExts = getResources().getStringArray(R.array.pref_file_types);
        //                                final String fileExt = fileExts[fileExtIndex].split("\\(")[1].replace(")", "");
        //                                final FileStore fileStore = new FileStore((Activity)context);
        //
        //                                new Runnable()
        //                                {
        //                                    @Override
        //                                    public void run()
        //                                    {
        //                                        try
        //                                        {
        //                                            if (fileStore.isExternalStorageWritable())
        //                                            {
        //                                                File storageLocation = usePublicStorage ? fileStore.getPublicStorageDir() : fileStore.getPrivateStorageDir();
        //                                                if (!storageLocation.exists())
        //                                                    throw new StorageNotReadableException("Couldn't gain access to your external storage.");
        //
        //                                                _tempFile = new File(storageLocation, filename + "_" + (storageLocation.listFiles().length + 1) + fileExt);
        //                                                FileOutputStream file = new FileOutputStream(_tempFile);
        //                                                _score = new Pattern();
        //                                                _score.addElement(new Tempo(_bpm));
        //                                                _score.addElement(new Instrument(Instrument.PIANO));
        //                                                _score.add(_scorePartWise.toJFuguePatternString());
        //                                                MusicXmlRenderer renderer = new MusicXmlRenderer();
        //                                                MusicStringParser parser = new MusicStringParser();
        //                                                parser.addParserListener(renderer);
        //                                                parser.parse(_score);
        //
        //                                                Serializer serializer = new Serializer(file, "UTF-8");
        //                                                serializer.setIndent(4);
        //                                                serializer.write(renderer.getMusicXMLDoc());
        //
        //                                                file.flush();
        //                                                file.close();
        //                                                Toast.makeText(context, "Saved as " + _tempFile.getName(), Toast.LENGTH_LONG).show();
        //                                                _tempFile = null;
        //                                            }
        //                                        }
        //                                        catch (IOException | StorageNotReadableException e)
        //                                        {
        //                                            new AlertDialog.Builder(context)
        //                                                    .setTitle("Failed to Save Recording")
        //                                                    .setMessage(e.getMessage())
        //                                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener()
        //                                                    {
        //                                                        @Override
        //                                                        public void onClick(DialogInterface dialog, int which)
        //                                                        {
        //                                                            dialog.dismiss();
        //                                                        }
        //                                                    }).create().show();
        //                                        }
        //                                    }
        //                                }.run();
        //
        //                                _recordButton.setVisibility(View.VISIBLE);
        //                                _playbackLayout.setVisibility(View.GONE);
        //
        //                                _startTime = 0;
        //                                _scorePartWise.clear();
        //                                _scoreUI.evaluateJavascript(VexFlowScriptGenerator.getInstance().clearScore(), new ValueCallback<String>()
        //                                {
        //                                    @Override
        //                                    public void onReceiveValue(String value)
        //                                    {
        //                                        Log.d(APPLICATION_TAG, "StopButton OnClickListener: Cleared WebView score.");
        //                                    }
        //                                });
        //
        //                                dialog.dismiss();
        //                            }
        //                        })
        //                        .setNegativeButton("No", new DialogInterface.OnClickListener()
        //                        {
        //                            @Override
        //                            public void onClick(DialogInterface dialog, int which)
        //                            {
        //                                _recordButton.setVisibility(View.VISIBLE);
        //                                _playbackLayout.setVisibility(View.GONE);
        //
        //                                _startTime = 0;
        //                                _scorePartWise.clear();
        //                                _scoreUI.evaluateJavascript(VexFlowScriptGenerator.getInstance().clearScore(), new ValueCallback<String>()
        //                                {
        //                                    @Override
        //                                    public void onReceiveValue(String value)
        //                                    {
        //                                        Log.d(APPLICATION_TAG, "StopButton OnClickListener: Cleared WebView score.");
        //                                    }
        //                                });
        //
        //                                dialog.cancel();
        //                            }
        //                        }).create().show();
        //                }
        //        });

        _scoreUI = findViewById(R.id.scoreUI);
        initializeWebView();

//        _valueAnimator = new ValueAnimator();
//        _valueAnimator.setDuration(600);
//        _valueAnimator.setInterpolator(AnimationUtils.FAST_OUT_LINEAR_IN_INTERPOLATOR);
//        _valueAnimator.setIntValues(255, 0);
//        _valueAnimator.addUpdateListener(animation -> {
//            int value = (int)animation.getAnimatedValue();
//            Log.d(APPLICATION_TAG, String.valueOf(value));
//        });
//        _valueAnimator.start();
    }

    /** {@inheritDoc} */
    @Override
    protected void onPause() {
        Log.d(APPLICATION_TAG, "MainActivity - OnPause");
        timerHandler.removeCallbacks(timerRunnable);
        //        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        //        editor.putString(TIME_SIGNATURE_TAG, _timeSignature.toString());
        //        editor.putInt(BPM_TAG, _bpm);
        //        editor.putLong(STARTTIME_TAG, _startTime);
        //        editor.apply();
        super.onPause();
    }

    /** {@inheritDoc} */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(APPLICATION_TAG, "Configuration Change - " + newConfig.toString());
        super.onConfigurationChanged(newConfig);
    }

    /** {@inheritDoc} */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(APPLICATION_TAG, "MainActivity - OnSaveInstanceState");
        outState.putBoolean(IS_RECORDING_TAG, IsRecording);
        super.onSaveInstanceState(outState);
    }

    /** {@inheritDoc} */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 3461 && _tempFile != null && _tempFile.exists()) {
            // TODO: implement a retry mechanism for file deletion
            boolean deleted = _tempFile.delete();
            _tempFile = null;
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_actions, menu);
        VivacePermissions.requestAllPermissions(this);
        return super.onCreateOptionsMenu(menu);
    }

    /** {@inheritDoc} */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(APPLICATION_TAG, "onOptionsItemSelected: " + item);

        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.action_share:
                try {
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                    String temp_filename = sharedPreferences.getString(SettingsActivity.KEY_FILE_STORAGE_NAME, "vivace_temp_recording.xml");

                    FileStore fileStore = new FileStore(this);
                    if (_tempFile == null) {
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
                    }

                    Intent shareIntent = new Intent()
                            .setAction(Intent.ACTION_SEND)
                            .setType("text/xml")
                            .putExtra(Intent.EXTRA_EMAIL, "Hello World")
                            .putExtra(Intent.EXTRA_STREAM, Uri.fromFile(_tempFile))
                            .putExtra(Intent.EXTRA_TEXT, "Sharing a file...")
                            .putExtra(Intent.EXTRA_SUBJECT, "Subject");
                    startActivityForResult(Intent.createChooser(shareIntent, "Share Your Recording"), 3461);
                } catch (IOException | StorageNotReadableException e) {
                    new AlertDialog.Builder(this)
                            .setTitle("Error Encountered")
                            .setMessage("Vivace was unable to share your recording.")
                            .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss())
                            .create()
                            .show();
                }

                break;
            case R.id.action_open:
                startActivity(new Intent(this, OpenRecordingActivity.class));
                break;
            case R.id.action_audio_settings:
                View sidePanel = findViewById(R.id.audio_settings_side_layout);
                if (_activityLayout.isDrawerVisible(sidePanel)) {
                    _activityLayout.closeDrawer(sidePanel);
                } else {
                    _activityLayout.openDrawer(sidePanel);
                }
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /** {@inheritDoc} */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        ToolbarFragment toolbarFragment = (ToolbarFragment)getFragmentManager().findFragmentById(R.id.toolbarFragment);
        Toolbar toolbar = (Toolbar)toolbarFragment.getView();

        switch (requestCode) {
            case VivacePermissionCodes.RECORD_AUDIO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    _recordButton.setVisibility(View.VISIBLE);                 // permission granted
                } else {
                    _recordButton.setVisibility(View.INVISIBLE);                // permission denied
                }

                break;
            case VivacePermissionCodes.INTERNET:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    _scoreUI.setVisibility(View.VISIBLE);                      // permission granted
                } else {
                    _scoreUI.setVisibility(View.INVISIBLE);                     // permission denied
                }

                break;
            case VivacePermissionCodes.READ_EXTERNAL_STORAGE:
                assert toolbar != null;

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    toolbar.getMenu().findItem(R.id.action_share).setVisible(true);     // permission granted
                } else {
                    toolbar.getMenu().findItem(R.id.action_share).setVisible(false);    // permission denied
                }

                break;
            case VivacePermissionCodes.WRITE_EXTERNAL_STORAGE:
                assert toolbar != null;

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    toolbar.getMenu().findItem(R.id.action_share).setVisible(true);     // permission granted
                } else {
                    toolbar.getMenu().findItem(R.id.action_share).setVisible(false);    // permission denied
                }

                break;
            default:
                Log.d(APPLICATION_TAG, "Got an unrecognized request code from asking for permissions: " + requestCode);
                break;
        }
    }

    /** {@inheritDoc} */
    @SuppressLint("DefaultLocale")
    @Override
    public void onTempoDialogPositiveClick(int tempo) {
        Log.d(APPLICATION_TAG, "MainActivity - onTempoDialogPositiveClick: tempo = " + tempo);

        _bpm = tempo;
        _tempoTextView.setText(_bpm + " BPM");
    }

    /** {@inheritDoc} */
    @Override
    public void onTempoDialogNegativeClick(int tempo) {
    }

    /** {@inheritDoc} */
    @Override
    public void onTimeSignDialogPositiveClick(String timeSign) {
        Log.d(APPLICATION_TAG, "MainActivity - onTimeSignDialogPositiveClick: time signature = " + timeSign);

        _timeSignature = new TimeSignature(timeSign);
        _timeSignatureTextView.setText(_timeSignature.toString());
    }

    /** {@inheritDoc} */
    @Override
    public void onTimeSignDialogNegativeClick(String timeSign) {
    }

    /** {@inheritDoc} */
    @Override
    public void onNewMeasure(Measure measure) {
        Log.d(APPLICATION_TAG, "MainActivity - onNewMeasure: Getting script to display notes.");

        try {
            final String script = VexFlowScriptGenerator.getInstance().addMeasureStave(measure);
            _scoreUI.getHandler().post(() -> {
                _scoreUI.evaluateJavascript(script, value -> {
                    Log.d(APPLICATION_TAG, "MainActivity - onNewMeasure: " + value);
                });
            });
        } catch (NullPointerException e) {
            Log.e(APPLICATION_TAG, "MainActivity - onNewMeasure: " + e.getMessage());
        }
    }

    private void analyzeAudio() {
        if (!IsRecording) {
            //            _startTime = System.currentTimeMillis();
            timerHandler.postDelayed(timerRunnable, 0);

            Thread thread = new Thread(new Runnable() {
                private static final String TAG = "FrequencyThreadTAG";
                private int channel_config = AudioFormat.CHANNEL_IN_MONO;
                private int format = AudioFormat.ENCODING_PCM_16BIT;
                private int sampleSize = 44100;
                //private int bufferSize = AudioRecord.getMinBufferSize(sampleSize, channel_config, format);
                // must be a power of 2 for the FFT transform to work
                private int bufferSize = closestPowerOf2(sampleSize / (_bpm * 4f / 60f));
                private AudioRecord audioInput = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleSize, channel_config, format, bufferSize);

                /** {@inheritDoc} */
                @Override
                public void run() {
                    Log.i(TAG, "bufferSize: " + String.valueOf(bufferSize));

                    //Read audio
                    short[] audioBuffer = new short[bufferSize]; //short
                    audioInput.startRecording();
                    MainActivity.IsRecording = true;
                    int bytesRecorded = 0;

                    while (MainActivity.IsRecording) {
                        bytesRecorded += audioInput.read(audioBuffer, 0, bufferSize);
                        Log.i(TAG, "bytesRecorded: " + String.valueOf(bytesRecorded));

                        double[] buffer = new double[audioBuffer.length];
                        int idx = 0;
                        for (int i = 0; i < audioBuffer.length && idx < audioBuffer.length; i += 2) {
                            short bLow = audioBuffer[i];
                            short bHigh = audioBuffer[i + 1];

                            buffer[idx++] = (bLow & 0xFF | bHigh << 8) / 32767f;
                            if (channel_config == AudioFormat.CHANNEL_IN_STEREO) {
                                i += 2;
                            }
                        }

                        FastFourierTransform fft = new FastFourierTransform(DFTNormalization.STANDARD);
                        ComplexNumber[] resultC = fft.transform(buffer, TransformType.FORWARD);

                        double[] results = new double[resultC.length];
                        for (int i = 0; i < resultC.length; ++i) {
                            double real = resultC[i].getReal();
                            double imag = resultC[i].getImaginary();

                            results[i] = Math.sqrt(real * real + imag * imag);
                        }

                        HashMap<String, Float> keys = DiscreteFourierTransform.processAudio(results, sampleSize, resultC.length, 24);
                        if (keys.keySet().isEmpty()) {
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
                        } else {
                            Log.d(TAG, "Found: " + keys.toString());
                            //_scorePartWise.addNote(keys.keySet(), 0.25);
                            String pitch = (String)keys.keySet().toArray()[0];
                            _scorePartWise.addNote(new Note(pitch, 0.25));
                            //                            for (final String note : keys.keySet())
                            //                            {
                            //                                Log.d(TAG, "Found: " + note + " at freq=\"" + keys.get(note) + "\"");
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
                private int closestPowerOf2(final double sixteenthsPerSecond) {
                    double[] powers_of_2 = { 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384, 32768 };
                    int result = -1;

                    for (int index = powers_of_2.length - 1; result == -1 && index > 0; index--) {
                        if (powers_of_2[index] > sixteenthsPerSecond && powers_of_2[index - 1] < sixteenthsPerSecond) {
                            result = (int)powers_of_2[index - 1];
                        }
                    }

                    return result;
                }
            });
            thread.start();
        } else {
            IsRecording = false;
            timerHandler.removeCallbacks(timerRunnable);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initializeWebView() {
        try {
            InputStream inputStream = getAssets().open("minifiedHTML.html");
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
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

            if (BuildConfig.DEBUG) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        } catch (IOException e) {
            Log.e(APPLICATION_TAG, e.getMessage());
            _recordButton.setVisibility(View.INVISIBLE);
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("An issue was encountered displaying the Music Sheet. Please quit and restart the application to continue. If this issue persists, please submit a " +
                                "report to https://github.com/colematthew4/Vivace/issues/new and describe your issue in detail.")
                    .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        }
    }
}