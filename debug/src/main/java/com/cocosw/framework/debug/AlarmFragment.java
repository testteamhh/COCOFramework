package com.cocosw.framework.debug;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cocosw.accessory.utils.AlarmDatabase;
import com.cocosw.accessory.views.adapter.SingleTypeAdapter;

import java.util.List;

/**
 * Project: cocoframework
 * Created by LiaoKai(soarcn) on 2015/1/13.
 */
public class AlarmFragment extends Fragment implements AdapterView.OnItemClickListener {


    private SingleTypeAdapter<AlarmDatabase.Record> mAdapter;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(android.R.layout.list_content,container,false);

        ListView listview = (ListView) view.findViewById(android.R.id.list);

        final List<AlarmDatabase.Record> list = new AlarmDatabase(getActivity().getContentResolver()).getAllAlarm();

        mAdapter = new SingleTypeAdapter<AlarmDatabase.Record>(getActivity(),android.R.layout.simple_list_item_2) {

            @Override
            protected int[] getChildViewIds() {
                return new int[]{android.R.id.text1,android.R.id.text2};
            }

            @Override
            protected void update(int i, AlarmDatabase.Record o) {
                setText(0,o.hour+":"+o.minute);
                setText(1,"#"+o.id+" "+o.message);
            }
        };
        listview.setAdapter(mAdapter);
        mAdapter.setItems(list);
        listview.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
