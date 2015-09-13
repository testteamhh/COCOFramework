package com.cocosw.framework.sample;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cocosw.accessory.views.layout.StickyScrollView;
import com.cocosw.accessory.views.widgets.BackdropImageView;
import com.cocosw.framework.core.BaseFragment;
import com.cocosw.framework.core.SystemBarTintManager;
import com.cocosw.framework.sample.network.Bean;
import com.cocosw.framework.sample.utils.PaletteManager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.Bind;

/**
 * Project: ToDoList
 * Created by LiaoKai(soarcn) on 2014/6/12.
 */
public class ShotDetail extends BaseFragment implements View.OnClickListener {

    public static final String TODO = "todo";
    @Bind(R.id.description)
    TextView mDescription;
    @Bind(R.id.scrollview)
    StickyScrollView mScrollview;
    @Bind(R.id.header_image)
    BackdropImageView mHeader;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.backdrop_toolbar)
    FrameLayout mBackdropToolbar;
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
        Bean.Shot todo = (Bean.Shot) getArguments().getSerializable(TODO);
        pm.updatePalette(picasso, todo.image_teaser_url, mHeader, null, mBackdropToolbar);
        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
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

        mToolbar.setNavigationOnClickListener(this);
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
        if (palette == null || v == null)
            return;
        Palette.Swatch item = palette.getDarkMutedSwatch();
        if (item == null)
            return;
        v.setBackgroundColor(item.getRgb());
        mDescription.setTextColor(item.getBodyTextColor());
    }

    @Override
    public void onInsetsChanged(SystemBarTintManager.SystemBarConfig insets) {
        super.onInsetsChanged(insets);
    }

    @Override
    protected boolean hasActionBarBlock() {
        return false;
    }

    @Override
    public void onStop() {
        super.onStop();
        Picasso.with(context).cancelRequest(mHeader);
    }

    @Override
    public void onClick(View v) {
        getActivity().onBackPressed();
    }
}
