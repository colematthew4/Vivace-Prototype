package cole.matthew.vivace.Helpers;

import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

final class OpenSourceSoftwareViewHolderFactory {
    OpenSourceSoftwareViewHolder createOpenSourceSoftwareViewHolder(@NotNull ViewGroup parent, @LayoutRes int layout) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new SmallOpenSourceSoftwareViewHolder(view);
    }

    OpenSourceSoftwareViewHolder createOpenSourceSoftwareViewHolder(@NotNull ViewGroup parent, @LayoutRes int layout, View detailsView) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new LargeOpenSourceSoftwareViewHolder(view, detailsView);
    }
}
