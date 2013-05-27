package com.cocosw.framework.exception;

import android.content.Context;
import android.widget.Toast;
import com.cocosw.framework.app.CocoApp;

public class ExceptionHandler {

	private static final int TOAST_DISPLAY_TIME = 4000;

	public static void handle(final Exception e, final Context ctx)
			throws CocoException {
		// e.printStackTrace();
		if (e instanceof CocoException) {
			throw (CocoException) e;
		}

		// Utils.dout(e);
		throw new CocoException(ErrorCode.WRONG_PASSWORD, "发生未知错误，稍后重试", e);
	}

	public static void defaultHandler(final Exception e) {
		try {
			ExceptionHandler.handle(e, null);
		} catch (final CocoException e1) {
			Toast.makeText(CocoApp.getInstance(), e1.getMessage(),
					ExceptionHandler.TOAST_DISPLAY_TIME).show();
			return;
		}
	}

    public static void reportError(Object ctx, String stackTraceString) {
        //MobclickAgent.reportError(this, Log.getStackTraceString(e));
    }
}
