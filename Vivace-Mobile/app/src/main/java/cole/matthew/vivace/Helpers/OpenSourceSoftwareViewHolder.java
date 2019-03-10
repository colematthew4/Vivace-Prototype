package cole.matthew.vivace.Helpers;

import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import cole.matthew.vivace.Models.OpenSourceSoftwareContent.OpenSourceSoftware;
import cole.matthew.vivace.R;

public abstract class OpenSourceSoftwareViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView _nameTextView;
    TextView _repoLinkTextView;
    WebView _licenseView;
    private List<ItemClickListener> _itemClickListeners = new ArrayList<>();

    /**
     * Creates an instance of a view holder to show third party libraries.
     *
     * @param ossContainerView The top-level view of the third party library layout.
     */
    OpenSourceSoftwareViewHolder(View ossContainerView) {
        super(ossContainerView);
        _nameTextView = ossContainerView.findViewById(R.id.oss_name);
        if ((_repoLinkTextView = ossContainerView.findViewById(R.id.repo_link)) != null) {
            _repoLinkTextView.setMovementMethod(LinkMovementMethod.getInstance());
        }

        if ((_licenseView = ossContainerView.findViewById(R.id.license)) != null) {
            _licenseView.getSettings().setLoadWithOverviewMode(true);
            _licenseView.getSettings().setUseWideViewPort(true);
            _licenseView.getSettings().setDisplayZoomControls(false);
            _licenseView.getSettings().setBuiltInZoomControls(true);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onClick(View v) {
        for (ItemClickListener listener : _itemClickListeners) {
            listener.onItemClick(v, getAdapterPosition());
        }
    }

    /**
     * Adds the listener that is invoked when the {@link OpenSourceSoftwareViewHolder} is clicked. You should use this instead of
     * {@link View#setOnClickListener(View.OnClickListener) itemView.setOnClickListener}.
     *
     * @param itemClickListener The listener to invoke.
     */
    public void addItemClickListener(ItemClickListener itemClickListener) {
        _itemClickListeners.add(itemClickListener);
    }

    /**
     * Sets the display information of the third party library.
     *
     * @param software The third party library to show details of.
     */
    public abstract void setSoftware(@NotNull OpenSourceSoftware software);

    interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}