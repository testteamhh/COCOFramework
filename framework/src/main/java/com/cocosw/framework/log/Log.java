package com.cocosw.framework.log;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.webkit.WebView;

import com.cocosw.framework.R;

import timber.log.Timber;

/**
 * User: soarcn
 * Date: 13-8-12
 * Time: 下午10:40
 */
public class Log {

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

//    public static void d(final Iterable iterable) {
//        d(FlipTableConverters.fromIterable(iterable, iterable.getClass()));
//    }

    public static void i(String str, Object... arg) {
        Timber.i(str, arg);
    }

    public static void i(String str) {
        Timber.i(str);
    }

    public static void d(String str) {
        Timber.d(str);
    }

    public static void d(String str, Object... arg) {
        Timber.d(str, arg);
    }

    public static void w(String str) {
        Timber.w(str);
    }

    public static void w(String str, Object... arg) {
        Timber.w(str, arg);
    }

    public static void e(String str) {
        Timber.e(str);
    }

    public static void e(String str, Object... arg) {
        Timber.e(str, arg);
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
        Timber.d(t, "Exception");
    }

    public static void e(final Throwable t) {
        Timber.e(t, "Exception");
    }

    public static void e(final Throwable t, String str, Object... arg) {
        Timber.e(t, str, arg);
    }

    /**
     * Show huge amount info with a dialog, HTML is allowed
     *
     * @param str
     */
    public static void dialog(Context context, String content, String... str) {
        create(context, content, str).show();
    }

    private static Dialog create(Context mContext, String mLicensesText, CharSequence... str) {
        //Get resources
        final WebView webView = new WebView(mContext);
        webView.loadDataWithBaseURL(null, mLicensesText, "text/html", "utf-8", null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext)

                .setView(webView)
                .setPositiveButton(R.string.ok, new Dialog.OnClickListener() {
                    public void onClick(final DialogInterface dialogInterface, final int i) {
                        dialogInterface.dismiss();
                    }
                });
        if (str != null && str.length > 0)
            builder.setTitle(str[0]);
        return builder.create();
    }

}
