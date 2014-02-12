package com.cocosw.framework.app;

/**
 * Created by Administrator on 14-2-7.
 */
public enum Priority {
    LOW(0),
    MID(500),
    HIGH(1000),
    SHOWSTOPPER(5000);

    private final int index;

    Priority(int index) {
        this.index = index;
    }

    public int index() {
        return index;
    }
}
