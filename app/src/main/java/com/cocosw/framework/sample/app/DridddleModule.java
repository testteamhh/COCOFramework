package com.cocosw.framework.sample.app;

import android.content.Context;

import com.cocosw.framework.sample.PopularList;
import com.cocosw.framework.sample.PopularRecyclerList;
import com.cocosw.framework.sample.ShotDetail;
import com.cocosw.framework.sample.utils.PaletteManager;
import com.squareup.picasso.Picasso;

import javax.inject.Singleton;

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
                DridddleApp.class,
                PopularList.class,
                ShotDetail.class,
                PopularRecyclerList.class
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

    @Provides
    @Singleton
    PaletteManager providePaletteManager() {
        return new PaletteManager();
    }

    @Provides
    Picasso providePicasso(Context context) {
        return Picasso.with(context);
    }
}
