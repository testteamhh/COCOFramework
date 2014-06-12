

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
import com.path.android.jobqueue.BaseJob;
import com.path.android.jobqueue.config.Configuration;
import com.path.android.jobqueue.di.DependencyInjector;
import com.path.android.jobqueue.log.CustomLogger;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import timber.log.Timber;


/**
 * Android Bootstrap application
 */
public abstract class CocoApp extends Application {

    protected static CocoApp instance;

    private static String TAG = "Coco";

    @Inject
    OkHttpClient client;

    @Inject
    Picasso picasso;

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

    public Picasso getPicasso() {
        return picasso;
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
        if (client!=null)
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
