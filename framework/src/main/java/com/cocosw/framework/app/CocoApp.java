

package com.cocosw.framework.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;

import com.cocosw.accessory.connectivity.NetworkConnectivity;
import com.cocosw.accessory.utils.UIUtils;
import com.cocosw.framework.BuildConfig;
import com.cocosw.framework.log.Log;
import com.cocosw.framework.uiquery.CocoQuery;
import com.path.android.jobqueue.BaseJob;
import com.path.android.jobqueue.config.Configuration;
import com.path.android.jobqueue.di.DependencyInjector;
import com.path.android.jobqueue.log.CustomLogger;

import org.jraf.android.util.activitylifecyclecallbackscompat.ActivityLifecycleCallbacksCompat;
import org.jraf.android.util.activitylifecyclecallbackscompat.ApplicationHelper;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import timber.log.Timber;


/**
 * Android Bootstrap application
 */
public abstract class CocoApp extends Application {

    protected static CocoApp instance;

    private static String TAG = "Coco";

    /**
     * Create main application
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public CocoApp() {
        instance = this;
    }

    public static CocoApp get(Context context) {
        return (CocoApp) context.getApplicationContext();
    }

    /**
     * Create main application
     *
     * @param context
     */
    public CocoApp(final Context context) {
        this();
        attachBaseContext(context);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        NetworkConnectivity.getInstance(this);
        TAG = getString(getApplicationInfo().labelRes);
        CocoQuery.setQueryClass(CocoQuery.ExtViewQuery.class);
        buildObjectGraphAndInject();
        if (getCrashTree() != null) {
            Timber.plant(getCrashTree());
        }
        if (config()!=null) {
            config().run();
        }
    }

    /**
     * register a new call back for Activity Lifecycle
     *
     * @param callback
     */
    public void registerActivityLifecycle(ActivityLifecycleCallbacksCompat callback) {
        ApplicationHelper.registerActivityLifecycleCallbacks(this, callback);
    }

    public static Application getApp() {
        if (instance == null) {
            throw new IllegalAccessError("Please create your own Application class inherit from CocoApp, and add it to manifest");
        }
        return instance;
    }

    private void buildObjectGraphAndInject() {
        long start = System.nanoTime();

        Injector.init(new SystemModule(this));
        Injector.init(new AndroidModule());
        long diff = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
        Log.i("Global object graph creation took %sms", diff);
    }


    protected Config config() {
        return null;
    }

    /**
     * Provide CrashTree for upload your app exception to online service
     *
     * @return
     */
    protected Timber.Tree getCrashTree() {
        return null;
    }


    // setup job manager configuration
    protected Configuration getJobManagerConfig() {
        Configuration configuration = new Configuration.Builder(this)
                .customLogger(new CustomLogger() {

                    @Override
                    public boolean isDebugEnabled() {
                        return true;
                    }

                    @Override
                    public void d(String text, Object... args) {
                        Log.d(text, args);
                    }

                    @Override
                    public void e(Throwable t, String text, Object... args) {
                        Log.e(t, text, args);
                    }

                    @Override
                    public void e(String text, Object... args) {
                        Log.e(text, args);
                    }
                })
                .minConsumerCount(1)//always keep at least one consumer alive
                .maxConsumerCount(3)//up to 3 consumers at a time
                .loadFactor(3)//3 jobs per consumer
                .consumerKeepAlive(120)//wait 2 minute
                .injector(new DependencyInjector() {
                    @Override
                    public void inject(BaseJob baseJob) {
                        Injector.inject(baseJob);
                    }
                })
                .build();
        return configuration;
    }
}
