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
import com.balysv.materialmenu.MaterialMenuIcon;
import com.cocosw.framework.core.DualPaneActivity;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Project: cocoframework
 * Created by LiaoKai(soarcn) on 2014/9/9.
 */
public class Main extends DualPaneActivity {

    private ColorDrawable abbg;
 //   private MaterialMenuIcon materialMenu;
    private boolean isDrawerOpened;

    @Override
    protected Fragment onCreateMasterPane() {
        CalligraphyConfig.initDefault("fonts/Roboto-Regular.ttf");
        return Fragment.instantiate(this, PopularList.class.getName());
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
        abbg = new ColorDrawable(getResources().getColor(R.color.themecolor));
        abbg.setAlpha(255);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN)
            abbg.setCallback(mDrawableCallback);
        getSupportActionBar().setBackgroundDrawable(abbg);
  //      materialMenu = new MaterialMenuIcon(this, Color.WHITE, MaterialMenuDrawable.Stroke.THIN);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        super.onPanelSlide(panel, slideOffset);
        abbg.setAlpha((int) (slideOffset * 255));
//        materialMenu.setTransformationOffset(
//                MaterialMenuDrawable.AnimationState.BURGER_ARROW,
//                isDrawerOpened ? 2 - slideOffset : slideOffset
//        );
    }

    @Override
    public void onPanelClosed(View panel) {
        super.onPanelClosed(panel);
        isDrawerOpened = false;
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public void onPanelOpened(View panel) {
        super.onPanelOpened(panel);
        isDrawerOpened = true;
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    //    materialMenu.syncState(savedInstanceState);
    }


    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
   //     materialMenu.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (!getPanel().isOpen()) {
                getPanel().openPane();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
