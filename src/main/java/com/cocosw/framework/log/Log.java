package com.cocosw.framework.log;

import android.database.Cursor;
import com.cocosw.framework.BuildConfig;

/**
 * User: soarcn
 * Date: 13-8-12
 * Time: 下午10:40
 */
public class Log {

    private static boolean debugFlag = BuildConfig.DEBUG;

    /**
     * 打印log
     *
     * @param obj
     */
    private static void dout(final Object obj) {
        if (Log.debugFlag) {
            // Views.showToast(obj.toString());
            if (obj != null) {
                Log.d("[dout]", "obj>>>>>>>>>>>>>" + obj.getClass().getName()
                        + ">>" + obj.toString());
            } else {
                Log.d("[dout]", "obj>>>>>>>>>>>>>NULL");
            }
        }
    }

    public static void d(final Object... obj) {
        for (final Object object : obj) {
            Log.dout(object);
        }
    }

    public static void d(final int obj) {
        if (Log.debugFlag) {
            Log.d("[dout]", "int>>>>>>>>>>>>>" + obj);
        }
    }

    public static void d(final String str) {
        if (Log.debugFlag) {
            // Views.showToast(str);
            Log.d("[dout]", "str>>>>>>>>>>>>>" + str);
        }
    }

    public static void d(final Cursor str) {
        if (Log.debugFlag) {
            Log.d("[dout]", Log.cur2Str(str));
        }
    }

    public static void doutCursor(final Cursor cursor) {
        final StringBuilder retval = new StringBuilder();

        retval.append("|");
        final int numcolumns = cursor.getColumnCount();
        for (int column = 0; column < numcolumns; column++) {
            final String columnName = cursor.getColumnName(column);
            retval.append(String.format("%-20s |",
                    columnName.substring(0, Math.min(20, columnName.length()))));
        }
        retval.append("\n|");
        for (int column = 0; column < numcolumns; column++) {
            for (int i = 0; i < 21; i++) {
                retval.append("-");
            }
            retval.append("+");
        }
        retval.append("\n|");

        while (cursor.moveToNext()) {
            for (int column = 0; column < numcolumns; column++) {
                final String columnValue = cursor.getString(column);
                retval.append(columnValue);
            }
            retval.append("\n");

        }
        if (Log.debugFlag) {
            Log.d("[dout]", retval.toString());
        }
    }

    public static void d(final String str, final String str2) {
        if (Log.debugFlag) {
            // Views.showToast(str + " " + str2);
            android.util.Log.d("[dout]", "str>>>>>>>>>>>>>" + str + " " + str2);
        }
    }

    /**
     * 把游标内容显示出来
     *
     * @param cur
     * @return
     */
    public static String cur2Str(final Cursor cur) {
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
        if (Log.debugFlag) {
            for (int i = 0; i < str.length; i++) {
                Log.d("[dout]", "str[" + i + "]>>>>>>>>>>>>>" + str[i]);
            }
        }
    }

    public static void d(final Throwable t) {
        Log.dout(android.util.Log.getStackTraceString(t));
    }
}
