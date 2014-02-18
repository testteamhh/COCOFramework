package com.cocosw.framework.app;

/**
 * Created by Administrator on 14-2-18.
 */
public abstract class Config{


    protected final CocoApp app;

    public Config(CocoApp app) {
        this.app=app;
    }

    public abstract void run() ;
}
