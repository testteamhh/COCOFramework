package com.cocosw.framework.debug;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Arrays;
import java.util.List;

import im.dino.dbinspector.activities.DbInspectorActivity;


/**
 * Project: cocoframework
 * Created by LiaoKai(soarcn) on 2014/5/15.
 */
public class DebugWindow extends Activity implements AdapterView.OnItemClickListener {

    private ArrayAdapter<String> mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coco_debug_ui_debug_window);
        ListView listview = (ListView) findViewById(R.id.listView);
        List<String> list = Arrays.asList("DB Inspector", "CatLog", "DebugDrawer");
        mAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, list);
        listview.setAdapter(mAdapter);
        listview.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                startActivity(new Intent(this, DbInspectorActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                break;
            case 1:
                startActivity(new Intent(this, CatLogWindow.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                break;
        }
        //   hide(this,DebugWindow.class,StandOutWindow.DEFAULT_ID);
    }

}
