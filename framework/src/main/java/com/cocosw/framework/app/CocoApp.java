

package com.cocosw.framework.app;

import android.app.Application;
import android.content.Context;

import com.cocosw.accessory.connectivity.NetworkConnectivity;
import com.cocosw.framework.log.Log;
import com.cocosw.framework.network.Network;
import com.cocosw.framework.uiquery.CocoQuery;
import com.cocosw.lifecycle.ActivityLifecycleCallbacksCompat;
import com.cocosw.lifecycle.FragmentLifecycleCallbacks;
import com.cocosw.lifecycle.LifecycleDispatcher;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import timber.log.Timber;


/**
 * Android Bootstrap application
 */
public abstract class CocoApp extends Application {

    protected static CocoApp instance;

    private static String TAG = "Coco";

    // looks hacky but no better way yet
    @Inject
    OkHttpClient client;

    /**
     * Create main application
     */
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
        try {
            NetworkConnectivity.getInstance(this);
        } catch (SecurityException ignore) {
        }
        TAG = getString(getApplicationInfo().labelRes);
        CocoQuery.setQueryClass(CocoQuery.ExtViewQuery.class);
        buildObjectGraphAndInject();
        if (getCrashTree() != null) {
            Timber.plant(getCrashTree());
        }
        if (config() != null) {
            config().run();
        }
        if (getAppModule() != null) {
            Injector.init(getAppModule(), this);
        }
        if (client != null)
            Network.init(this, client);
    }

    protected Object getAppModule() {
        return null;
    }

    /**
     * register a new call back for Activity Lifecycle
     *
     * @param callback
     */
    public void registerActivityLifecycle(ActivityLifecycleCallbacksCompat callback) {
        LifecycleDispatcher.registerActivityLifecycleCallbacks(this, callback);
    }

    /**
     * register a new call back for Fragment Lifecycle
     *
     * @param callback
     */
    public void registerFragmentLifecycle(FragmentLifecycleCallbacks callback) {
        LifecycleDispatcher.registerFragmentLifecycleCallbacks(this, callback);
    }

    public static CocoApp getApp() {
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


    protected Runnable config() {
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

}
