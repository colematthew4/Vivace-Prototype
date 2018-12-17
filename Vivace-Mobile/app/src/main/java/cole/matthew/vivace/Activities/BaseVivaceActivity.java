package cole.matthew.vivace.Activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import cole.matthew.vivace.Fragments.ToolbarFragment;
import cole.matthew.vivace.R;

public abstract class BaseVivaceActivity extends AppCompatActivity implements ToolbarFragment.OnFragmentInteractionListener {
    /** Set up the {@link android.app.ActionBar}, if the API is available. */
    protected void setupActionBar() {
        ToolbarFragment toolbarFragment = (ToolbarFragment)getSupportFragmentManager().findFragmentById(R.id.toolbarFragment);
        Toolbar toolbar = (Toolbar)toolbarFragment.getView();
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
