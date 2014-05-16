package com.cocosw.framework.debug;/*
 * Copyright (c) 2014. www.cocosw.com
 */


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ListView;

import net.daverix.logcatviewer.LogAdapter;
import net.daverix.logcatviewer.LogFilter;
import net.daverix.logcatviewer.LogHandler;
import net.daverix.logcatviewer.LogHandlerService;
import net.daverix.logcatviewer.LogItem;
import net.daverix.logcatviewer.LogPriorityFilter;
import net.daverix.logcatviewer.LogTextFactory;
import net.daverix.logcatviewer.LogcatTextFactory;
import net.daverix.logcatviewer.Priority;

import java.util.ArrayList;
import java.util.List;

import wei.mark.standout.StandOutWindow;
import wei.mark.standout.constants.StandOutFlags;
import wei.mark.standout.ui.Window;

/**
 * Project: insight
 * Created by LiaoKai(soarcn) on 2014/5/16.
 */
public class CatLogWindow extends StandOutWindow implements LogHandler.OnLogItemReadListener {

    private ListView listView;

    @Override
    public String getAppName() {
        return "CatLog";
    }

    @Override
    public int getAppIcon() {
        return com.cocosw.framework.debug.R.drawable.ic_launcher_dbinspector;
    }

    @Override
    public void createAndAttachView(int id, FrameLayout frame) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(android.R.layout.list_content, frame, true);

        mLogTextFactory = new LogcatTextFactory();
        listView = (ListView) view.findViewById(android.R.id.list);
        listView.setFastScrollEnabled(true);
        listView.setBackgroundColor(0x99000000);
        listView.setStackFromBottom(true);
        bindService(new Intent(this, LogHandlerService.class), mServiceConnection, Context.BIND_AUTO_CREATE);

    }

    // the window will be centered
    @Override
    public StandOutLayoutParams getParams(int id, Window window) {
        return new StandOutLayoutParams(id, 400, 600,
                StandOutLayoutParams.CENTER,
                StandOutLayoutParams.CENTER, 100, 100);
    }

    // move the window by dragging the view
    @Override
    public int getFlags(int id) {
        return StandOutFlags.FLAG_DECORATION_SYSTEM
                | StandOutFlags.FLAG_BODY_MOVE_ENABLE
                | StandOutFlags.FLAG_WINDOW_HIDE_ENABLE
                | StandOutFlags.FLAG_WINDOW_BRING_TO_FRONT_ON_TAP
                | StandOutFlags.FLAG_WINDOW_EDGE_LIMITS_ENABLE;
    }

    @Override
    public String getHiddenNotificationMessage(int id) {
        return "Click to restore #" + id;
    }

    // return an Intent that restores the MultiWindow
    @Override
    public Intent getHiddenNotificationIntent(int id) {
        return StandOutWindow.getShowIntent(this, getClass(), id);
    }


    private static final String ARG_LOG_LEVEL = "loglevel";
    private LogHandler mLogHandler;
    private LogAdapter mLogAdapter;
    private Priority mLogLevel = Priority.DEBUG;
    private LogTextFactory mLogTextFactory;
    private LogFilter mLogFilter;


    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mLogHandler = ((LogHandlerService.LogHandlerServiceBinder) service).getLogHandler();
            mLogFilter = new LogPriorityFilter(mLogHandler);

            loadItems();

            mLogHandler.addOnLogItemReadListener(CatLogWindow.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mLogHandler.removeOnLogItemReadListener(CatLogWindow.this);
            mLogHandler = null;
        }
    };

    private void loadItems() {
        if (mLogAdapter == null) {
            mLogAdapter = new LogAdapter(this, mLogFilter.getFilteredItems(mLogLevel));
            setListAdapter(mLogAdapter);

            ListView listView = getListView();
            if (listView != null) {
                listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
                listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
                listView.setSelector(R.drawable.list_selector_holo_light);
            }
        } else {
            mLogAdapter.setItems(mLogFilter.getFilteredItems(mLogLevel));
            mLogAdapter.notifyDataSetChanged();
        }
    }

    private void setListAdapter(final LogAdapter mLogAdapter) {
        listView.setAdapter(mLogAdapter);
        mLogAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        unbindService(mServiceConnection);
        super.onDestroy();
    }

    @Override
    public void onLogItemRead(LogItem item) {
        // if(isAdded()) {
        loadItems();
        //  }
    }


    private void filter(Priority priority) {
        mLogLevel = priority;
        loadItems();
    }

    @Override
    public List<DropDownListItem> getDropDownItems(int id) {
        List<DropDownListItem> items = new ArrayList<DropDownListItem>();
        items.add(new DropDownListItem(android.R.drawable.ic_menu_help,
                "VERBOSE", new Runnable() {

            @Override
            public void run() {
                filter(Priority.VERBOSE);
            }
        }
        ));
        items.add(new DropDownListItem(android.R.drawable.ic_menu_preferences,
                "DEBUG", new Runnable() {

            @Override
            public void run() {
                filter(Priority.DEBUG);
            }
        }
        ));
        items.add(new DropDownListItem(android.R.drawable.ic_menu_preferences,
                "INFO", new Runnable() {

            @Override
            public void run() {
                filter(Priority.INFO);
            }
        }
        ));
        items.add(new DropDownListItem(android.R.drawable.ic_menu_preferences,
                "WARNING", new Runnable() {

            @Override
            public void run() {
                filter(Priority.WARNING);
            }
        }
        ));
        items.add(new DropDownListItem(android.R.drawable.ic_menu_preferences,
                "ERROR", new Runnable() {

            @Override
            public void run() {
                filter(Priority.ERROR);
            }
        }
        ));
        items.add(new DropDownListItem(android.R.drawable.ic_menu_preferences,
                "FATAL", new Runnable() {

            @Override
            public void run() {
                filter(Priority.FATAL);
            }
        }
        ));
        items.add(new DropDownListItem(android.R.drawable.ic_menu_preferences,
                "CLEAR", new Runnable() {

            @Override
            public void run() {
                mLogAdapter = new LogAdapter(CatLogWindow.this, mLogFilter.getFilteredItems(mLogLevel));
                setListAdapter(mLogAdapter);
            }
        }
        ));
        return items;
    }

    protected List<LogItem> getCheckedItems() throws IllegalStateException {
        ListView listView = getListView();

        if (listView == null)
            throw new IllegalStateException("List view is null");

        List<LogItem> checkedItems = new ArrayList<LogItem>();
        SparseBooleanArray positions = listView.getCheckedItemPositions();
        if (positions == null)
            throw new IllegalStateException("checkedItemPositions() returns null");

        for (int i = 0; i < mLogAdapter.getCount(); i++) {
            if (positions.get(i, false)) {
                checkedItems.add(mLogAdapter.getItem(i));
            }
        }

        return checkedItems;
    }


    public ListView getListView() {
        return listView;
    }
}
