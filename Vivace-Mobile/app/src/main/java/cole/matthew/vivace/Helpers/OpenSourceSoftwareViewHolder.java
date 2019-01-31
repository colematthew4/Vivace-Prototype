package cole.matthew.vivace.Helpers;

import android.animation.ObjectAnimator;
import android.graphics.drawable.RotateDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import cole.matthew.vivace.Models.OpenSourceSoftwareContent;
import cole.matthew.vivace.R;

public class OpenSourceSoftwareViewHolder extends RecyclerView.ViewHolder {
    private OnExpandedListener _onExpandedListener;
    private final View _ossContainer;
    private final TextView _nameTextView;
    private final TextView _repoLinkTextView;
    private final LinearLayout _licenseView;
    private final LayoutInflater _licensePartInflater;
    private final View _ossContentContainer;
    private boolean _expanded = false;
    private boolean _expanding = false;
    private boolean _collapsing = false;
    private int _previousHeight = 0;

    /**
     * Creates an instance of a view holder to show third party libraries.
     * @param ossContainerView The top-level view of the third party library layout.
     */
    OpenSourceSoftwareViewHolder(View ossContainerView) {
        super(ossContainerView);
        _ossContainer = ossContainerView;
        _nameTextView = ossContainerView.findViewById(R.id.oss_name);
        _repoLinkTextView = ossContainerView.findViewById(R.id.repo_link);
        _repoLinkTextView.setMovementMethod(LinkMovementMethod.getInstance());
        _licenseView = ossContainerView.findViewById(R.id.license);
        _licensePartInflater = LayoutInflater.from(ossContainerView.getContext());
        _ossContentContainer = ossContainerView.findViewById(R.id.oss_content);
        _nameTextView.setOnClickListener(v -> {
            if (_expanded) {
                collapse();
            } else {
                expand();
            }
        });
    }

    /**
     * Sets the display information of the third party library.
     * @param software The third party library to show details of.
     */
    public void setSoftware(@NotNull OpenSourceSoftwareContent.OpenSourceSoftware software) {
        _nameTextView.setText(software.getName());
        String htmlUrl = "<a href=\"" + software.getUrl() + "\">" + software.getUrl() + "</a>";
        _repoLinkTextView.setText(Html.fromHtml(htmlUrl, Html.FROM_HTML_MODE_COMPACT));
        for (String licenseText : software.getLicense().split("\n")) {
            TextView licensePart = (TextView)_licensePartInflater.inflate(R.layout.license_paragraph_layout, null);
            licensePart.setText(licenseText);
            _licenseView.addView(licensePart);
        }
    }

    /**
     * Expands to show the license and url of the selected third party libraries.
     */
    private void expand() {
        int initialHeight = _ossContentContainer.getHeight();
        if (_expanding || _collapsing) {
            _previousHeight = initialHeight;
        }

        _ossContentContainer.measure(LinearLayout.LayoutParams.MATCH_PARENT, 900);
        int targetHeight = _ossContentContainer.getMeasuredHeight();

        if (targetHeight - initialHeight != 0) {
            animateViews(initialHeight, targetHeight - initialHeight, AnimationState.EXPANDING);
        }
    }

    private void collapse() {
        int initialHeight = _ossContentContainer.getMeasuredHeight();
        if (initialHeight - _previousHeight != 0) {
            animateViews(initialHeight, initialHeight - _previousHeight, AnimationState.COLLAPSING);
        }
    }

    private void animateViews(int initialHeight, int distance, AnimationState animationType) {
        Animation expandAnimation = new Animation() {
            /**
             * <p>Indicates whether or not this animation will affect the bounds of the
             * animated view. For instance, a fade animation will not affect the bounds
             * whereas a 200% scale animation will.</p>
             *
             * @return true if this animation will change the view's bounds
             */
            @Override
            public boolean willChangeBounds() {
                return true;
            }

            /**
             * Helper for getTransformation. Subclasses should implement this to apply
             * their transforms given an interpolation value.  Implementations of this
             * method should always replace the specified Transformation or document
             * they are doing otherwise.
             *
             * @param interpolatedTime The value of the normalized time (0.0 to 1.0)
             *                         after it has been run through the interpolation function.
             * @param t                The Transformation object to fill in with the current
             */
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1f) {
                    // setting isExpanding/isCollapsing to false
                    _expanding = false;
                    _collapsing = false;

                    if (_onExpandedListener != null) {
                        if (animationType == AnimationState.EXPANDING) {
                            _onExpandedListener.onExpandChanged(_ossContainer, true);
                        } else {
                            _onExpandedListener.onExpandChanged(_ossContainer, false);
                        }
                    }
                }

                _ossContentContainer.getLayoutParams().height = (int)((animationType == AnimationState.EXPANDING) ? (initialHeight + distance * interpolatedTime) : (initialHeight - distance * interpolatedTime));
                _ossContentContainer.requestLayout();
            }
        };
        expandAnimation.setDuration(350);

        _expanding = animationType == AnimationState.EXPANDING;
        _collapsing = animationType == AnimationState.COLLAPSING;

        RotateDrawable arrowDrawable = (RotateDrawable)_nameTextView.getCompoundDrawables()[2];
        _ossContentContainer.startAnimation(expandAnimation);
        if (animationType == AnimationState.EXPANDING) {
            ObjectAnimator.ofInt(arrowDrawable, "level", 0, 10000).setDuration(350).start();
        } else if (animationType == AnimationState.COLLAPSING) {
            ObjectAnimator.ofInt(arrowDrawable, "level", 10000, 0).setDuration(350).start();
        }

        Log.d(this.getClass().getName(), "Started Animation: " + (_expanding ? "Expanding" : "Collapsing"));
        _expanded = animationType == AnimationState.EXPANDING;
    }

    /**
     * TODO: do something with this interface? Probably just remove it
     */
    public interface OnExpandedListener {
        void onExpandChanged(View view, boolean isExpanded);
    }

    /**
     * Enum to describe the animation state of the expanding info container
     */
    private enum AnimationState {
        COLLAPSING,
        EXPANDING
    }
}