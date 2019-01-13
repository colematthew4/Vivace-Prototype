package cole.matthew.vivace.Activities;

import android.os.Bundle;

import cole.matthew.vivace.Fragments.RecordingListFragment;
import cole.matthew.vivace.Models.Recordings.IRecording;
import cole.matthew.vivace.R;

public class OpenRecordingActivity extends BaseVivaceActivity implements RecordingListFragment.OnListFragmentInteractionListener {
    /** {@inheritDoc} */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open_recording_layout);
        setupActionBar();
    }

    @Override
    public void onListFragmentInteraction(IRecording recording) {
    }
}
