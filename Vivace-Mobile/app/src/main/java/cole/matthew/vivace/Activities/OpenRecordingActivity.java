package cole.matthew.vivace.Activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import cole.matthew.vivace.Fragments.RecordingListFragment;
import cole.matthew.vivace.R;
import cole.matthew.vivace.dummy.DummyContent;

public class OpenRecordingActivity extends BaseVivaceActivity implements RecordingListFragment.OnListFragmentInteractionListener {
    /** {@inheritDoc} */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_recording);
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

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
    }
}
