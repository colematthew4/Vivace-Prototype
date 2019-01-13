package cole.matthew.vivace.Fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cole.matthew.vivace.Exceptions.StorageNotReadableException;
import cole.matthew.vivace.Exceptions.StorageNotWritableException;
import cole.matthew.vivace.Helpers.RecordingListRecyclerViewAdapter;
import cole.matthew.vivace.Helpers.RecyclerViewItemTouchHelper;
import cole.matthew.vivace.Models.Recordings.IRecording;
import cole.matthew.vivace.R;

/**
 * A fragment representing a list of Items.
 * <p />
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class RecordingListFragment extends Fragment implements RecyclerViewItemTouchHelper.RecyclerViewItemTouchHelperListener {
    // TODO: parameters
    private OnListFragmentInteractionListener _listener;
    private Context _context;
    private RecordingListRecyclerViewAdapter _adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecordingListFragment() {
    }

    /** {@inheritDoc} */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            _listener = (OnListFragmentInteractionListener)context;
            _context = context;
            _adapter = new RecordingListRecyclerViewAdapter((Activity)context);
        }
        else {
            throw new RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recordinglist, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView)view;
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.addItemDecoration(new DividerItemDecoration(_context, DividerItemDecoration.VERTICAL));
            recyclerView.setAdapter(_adapter);

            ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerViewItemTouchHelper(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, this);
            new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        }

        return view;
    }

    /** {@inheritDoc} */
    @Override
    public void onDetach() {
        super.onDetach();
        _listener = null;
    }

    /** {@inheritDoc} */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        final int deletedIndex = viewHolder.getAdapterPosition();
        // get the removed item name to display it in snack bar
        String name = _adapter.getRecording(deletedIndex).getName();

        // remove the item from recycler view
        final IRecording deletedRecording = _adapter.removeRecording(deletedIndex);

        // showing snack bar with undo option
        Snackbar snackbar = Snackbar.make(viewHolder.itemView, name + " removed from cart!", Snackbar.LENGTH_LONG);
        snackbar.addCallback(new Snackbar.Callback() {
            /** {@inheritDoc} */
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                try {
                    _adapter.deleteRecording(deletedRecording);
                }
                catch (StorageNotReadableException | StorageNotWritableException e) {
                    Log.e(getString(R.string.application_tag), e.getMessage());
                }
            }
        });
        snackbar.setAction("UNDO", view -> _adapter.restoreItem(deletedRecording, deletedIndex));
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(IRecording recording);
    }
}
