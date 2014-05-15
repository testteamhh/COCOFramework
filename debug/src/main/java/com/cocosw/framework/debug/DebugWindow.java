package com.cocosw.framework.debug;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;
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
//        ListView listview = (ListView) view.findViewById(R.id.listView);
//        mAdapter = new ArrayAdapter<String>(this,android.R.id.text1);
//        mAdapter.addAll(Lists.newArrayList("aaa","bbb","ccc"));
//        listview.setAdapter(mAdapter);
//        listview.setOnItemClickListener(this);
        view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DebugWindow.this, DbInspectorActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
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

//    @Override
//    public String getPersistentNotificationTitle(int id) {
//        return getAppName() + " Running";
//    }
//
//    @Override
//    public String getPersistentNotificationMessage(int id) {
//        return "Click to add a new " + getAppName();
//    }
//
//    // return an Intent that creates a new MultiWindow
//    @Override
//    public Intent getPersistentNotificationIntent(int id) {
//        return StandOutWindow.getShowIntent(this, getClass(), getUniqueId());
//    }

    @Override
    public int getHiddenIcon() {
        return android.R.drawable.ic_menu_info_details;
    }

    @Override
    public String getHiddenNotificationTitle(int id) {
        return getAppName() + " Hidden";
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
        if (position == 0)
            startActivity(new Intent(this, DbInspectorActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @Override
    public List<DropDownListItem> getDropDownItems(int id) {
        List<DropDownListItem> items = new ArrayList<DropDownListItem>();
        items.add(new DropDownListItem(android.R.drawable.ic_menu_help,
                "About", new Runnable() {

            @Override
            public void run() {
                Toast.makeText(
                        DebugWindow.this,
                        getAppName()
                                + " is a demonstration of StandOut.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
        ));
        items.add(new DropDownListItem(android.R.drawable.ic_menu_preferences,
                "Settings", new Runnable() {

            @Override
            public void run() {
                Toast.makeText(DebugWindow.this,
                        "There are no settings.", Toast.LENGTH_SHORT)
                        .show();
            }
        }
        ));
        return items;
    }

    @Override
    public void onReceiveData(int id, int requestCode, Bundle data,
                              Class<? extends StandOutWindow> fromCls, int fromId) {
        // receive data from WidgetsWindow's button press
        // to show off the data sending framework
        switch (requestCode) {
//            case DATA_CHANGED_TEXT:
//                Window window = getWindow(id);
//                if (window == null) {
//                    String errorText = String.format(Locale.US,
//                            "%s received data but Window id: %d is not open.",
//                            getAppName(), id);
//                    Toast.makeText(this, errorText, Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                String changedText = data.getString("changedText");
//                TextView status = (TextView) window.findViewById(R.id.id);
//                status.setTextSize(20);
//                status.setText("Received data from WidgetsWindow: "
//                        + changedText);
//                break;
            default:
                Log.d("MultiWindow", "Unexpected data received.");
                break;
        }
    }
}
