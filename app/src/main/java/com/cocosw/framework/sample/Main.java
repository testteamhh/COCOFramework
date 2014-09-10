package com.cocosw.framework.sample;

import android.content.Context;
import android.support.v4.app.Fragment;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Project: cocoframework
 * Created by LiaoKai(soarcn) on 2014/9/9.
 */
public class Main extends DualPaneActivity {

    @Override
    protected Fragment onCreateMasterPane() {
        CalligraphyConfig.initDefault("fonts/Roboto-Regular.ttf");
        return Fragment.instantiate(this,TodoList.class.getName());
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }


}
