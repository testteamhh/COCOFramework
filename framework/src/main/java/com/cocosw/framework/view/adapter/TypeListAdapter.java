package com.cocosw.framework.view.adapter;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;

import com.cocosw.adapter.SingleTypeAdapter;
import com.cocosw.framework.uiquery.CocoQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Administrator
 * Date: 13-7-17
 * Time: 下午6:32
 */
public abstract class TypeListAdapter<T> extends SingleTypeAdapter<T> implements
        CocoAdapter<T> {

    protected Context context;
    protected View.OnClickListener onViewClickInListListener;
    private boolean loading = true;

    public TypeListAdapter(Context context, int layoutResourceId) {
        this(context, layoutResourceId, null);
    }

    public TypeListAdapter(Context context, int layoutResourceId, List<T> items) {
        super(context, layoutResourceId);
        this.context = context;
        setItems(items);
    }


    @Override
    public void remove(int position) {
        this.getItems().remove(position);
    }

    /**
     * 往数据后面加入数据
     *
     * @param values
     */
    @Override
    public void add(final List<T> values) {
        if (values != null) {
            this.getItems().addAll(values);
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
            this.getItems().add(value);
        }
    }

    /**
     * 往数据前面加入数据
     *
     * @param values
     */
    @Override
    public void append(final List<T> values) {
        this.getItems().addAll(0, values);
    }

    /**
     * 往数据前面加入数据
     *
     * @param values
     */
    @Override
    public void append(final T values) {
        this.getItems().add(0, values);
    }

    @Override
    public void updateList(List<T> values) {
        setItems(values);
    }

    /**
     * 通知UI更新
     */
    @Override
    public void notifyDataChange() {
        this.loading = false;
        notifyDataSetChanged();
    }


    @Override
    public long getItemId(final int position) {
        return getItem(position).hashCode();
    }

    /**
     * Check whether a {@link android.app.LauncherActivity.ListItem} is already in this adapter.
     *
     * @param item Item to be verified whether it is in the adapter.
     */
    public boolean contains(final T item) {
        return getItems().contains(item);
    }

    @Override
    public void setListWatch(final View.OnClickListener listener) {
        this.onViewClickInListListener = listener;
    }

    @Override
    public void refresh() {
        getItems().clear();
    }

    @Override
    public boolean isEmpty() {
        return getItems().size() == 0 & !loading;
    }

    @Override
    public List<T> getItems() {
        return super.getItems();
    }
}
