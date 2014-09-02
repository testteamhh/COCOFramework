package com.cocosw.framework.core;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cocosw.accessory.views.adapter.HeaderFooterListAdapter;
import com.cocosw.framework.R;
import com.cocosw.framework.R.id;
import com.cocosw.framework.exception.CocoException;
import com.cocosw.framework.view.adapter.CocoAdapter;

import java.util.List;

public abstract class ListFragment<T> extends AdapterViewFragment<T, ListView> implements
        OnItemClickListener, OnScrollListener {

    private View footerView;
    private View headerView;
    protected HeaderFooterListAdapter<BaseAdapter> wrapAdapter;

    /**
     * Create adapter to display items
     *
     * @return adapter
     * @throws Exception
     */
    private HeaderFooterListAdapter<BaseAdapter> createAdapter()
            throws Exception {
        mAdapter = (BaseAdapter) createAdapter(items);
        wrapAdapter = new HeaderFooterListAdapter<>(
                getList(), wrapperAdapter(mAdapter));
        return wrapAdapter;
    }


    public View getFooterView() {
        return footerView;
    }

    @Override
    public int layoutId() {
        return R.layout.inc_progresslist;
    }

    /**
     * Get list adapter
     *
     * @return list adapter
     */
    protected HeaderFooterListAdapter<BaseAdapter> getHeaderAdapter() {
        if (getList() != null) {
            return wrapAdapter;
        } else {
            return null;
        }
    }

    public View getHeaderView() {
        return headerView;
    }


    @Override
    protected void showRefresh(final CocoException e) {
        setFooter(R.layout.refreshview);
        ((TextView) getFooterView().findViewById(id.empty_msg))
                .setText(refreshText(e));
        getFooterView().findViewById(id.empty_image).setVisibility(View.GONE);
        getFooterView().findViewById(id.empty_button).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(final View arg0) {
                        refreshAction();
                        getHeaderAdapter().clearFooters();
                    }
                });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        headerView = null;
        footerView = null;
    }


    @Override
    public abstract List<T> pendingData(Bundle args) throws Exception;

    /**
     * 设置Footerview
     *
     * @param uiRes
     */
    protected void setFooter(final int uiRes) {
        final View view = LayoutInflater.from(context).inflate(uiRes, null);
        getHeaderAdapter().clearFooters();
        getHeaderAdapter().addFooter(view);
        footerView = view;
    }

    /**
     * 设置Headview
     *
     * @param uiRes
     */
    protected void setHeader(final int uiRes) {
        final View view = LayoutInflater.from(context).inflate(uiRes, null);
        getHeaderAdapter().clearHeaders();
        getHeaderAdapter().addHeader(view);
        headerView = view;
    }


    @Override
    protected void constractAdapter() throws Exception {
        getList().setAdapter(createAdapter());
    }


    protected void updateList(final List<T> items) {
        ((CocoAdapter) getHeaderAdapter().getWrappedAdapter()).updateList(items);
    }

}
