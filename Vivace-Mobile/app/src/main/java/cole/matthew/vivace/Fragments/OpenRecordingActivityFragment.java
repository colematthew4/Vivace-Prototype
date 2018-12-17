package cole.matthew.vivace.Fragments;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cole.matthew.vivace.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class OpenRecordingActivityFragment extends Fragment {
    public OpenRecordingActivityFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_open_recording, container, false);
    }
}
