package com.cocosw.framework.sample;



import com.cocosw.framework.sample.app.DridddleApp;

/**
 * Project: ${PROJECT_NAME}
 * Created by LiaoKai(soarcn) on 14-2-18.
 */
public class AppConfig implements Runnable {

    private final DridddleApp app;

    public AppConfig(DridddleApp app) {
        this.app = (app);
    }

    @Override
    public void run() {

    }
}
