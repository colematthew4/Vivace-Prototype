package cole.matthew.vivace.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import cole.matthew.vivace.Helpers.FileStore;
import cole.matthew.vivace.Models.Recordings.IRecording;
import cole.matthew.vivace.R;

/**
 * A fragment representing a list of Items.
 * <p />
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class RecordingBottomSheetFragment extends Fragment {
    private static final String RECORINGS_COUNT = "VIVACE_RECORDING_LIST_COUNT";
    private static final String RECORDING_BOTTOM_SHEET_FRAGMENT_TAG = "VIVACE_RECORDING_BOTTOM_SHEET_FRAGMENT";
    private RecyclerView _recordingListRecyclerView;
    private RecyclerView.Adapter _recordingListAdapter;
    private RecyclerView.LayoutManager _recordingListLayoutManager;
    private LinearLayout _recordingListLayout;
    //    private OnRecordingListFragmentInteractionListener mListener;
    private BottomSheetBehavior _recordingListBottomSheetBehavior;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecordingBottomSheetFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static RecordingBottomSheetFragment newInstance(int columnCount) {
        RecordingBottomSheetFragment fragment = new RecordingBottomSheetFragment();
        //        Bundle args = new Bundle();
        //        args.putInt(ARG_COLUMN_COUNT, columnCount);
        //        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //        if (context instanceof OnRecordingListFragmentInteractionListener)
        //        {
        //            mListener = (OnRecordingListFragmentInteractionListener)context;
        //        }
        //        else
        //        {
        //            throw new RuntimeException(
        //                    context.toString() + " must implement OnRecordingListFragmentInteractionListener");
        //        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        //        if (getArguments() != null)
        //        {
        //            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        //        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recording_list, container, false);
        _recordingListLayout = view.findViewById(R.id.recordingListLayout);
        _recordingListLayout.setOnClickListener(new View.OnClickListener() {
            /** {@inheritDoc} */
            @Override
            public void onClick(View v) {
                Log.d(RECORDING_BOTTOM_SHEET_FRAGMENT_TAG, "RecordingListLayout onClick");
                toggleState();
            }
        });

        _recordingListBottomSheetBehavior = BottomSheetBehavior.from(_recordingListLayout);
        _recordingListBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            /** {@inheritDoc} */
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        //                        Log.d(RECORDING_BOTTOM_SHEET_FRAGMENT_TAG, "Hidden");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        //                        Log.d(RECORDING_BOTTOM_SHEET_FRAGMENT_TAG, "Expanded");
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        //                        Log.d(RECORDING_BOTTOM_SHEET_FRAGMENT_TAG, "Collapsed");
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        //                        Log.d(RECORDING_BOTTOM_SHEET_FRAGMENT_TAG, "Dragging");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        //                        Log.d(RECORDING_BOTTOM_SHEET_FRAGMENT_TAG, "Settling");
                        break;
                    default:
                        //                        Log.d(RECORDING_BOTTOM_SHEET_FRAGMENT_TAG, String.valueOf(_recordingListBottomSheetBehavior.getState()));
                        break;
                }
            }

            /** {@inheritDoc} */
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                //                Log.d(RECORDING_BOTTOM_SHEET_FRAGMENT_TAG, "onSlide");
            }
        });

        _recordingListAdapter = new RecordingListAdapter();
        _recordingListLayoutManager = new LinearLayoutManager(getContext());
        _recordingListRecyclerView = view.findViewById(R.id.recordingList);
        _recordingListRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        _recordingListRecyclerView.addOnItemTouchListener(
                new RecordingListDialogFragment.RecordingListTouchListener(getContext(), _recordingListRecyclerView,
                                                                           new RecordingListDialogFragment.RecordingListTouchListener.RecordingListClickListener() {
                                                                               /** {@inheritDoc} */
                                                                               @Override
                                                                               public void onClick(View view, int position) {
                                                                                   // TODO: Load the recording into sheet music, display it, and play the audio
                                                                                   Log.d(RECORDING_BOTTOM_SHEET_FRAGMENT_TAG,
                                                                                         view.toString() + ": onClick");
                                                                               }

                                                                               /** {@inheritDoc */
                                                                               @Override
                                                                               public void onLongClick(View view, int position) {
                                                                                   Log.d(RECORDING_BOTTOM_SHEET_FRAGMENT_TAG, "onLongClick");
                                                                               }
                                                                           }));
        _recordingListRecyclerView.setLayoutManager(_recordingListLayoutManager);
        _recordingListRecyclerView.setAdapter(_recordingListAdapter);

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //        mListener = null;
    }

    /**
     * Toggles the state of the bottom sheet
     */
    private void toggleState() {
        if (_recordingListBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            _recordingListBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
        else {
            _recordingListBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
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
        void onListFragmentInteraction(RecordingViewHolder item);
    }

    /**
     *
     */
    private class RecordingViewHolder extends RecyclerView.ViewHolder {
        private final TextView _recordingTextView;

        // TODO: Add Javadoc
        RecordingViewHolder(@NotNull LayoutInflater inflater, ViewGroup parent) {
            // TODO: Customize the item layout
            super(inflater.inflate(R.layout.fragment_recording, parent, false));
            _recordingTextView = itemView.findViewById(R.id.text);
            //            _recordingTextView.setOnClickListener(new View.OnClickListener()
            //            {
            //                @Override
            //                public void onClick(View v)
            //                {
            //                    if (mListener != null)
            //                    {
            //                        mListener.onRecordingClicked(getAdapterPosition());
            //                        dismiss();
            //                    }
            //                }
            //            });
        }

        RecordingViewHolder(View view) {
            super(view);
            _recordingTextView = view.findViewById(R.id.lastModified);
            ;
        }
    }

    /**
     *
     */
    private class RecordingListAdapter extends RecyclerView.Adapter<RecordingViewHolder> {
        private final int _itemCount;
        private final List<IRecording> _recordings;

        /** Creates an instance of the adapter for viewing saved recordings. */
        RecordingListAdapter() {
            final FileStore fileStore = new FileStore((Activity)getContext());
            _recordings = fileStore.getRecordings();
            _itemCount = _recordings.size();
        }

        /**
         * Creates an instance of the adapter for viewing saved recordings.
         *
         * @param activity The activity to getRecording the saved recordings for
         */
        RecordingListAdapter(Activity activity) {
            final FileStore fileStore = new FileStore(activity);
            _recordings = fileStore.getRecordings();
            _itemCount = _recordings.size();
        }

        /**
         * Creates an instance of the adapter for viewing saved recordings.
         *
         * @param context The context to getRecording the saved recordings for
         */
        RecordingListAdapter(Context context) {
            this((Activity)context);
        }

        /** {@inheritDoc} */
        @NonNull
        @Override
        public RecordingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            FrameLayout fragmentRecording = (FrameLayout)LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_recording, parent, false);
            return new RecordingViewHolder(fragmentRecording);
        }

        /** {@inheritDoc} */
        @Override
        public void onBindViewHolder(@NonNull RecordingViewHolder holder, int position) {
            holder._recordingTextView.setText(_recordings.get(position).getName());
        }

        /** {@inheritDoc} */
        @Override
        public int getItemCount() {
            return _itemCount;
        }
    }

    public static class RecordingListTouchListener implements RecyclerView.OnItemTouchListener {
        private GestureDetector _gestureDetector;
        private RecordingListDialogFragment.RecordingListTouchListener.RecordingListClickListener _clickListener;

        /**
         * Creates an instance of this listener.
         *
         * @param context       The application's context
         * @param recyclerView  The {@link RecyclerView} to getRecording the gesture information from.
         * @param clickListener The listener to invoke the event functionality
         */
        RecordingListTouchListener(Context context, final RecyclerView recyclerView,
                                   final RecordingListDialogFragment.RecordingListTouchListener.RecordingListClickListener clickListener) {
            _clickListener = clickListener;
            _gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                /** {@inheritDoc} */
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    Log.d(RECORDING_BOTTOM_SHEET_FRAGMENT_TAG, "onSingleTapUp");
                    return true;
                }

                /** {@inheritDoc} */
                @Override
                public void onLongPress(MotionEvent e) {
                    //                    Log.d(RECORDING_BOTTOM_SHEET_FRAGMENT_TAG, "onLongPress");
                    Log.d(RECORDING_BOTTOM_SHEET_FRAGMENT_TAG, "onLongPress: " + e.toString());
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && _clickListener != null)
                        _clickListener.onLongClick(child, recyclerView.getChildLayoutPosition(child));
                }
            });
        }

        /** {@inheritDoc} */
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            Log.d(RECORDING_BOTTOM_SHEET_FRAGMENT_TAG, "onInterceptTouchEvent");
            //            View child = rv.findChildViewUnder(e.getX(), e.getY());
            //            if (child != null && _clickListener != null && _gestureDetector.onTouchEvent(e))
            //                _clickListener.onClick(child, rv.getChildLayoutPosition(child));
            return true;
        }

        /** {@inheritDoc} */
        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            Log.d(RECORDING_BOTTOM_SHEET_FRAGMENT_TAG, e.toString() + ": onTouchEvent");
        }

        /** {@inheritDoc} */
        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            Log.d(RECORDING_BOTTOM_SHEET_FRAGMENT_TAG, "onRequestDisallowInterceptTouchEvent");
        }

        public interface RecordingListClickListener {
            void onClick(View view, int position);

            void onLongClick(View view, int position);
        }
    }
}
