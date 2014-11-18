package com.cocosw.framework.sample;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.graphics.PaletteItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cocosw.accessory.views.ABHelper;
import com.cocosw.accessory.views.layout.ObservableScrollView;
import com.cocosw.accessory.views.layout.StickyScrollView;
import com.cocosw.accessory.views.widgets.BackdropImageView;
import com.cocosw.framework.core.BaseFragment;
import com.cocosw.framework.core.SystemBarTintManager;
import com.cocosw.framework.sample.network.Bean;
import com.cocosw.framework.sample.utils.PaletteManager;
import com.cocosw.framework.sample.view.AnimatedLinearLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.InjectView;

/**
 * Project: ToDoList
 * Created by LiaoKai(soarcn) on 2014/6/12.
 */
public class ShotDetail extends BaseFragment implements ObservableScrollView.OnScrollChangedListener {

    public static final String TODO = "todo";
    @InjectView(R.id.header)
    BackdropImageView mHeader;
    @InjectView(R.id.detail)
    TextView mDetail;
    @InjectView(R.id.description)
    TextView mDescription;
    @InjectView(R.id.scrollview)
    StickyScrollView mScrollview;
    @InjectView(R.id.info)
    ImageButton mInfo;
    @InjectView(R.id.star)
    ImageButton mStar;
    @InjectView(R.id.container)
    AnimatedLinearLayout mContainer;
    private ABHelper abhelper;

    @Inject
    Picasso picasso;

    @Inject
    PaletteManager pm;

    @Override
    public int layoutId() {
        return R.layout.tododetail;
    }

    @Override
    protected void setupUI(View view, Bundle bundle) throws Exception {
        if (getArguments() == null)
            return;
        inject();
        mContainer.setAnimationsEnabled(true);
        Bean.Shot todo = (Bean.Shot) getArguments().getSerializable(TODO);
        q.v(mDetail).text(todo.title);
        pm.updatePalette(picasso, todo.image_teaser_url, mHeader, mDetail, mDetail);

        picasso.load(todo.image_400_url).into(mHeader, new Callback() {
            @Override
            public void onSuccess() {
                if (mHeader != null && mHeader.getDrawable() != null)
                    colorize(((BitmapDrawable) mHeader.getDrawable()).getBitmap());
            }

            @Override
            public void onError() {

            }
        });
        q.v(mDescription).html(todo.description);

        abhelper = new ABHelper(new ColorDrawable(R.color.transparent), mHeader, getResources().getDimensionPixelSize(R.dimen.abc_action_bar_default_height_material)) {

            @Override
            protected void setActionBarBackground(Drawable who) {

            }
        };
        mScrollview.setOnScrollChangedListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        picasso.cancelRequest(mHeader);
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//
//        View[] views = new View[mContainer.getChildCount()];
//        for (int i = 0; i < views.length; i++) {
//            views[i] = mContainer.getChildAt(i);
//        }
//        mContainer.removeAllViews();
//
//        for (View view : views) {
//            mContainer.addView(view);
//        }
//    }

    private void colorize(Bitmap photo) {
        Palette.generateAsync(photo, new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                applyPalette(palette);
            }
        });
    }

    private void applyPalette(Palette palette) {
        PaletteItem item = palette.getDarkMutedColor();
        if (item != null) {
            mDetail.setBackgroundColor(item.getRgb());
            v.setBackgroundDrawable(new ColorDrawable(item.getRgb()));
        }

        item = palette.getVibrantColor();
        if (item != null)
            mDetail.setTextColor(item.getRgb());

        item = palette.getMutedColor();
        if (item != null) {
            mDetail.setBackgroundColor(item.getRgb());
        }

        item = palette.getLightVibrantColor();
        if (item != null)
            mDescription.setTextColor(item.getRgb());

    }

    @Override
    public void onInsetsChanged(SystemBarTintManager.SystemBarConfig insets) {
        //super.onInsetsChanged(insets);
    }

    @Override
    public void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt) {
        mHeader.offsetBackdrop(abhelper.onScroll(who, t));
    }

    @Override
    public void onStop() {
        super.onStop();
        Picasso.with(context).cancelRequest(mHeader);
    }
}
