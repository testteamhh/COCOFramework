package com.cocosw.framework.debug;

import android.app.Activity;
import android.os.Bundle;

import wei.mark.standout.StandOutWindow;

/**
 * Project: cocoframework
 * Created by LiaoKai(soarcn) on 2014/5/15.
 */
public class DebugCenter extends Activity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StandOutWindow.closeAll(this, DebugWindow.class);
        StandOutWindow
                .show(this, DebugWindow.class, StandOutWindow.DEFAULT_ID);
        finish();
    }

}
