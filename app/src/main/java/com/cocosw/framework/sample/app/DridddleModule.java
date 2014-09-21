package com.cocosw.framework.sample.app;

import dagger.Module;
import dagger.Provides;

/**
 * Project: cocoframework
 * Created by LiaoKai(soarcn) on 2014/9/21.
 */
@Module(
        complete = false,
        library = false,
        overrides = true,

        injects = {
                DridddleApp.class
        }
)

public class DridddleModule {

    private final DridddleApp app;

    public DridddleModule(DridddleApp app) {
        this.app = app;
    }

    @Provides
    DridddleApp provideInsightApp() {
        return app;
    }

}
