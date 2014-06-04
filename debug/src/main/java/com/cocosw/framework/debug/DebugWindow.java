package com.cocosw.framework.debug;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.google.common.collect.Lists;
import com.readystatesoftware.notificationlog.Log;

import java.util.List;

import im.dino.dbinspector.activities.DbInspectorActivity;
import wei.mark.standout.StandOutWindow;
import wei.mark.standout.constants.StandOutFlags;
import wei.mark.standout.ui.Window;

/**
 * Project: cocoframework
 * Created by LiaoKai(soarcn) on 2014/5/15.
 */
public class DebugWindow extends StandOutWindow implements AdapterView.OnItemClickListener {

    private ArrayAdapter<String> mAdapter;

    @Override
    public String getAppName() {
        return "Debug";
    }

    @Override
    public int getAppIcon() {
        return R.drawable.ic_launcher_dbinspector;
    }

    @Override
    public void createAndAttachView(int id, FrameLayout frame) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.coco_debug_ui_debug_window, frame, true);
        assert view != null;
        ListView listview = (ListView) view.findViewById(R.id.listView);
        List<String> list = Lists.newArrayList("DB Inspector", "CatLog", "应用内调试信息输出");
        mAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, list);
        listview.setAdapter(mAdapter);
        listview.setOnItemClickListener(this);
    }

    // the window will be centered
    @Override
    public StandOutLayoutParams getParams(int id, Window window) {
        return new StandOutLayoutParams(id, 400, 300,
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

    @Override
    public Animation getShowAnimation(int id) {
        if (isExistingId(id)) {
            // restore
            return AnimationUtils.loadAnimation(this,
                    android.R.anim.slide_in_left);
        } else {
            // show
            return super.getShowAnimation(id);
        }
    }

    @Override
    public boolean onClose(int id, Window window) {
        if (isExistingId(id))
            hide(id);
        return true;
    }

    @Override
    public Animation getHideAnimation(int id) {
        return AnimationUtils.loadAnimation(this,
                android.R.anim.slide_out_right);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                startActivity(new Intent(this, DbInspectorActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                //hide(getUniqueId());
                break;
            case 1:
                StandOutWindow
                        .show(this, CatLogWindow.class, position);
                break;
            case 2:
                Log.initialize(this);
        }
        //   hide(this,DebugWindow.class,StandOutWindow.DEFAULT_ID);
    }

}
