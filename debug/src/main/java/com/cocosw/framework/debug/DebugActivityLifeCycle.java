package com.cocosw.framework.debug;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;


import org.jraf.android.util.activitylifecyclecallbackscompat.ActivityLifecycleCallbacksCompat;

/**
* Created by Administrator on 14-2-18.
*/
public class DebugActivityLifeCycle implements ActivityLifecycleCallbacksCompat {
    @Override
    public void onActivityStopped(Activity activity) {
        Log.d(activity.getLocalClassName(),"onActivityStopped activity=" + activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.d(activity.getLocalClassName(),"onActivityStarted activity=" + activity);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Log.d(activity.getLocalClassName(),"onActivitySaveInstanceState activity=" + activity);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.d(activity.getLocalClassName(),"onActivityResumed activity=" + activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.d(activity.getLocalClassName(),"onActivityPaused activity=" + activity);
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.d(activity.getLocalClassName(),"onActivityDestroyed activity=" + activity);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Log.d(activity.getLocalClassName(),"onActivityCreated activity=" + activity);
    }
}

