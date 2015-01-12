package com.cocosw.framework.toolkit;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.DecelerateInterpolator;


import com.cocosw.accessory.views.ABHelper;
import com.cocosw.accessory.views.ViewUtils;
import com.cocosw.accessory.views.layout.CollapsingTitleLayout;
import com.cocosw.accessory.views.widgets.BackdropImageView;
import com.cocosw.framework.R;
import java.util.ArrayList;

/**
 * Project: cocoframework
 * Created by LiaoKai(soarcn) on 2014/12/13.
 */
public class ToolbarHelper {

    private ActionBar actionBar;
    private View header;
    private ABHelper helper;
    private int height;
    private final Context context;

    private Toolbar toolbar;

    private CollapsingTitleLayout mCollapsingTitleLayout;
    private BackdropImageView mBackdropImageView;
    private boolean mActionBarAutoHideEnabled;
    private int mActionBarAutoHideMinY;
    private int mActionBarAutoHideSensivity;
    final static int ITEMS_THRESHOLD = 3;
    int lastFvi = 0;
    private static final int HEADER_HIDE_ANIM_DURATION = 300;
    private ArrayList<View> mHideableHeaderViews = new ArrayList<View>();
    private boolean mActionBarShown;
    private int mActionBarAutoHideSignal = 0;


    public ToolbarHelper(final Toolbar toolbar, final View header, final Drawable bg) {
        this.toolbar = toolbar;
        ViewUtils.runOnLayoutIsReady(toolbar,new Runnable() {
            @Override
            public void run() {
                height = toolbar.getHeight();
                helper = new ABHelper(bg,header,height) {
                    @Override
                    protected void setActionBarBackground(Drawable drawable) {
                        ViewUtils.setBackground(toolbar, drawable);
                    }
                };
                if (mBackdropImageView!=null) {
                    mBackdropImageView.setScrimOffset(height);
                }
            }
        });

        context = header.getContext();

        this.header = header;
        if (header instanceof CollapsingTitleLayout) {
            mCollapsingTitleLayout = (CollapsingTitleLayout) header;
        }
    }

    public ToolbarHelper(final Toolbar toolbar, View header) {
        this(toolbar, header, null);
    }

    public ToolbarHelper(final Toolbar toolbar, View header, int bgColor) {
        this(toolbar, header, new ColorDrawable(bgColor));
    }

    public ToolbarHelper(final ActionBar actionBar, final View header, final Drawable bg) {
        this.actionBar = actionBar;
        context = header.getContext();

        ViewUtils.runOnLayoutIsReady(header,new Runnable() {
            @Override
            public void run() {
                height = actionBar.getHeight();
                helper = new ABHelper(bg,header, height) {
                    @Override
                    protected void setActionBarBackground(Drawable drawable) {
                        actionBar.setBackgroundDrawable(drawable);
                    }
                };
                if (mBackdropImageView!=null) {
                    mBackdropImageView.setScrimOffset(height);
                }
            }
        });

        if (header instanceof CollapsingTitleLayout) {
            mCollapsingTitleLayout = (CollapsingTitleLayout) header;
        }
        this.header = header;
    }

    public ToolbarHelper(final ActionBar actionBar, View header, int bgColor) {
        this(actionBar, header, new ColorDrawable(bgColor));
    }

    public ToolbarHelper image(BackdropImageView imageView,int scrimColor) {
        this.mBackdropImageView = imageView;
        imageView.setScrimColor(scrimColor);
        return this;
    }

    public ToolbarHelper image(BackdropImageView imageView) {
        return image(imageView,Color.TRANSPARENT);
    }

    public ToolbarHelper parallax(float parallaxFriction) {
        if (mBackdropImageView!=null)
            mBackdropImageView.setOffsetFriction(parallaxFriction);
        return this;
    }

    private void setBackdropOffset(float offset) {
        if (mCollapsingTitleLayout != null) {
            mCollapsingTitleLayout.setScrollOffset(offset);
        }
        if (mBackdropImageView != null) {
            if (mCollapsingTitleLayout!=null)
                mBackdropImageView.setScrollOffset(
                        (int) ((height - mBackdropImageView.getHeight()) * offset));
            else
                mBackdropImageView.setScrollOffset(
                        (int) ((mBackdropImageView.getHeight()-height) * offset));
        }
    }

    public void onScroll(android.widget.AbsListView absListView, int firstVisibleItem, int visibleItemCount) {
        setBackdropOffset(helper.onScroll(absListView, firstVisibleItem, visibleItemCount));
        if (mActionBarAutoHideEnabled) {
            onMainContentScrolled(firstVisibleItem <= ITEMS_THRESHOLD ? 0 : Integer.MAX_VALUE,
                    lastFvi - firstVisibleItem > 0 ? Integer.MIN_VALUE :
                            lastFvi == firstVisibleItem ? 0 : Integer.MAX_VALUE
            );
            lastFvi = firstVisibleItem;
        }
    }

    public void onScroll(android.widget.ScrollView scrollView, int topScroll) {
        setBackdropOffset(helper.onScroll(scrollView, topScroll));

        if (mActionBarAutoHideEnabled) {
            onMainContentScrolled(topScroll<=height?0:Integer.MAX_VALUE,
                    lastFvi - topScroll > 0 ? Integer.MIN_VALUE :
                            lastFvi == topScroll ? 0 : Integer.MAX_VALUE
            );
            lastFvi = topScroll;
        }
    }


    public ToolbarHelper autoHide(boolean autoHide) {
        initActionBarAutoHide(autoHide);
        return this;
    }

    public ToolbarHelper hideableHeaderViews(View hideableHeaderView) {
        if (!mHideableHeaderViews.contains(hideableHeaderView)) {
            mHideableHeaderViews.add(hideableHeaderView);
        }
        return this;
    }


    /**
     * Initializes the Action Bar auto-hide (aka Quick Recall) effect.
     */
    private void initActionBarAutoHide(boolean autoHide) {
        mActionBarAutoHideEnabled = autoHide;
        mActionBarAutoHideMinY = context.getResources().getDimensionPixelSize(
                R.dimen.action_bar_auto_hide_min_y);
        mActionBarAutoHideSensivity = context.getResources().getDimensionPixelSize(
                R.dimen.action_bar_auto_hide_sensivity);
    }

    /**
     * Indicates that the main content has scrolled (for the purposes of showing/hiding
     * the action bar for the "action bar auto hide" effect). currentY and deltaY may be exact
     * (if the underlying view supports it) or may be approximate indications:
     * deltaY may be INT_MAX to mean "scrolled forward indeterminately" and INT_MIN to mean
     * "scrolled backward indeterminately".  currentY may be 0 to mean "somewhere close to the
     * start of the list" and INT_MAX to mean "we don't know, but not at the start of the list"
     */
    private void onMainContentScrolled(int currentY, int deltaY) {
        if (deltaY > mActionBarAutoHideSensivity) {
            deltaY = mActionBarAutoHideSensivity;
        } else if (deltaY < -mActionBarAutoHideSensivity) {
            deltaY = -mActionBarAutoHideSensivity;
        }

        if (Math.signum(deltaY) * Math.signum(mActionBarAutoHideSignal) < 0) {
            // deltaY is a motion opposite to the accumulated signal, so reset signal
            mActionBarAutoHideSignal = deltaY;
        } else {
            // add to accumulated signal
            mActionBarAutoHideSignal += deltaY;
        }

        boolean shouldShow = currentY < mActionBarAutoHideMinY ||
                (mActionBarAutoHideSignal <= -mActionBarAutoHideSensivity);
        autoShowOrHideActionBar(shouldShow);
    }

    protected void autoShowOrHideActionBar(boolean show) {
        if (show == mActionBarShown) {
            return;
        }
        mActionBarShown = show;
        if (show) {
            if (actionBar!=null)
                actionBar.show();
            if (toolbar!=null)
                toolbar.setVisibility(View.VISIBLE);
        } else {
            if (actionBar!=null)
                actionBar.hide();
            if (toolbar!=null)
                toolbar.setVisibility(View.GONE);
        }
        onActionBarAutoShowOrHide(show);
    }

    protected void onActionBarAutoShowOrHide(boolean shown) {
//        if (mStatusBarColorAnimator != null) {
//            mStatusBarColorAnimator.cancel();
//        }
//        mStatusBarColorAnimator = ObjectAnimator.ofInt(mLPreviewUtils, "statusBarColor",
//                shown ? mThemedStatusBarColor : Color.BLACK).setDuration(250);
//        mStatusBarColorAnimator.setEvaluator(ARGB_EVALUATOR);
//        mStatusBarColorAnimator.start();

        for (View view : mHideableHeaderViews) {
            if (shown) {
                view.animate()
                        .translationY(0)
                        .alpha(1)
                        .setDuration(HEADER_HIDE_ANIM_DURATION)
                        .setInterpolator(new DecelerateInterpolator());
            } else {
                view.animate()
                        .translationY(-view.getBottom())
                        .alpha(0)
                        .setDuration(HEADER_HIDE_ANIM_DURATION)
                        .setInterpolator(new DecelerateInterpolator());
            }
        }
    }


}
