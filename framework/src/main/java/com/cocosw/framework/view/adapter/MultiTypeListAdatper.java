package com.cocosw.framework.view.adapter;

import android.content.Context;
import android.view.View;

import com.cocosw.adapter.MultiTypeAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Administrator
 * Date: 13-7-17
 * Time: 下午6:32
 */
public abstract class MultiTypeListAdatper<T> extends MultiTypeAdapter<T> implements
        CocoAdapter<T> {

    private List<T> dataList;
    protected Context context;
    protected View.OnClickListener onViewClickInListListener;
    private boolean loading = true;

    public MultiTypeListAdatper(Context context) {
        this(context, null);
    }

    public MultiTypeListAdatper(Context context, List<T> dataList) {
        super(context);
        this.context = context;
        if (dataList == null) {
            this.dataList = new ArrayList<T>();
        } else
            this.dataList = dataList;
    }

    @Override
    public List<T> getItems() {
        return dataList;
    }

    @Override
    public T getItem(int position) {
        return super.getItem(position);
    }

    /**
     * get object item type
     *
     * @param obj
     * @return
     */
    public abstract int getType(T obj);


    /**
     * 往数据后面加入数据
     *
     * @param values
     */
    @Override
    public void add(final List<T> values) {
        if (values != null && values.size() > 0) {
            //  this.dataList.addAll(values);
            for (T obj : values) {
                add(obj);
            }
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
            addItem(getType(value), value);
        }
    }

    /**
     * 往数据前面加入数据
     *
     * @param values
     */
    @Override
    public void append(final List<T> values) {
        this.dataList.addAll(0, values);
    }

    /**
     * 往数据前面加入数据
     *
     * @param values
     */
    @Override
    public void append(final T values) {
        this.dataList.add(0, values);
    }

    @Override
    public void updateList(List<T> values) {
        clear();
        add(values);
    }

    @Override
    public void remove(int position) {
        this.dataList.remove(position);
        removeItem(position);
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
        return getDataList().contains(item);
    }

    @Override
    public void setListWatch(final View.OnClickListener listener) {
        this.onViewClickInListListener = listener;
    }

    @Override
    public void refresh() {
        super.clear();
        getDataList().clear();
    }

    @Override
    public boolean isEmpty() {
        return getDataList().size() == 0 & !loading;
    }

    protected List<T> getDataList() {
        return dataList;
    }

}
