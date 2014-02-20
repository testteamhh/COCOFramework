package com.cocosw.framework.exception;

public class CocoException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = -327804094669868051L;

    public static final int ERROR = 1;
    public static final int WARNING = 2;
    public static final int NOTICE = 3;

    // 用于显示的string id
    private int resid;

    private int level;

    private int errorCode;

    public int getErrorCode() {
        return errorCode;
    }

    public CocoException(final int errorCode, final Throwable t) {
        super(t);
        this.errorCode = errorCode;
    }

    public CocoException(final String msg, final Throwable cause) {
        super(msg, cause);

    }

    public CocoException(final String msg) {
        super(msg);
    }

    public CocoException(final int errorCode, final String msg) {
        super(msg);
        this.errorCode = errorCode;
    }

    public CocoException(final int errorCode, final String msg,
                         final Throwable t) {
        this(msg, t);
        this.errorCode = errorCode;
    }
}
