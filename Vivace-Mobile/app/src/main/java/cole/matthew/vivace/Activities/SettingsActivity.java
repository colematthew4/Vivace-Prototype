package cole.matthew.vivace.Activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import cole.matthew.vivace.Exceptions.InsufficientStorageException;
import cole.matthew.vivace.Exceptions.StorageNotReadableException;
import cole.matthew.vivace.Exceptions.StorageNotWritableException;
import cole.matthew.vivace.Fragments.ToolbarFragment;
import cole.matthew.vivace.Helpers.BaseVivacePreferenceFragment;
import cole.matthew.vivace.Helpers.FileStore;
import cole.matthew.vivace.R;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity implements IVivaceActivity {
    public static final String KEY_FILE_STORAGE_NAME = "file_storage_name";
    public static final String KEY_FILE_STORAGE_TYPE = "file_storage_type";
    public static final String KEY_FILE_STORAGE_LOCATION = "file_storage_location";

    /**
     * A preference value change listener that transfers saved files to/from public and private
     * external storage on your device.
     */
    private static Preference.OnPreferenceChangeListener sBindSwitchPreferenceValueListener = new Preference.OnPreferenceChangeListener() {
        /** {@inheritDoc} */
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            boolean result = true;
            boolean switchValue = (boolean)newValue;

            try {
                FileStore fileStore = new FileStore((Activity)preference.getContext());

                if (switchValue)
                    fileStore.transferStorageToPublic();
                else
                    fileStore.transferStorageToPrivate();
            }
            catch (InsufficientStorageException e) {
                result = false;
                new AlertDialog.Builder(preference.getContext())
                        .setTitle("Not Enough Storage")
                        .setMessage(e.getMessage())
                        .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss())
                        .create()
                        .show();
            }
            catch (StorageNotReadableException | StorageNotWritableException e) {
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

    /** {@inheritDoc} */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @Override
    public void setupActionBar() {
        ToolbarFragment toolbarFragment = (ToolbarFragment)getFragmentManager().findFragmentById(R.id.toolbarFragment);
        Toolbar toolbar = (Toolbar)toolbarFragment.getView();
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        boolean result = true;

        switch (item.getItemId()) {
            case android.R.id.home:
                if (!super.onMenuItemSelected(featureId, item))
                    NavUtils.navigateUpFromSameTask(this);

                break;
            default:
                result = super.onMenuItemSelected(featureId, item);
                break;
        }

        return result;
    }

    /** {@inheritDoc} */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(@NotNull Context context) {
        return (context.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /** {@inheritDoc} */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName) ||
               VivaceSettingsPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows Vivace's general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    public static class VivaceSettingsPreferenceFragment extends BaseVivacePreferenceFragment {
        /**
         * Called to do initial creation of a fragment.  This is called after
         * {@link #onAttach(Activity)} and before
         * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}, but is not called if the fragment
         * instance is retained across Activity re-creation (see {@link #setRetainInstance(boolean)}).
         * <p>Note that this can be called while the fragment's activity is
         * still in the process of being created.  As such, you can not rely
         * on things like the activity's content view hierarchy being initialized
         * at this point.  If you want to do work once the activity itself is
         * created, see {@link #onActivityCreated(Bundle)}.
         * <p>If your app's <code>targetSdkVersion</code> is {@link android.os.Build.VERSION_CODES#M}
         * or lower, child fragments being restored from the savedInstanceState are restored after
         * <code>onCreate</code> returns. When targeting {@link android.os.Build.VERSION_CODES#N} or
         * above and running on an N or newer platform version
         * they are restored by <code>Fragment.onCreate</code>.</p>
         *
         * @param savedInstanceState If the fragment is being re-created from
         *                           a previous saved state, this is the state.
         */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_settings);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference(SettingsActivity.KEY_FILE_STORAGE_NAME));
            bindPreferenceSummaryToValue(findPreference(SettingsActivity.KEY_FILE_STORAGE_TYPE));
            findPreference(
                    SettingsActivity.KEY_FILE_STORAGE_LOCATION).setOnPreferenceChangeListener(
                    sBindSwitchPreferenceValueListener);
        }
    }
}