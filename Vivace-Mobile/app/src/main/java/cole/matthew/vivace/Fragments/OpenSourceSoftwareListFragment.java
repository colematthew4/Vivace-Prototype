package cole.matthew.vivace.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cole.matthew.vivace.Helpers.OpenSourceSoftwareRecyclerViewAdapter;
import cole.matthew.vivace.Models.OpenSourceSoftwareContent;
import cole.matthew.vivace.Models.OpenSourceSoftwareContent.OpenSourceSoftware;
import cole.matthew.vivace.R;

/**
 * A fragment representing a list of {@link OpenSourceSoftware}.
 */
public class OpenSourceSoftwareListFragment extends Fragment {
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OpenSourceSoftwareListFragment() { }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.oss_listlayout, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView)view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
            List<OpenSourceSoftware> ossList = new ArrayList<>();

            try {
                OpenSourceSoftwareContent content = new OpenSourceSoftwareContent(context);
                ossList = content.getOpenSourceSoftware();
            } catch (IOException | XmlPullParserException e) {
                // TODO: Implement logging and exception handling
                Log.e(this.getClass().getName(), e.getMessage());
            }

            recyclerView.setAdapter(new OpenSourceSoftwareRecyclerViewAdapter(ossList));
        }

        return view;
    }
}
