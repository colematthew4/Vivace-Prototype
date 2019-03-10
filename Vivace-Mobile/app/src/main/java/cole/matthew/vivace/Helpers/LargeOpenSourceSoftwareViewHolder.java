package cole.matthew.vivace.Helpers;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;

import org.jetbrains.annotations.NotNull;

import cole.matthew.vivace.Models.OpenSourceSoftwareContent.OpenSourceSoftware;
import cole.matthew.vivace.R;

public class LargeOpenSourceSoftwareViewHolder extends OpenSourceSoftwareViewHolder {
    /**
     * Creates an instance of a view holder to show third party libraries.
     *
     * @param ossContainerView The top-level view of the third party library layout.
     * @param detailsView A view that some adapters use for attaching content when items are selected.
     */
    LargeOpenSourceSoftwareViewHolder(@NotNull View ossContainerView, @NotNull View detailsView) {
        super(ossContainerView);
        _repoLinkTextView = detailsView.findViewById(R.id.repo_link);
        _repoLinkTextView.setMovementMethod(LinkMovementMethod.getInstance());

        _licenseView = detailsView.findViewById(R.id.license);
        _licenseView.getSettings().setLoadWithOverviewMode(true);
        _licenseView.getSettings().setUseWideViewPort(true);
        _licenseView.getSettings().setDisplayZoomControls(false);
        _licenseView.getSettings().setBuiltInZoomControls(true);

        itemView.setOnClickListener(this);
    }

    /** {@inheritDoc} */
    @Override
    public void setSoftware(@NotNull OpenSourceSoftware software) {
        _nameTextView.setText(software.getName());
        addItemClickListener((v, position) -> {
            String htmlUrl = "<a href=\"" + software.getUrl() + "\">" + software.getUrl() + "</a>";
            _repoLinkTextView.setText(Html.fromHtml(htmlUrl, Html.FROM_HTML_MODE_COMPACT));

            String license = software.getLicense();
            _licenseView.loadData(license, "text/html", null);
        });
    }
}