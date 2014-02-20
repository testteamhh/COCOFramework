package com.cocosw.framework.app;

/**
 * A runnable object for application, will be called when application start.
 * <p/>
 * you can place config object to different release.
 * <p/>
 * Created by Administrator on 14-2-18.
 */
public abstract class Config implements Runnable {


    protected final CocoApp app;

    public Config(CocoApp app) {
        this.app = app;
    }

    public abstract void run();
}
