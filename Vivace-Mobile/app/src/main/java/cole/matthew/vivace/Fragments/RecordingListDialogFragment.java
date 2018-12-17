package cole.matthew.vivace.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import cole.matthew.vivace.Helpers.FileStore;
import cole.matthew.vivace.Models.Recordings.IRecording;
import cole.matthew.vivace.R;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     RecordingListDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 * <p>You activity (or fragment) needs to implement {@link RecordingListDialogFragment.RecordingListTouchListener}.</p>
 */
public class RecordingListDialogFragment extends BottomSheetDialogFragment {
    // TODO: Customize parameter argument names
    private static final String RECORDINGS_COUNT = "VIVACE_RECORDING_LIST_COUNT";
    //    private Listener mListener;

    private RecyclerView _recordingListRecyclerView;
    private RecyclerView.Adapter _recordingListAdapter;
    private RecyclerView.LayoutManager _recordingListLayoutManager;

    // TODO: Customize parameters
    public static RecordingListDialogFragment newInstance(Activity context) {
        final RecordingListDialogFragment fragment = new RecordingListDialogFragment();
        //        final Bundle args = new Bundle();
        //        final FileStore fileStore = new FileStore(context);
        //        args.putInt(RECORDINGS_COUNT, fileStore.getRecordingCount());
        //        fragment.setArguments(args);
        return fragment;
    }

    /** {@inheritDoc} */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recording_list_dialog, container, false);
    }

    /** {@inheritDoc} */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        assert getArguments() != null;

        _recordingListAdapter = new RecordingListAdapter();
        _recordingListLayoutManager = new LinearLayoutManager(getContext());
        //        _recordingListRecyclerView = (RecyclerView)view;
        //        _recordingListRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        //        _recordingListRecyclerView.addOnItemTouchListener(new RecordingListTouchListener(getContext(), _recordingListRecyclerView, new RecordingListTouchListener.RecordingListClickListener()
        //        {
        //            /** {@inheritDoc} */
        //            @Override
        //            public void onClick(View view, int position)
        //            {
        //                // TODO: Load the recording into sheet music, display it, and play the audio
        //            }
        //
        //            /** {@inheritDoc} */
        //            @Override
        //            public void onLongClick(View view, int position)
        //            { }
        //        }));
        //        _recordingListRecyclerView.setLayoutManager(_recordingListLayoutManager);
        //        _recordingListRecyclerView.setAdapter(_recordingListAdapter);
    }

    /** {@inheritDoc} */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //        final Fragment parent = getParentFragment();
        //        if (parent != null)
        //            mListener = (Listener)parent;
        //        else
        //            mListener = (Listener)context;
    }

    /** {@inheritDoc} */
    @Override
    public void onDetach() {
        //        mListener = null;
        super.onDetach();
    }

    //    public interface Listener
    //    {
    //        void onRecordingClicked(int position);
    //    }

    /**
     *
     */
    private class RecordingViewHolder extends RecyclerView.ViewHolder {
        private final TextView _recordingTextView;

        // TODO: Add Javadoc
        RecordingViewHolder(@NotNull LayoutInflater inflater, ViewGroup parent) {
            // TODO: Customize the item layout
            super(inflater.inflate(R.layout.fragment_recording_list_dialog_item, parent, false));
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

        RecordingViewHolder(TextView textView) {
            super(textView);
            _recordingTextView = textView;
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
            TextView textView = (TextView)LayoutInflater.from(parent.getContext())
                                                        .inflate(R.layout.fragment_recording_list_dialog_item, parent, false);
            return new RecordingViewHolder(textView);
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
        private RecordingListClickListener _clickListener;

        /**
         * Creates an instance of this listener.
         *
         * @param context       The application's context
         * @param recyclerView  The {@link RecyclerView} to getRecording the gesture information from.
         * @param clickListener The listener to invoke the event functionality
         */
        RecordingListTouchListener(Context context, final RecyclerView recyclerView, final RecordingListClickListener clickListener) {
            _clickListener = clickListener;
            _gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                /** {@inheritDoc} */
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                //                /** {@inheritDoc} */
                //                @Override
                //                public void onLongPress(MotionEvent e)
                //                {
                //                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                //                    if (child != null && _clickListener != null)
                //                        _clickListener.onLongClick(child, recyclerView.getChildLayoutPosition(child));
                //                }
            });
        }

        /** {@inheritDoc} */
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && _clickListener != null && _gestureDetector.onTouchEvent(e))
                _clickListener.onClick(child, rv.getChildLayoutPosition(child));

            return false;
        }

        /** {@inheritDoc} */
        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        /** {@inheritDoc} */
        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }

        public interface RecordingListClickListener {
            void onClick(View view, int position);

            void onLongClick(View view, int position);
        }
    }
}
