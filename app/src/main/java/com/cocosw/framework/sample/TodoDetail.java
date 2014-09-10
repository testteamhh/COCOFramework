package com.cocosw.framework.sample;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cocosw.accessory.views.ABHelper;
import com.cocosw.accessory.views.layout.ObservableScrollView;
import com.cocosw.framework.core.BaseFragment;
import com.cocosw.framework.core.SystemBarTintManager;
import com.cocosw.framework.sample.network.Bean;
import com.cocosw.framework.view.BackdropImageView;
import com.cocosw.framework.view.StickyScrollView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import butterknife.InjectView;

/**
 * Project: ToDoList
 * Created by LiaoKai(soarcn) on 2014/6/12.
 */
public class TodoDetail extends BaseFragment implements ObservableScrollView.OnScrollChangedListener {

    public static final String TODO = "todo";
    @InjectView(R.id.header)
    BackdropImageView mHeader;
    @InjectView(R.id.detail)
    TextView mDetail;
    @InjectView(R.id.description)
    TextView mDescription;
    @InjectView(R.id.other)
    TextView mOther;
    @InjectView(R.id.scrollview)
    StickyScrollView mScrollview;
    private ABHelper abHelper;

    @Override
    public int layoutId() {
        return R.layout.tododetail;
    }

    @Override
    protected void setupUI(View view, Bundle bundle) throws Exception {
        if (getArguments() == null)
            return;
        Bean.Shot todo = (Bean.Shot) getArguments().getSerializable(TODO);
        q.id(R.id.detail).text(todo.title);
        Picasso.with(context).load(todo.image_400_url).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                mHeader.setImageBitmap(bitmap);
                colorize(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
        q.v(mOther).text(todo.toString());
        q.v(mDescription).html(todo.description);
        mScrollview.setOnScrollChangedListener(this);
        abHelper = new ABHelper(new ColorDrawable(getResources().getColor(R.color.transparent)),mHeader,0) {
            @Override
            protected void setActionBarBackground(Drawable who) {
                getActionBar().setBackgroundDrawable(who);
            }
        };

    }

    private void colorize(Bitmap photo) {
        Palette.generateAsync(photo, new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                applyPalette(palette);
            }
        });
    }

    private void applyPalette(Palette palette) {
        v.setBackgroundDrawable(new ColorDrawable(palette.getDarkMutedColor().getRgb()));

        mDetail.setTextColor(palette.getVibrantColor().getRgb());

        mDescription.setTextColor(palette.getLightVibrantColor().getRgb());

        mOther.setBackgroundColor(palette.getLightMutedColor().getRgb());


    }


    @Override
    public void onInsetsChanged(SystemBarTintManager.SystemBarConfig insets) {
        //super.onInsetsChanged(insets);
    }

    @Override
    public void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt) {
        mHeader.offsetBackdrop(abHelper.onScroll(who,t));
    }
}
