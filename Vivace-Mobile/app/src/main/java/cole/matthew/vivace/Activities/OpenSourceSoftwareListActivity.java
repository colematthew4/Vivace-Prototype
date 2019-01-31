package cole.matthew.vivace.Activities;

import android.os.Bundle;

import cole.matthew.vivace.R;

public class OpenSourceSoftwareListActivity extends BaseVivaceActivity {
    /** {@inheritDoc} */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opensourcesoftware_activity_layout);
        setupActionBar();
    }
}
