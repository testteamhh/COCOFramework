package com.cocosw.framework.exception;

import android.content.Context;
import android.widget.Toast;

import com.cocosw.framework.log.Log;

/**
 * ExcetionManager is a exception router in UI layer.
 *
 *
 */
public class ExceptionManager {

    private static final int TOAST_DISPLAY_TIME = 4000;

    /**
     * preset handler will display exception msg as toast bar
     */
    public static final ExceptionHandler toastHandler = new ExceptionHandler() {
        @Override
        public boolean exception(Exception e, Context ctx, Object source) throws CocoException {
            Toast.makeText(ctx, e.getMessage(), ExceptionManager.TOAST_DISPLAY_TIME).show();
            return false;
        }

        @Override
        public void error(Exception e, Context ctx, Object source) {
            Toast.makeText(ctx, e.getMessage(), ExceptionManager.TOAST_DISPLAY_TIME).show();
        }
    };

    /**
     * preset handler will only log the exception
     */
    public static class LogExceptionHandler implements ExceptionHandler {
        @Override
        public boolean exception(Exception e, Context ctx, Object source) throws CocoException {
            Log.e(e);
            return false;
        }

        @Override
        public void error(Exception e, Context ctx, Object source) {
            Log.e(e);
        }
    }

    ;

    private static ExceptionHandler handler = new LogExceptionHandler();


    public static void handle(final Exception e, final Context source)
            throws CocoException {
        handle(e, source, source);
    }

    public static void error(final Exception e, final Context source) {
        error(e, source, source);
    }

    public static void error(Exception e, Context ctx, Object source) {
        if (handler != null) {
            handler.error(e, ctx, source);
        }
    }

    public static void handle(final Exception e, final Context ctx, final Object source) throws CocoException {

        if (handler != null && handler.exception(e, ctx, source))
            return;
        if (e instanceof CocoException) {
            throw (CocoException) e;
        }
        throw new CocoException("发生未知错误，稍后重试", e);
    }

    public static void setHandler(ExceptionHandler handler) {
        ExceptionManager.handler = handler;
    }

    public interface ExceptionHandler {

        /**
         * Handle logic exception in UI layer,
         * return true will interupt the default exception process, which means UI will not show any messages.
         *
         * @param e
         * @param ctx
         * @param source
         * @return
         * @throws CocoException
         */
        boolean exception(final Exception e, final Context ctx, final Object source) throws CocoException;

        /**
         * Some error happened and can't recover from disaster
         *
         * @param e
         * @param ctx
         * @param source
         */
        void error(Exception e, Context ctx, Object source);
    }
}
