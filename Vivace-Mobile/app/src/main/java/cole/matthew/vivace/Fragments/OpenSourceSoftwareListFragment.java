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
    private RecyclerView _recyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
     */
    public OpenSourceSoftwareListFragment() { }

    /** {@inheritDoc} */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.oss_listlayout, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            _recyclerView = (RecyclerView)view;
            setupRecyclerView(_recyclerView);
        } else {
            _recyclerView = view.findViewById(R.id.oss_list);
            if (_recyclerView == null) {
                throw new UnsupportedOperationException("Your content must have a RecyclerView whose id attribute is 'R.id.oss_list'");
            }

            setupRecyclerView(_recyclerView, view.findViewById(R.id.oss_content));
        }

        return view;
    }

    /**
     * Sets up the {@link RecyclerView} with a common set of properties and attaches the {@link android.support.v7.widget.RecyclerView.Adapter}.

     * @param recyclerView The {@link RecyclerView} to set up.
     */
    private void setupRecyclerView(RecyclerView recyclerView) {
        Context context = recyclerView.getContext();
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

    /**
     * Sets up the {@link RecyclerView} with a common set of properties and attaches the {@link android.support.v7.widget.RecyclerView.Adapter}.

     * @param recyclerView The {@link RecyclerView} to set up.
     * @param detailView A view that some adapters use for attaching content when items are selected. Can be {@code null}.
     */
    private void setupRecyclerView(RecyclerView recyclerView, View detailView) {
        Context context = recyclerView.getContext();
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

        recyclerView.setAdapter(new OpenSourceSoftwareRecyclerViewAdapter(ossList, detailView));
    }

    /** {@inheritDoc} */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("RecyclerViewScrollPosition", ((LinearLayoutManager)_recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition());
        outState.putInt("RecyclerViewSelectedPosition", ((OpenSourceSoftwareRecyclerViewAdapter)_recyclerView.getAdapter()).getSelectedPosition());
    }

    /** {@inheritDoc} */
    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            ((OpenSourceSoftwareRecyclerViewAdapter)_recyclerView.getAdapter()).setSelectedPosition(savedInstanceState.getInt("RecyclerViewSelectedPosition"));
            _recyclerView.getLayoutManager().scrollToPosition(savedInstanceState.getInt("RecyclerViewScrollPosition"));
        }
    }
}
