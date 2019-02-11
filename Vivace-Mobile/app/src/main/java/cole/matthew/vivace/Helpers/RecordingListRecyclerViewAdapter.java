package cole.matthew.vivace.Helpers;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import cole.matthew.vivace.Exceptions.StorageNotReadableException;
import cole.matthew.vivace.Exceptions.StorageNotWritableException;
import cole.matthew.vivace.Fragments.RecordingListFragment.OnListFragmentInteractionListener;
import cole.matthew.vivace.Models.Recordings.IRecording;
import cole.matthew.vivace.R;

import static java.text.DateFormat.getDateTimeInstance;

/**
 * {@link RecyclerView.Adapter} that can display a {@link IRecording} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public final class RecordingListRecyclerViewAdapter extends RecyclerView.Adapter<RecordingListRecyclerViewAdapter.RecordingViewHolder> {
    private final List<IRecording> _recordings;
    private final FileStore _fileStore;
//    private final OnListFragmentInteractionListener _listener;

    public RecordingListRecyclerViewAdapter(Activity activity) {
        _fileStore = new FileStore(activity);
        _recordings = _fileStore.getRecordings();
    }

//    public RecordingListRecyclerViewAdapter(List<DummyItem> recordings, OnListFragmentInteractionListener listener) {
//        _recordings = recordings;
//        _listener = listener;
//    }

    /** {@inheritDoc} */
    @NonNull
    @Override
    public RecordingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_recording, parent, false);
        return new RecordingViewHolder(view);
    }

    /** {@inheritDoc} */
    @Override
    public void onBindViewHolder(@NonNull final RecordingViewHolder holder, int position) {
        holder.setRecording(_recordings.get(position));

        //        holder._view.setOnClickListener(v -> {
        //            if (null != _listener) {
        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        //                _listener.onListFragmentInteraction(holder._item);
        //            }
        //        });
    }

    /** {@inheritDoc} */
    @Override
    public int getItemCount() {
        return _recordings.size();
    }

    /**
     * Gets the recording at the specified position in the recording list.
     *
     * @param position The index of the recording.
     *
     * @return The recording at the specified position.
     *
     * @exception IndexOutOfBoundsException if the index is out of range (<tt>index &lt; 0 || index &gt; size()</tt>)
     */
    public IRecording getRecording(int position) {
        return _recordings.get(position);
    }

    /**
     * Removes the element at the specified position in the recording list. Returns the element that
     * was removed from the list.
     *
     * @param position the index of the element to be removed
     *
     * @return The element previously at the specified position.
     *
     * @exception UnsupportedOperationException if the <tt>remove</tt> operation is not supported by this list
     * @exception IndexOutOfBoundsException     if the index is out of range (<tt>index &lt; 0 || index &gt;= size()</tt>)
     */
    public IRecording removeRecording(int position) {
        // notify the item removed by position to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
        return _recordings.remove(position);
    }

    /**
     * Restores the recording that was removed from the file system.
     *
     * @param recording The recording to restore.
     * @param position  index at which the specified element is to be inserted.
     *
     * @exception IndexOutOfBoundsException if the index is out of range (<tt>index &lt; 0 || index &gt; size()</tt>)
     */
    public void restoreItem(IRecording recording, int position) {
        _recordings.add(position, recording);
        // notify recording added by position
        notifyItemInserted(position);
    }

    /**
     * Permanently deletes a recording from the filesystem.
     *
     * @param recording The recording to delete from the file system.
     */
    public void deleteRecording(IRecording recording)
            throws StorageNotReadableException, StorageNotWritableException
    {
        File recordingFile = new File(recording.getPath());
        _fileStore.delete(recordingFile);
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    public final class RecordingViewHolder extends RecyclerView.ViewHolder {
        private final Context _context;
        private IRecording _recording;
        private final TextView _recordingFileName;
        private final TextView _recordingLastModified;
        private final ImageView _recordingFileIcon;
        private RelativeLayout _foreground;

        /**
         * Instantiates a RecordingViewHolder with the given item view.
         *
         * @param view The item view to place in the RecyclerView.
         *
         * @exception IllegalArgumentException parameter {@code view} is {@code null}.
         * @exception NullPointerException     if the context attached to the item view is {@code null}.
         */
        RecordingViewHolder(@NonNull View view)
                throws NullPointerException
        {
            super(view);
            Objects.requireNonNull((_context = view.getContext()), "The context to attach to the item view must not be null.");
            _recordingFileName = view.findViewById(R.id.file_name);
            _recordingLastModified = view.findViewById(R.id.lastModified);
            _recordingFileIcon = view.findViewById(R.id.recording_file_icon);
            _foreground = view.findViewById(R.id.foreground);
        }

        /**
         * Gets the foreground of item view as a RelativeLayout.
         *
         * @return The foreground of the item view.
         */
        RelativeLayout getForeground() {
            return _foreground;
        }

        /**
         * Sets the recording stored in the item view. Changes the file icon based on the recording
         * type.
         *
         * @param recording The recording to attach to the item view.
         *
         * @exception NullPointerException if {@code recording} is {@code null}.
         */
        public void setRecording(@NonNull IRecording recording)
                throws NullPointerException
        {
            Objects.requireNonNull(recording, "The recording must not be null.");
            _recording = recording;
            int fileDrawable = R.drawable.ic_file_black_24dp;

            switch (_recording.getRecordingType()) {
                case MUSICXML:
                    fileDrawable = R.drawable.ic_file_xml;
                    break;
                case MIDI:
                    fileDrawable = R.drawable.ic_file_midi;
                    break;
                case WAV:
                    fileDrawable = R.drawable.ic_music_file_black_24dp;
                    break;
                case MP3:
                    fileDrawable = R.drawable.ic_music_file_black_24dp;
                    break;
                default:
                    break;
            }

            _recordingFileIcon.setImageDrawable(_context.getDrawable(fileDrawable));
            setRecordingFileName(recording.getName());
            setModifiedDate(recording.getLastModified());
        }

        /**
         * Sets the primary text of the item view to the name of the recording.
         *
         * @param name The name of the recording.
         */
        public void setRecordingFileName(String name) {
            _recordingFileName.setText(name);
        }

        /**
         * Sets the secondary text of the item view to the date the recording was last modified.
         *
         * @param lastModified The date the recording was last modified.
         */
        public void setModifiedDate(String lastModified) {
            _recordingLastModified.setText(lastModified);
        }

        /**
         * Sets the secondary text of the item view to the date the recording was last modified.
         *
         * @param lastModified The date the recording was last modified.
         */
        public void setModifiedDate(LocalDateTime lastModified) {
            _recordingLastModified.setText(getDateTimeInstance().format(lastModified));
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return super.toString() + "'" + _recordingLastModified.getText() + "'";
        }
    }
}
