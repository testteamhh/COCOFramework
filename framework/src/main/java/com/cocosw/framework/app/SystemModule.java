package com.cocosw.framework.app;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.os.StatFs;

import com.cocosw.accessory.connectivity.NetworkConnectivity;
import com.cocosw.framework.log.Log;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.otto.Bus;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module(
        complete = false,
        library = true,
        injects = {CocoApp.class}
)

public class SystemModule {

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
    Bus provideBus() {
        return CocoBus.getInstance();
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
    Picasso providePicasso(Application app, OkHttpClient client, com.squareup.picasso.Cache cache) {
        return new Picasso.Builder(app)
                .downloader(new OkHttpDownloader(client))
                .memoryCache(cache)
                .listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception e) {
                        Log.d("Failed to load image: %s", uri);
                    }
                })
                .build();
    }

    private static final String HTTP_CACHE = "coco-http";
    private static final int MIN_DISK_CACHE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final int MAX_DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB

    @Provides
    @Singleton
    Cache provideCache(Application app) {
        try {
            return new Cache(createDefaultCacheDir(app), calculateDiskCacheSize(createDefaultCacheDir(app)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Provides
    @Singleton
    com.squareup.picasso.Cache providePicassoCache(Application app) {
        return new LruCache(app);
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
