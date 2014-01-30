/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Project  : CdutHelperAndroid
 * Package  : net.solosky.cduthelper.android.adapter
 * File     : SimpleListAdapter.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2011-5-20
 * License  : Apache License 2.0
 */
package com.cocosw.framework.view.adapter;

import android.app.LauncherActivity.ListItem;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author solosky <solosky772@qq.com>
 */
public abstract class SimpleListAdapter<T, V extends ViewHolder> extends BaseAdapter implements
        CocoAdapter<T> {

    private final LayoutInflater mInflater;
    private List<T> dataList;
    protected Context context;
    protected OnClickListener onViewClickInListListener;
    private boolean loading = true;

    /**
     * 默认会构造为arraylist以后可以换
     */
    public SimpleListAdapter(final Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.dataList = new ArrayList<T>();
    }

    /**
     * @param dataList
     */
    public SimpleListAdapter(final Context context, List<T> dataList) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        if (dataList == null) {
            dataList = new ArrayList<T>(0);
        }
        this.dataList = dataList;
    }

    /**
     * 如果为空,那么Gone,否则visible
     *
     * @param view
     * @param obj
     */
    protected boolean notEmptyView(final View view, final Object obj) {
        if (obj == null) {
            view.setVisibility(View.GONE);
            return false;
        } else {
            view.setVisibility(View.VISIBLE);
            return true;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        return dataList.size();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public T getItem(final int i) {
        if (i != AdapterView.INVALID_POSITION & i < dataList.size()) {
            return dataList.get(i);
        } else {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(final int i) {
        return 0;
    }

    @Override
    public View getView(final int position, final View convertView,
                        final ViewGroup parent) {
        V holder = null;
        if (convertView == null) {
            holder = newView(position, parent);
            holder.contentView.setTag(holder);
        } else {
            holder = (V) convertView.getTag();
        }
        holder.position = position;
        holder.parent = parent;
        fillView(holder, position, parent);
        return holder.contentView;
    }

    protected Context getContext() {
        return context;
    }

    // 创建一个新的View
    public abstract V newView(int position, ViewGroup parent);

    // 填充这个View
    public abstract void fillView(V viewHolder, int position,
                                  ViewGroup parent);

    // 展开一个View
    public View inflate(final int resourceId) {
        return this.mInflater.inflate(resourceId, null);
    }

    protected List<T> getDataList() {
        return dataList;
    }

    public void updateList(final List<T> values) {
        this.dataList = values;
    }

    /**
     * 往数据后面加入数据
     *
     * @param values
     */
    @Override
    public void add(final List<T> values) {
        if (values != null) {
            this.dataList.addAll(values);
        }
    }

    /**
     * 往数据后面加入数据
     *
     * @param value
     */
    @Override
    public void add(final T value) {
        if (value != null) {
            this.dataList.add(value);
        }
    }

    /**
     * 往数据前面加入数据
     *
     * @param values
     */
    @Override
    public void append(final List<T> values) {
        this.dataList.addAll(values);
    }

    /**
     * 往数据前面加入数据
     *
     * @param values
     */
    @Override
    public void append(final T values) {
        this.dataList.add(values);
    }

    @Override
    public void remove(int position) {
        this.dataList.remove(position);
    }

    /**
     * 通知UI更新
     */
    @Override
    public void notifyDataChange() {
        this.loading = false;
        notifyDataSetChanged();
    }

    /**
     * Check whether a {@link ListItem} is already in this adapter.
     *
     * @param item Item to be verified whether it is in the adapter.
     */
    public boolean contains(final T item) {
        return getDataList().contains(item);
    }

    @Override
    public void setListWatch(final OnClickListener listener) {
        this.onViewClickInListListener = listener;
    }

    @Override
    public void refresh() {
        getDataList().clear();
    }

    @Override
    public boolean isEmpty() {
        return getDataList().size() == 0 & !loading;
    }

}
