package com.cocosw.framework.debug;

import android.app.Activity;
import android.os.Bundle;

import com.cocosw.lifecycle.ActivityLifecycleCallbacksCompat;

/**
 * support ViewServer
 *
 * Created by Administrator on 14-2-18.
 */
public class ViewServerActiviyCycle implements ActivityLifecycleCallbacksCompat {
    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        ViewServer.get(activity).addWindow(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        ViewServer.get(activity).setFocusedWindow(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        ViewServer.get(activity).removeWindow(activity);
    }
}
