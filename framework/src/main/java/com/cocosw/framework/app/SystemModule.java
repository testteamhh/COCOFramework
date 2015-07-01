package com.cocosw.framework.app;

import android.app.Application;
import android.content.Context;
import android.os.StatFs;

import com.cocosw.accessory.connectivity.NetworkConnectivity;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module(
        complete = false,
        library = true
)

public class SystemModule {

    private static final String HTTP_CACHE = "coco-http";
    private static final int MIN_DISK_CACHE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final int MAX_DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB
    private final Application app;


    public SystemModule(CocoApp app) {
        this.app = app;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return app;
    }

    @Provides
    @Singleton
    Context provideContext() {
        return app;
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(Application app, Cache cache) {
        OkHttpClient client = new OkHttpClient();
        client.setCache(cache);
        return client;
    }

    @Provides
    @Singleton
    Cache provideCache(Application app) {
        return new Cache(createDefaultCacheDir(app), calculateDiskCacheSize(createDefaultCacheDir(app)));
    }

    private File createDefaultCacheDir(Context context) {
        File cache = new File(context.getApplicationContext().getCacheDir(), HTTP_CACHE);
        if (!cache.exists()) {
            cache.mkdirs();
        }
        return cache;
    }

    private int calculateDiskCacheSize(File dir) {
        StatFs statFs = new StatFs(dir.getAbsolutePath());
        int available = statFs.getBlockCount() * statFs.getBlockSize();
        int size = available / 50;
        return Math.max(Math.min(size, MAX_DISK_CACHE_SIZE), MIN_DISK_CACHE_SIZE);
    }


    @Provides
    NetworkConnectivity provideNetworkConnectivity(Application app) {
        return NetworkConnectivity.getInstance(app);
    }
}
