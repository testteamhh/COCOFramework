package com.cocosw.framework.sample;


import com.cocosw.framework.debug.DebugActivityLifeCycle;
import com.cocosw.framework.debug.DebugFragmentLiftCycle;
import com.cocosw.framework.debug.DebugUtils;
import com.cocosw.framework.debug.NotificationLog;
import com.cocosw.framework.debug.ViewServerActiviyCycle;
import com.cocosw.framework.sample.app.DridddleApp;
import com.readystatesoftware.notificationlog.Log;

import timber.log.Timber;

/**
 * Project: ${PROJECT_NAME}
 * Created by LiaoKai(soarcn) on 14-2-18.
 */
public class AppConfig implements Runnable {

    private final DridddleApp app;

    public AppConfig(DridddleApp app) {
        this.app = (app);
    }

    @Override
    public void run() {
        if (DebugUtils.isViewServerNeeded(app))
            app.registerActivityLifecycle(new ViewServerActiviyCycle());
        app.registerActivityLifecycle(new DebugActivityLifeCycle());
        app.registerFragmentLifecycle(new DebugFragmentLiftCycle());
        DebugUtils.setupStrictMode();
        Timber.plant(new Timber.DebugTree());
        if (BuildConfig.notificationLog)
            Timber.plant(new NotificationLog());
        Log.initialize(app, app.getApplicationInfo().icon);
        DebugUtils.setupStetho(app);
    }
}
