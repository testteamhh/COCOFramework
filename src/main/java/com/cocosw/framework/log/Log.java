package com.cocosw.framework.log;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.text.Html;
import android.webkit.WebView;
import com.cocosw.framework.BuildConfig;
import timber.log.Timber;

/**
 * User: soarcn
 * Date: 13-8-12
 * Time: 下午10:40
 */
public class Log {

    static {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }


    /** A tree which logs important information for crash reporting. */
    private static class CrashReportingTree extends Timber.DebugTree {

        @Override
        public void e(String message, Object... args) {
            super.e(message, args);
        }

        @Override
        public void e(Throwable t, String message, Object... args) {
            super.e(t, message, args);
        }
    }


    /**
     * 打印log
     *
     * @param obj
     */
    private static void dout(final Object obj) {
        if (obj != null) {
            Timber.d("obj>>>>>>>>>>>>>" + obj.getClass().getName()
                    + ">>" + obj.toString());
        } else {
            Timber.d("obj>>>>>>>>>>>>>NULL");
        }
    }

    public static void dout(final Object... obj) {
        for (final Object object : obj) {
            Log.dout(object);
        }
    }

    public static void i(String str) {
        Timber.i(str);
    }

    public static void d(String str) {
        Timber.d(str);
    }

    public static void w(String str) {
        Timber.w(str);
    }

    public static void e(String str) {
        Timber.e(str);
    }


    public static void d(final Object str) {
        Timber.d(str.toString());
    }

    public static void d(final Cursor str) {
        Timber.d(Log.cur2Str(str));
    }


    /**
     * 把游标内容显示出来
     *
     * @param cur
     * @return
     */
    private static String cur2Str(final Cursor cur) {
        final StringBuffer buf = new StringBuffer();
        final String[] col = cur.getColumnNames();
        for (int i = 0; i < col.length; i++) {
            final String str = col[i];
            try {
                buf.append("; [" + str + "]:").append(cur.getString(i));
            } catch (final Exception e) {

            }

        }
        return buf.toString();
    }

    public static void d(final String[] str) {
            for (int i = 0; i < str.length; i++) {
                d("str[" + i + "]>>>>>>>>>>>>>" + str[i]);
            }
    }

    public static void d(final Throwable t) {
        Timber.d(t,"Exception");
    }

    /**
     * Show huge amount info with a dialog, HTML is allowed
     *
     * @param str
     */
    public static void dialog(Context context, String content, String... str) {
        //TODO html/non-html
        create(context,content,str).show();
    }

    private static Dialog create(Context mContext, String mLicensesText,String... str) {
        //Get resources
        final WebView webView = new WebView(mContext);
        webView.loadDataWithBaseURL(null, mLicensesText, "text/html", "utf-8", null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext)

                .setView(webView)
                .setPositiveButton("Close", new Dialog.OnClickListener() {
                    public void onClick(final DialogInterface dialogInterface, final int i) {
                        dialogInterface.dismiss();
                    }
                });
        if (str!=null)
            builder.setTitle(str[0]);
        final AlertDialog dialog = builder.create();
        return dialog;
    }

}
