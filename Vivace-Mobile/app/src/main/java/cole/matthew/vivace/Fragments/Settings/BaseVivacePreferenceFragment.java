package cole.matthew.vivace.Fragments.Settings;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v14.preference.PreferenceDialogFragment;
import android.support.v14.preference.PreferenceFragment;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager;

import org.jetbrains.annotations.NotNull;

import cole.matthew.vivace.Helpers.FileStore;
import cole.matthew.vivace.Models.Exceptions.InsufficientStorageException;
import cole.matthew.vivace.Models.Exceptions.StorageNotReadableException;
import cole.matthew.vivace.Models.Exceptions.StorageNotWritableException;

public abstract class BaseVivacePreferenceFragment extends PreferenceFragment {
    protected Resources _resources;

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    protected static Preference.OnPreferenceChangeListener BIND_PREFFERENCE_SUMMARY_VALUE_LISTENER = new Preference.OnPreferenceChangeListener() {
        /**
         * Called when a Preference has been changed by the user. This is
         * called before the state of the Preference is about to be updated and
         * before the state is persisted.
         *
         * @param preference The changed Preference.
         * @param value The new value of the Preference.
         * @return True to update the state of the Preference with the new value.
         */
        @Override
        public boolean onPreferenceChange(Preference preference, @NotNull Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference)preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
            } else if (!(preference instanceof SwitchPreference)) {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }

            return true;
        }
    };

    /**
     * A preference value change listener that transfers saved files to/from public and private
     * external storage on your device.
     */
    protected static final Preference.OnPreferenceChangeListener STORAGE_DIRECTORY_SWITCH_PREFERENCE_LISTENER = new Preference.OnPreferenceChangeListener() {
        /** {@inheritDoc} */
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            boolean result = true;
            boolean switchValue = (boolean)newValue;

            try {
                FileStore fileStore = new FileStore((Activity)preference.getContext());

                if (switchValue) {
                    fileStore.transferStorageToPublic();
                } else {
                    fileStore.transferStorageToPrivate();
                }
            } catch (InsufficientStorageException e) {
                result = false;
                new AlertDialog.Builder(preference.getContext())
                        .setTitle("Not Enough Storage")
                        .setMessage(e.getMessage())
                        .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss())
                        .create()
                        .show();
            } catch (StorageNotReadableException | StorageNotWritableException e) {
                result = false;
                new AlertDialog.Builder(preference.getContext())
                        .setTitle("Failed to Save Recordings to Device Storage")
                        .setMessage(e.getMessage())
                        .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss())
                        .create()
                        .show();
            }

            return result;
        }
    };

    /**
     * {@inheritDoc}
     * Initializes the resource cache.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _resources = getResources();
    }

    /** {@inheritDoc} */
    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (preference instanceof TempoPickerPreference) {
            PreferenceDialogFragment fragment = TempoPickerDialogPreferenceFragment.newInstance(preference.getKey());
            fragment.setTargetFragment(this, 0);
            fragment.show(getFragmentManager(), null);
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #BIND_PREFFERENCE_SUMMARY_VALUE_LISTENER
     */
    protected static void bindPreferenceSummaryToValue(@NotNull Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(BIND_PREFFERENCE_SUMMARY_VALUE_LISTENER);

        // Trigger the listener immediately with the preference's current value.
        BIND_PREFFERENCE_SUMMARY_VALUE_LISTENER.onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences(preference.getContext())
                                                                                                .getString(preference.getKey(), ""));
    }
}
