

package com.cocosw.framework.app;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import com.androidquery.util.AQUtility;
import com.cocosw.accessory.utils.FakeX509TrustManager;
import com.cocosw.accessory.utils.UIUtils;
import com.cocosw.framework.BuildConfig;


/**
 * Android Bootstrap application
 */
public class CocoApp extends Application {

    private static CocoApp instance;

    /**
     * Create main application
     */
    public CocoApp() {
        FakeX509TrustManager.allowAllSSL();
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
