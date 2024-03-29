package cole.matthew.vivace.Helpers;

import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.jetbrains.annotations.NotNull;

import cole.matthew.vivace.Models.OpenSourceSoftware;
import cole.matthew.vivace.R;

public class SmallOpenSourceSoftwareViewHolder extends OpenSourceSoftwareViewHolder {
    private final ImageView _dropdownImage;
    private final View _ossContentContainer;
    private AnimationState _animationState = AnimationState.COLLAPSED;
    private int _previousHeight = 0;
    private final RotateAnimation _openArrowAnim;
    private final RotateAnimation _closeArrowAnim;

    /**
     * Creates an instance of a view holder to show third party libraries.
     *
     * @param ossContainerView The top-level view of the third party library layout.
     */
    SmallOpenSourceSoftwareViewHolder(View ossContainerView) {
        super(ossContainerView);
        View ossBanner = ossContainerView.findViewById(R.id.oss_banner);
        ossBanner.setOnClickListener(this);

        _dropdownImage = ossContainerView.findViewById(R.id.dropdown_arrow);
        _ossContentContainer = ossContainerView.findViewById(R.id.oss_content);
        _openArrowAnim = (RotateAnimation)AnimationUtils.loadAnimation(ossContainerView.getContext(), R.anim.dropdown_open);
        _closeArrowAnim = (RotateAnimation)AnimationUtils.loadAnimation(ossContainerView.getContext(), R.anim.dropdown_close);

        addItemClickListener((view, position) -> {
            if (_animationState == AnimationState.EXPANDED) {
                collapse();
            } else if (_animationState == AnimationState.COLLAPSED) {
                expand();
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void setSoftware(@NotNull OpenSourceSoftware software) {
        _nameTextView.setText(software.getName());

        String htmlUrl = "<a href=\"" + software.getUrl() + "\">" + software.getUrl() + "</a>";
        _repoLinkTextView.setText(Html.fromHtml(htmlUrl, Html.FROM_HTML_MODE_COMPACT));

        String license = software.getLicense();
        _licenseView.loadData(license, "text/html", null);
    }

    /**
     * Expands to show the license and url of the selected third party libraries.
     */
    private void expand() {
        int initialHeight = _ossContentContainer.getHeight();
        if (_animationState == AnimationState.EXPANDING || _animationState == AnimationState.COLLAPSING) {
            _previousHeight = initialHeight;
        }

        _ossContentContainer.measure(LinearLayout.LayoutParams.MATCH_PARENT, 900);
        int targetHeight = _ossContentContainer.getMeasuredHeight();

        if (targetHeight - initialHeight != 0) {
            animateViews(initialHeight, targetHeight - initialHeight, AnimationState.EXPANDING);
        }
    }

    /**
     * Collapses the license and url of the third party library that's currently being shown.
     */
    private void collapse() {
        int initialHeight = _ossContentContainer.getMeasuredHeight();
        if (initialHeight - _previousHeight != 0) {
            animateViews(initialHeight, initialHeight - _previousHeight, AnimationState.COLLAPSING);
        }
    }

    /**
     * Applies and starts an expanding/collapsing animation given the {@code animationType}.
     *
     * @param initialHeight The initial height of the {@link View} to animate.
     * @param distance The final height of the {@link View} to animate.
     * @param animationType The type of animation to perform, either {@code COLLAPSING} or {@code EXPANDING}.
     */
    private void animateViews(int initialHeight, int distance, AnimationState animationType) {
        Animation expandAnimation = new Animation() {
            /** {@inheritDoc} */
            @Override
            public boolean willChangeBounds() {
                return true;
            }

            /** {@inheritDoc} */
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1f) {
                    // setting isExpanding/isCollapsing to false
                    _animationState = animationType == AnimationState.EXPANDING ? AnimationState.EXPANDED : AnimationState.COLLAPSED;
                }

                _ossContentContainer.getLayoutParams().height = (int)((animationType == AnimationState.EXPANDING) ? (initialHeight + distance * interpolatedTime) : (initialHeight - distance * interpolatedTime));
                _ossContentContainer.requestLayout();
            }
        };
        expandAnimation.setDuration(350);

        _animationState = animationType;
        _ossContentContainer.startAnimation(expandAnimation);

        if (_animationState == AnimationState.EXPANDING) {
            _dropdownImage.startAnimation(_openArrowAnim);
        } else if (_animationState == AnimationState.COLLAPSING) {
            _dropdownImage.startAnimation(_closeArrowAnim);
        }

        Log.d(this.getClass().getName(), "Started Animation: " + (_animationState == AnimationState.EXPANDING ? "Expanding " : "Collapsing ") + _nameTextView.getText());
        _animationState = _animationState == AnimationState.EXPANDING ? AnimationState.EXPANDED : AnimationState.COLLAPSED;
    }

    /**
     * Enum to describe the animation state of the expanding info container
     */
    private enum AnimationState {
        COLLAPSING,
        EXPANDING,
        EXPANDED,
        COLLAPSED
    }
}