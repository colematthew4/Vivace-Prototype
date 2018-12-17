//package cole.matthew.vivace;
//
//import android.annotation.TargetApi;
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.content.res.Configuration;
//import android.media.Ringtone;
//import android.media.RingtoneManager;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.preference.ListPreference;
//import android.preference.Preference;
//import android.preference.PreferenceActivity;
//import android.support.v7.app.ActionBar;
//import android.preference.PreferenceFragment;
//import android.preference.PreferenceManager;
//import android.preference.RingtonePreference;
//import android.support.v7.widget.Toolbar;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.MenuItem;
//import android.support.v4.app.NavUtils;
//import android.view.ViewGroup;
//
//import org.jetbrains.annotations.NotNull;
//
//import java.util.List;
//
///**
// * A {@link PreferenceActivity} that presents a set of application settings. On
// * handset devices, settings are presented as a single list. On tablets,
// * settings are split by category, with category headers shown to the left of
// * the list of settings.
// * <p>
// * See <a href="http://developer.android.com/design/patterns/settings.html">
// * Android Design: Settings</a> for design guidelines and the <a
// * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
// * API Guide</a> for more information on developing a Settings UI.
// */
//public class SettingsActivity extends AppCompatPreferenceActivity
//{
//    /**
//     * A preference value change listener that updates the preference's summary
//     * to reflect its new value.
//     */
//    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener()
//    {
//        @Override
//        public boolean onPreferenceChange(Preference preference, Object value)
//        {
//            String stringValue = value.toString();
//
//            if (preference instanceof ListPreference)
//            {
//                // For list preferences, look up the correct display value in
//                // the preference's 'entries' list.
//                ListPreference listPreference = (ListPreference)preference;
//                int index = listPreference.findIndexOfValue(stringValue);
//
//                // Set the summary to reflect the new value.
//                preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
//
//            }
//            else if (preference instanceof RingtonePreference)
//            {
//                // For ringtone preferences, look up the correct display value
//                // using RingtoneManager.
//                if (TextUtils.isEmpty(stringValue))
//                {
//                    // Empty values correspond to 'silent' (no ringtone).
//                    preference.setSummary(R.string.pref_ringtone_silent);
//
//                }
//                else
//                {
//                    Ringtone ringtone = RingtoneManager.getRingtone(preference.getContext(), Uri.parse(stringValue));
//
//                    if (ringtone == null)
//                    {
//                        // Clear the summary if there was a lookup error.
//                        preference.setSummary(null);
//                    }
//                    else
//                    {
//                        // Set the summary to reflect the new ringtone display
//                        // name.
//                        String name = ringtone.getTitle(preference.getContext());
//                        preference.setSummary(name);
//                    }
//                }
//
//            }
//            else
//            {
//                // For all other preferences, set the summary to the value's
//                // simple string representation.
//                preference.setSummary(stringValue);
//            }
//            return true;
//        }
//    };
//
//    /**
//     * Binds a preference's summary to its value. More specifically, when the
//     * preference's value is changed, its summary (line of text below the
//     * preference title) is updated to reflect the value. The summary is also
//     * immediately updated upon calling this method. The exact display format is
//     * dependent on the type of preference.
//     *
//     * @see #sBindPreferenceSummaryToValueListener
//     */
//    private static void bindPreferenceSummaryToValue(@NotNull Preference preference)
//    {
//        // Set the listener to watch for value changes.
//        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
//
//        // Trigger the listener immediately with the preference's
//        // current value.
//        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences(preference.getContext())
//                                                                                              .getString(preference.getKey(), ""));
//    }
//
//    /** {@inheritDoc} */
//    @Override
//    protected void onCreate(Bundle savedInstanceState)
//    {
//        super.onCreate(savedInstanceState);
//        setupActionBar();
//    }
//
//    /**
//     * Set up the {@link android.app.ActionBar}, if the API is available.
//     */
//    private void setupActionBar()
//    {
//        ToolbarFragment toolbarFragment = new ToolbarFragment();
//        Toolbar toolbar = (Toolbar)toolbarFragment.getView();
//        setSupportActionBar(toolbar);
//
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null)
//        {
//            // Show the Up button in the action bar.
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }
//    }
//
//    /** {@inheritDoc} */
//    @Override
//    public boolean onMenuItemSelected(int featureId, MenuItem item)
//    {
//        boolean result = true;
//
//        switch (item.getItemId())
//        {
//            case android.R.id.home:
//                if (!super.onMenuItemSelected(featureId, item))
//                    NavUtils.navigateUpFromSameTask(this);
//
//                break;
//            default:
//                result = super.onMenuItemSelected(featureId, item);
//                break;
//        }
//
//        return result;
//    }
//
//    /** {@inheritDoc} */
//    @Override
//    public boolean onIsMultiPane()
//    {
//        return isXLargeTablet(this);
//    }
//
//    /**
//     * Helper method to determine if the device has an extra-large screen. For
//     * example, 10" tablets are extra-large.
//     */
//    private static boolean isXLargeTablet(@NotNull Context context)
//    {
//        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
//    }
//
//    /** {@inheritDoc} */
//    @Override
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    public void onBuildHeaders(List<Header> target)
//    {
//        loadHeadersFromResource(R.xml.pref_headers, target);
//    }
//
//    /**
//     * This method stops fragment injection in malicious applications.
//     * Make sure to deny any unknown fragments here.
//     */
//    protected boolean isValidFragment(String fragmentName)
//    {
//        return PreferenceFragment.class.getName().equals(fragmentName) ||
//               VivaceSettingsPreferenceFragment.class.getName().equals(fragmentName);
//        //               GeneralPreferenceFragment.class.getName().equals(fragmentName) ||
//        //               DataSyncPreferenceFragment.class.getName().equals(fragmentName) ||
//        //               NotificationPreferenceFragment.class.getName().equals(fragmentName);
//    }
//
//    /**
//     * This fragment shows Vivace's general preferences only. It is used when the
//     * activity is showing a two-pane settings UI.
//     */
//    @TargetApi(Build.VERSION_CODES.N)
//    public static class VivaceSettingsPreferenceFragment extends PreferenceFragment
//    {
//        /**
//         * Called to do initial creation of a fragment.  This is called after
//         * {@link #onAttach(Activity)} and before
//         * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}, but is not called if the fragment
//         * instance is retained across Activity re-creation (see {@link #setRetainInstance(boolean)}).
//         *
//         * <p>Note that this can be called while the fragment's activity is
//         * still in the process of being created.  As such, you can not rely
//         * on things like the activity's content view hierarchy being initialized
//         * at this point.  If you want to do work once the activity itself is
//         * created, see {@link #onActivityCreated(Bundle)}.
//         *
//         * <p>If your app's <code>targetSdkVersion</code> is {@link android.os.Build.VERSION_CODES#M}
//         * or lower, child fragments being restored from the savedInstanceState are restored after
//         * <code>onCreate</code> returns. When targeting {@link android.os.Build.VERSION_CODES#N} or
//         * above and running on an N or newer platform version
//         * they are restored by <code>Fragment.onCreate</code>.</p>
//         *
//         * @param savedInstanceState If the fragment is being re-created from
//         * a previous saved state, this is the state.
//         */
//        @Override
//        public void onCreate(Bundle savedInstanceState)
//        {
//            super.onCreate(savedInstanceState);
//            addPreferencesFromResource(R.xml.pref_settings);
//            setHasOptionsMenu(true);
//
//            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
//            // to their values. When their values change, their summaries are
//            // updated to reflect the new value, per the Android Design
//            // guidelines.
//            bindPreferenceSummaryToValue(findPreference("file_storage_name"));
//            bindPreferenceSummaryToValue(findPreference("file_storage_type"));
//        }
//
//        /** {@inheritDoc} */
//        @Override
//        public boolean onOptionsItemSelected(MenuItem item)
//        {
//            boolean result = true;
//
//            switch (item.getItemId())
//            {
//                case android.R.id.home:
//                    startActivity(new Intent(getActivity(), SettingsActivity.class));
//                    break;
//                default:
//                    result = super.onOptionsItemSelected(item);
//                    break;
//            }
//
//            return result;
//        }
//    }
//
//    /**
//     * This fragment shows general preferences only. It is used when the
//     * activity is showing a two-pane settings UI.
//     */
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    public static class GeneralPreferenceFragment extends PreferenceFragment
//    {
//        /** {@inheritDoc} */
//        @Override
//        public void onCreate(Bundle savedInstanceState)
//        {
//            super.onCreate(savedInstanceState);
//            addPreferencesFromResource(R.xml.pref_general);
//            setHasOptionsMenu(true);
//
//            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
//            // to their values. When their values change, their summaries are
//            // updated to reflect the new value, per the Android Design
//            // guidelines.
//            bindPreferenceSummaryToValue(findPreference("example_text"));
//            bindPreferenceSummaryToValue(findPreference("example_list"));
//        }
//
//        /** {@inheritDoc} */
//        @Override
//        public boolean onOptionsItemSelected(MenuItem item)
//        {
//            int id = item.getItemId();
//            if (id == android.R.id.home)
//            {
//                startActivity(new Intent(getActivity(), SettingsActivity.class));
//                return true;
//            }
//            return super.onOptionsItemSelected(item);
//        }
//    }
//
//    /**
//     * This fragment shows notification preferences only. It is used when the
//     * activity is showing a two-pane settings UI.
//     */
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    public static class NotificationPreferenceFragment extends PreferenceFragment
//    {
//        /** {@inheritDoc} */
//        @Override
//        public void onCreate(Bundle savedInstanceState)
//        {
//            super.onCreate(savedInstanceState);
//            addPreferencesFromResource(R.xml.pref_notification);
//            setHasOptionsMenu(true);
//
//            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
//            // to their values. When their values change, their summaries are
//            // updated to reflect the new value, per the Android Design
//            // guidelines.
//            bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
//        }
//
//        /** {@inheritDoc} */
//        @Override
//        public boolean onOptionsItemSelected(MenuItem item)
//        {
//            int id = item.getItemId();
//            if (id == android.R.id.home)
//            {
//                startActivity(new Intent(getActivity(), SettingsActivity.class));
//                return true;
//            }
//            return super.onOptionsItemSelected(item);
//        }
//    }
//
//    /**
//     * This fragment shows data and sync preferences only. It is used when the
//     * activity is showing a two-pane settings UI.
//     */
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    public static class DataSyncPreferenceFragment extends PreferenceFragment
//    {
//        /** {@inheritDoc} */
//        @Override
//        public void onCreate(Bundle savedInstanceState)
//        {
//            super.onCreate(savedInstanceState);
//            addPreferencesFromResource(R.xml.pref_data_sync);
//            setHasOptionsMenu(true);
//
//            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
//            // to their values. When their values change, their summaries are
//            // updated to reflect the new value, per the Android Design
//            // guidelines.
//            bindPreferenceSummaryToValue(findPreference("sync_frequency"));
//        }
//
//        /** {@inheritDoc} */
//        @Override
//        public boolean onOptionsItemSelected(MenuItem item)
//        {
//            int id = item.getItemId();
//            if (id == android.R.id.home)
//            {
//                startActivity(new Intent(getActivity(), SettingsActivity.class));
//                return true;
//            }
//            return super.onOptionsItemSelected(item);
//        }
//    }
//}
package cole.matthew.vivace.Activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import cole.matthew.vivace.Exceptions.InsufficientStorageException;
import cole.matthew.vivace.Exceptions.StorageNotReadableException;
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
public class SettingsActivity extends BaseVivaceActivity {
    public static final String KEY_FILE_STORAGE_NAME = "file_storage_name";
    public static final String KEY_FILE_STORAGE_TYPE = "file_storage_type";
    public static final String KEY_FILE_STORAGE_LOCATION = "file_storage_location";

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
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
            }
            else if (!(preference instanceof SwitchPreference)) {
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
            catch (StorageNotReadableException e) {
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
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(@NotNull Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences(preference.getContext())
                                                                                              .getString(preference.getKey(), ""));
    }

    /** {@inheritDoc} */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        setupActionBar();
    }

    /** {@inheritDoc} */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result = true;

        switch (item.getItemId()) {
            case android.R.id.home:
                if (!super.onOptionsItemSelected(item))
                    NavUtils.navigateUpFromSameTask(this);

                break;
            default:
                result = super.onOptionsItemSelected(item);
                break;
        }

        return result;
    }

    //    /** {@inheritDoc} */
    //    @Override
    //    public boolean onMenuItemSelected(int featureId, MenuItem item)
    //    {
    //        boolean result = true;
    //
    //        switch (item.getItemId())
    //        {
    //            case android.R.id.home:
    //                if (!super.onMenuItemSelected(featureId, item))
    //                    NavUtils.navigateUpFromSameTask(this);
    //
    //                break;
    //            default:
    //                result = super.onMenuItemSelected(featureId, item);
    //                break;
    //        }
    //
    //        return result;
    //    }
    //
    //    /** {@inheritDoc} */
    //    @Override
    //    public boolean onIsMultiPane()
    //    {
    //        return isXLargeTablet(this);
    //    }
    //
    //    /**
    //     * Helper method to determine if the device has an extra-large screen. For
    //     * example, 10" tablets are extra-large.
    //     */
    //    private static boolean isXLargeTablet(@NotNull Context context)
    //    {
    //        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    //    }
    //
    //    /** {@inheritDoc} */
    //    @Override
    //    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    //    public void onBuildHeaders(List<Header> target)
    //    {
    //        loadHeadersFromResource(R.xml.pref_headers, target);
    //    }
    //
    //    /**
    //     * This method stops fragment injection in malicious applications.
    //     * Make sure to deny any unknown fragments here.
    //     */
    //    protected boolean isValidFragment(String fragmentName)
    //    {
    //        return PreferenceFragment.class.getName().equals(fragmentName) ||
    //               VivaceSettingsPreferenceFragment.class.getName().equals(fragmentName);
    //        //               GeneralPreferenceFragment.class.getName().equals(fragmentName) ||
    //        //               DataSyncPreferenceFragment.class.getName().equals(fragmentName) ||
    //        //               NotificationPreferenceFragment.class.getName().equals(fragmentName);
    //    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    /**
     * This fragment shows Vivace's general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.N)
    public static class VivaceSettingsPreferenceFragment extends PreferenceFragment {
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
            findPreference(SettingsActivity.KEY_FILE_STORAGE_LOCATION).setOnPreferenceChangeListener(sBindSwitchPreferenceValueListener);
        }

        /** {@inheritDoc} */
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            boolean result = true;

            switch (item.getItemId()) {
                case android.R.id.home:
                    startActivity(new Intent(getActivity(), SettingsActivity.class));
                    break;
                default:
                    result = super.onOptionsItemSelected(item);
                    break;
            }

            return result;
        }
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        /** {@inheritDoc} */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("example_text"));
            bindPreferenceSummaryToValue(findPreference("example_list"));
        }

        /** {@inheritDoc} */
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            boolean result = true;

            switch (item.getItemId()) {
                case android.R.id.home:
                    startActivity(new Intent(getActivity(), SettingsActivity.class));
                    break;
                default:
                    result = super.onOptionsItemSelected(item);
                    break;
            }

            return result;
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
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
            addPreferencesFromResource(R.xml.pref_notification);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
        }

        /** {@inheritDoc} */
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            boolean result = true;

            switch (item.getItemId()) {
                case android.R.id.home:
                    startActivity(new Intent(getActivity(), SettingsActivity.class));
                    break;
                default:
                    result = super.onOptionsItemSelected(item);
                    break;
            }

            return result;
        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment {
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
            addPreferencesFromResource(R.xml.pref_data_sync);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("sync_frequency"));
        }

        /** {@inheritDoc} */
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            boolean result = true;

            switch (item.getItemId()) {
                case android.R.id.home:
                    startActivity(new Intent(getActivity(), SettingsActivity.class));
                    break;
                default:
                    result = super.onOptionsItemSelected(item);
                    break;
            }

            return result;
        }
    }
}