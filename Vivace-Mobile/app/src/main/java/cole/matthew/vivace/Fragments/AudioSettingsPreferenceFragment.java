package cole.matthew.vivace.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import cole.matthew.vivace.R;

/**
 * This fragment shows Vivace's general preferences only. It is used when the
 * activity is showing a two-pane settings UI.
 */
public final class AudioSettingsPreferenceFragment extends BaseVivacePreferenceFragment {
    /**
     * Called to do initial creation of a fragment.  This is called after
     * {@link #onAttach(Activity)} and before
     * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}, but is not called if the fragment
     * instance is retained across Activity re-creation (see {@link #setRetainInstance(boolean)}).
     * <p>Note that this can be called while the fragment's activity is still in the process of
     * being created.  As such, you can not rely on things like the activity's content view
     * hierarchy being initialized at this point. If you want to do work once the activity itself is
     * created, see {@link #onActivityCreated(Bundle)}.
     * <p>If your app's <code>targetSdkVersion</code> is {@link android.os.Build.VERSION_CODES#M}
     * or lower, child fragments being restored from the savedInstanceState are restored after
     * <code>onCreate</code> returns. When targeting {@link android.os.Build.VERSION_CODES#N} or
     * above and running on an N or newer platform version they are restored by
     * <code>Fragment.onCreate</code>.</p>
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.audio_settings);
        setHasOptionsMenu(false);

        // Bind the summaries of List/Switch preferences to their values. When their values change,
        // their summaries are updated to reflect the new value, per the Android Design guidelines.
        bindPreferenceSummaryToValue(findPreference("audio_setting_time_sign"));
        bindPreferenceSummaryToValue(findPreference("audio_setting_instrument"));
    }
}
