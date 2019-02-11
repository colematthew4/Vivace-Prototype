package cole.matthew.vivace.Helpers;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import cole.matthew.vivace.Models.OpenSourceSoftwareContent;
import cole.matthew.vivace.Models.OpenSourceSoftwareContent.OpenSourceSoftware;
import cole.matthew.vivace.R;

/**
 * {@link RecyclerView.Adapter} that can display a {@link OpenSourceSoftware}.
 */
public class OpenSourceSoftwareRecyclerViewAdapter extends RecyclerView.Adapter<OpenSourceSoftwareViewHolder> {
    private final List<OpenSourceSoftware> _oss;

    /**
     * Creates an instance of an {@link OpenSourceSoftwareRecyclerViewAdapter} with the given contents.
     * @param oss The {@link OpenSourceSoftware} to insert into the {@link RecyclerView}.
     */
    public OpenSourceSoftwareRecyclerViewAdapter(@NotNull List<OpenSourceSoftwareContent.OpenSourceSoftware> oss) {
        _oss = oss;
    }

    /** {@inheritDoc} */
    @NonNull
    @Override
    public OpenSourceSoftwareViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.oss_layout, parent, false);
        return new OpenSourceSoftwareViewHolder(view);
    }

    /** {@inheritDoc} */
    @Override
    public void onBindViewHolder(@NonNull final OpenSourceSoftwareViewHolder holder, int position) {
        holder.setSoftware(_oss.get(position));
    }

    /** {@inheritDoc} */
    @Override
    public int getItemCount() {
        return _oss.size();
    }
}
