package com.cocosw.framework.sample;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.view.View;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.balysv.materialmenu.extras.abc.MaterialMenuIconCompat;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Project: cocoframework
 * Created by LiaoKai(soarcn) on 2014/9/9.
 */
public class Main extends DualPaneActivity {

    private MaterialMenuIconCompat materialMenu;
    private ColorDrawable abbg;

    @Override
    protected Fragment onCreateMasterPane() {

        CalligraphyConfig.initDefault("fonts/Roboto-Regular.ttf");
        return Fragment.instantiate(this,TodoList.class.getName());
    }

    private Drawable.Callback mDrawableCallback = new Drawable.Callback() {
        @Override
        public void invalidateDrawable(Drawable who) {
            getSupportActionBar().setBackgroundDrawable(who);
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {
        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        materialMenu = new MaterialMenuIconCompat(this, Color.WHITE, MaterialMenuDrawable.Stroke.THIN);
        materialMenu.setState(MaterialMenuDrawable.IconState.BURGER);
        abbg = new ColorDrawable(getResources().getColor(R.color.themecolor));
        abbg.setAlpha(255);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN)
            abbg.setCallback(mDrawableCallback);
        getSupportActionBar().setBackgroundDrawable(abbg);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        super.onPanelSlide(panel, slideOffset);
        abbg.setAlpha((int) (slideOffset * 255));
    }

    @Override
    public void onPanelClosed(View panel) {
        super.onPanelClosed(panel);
        materialMenu.animatePressedState(MaterialMenuDrawable.IconState.ARROW);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public void onPanelOpened(View panel) {
        super.onPanelOpened(panel);
        materialMenu.animatePressedState(MaterialMenuDrawable.IconState.BURGER);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        materialMenu.syncState(savedInstanceState);
    }


    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        materialMenu.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (!mPanel.isOpen()) {
                mPanel.openPane();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
