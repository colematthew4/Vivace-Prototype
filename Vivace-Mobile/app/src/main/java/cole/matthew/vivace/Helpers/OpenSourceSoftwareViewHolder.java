package cole.matthew.vivace.Helpers;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.RotateDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import cole.matthew.vivace.Models.OpenSourceSoftwareContent;
import cole.matthew.vivace.R;

public class OpenSourceSoftwareViewHolder extends RecyclerView.ViewHolder {
    private final TextView _nameTextView;
    private final TextView _repoLinkTextView;
    private final WebView _licenseView;
    private final LayoutInflater _licensePartInflater;
    private final View _ossContentContainer;
    private AnimationState _animationState = AnimationState.COLLAPSED;
    private int _previousHeight = 0;

    /**
     * Creates an instance of a view holder to show third party libraries.
     * @param ossContainerView The top-level view of the third party library layout.
     */
    OpenSourceSoftwareViewHolder(View ossContainerView) {
        super(ossContainerView);
        _nameTextView = ossContainerView.findViewById(R.id.oss_name);
        _repoLinkTextView = ossContainerView.findViewById(R.id.repo_link);
        _repoLinkTextView.setMovementMethod(LinkMovementMethod.getInstance());
        _licenseView = ossContainerView.findViewById(R.id.license);
        _licenseView.getSettings().setLoadWithOverviewMode(true);
        _licenseView.getSettings().setUseWideViewPort(true);
        _licensePartInflater = LayoutInflater.from(ossContainerView.getContext());
        _ossContentContainer = ossContainerView.findViewById(R.id.oss_content);
        _nameTextView.setOnClickListener(v -> {
            if (_animationState == AnimationState.EXPANDED) {
                collapse();
            } else if (_animationState == AnimationState.COLLAPSED) {
                expand();
            }
        });
    }

    private int getScale(View parent) {
        Display display = ((WindowManager)parent.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point points = new Point();
        display.getSize(points);
        Double value = (points.x / 50f) * 100d;
        return value.intValue();
    }

    /**
     * Sets the display information of the third party library.
     * @param software The third party library to show details of.
     */
    public void setSoftware(@NotNull OpenSourceSoftwareContent.OpenSourceSoftware software) {
        _nameTextView.setText(software.getName());

        String htmlUrl = "<a href=\"" + software.getUrl() + "\">" + software.getUrl() + "</a>";
        _repoLinkTextView.setText(Html.fromHtml(htmlUrl, Html.FROM_HTML_MODE_COMPACT));

        String license = software.getLicense();
//        for (int length = 0; length < license.length(); length += 9000) {
//            String licenseTextPart = length + 9000 < license.length() ?
//                                     license.substring(length, length + 9000) :
//                                     license.substring(length);
//            TextView licensePart = (TextView)_licensePartInflater.inflate(R.layout.license_paragraph_layout, null);
//            licensePart.setText(licenseTextPart);
//            _licenseView.addView(licensePart);
//        }
        _licenseView.loadData(license, "text/html", null);

//        ExpandableListView expandableListView = _container.findViewById(R.id.expandable);
//        List<Map<String, List<String>>> groupData = new ArrayList<>();
//        List<String> details = new ArrayList<>();
//        details.add(htmlUrl);
//        details.add(license);
//        Map<String, List<String>> data = new HashMap<>();
//        data.put(software.getName(), details);
//        groupData.add(data);
//        ExpandableListAdapter adapter = new BaseExpandableListAdapter() {
//            @Override
//            public int getGroupCount() {
//                return 1;
//            }
//
//            @Override
//            public int getChildrenCount(int groupPosition) {
//                return 2;
//            }
//
//            @Override
//            public Object getGroup(int groupPosition) {
//                return groupData.get(groupPosition);
//            }
//
//            @Override
//            public Object getChild(int groupPosition, int childPosition) {
//                return details.get(childPosition);
//            }
//
//            @Override
//            public long getGroupId(int groupPosition) {
//                return software.getName().length();
//            }
//
//            @Override
//            public long getChildId(int groupPosition, int childPosition) {
//                return software.getUrl().length();
//            }
//
//            @Override
//            public boolean hasStableIds() {
//                return false;
//            }
//
//            @Override
//            public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
//                return null;
//            }
//
//            @Override
//            public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
//                return null;
//            }
//
//            @Override
//            public boolean isChildSelectable(int groupPosition, int childPosition) {
//                return false;
//            }
//        };
//        for (String licenseTextPart : license.split(("\n"))) {
//            int padding = licenseTextPart.replaceAll("^(\\s+).+", "$1").length() * 2;
//            TextView licensePart = (TextView)_licensePartInflater.inflate(R.layout.license_paragraph_layout, null);
//            if (!licenseTextPart.isEmpty() && padding != 0 && padding != licenseTextPart.length() * 2) {
//                if (padding < 40) {
//                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                    layoutParams.setMargins(padding, 0, padding, 0);
//                    licensePart.setLayoutParams(layoutParams);
//                    licensePart.setPadding(padding, 0, padding, 0);
//                } else {
//                    licensePart.setGravity(Gravity.CENTER_HORIZONTAL);
//                }
//            }
//
//            licensePart.setText(licenseTextPart.trim());
//            _licenseView.addView(licensePart);
//        }
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

        RotateDrawable arrowDrawable = (RotateDrawable)_nameTextView.getCompoundDrawables()[2];
        _ossContentContainer.startAnimation(expandAnimation);

        if (_animationState == AnimationState.EXPANDING) {
            ObjectAnimator.ofInt(arrowDrawable, "level", 0, 10000).setDuration(350).start();
        } else if (_animationState == AnimationState.COLLAPSING) {
            ObjectAnimator.ofInt(arrowDrawable, "level", 10000, 0).setDuration(350).start();
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