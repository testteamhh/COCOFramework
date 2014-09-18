package com.cocosw.framework.core;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cocosw.accessory.views.adapter.HeaderFooterListAdapter;
import com.cocosw.framework.R;
import com.cocosw.framework.exception.CocoException;
import com.cocosw.framework.view.adapter.CocoAdapter;

import java.util.List;

/**
 * Project: cocoframework
 * Created by LiaoKai(soarcn) on 2014/9/18.
 */
public abstract class ListAdapterViewFragment<T, A extends AbsListView> extends AdapterViewFragment<T, A> {

    protected HeaderFooterListAdapter<BaseAdapter> wrapAdapter;
    private View footerView;
    private View headerView;

    /**
     * Create adapter to display items
     *
     * @return adapter
     * @throws Exception
     */
    private HeaderFooterListAdapter<BaseAdapter> createAdapter()
            throws Exception {
        mAdapter = (BaseAdapter) createAdapter(items);
        wrapAdapter = new HeaderFooterListAdapter<>(wrapperAdapter(mAdapter), context);
        return wrapAdapter;
    }

    protected void constractAdapter() throws Exception {
        getList().setAdapter(createAdapter());
    }

    public View getHeaderView() {
        return headerView;
    }

    public View getFooterView() {
        return footerView;
    }


    @Override
    protected void showRefresh(final CocoException e) {
        setFooter(R.layout.refreshview);
        ((TextView) getFooterView().findViewById(R.id.empty_msg))
                .setText(refreshText(e));
        getFooterView().findViewById(R.id.empty_image).setVisibility(View.GONE);
        getFooterView().findViewById(R.id.empty_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View arg0) {
                        refreshAction();
                        getHeaderAdapter().clearFooters();
                    }
                });
    }


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
    public void onDestroyView() {
        headerView = null;
        footerView = null;
        wrapAdapter = null;
        super.onDestroyView();
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

    protected void updateList(final List<T> items) {
        ((CocoAdapter) getHeaderAdapter().getWrappedAdapter()).updateList(items);
    }

}
