package com.cocosw.framework.sample.app;

import com.cocosw.framework.app.CocoApp;
import com.cocosw.framework.sample.AppConfig;
import com.crashlytics.android.Crashlytics;

import timber.log.Timber;

/**
 * Project: ToDoList
 * Created by LiaoKai(soarcn) on 2014/6/12.
 */
public class DridddleApp extends CocoApp {

    @Override
    public void onCreate() {
        super.onCreate();
        Crashlytics.start(this);

        Timber.plant(new Timber.DebugTree());
    }

    @Override
    protected Object getAppModule() {
        return new DridddleModule(this);
    }

    @Override
    protected Runnable config() {
        return new AppConfig(this);
    }

    @Override
    protected Timber.Tree getCrashTree() {
        // Umeng exception analysis service
        return new Timber.DebugTree() {

            @Override
            public void w(String s, Object... objects) {
                Crashlytics.log(String.format(s, objects));
            }

            @Override
            public void w(Throwable throwable, String s, Object... objects) {
                Crashlytics.logException(throwable);
            }

            @Override
            public void e(String s, Object... objects) {
                Crashlytics.log(String.format(s, objects));
            }

            @Override
            public void e(Throwable throwable, String s, Object... objects) {
                Crashlytics.logException(throwable);
            }
        };
    }
}
