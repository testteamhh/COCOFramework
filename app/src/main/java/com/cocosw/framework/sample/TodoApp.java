package com.cocosw.framework.sample;

import com.cocosw.framework.app.CocoApp;

import timber.log.Timber;

/**
 * Project: ToDoList
 * Created by LiaoKai(soarcn) on 2014/6/12.
 */
public class TodoApp extends CocoApp {

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
    }
}
