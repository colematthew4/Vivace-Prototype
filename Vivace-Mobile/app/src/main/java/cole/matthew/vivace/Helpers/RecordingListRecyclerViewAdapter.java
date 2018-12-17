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

import java.util.List;

import cole.matthew.vivace.Fragments.RecordingListFragment.OnListFragmentInteractionListener;
import cole.matthew.vivace.Models.Recordings.IRecording;
import cole.matthew.vivace.R;
import cole.matthew.vivace.dummy.DummyContent.DummyItem;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class RecordingListRecyclerViewAdapter extends RecyclerView.Adapter<RecordingListRecyclerViewAdapter.RecordingViewHolder> {
    private final List<IRecording> _recordings;
//    private final OnListFragmentInteractionListener _listener;

    public RecordingListRecyclerViewAdapter(Activity activity) {
        final FileStore fileStore = new FileStore(activity);
        _recordings = fileStore.getRecordings();
    }

//    public RecordingListRecyclerViewAdapter(List<DummyItem> recordings, OnListFragmentInteractionListener listener) {
//        _recordings = recordings;
//        _listener = listener;
//    }

    @NonNull
    @Override
    public RecordingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_recording, parent, false);
        return new RecordingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecordingViewHolder holder, int position) {
        holder.setRecording(_recordings.get(position));
        holder.setRecordingFileName(_recordings.get(position).getName());
        holder.setModifiedDate(_recordings.get(position).getLastModified());

        //        holder._view.setOnClickListener(v -> {
        //            if (null != _listener) {
        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        //                _listener.onListFragmentInteraction(holder._item);
        //            }
        //        });
    }

    @Override
    public int getItemCount() {
        return _recordings.size();
    }

    public IRecording getRecording(int position) {
        return _recordings.get(position);
    }

    public void deleteRecording(int position) {
        _recordings.remove(position);
        // notify the item removed by position to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(IRecording item, int position) {
        _recordings.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }

    public class RecordingViewHolder extends RecyclerView.ViewHolder {
        private final Context _context;
        private IRecording _recording;
        private final TextView _recordingFileName;
        private final TextView _recordingLastModified;
        private final ImageView _recordingFileIcon;
        private RelativeLayout _foreground;

        RecordingViewHolder(View view) {
            super(view);
            _context = view.getContext();
            assert _context != null;
            _recordingFileName = view.findViewById(R.id.file_name);
            _recordingLastModified = view.findViewById(R.id.lastModified);
            _recordingFileIcon = view.findViewById(R.id.recording_file_icon);
            _foreground = view.findViewById(R.id.recyclerview_foreground);
        }

        RelativeLayout getForeground() {
            return _foreground;
        }

        public void setRecording(IRecording recording) {
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
        }

        public void setRecordingFileName(String name) {
            _recordingFileName.setText(name);
        }

        public void setModifiedDate(String lastModified) {
            _recordingLastModified.setText(lastModified);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + _recordingLastModified.getText() + "'";
        }
    }
}
