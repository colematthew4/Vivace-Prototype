package cole.matthew.vivace.Fragments.Settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cole.matthew.vivace.Activities.OpenSourceSoftwareListActivity;
import cole.matthew.vivace.R;

/**
 * This fragment shows Vivace's general preferences only. It is used when the
 * activity is showing a two-pane settings UI.
 */
public final class VivaceSettingsPreferenceFragment extends BaseVivacePreferenceFragment {
    private String KEY_STORAGE_FILENAME;
    private String KEY_STORAGE_FILETYPE;
    private String KEY_STORAGE_DIRECTORY;
    private String KEY_ABOUT_THIRD_PARTY_LIBRARIES;

    /** {@inheritDoc} */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindPreferenceKeys();
    }

    /** {@inheritDoc} */
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        setPreferencesFromResource(R.xml.vivace_settings, s);
    }

    /** {@inheritDoc} */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        bindPreferenceSummaryToValue();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * Binds the keys associated with the settings in this {@link android.support.v14.preference.PreferenceFragment}.
     */
    private void bindPreferenceKeys() {
        KEY_STORAGE_FILENAME = _resources.getString(R.string.settings_key_storage_filename);
        KEY_STORAGE_FILETYPE = _resources.getString(R.string.settings_key_storage_filetype);
        KEY_STORAGE_DIRECTORY = _resources.getString(R.string.settings_key_storage_directory);
        KEY_ABOUT_THIRD_PARTY_LIBRARIES = _resources.getString(R.string.settings_key_about_oss);
    }

    /**
     * Bind the summaries of preferences to their values. When their values change, their summaries are updated to reflect the new value, per the Android Design guidelines.
     */
    private void bindPreferenceSummaryToValue() {
        bindPreferenceSummaryToValue(findPreference(KEY_STORAGE_FILENAME));
        bindPreferenceSummaryToValue(findPreference(KEY_STORAGE_FILETYPE));
        findPreference(KEY_STORAGE_DIRECTORY).setOnPreferenceChangeListener(STORAGE_DIRECTORY_SWITCH_PREFERENCE_LISTENER);
        findPreference(KEY_ABOUT_THIRD_PARTY_LIBRARIES).setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(getActivity(), OpenSourceSoftwareListActivity.class));
            return true;
        });
    }
}