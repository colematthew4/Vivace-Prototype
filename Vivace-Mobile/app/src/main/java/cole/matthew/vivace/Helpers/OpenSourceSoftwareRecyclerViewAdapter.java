package cole.matthew.vivace.Helpers;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import cole.matthew.vivace.Models.OpenSourceSoftwareContent.OpenSourceSoftware;
import cole.matthew.vivace.R;

/**
 * {@link RecyclerView.Adapter} that can display a {@link OpenSourceSoftware}.
 */
public class OpenSourceSoftwareRecyclerViewAdapter extends RecyclerView.Adapter<OpenSourceSoftwareViewHolder> {
    private final OpenSourceSoftwareViewHolderFactory _adapterFactory = new OpenSourceSoftwareViewHolderFactory();
    private final List<OpenSourceSoftware> _oss;
    private View _detailsView;
    private int _selectedItem = -1;

    /**
     * Creates an instance of an {@link OpenSourceSoftwareRecyclerViewAdapter} with the given contents.
     *
     * @param oss The {@link OpenSourceSoftware} to insert into the {@link RecyclerView}.
     */
    public OpenSourceSoftwareRecyclerViewAdapter(@NotNull List<OpenSourceSoftware> oss) {
        this(oss, null);
    }

    /**
     * Creates an instance of an {@link OpenSourceSoftwareRecyclerViewAdapter} with the given contents.
     *
     * @param oss The {@link OpenSourceSoftware} to insert into the {@link RecyclerView}.
     * @param detailsView A view that some adapters use for attaching content when items are selected. Can be {@code null}.
     */
    public OpenSourceSoftwareRecyclerViewAdapter(@NotNull List<OpenSourceSoftware> oss, View detailsView) {
        _oss = oss;
        _detailsView = detailsView;
    }

    /**
     * Gets the position of the selected item in the adapter.
     *
     * @return The position of the selected view.
     */
    public int getSelectedPosition() {
        return _selectedItem;
    }

    /**
     * Sets the position of the selected item in the adapter.
     *
     * @param selectedPosition The position of the selected view.
     */
    public void setSelectedPosition(int selectedPosition) {
        _selectedItem = selectedPosition;
    }

    /** {@inheritDoc} */
    @NonNull
    @Override
    public OpenSourceSoftwareViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        OpenSourceSoftwareViewHolder holder = _detailsView == null ?
                                              _adapterFactory.createOpenSourceSoftwareViewHolder(parent, R.layout.oss_layout) :
                                              _adapterFactory.createOpenSourceSoftwareViewHolder(parent, R.layout.oss_title, _detailsView);
        holder.addItemClickListener((v, position) -> _selectedItem = position);
        return holder;
    }

    /** {@inheritDoc} */
    @Override
    public void onBindViewHolder(@NonNull final OpenSourceSoftwareViewHolder holder, int position) {
        holder.setSoftware(_oss.get(position));
        holder.itemView.setSelected(_selectedItem == position);
        if (_selectedItem == position) {
            holder.onClick(holder.itemView);
        }
    }

    /** {@inheritDoc} */
    @Override
    public int getItemCount() {
        return _oss.size();
    }
}
