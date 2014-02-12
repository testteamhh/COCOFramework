package com.cocosw.framework.app;

import android.app.Application;
import android.content.Context;
import android.net.Uri;

import com.cocosw.accessory.connectivity.NetworkConnectivity;
import com.path.android.jobqueue.JobManager;
import com.squareup.okhttp.HttpResponseCache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.otto.Bus;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import timber.log.Timber;


@Module(
        complete = false,
        library = true
)
public class SystemModule {

    private final Application app;

    public SystemModule(CocoApp app) {
        this.app = app;
    }

    @Provides
    @Singleton
    JobManager provideJobManager(Application context) {
        return new JobManager(context, ((CocoApp) context).getJobManagerConfig());
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

    static final int DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(Application app) {
        return createOkHttpClient(app);
    }

    @Provides
    @Singleton
    Picasso providePicasso(Application app, OkHttpClient client) {
        return new Picasso.Builder(app)
                .downloader(new OkHttpDownloader(client))
                .listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception e) {
                        Timber.e(e, "Failed to load image: %s", uri);
                    }
                })
                .build();
    }

    static OkHttpClient createOkHttpClient(Application app) {
        OkHttpClient client = new OkHttpClient();

        // Install an HTTP cache in the application cache directory.
        try {
            File cacheDir = new File(app.getCacheDir(), "http");
            HttpResponseCache cache = new HttpResponseCache(cacheDir, DISK_CACHE_SIZE);
            client.setResponseCache(cache);
        } catch (IOException e) {
            Timber.e(e, "Unable to install disk cache.");
        }

        return client;
    }

    @Provides
    NetworkConnectivity provideNetworkConnectivity(Application app) {
        return NetworkConnectivity.getInstance(app);
    }
}
