package cole.matthew.vivace.Fragments.Settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cole.matthew.vivace.R;

/**
 * This fragment shows Vivace's general preferences only. It is used when the
 * activity is showing a two-pane settings UI.
 */
public final class AudioSettingsPreferenceFragment extends BaseVivacePreferenceFragment {
    private String KEY_AUDIO_TIME_SIGNATURE;
    private String KEY_AUDIO_TEMPO;
    private String KEY_AUDIO_INSTRUMENT;

    /** {@inheritDoc} */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindPreferenceKeys();
    }

    /** {@inheritDoc} */
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        setPreferencesFromResource(R.xml.audio_settings, s);
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
        KEY_AUDIO_TIME_SIGNATURE = _resources.getString(R.string.settings_audio_time_sign);
        KEY_AUDIO_TEMPO = _resources.getString(R.string.settings_audio_tempo);
        KEY_AUDIO_INSTRUMENT = _resources.getString(R.string.settings_audio_instrument);
    }

    /**
     * Binds the summaries of preferences to their values. When their values change, their summaries are updated to reflect the new value, per the Android Design guidelines.
     */
    private void bindPreferenceSummaryToValue() {
        bindPreferenceSummaryToValue(findPreference(KEY_AUDIO_TIME_SIGNATURE));
        bindPreferenceSummaryToValue(findPreference(KEY_AUDIO_INSTRUMENT));
    }
}
