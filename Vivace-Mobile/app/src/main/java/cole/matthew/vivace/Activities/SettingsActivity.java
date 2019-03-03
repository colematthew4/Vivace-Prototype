package cole.matthew.vivace.Activities;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.jetbrains.annotations.NotNull;

import cole.matthew.vivace.Fragments.ToolbarFragment;
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
public final class SettingsActivity extends BaseVivaceActivity {
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
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result = true;

        switch (item.getItemId()) {
            case android.R.id.home:
                if (!super.onOptionsItemSelected(item)) {
                    NavUtils.navigateUpFromSameTask(this);
                }

                break;
            default:
                result = super.onOptionsItemSelected(item);
                break;
        }

        return result;
    }

//    /** {@inheritDoc} */
//    @Override
//    public boolean onIsMultiPane() {
//        return isXLargeTablet(this);
//    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(@NotNull Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

//    /** {@inheritDoc} */
//    @Override
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    public void onBuildHeaders(List<Header> target) {
//        loadHeadersFromResource(R.xml.pref_headers, target);
//    }
//
//    /**
//     * This method stops fragment injection in malicious applications.
//     * Make sure to deny any unknown fragments here.
//     */
//    protected boolean isValidFragment(String fragmentName) {
//        return BaseVivacePreferenceFragment.class.getName().equals(fragmentName) || VivaceSettingsPreferenceFragment.class.getName().equals(fragmentName);
//    }
}