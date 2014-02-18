package com.cocosw.framework.debug;

import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import com.cocosw.accessory.utils.UIUtils;
import com.cocosw.accessory.utils.Utils;

/**
 * Created by Administrator on 14-2-18.
 */
public class DebugUtils {

    /**
     * ViewServer only apply for unrooted <r11 device
     * @param context
     * @return
     */
    public static boolean isViewServerNeeded(Context context){
        return (Build.VERSION.SDK_INT>11 && !Utils.isRooted(context));
    }

    public static void setupStrictMode() {
        if (UIUtils.hasGingerbread()) {
            StrictMode
                    .setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                            .detectNetwork().penaltyLog().build());
        }
    }


}
