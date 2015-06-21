package com.cocosw.framework.view.adapter;

import android.view.View;
import android.widget.ListAdapter;

import java.util.List;

public interface CocoAdapter<T> {

    /**
     * Append data at the end of the list
     *
     * @param values
     */
    public void add(List<T> values);

    /**
     * Append data at the end of the list
     *
     * @param value
     */
    public void add(T value);

    /**
     * Append data in the front of the list
     *
     * @param values
     */
    public void append(List<T> values);

    /**
     * Append data in the front of the list
     *
     * @param values
     */
    public void append(T values);


    T getItem(int i);

    void updateList(List<T> values);

    /**
     * Notify UI update
     */
    public void notifyDataChange();

    /**
     * Set onclick listener for item view click
     *
     * @param listener
     */
    public void setListWatch(View.OnClickListener listener);

    /**
     * 重新载入数据接口
     */
    public void refresh();

    /**
     * remove item from position
     *
     * @param position
     */
    public void remove(int position);

    int getCount();

    java.util.List<T> getItems();
}
