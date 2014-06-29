package com.cocosw.framework.core;

import android.support.v4.app.Fragment;

import com.cocosw.accessory.views.complex.CocoPager;


public interface Carousel {

    Pager<Carousel> getCurrentPager();

    void nav(Class<? extends Fragment> clz);

    Pager<Carousel> nextPager(final int id);

    Pager<Carousel> prevPager(final int id);

    CocoPager getViewPager();
}
