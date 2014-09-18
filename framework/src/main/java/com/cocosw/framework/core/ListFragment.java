package com.cocosw.framework.core;

import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.cocosw.framework.R;

public abstract class ListFragment<T> extends AdapterViewFragment<T, ListView> implements
        OnItemClickListener, OnScrollListener {


    @Override
    public int layoutId() {
        return R.layout.inc_progresslist;
    }


}
