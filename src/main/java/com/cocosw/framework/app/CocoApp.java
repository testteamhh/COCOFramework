

package com.cocosw.framework.app;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import com.androidquery.util.AQUtility;
import com.cocosw.accessory.utils.FakeX509TrustManager;
import com.cocosw.accessory.utils.UIUtils;
import com.cocosw.framework.BuildConfig;
import com.cocosw.framework.network.Network;
import com.github.kevinsawicki.http.HttpRequest;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.FROYO;


/**
 * Android Bootstrap application
 */
public class CocoApp extends Application {

    private static CocoApp instance;

    /**
     * Create main application
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public CocoApp() {
        Network.init();
        if (BuildConfig.DEBUG) {
            AQUtility.setDebug(true);
            if (UIUtils.hasGingerbread()) {
                StrictMode
                        .setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                                .detectNetwork().penaltyLog().build());
            }
        }
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
    }


    public static CocoApp getInstance() {
        return instance;
    }
}
