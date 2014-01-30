package com.cocosw.framework.view.adapter;

import android.view.View;
import android.widget.ListAdapter;

import java.util.List;

public interface CocoAdapter<T> extends ListAdapter {

    /**
     * 往数据后面加入数据
     *
     * @param values
     */
    public void add(List<T> values);

    /**
     * 往数据后面加入数据
     *
     * @param value
     */
    public void add(T value);

    /**
     * 往数据前面加入数据
     *
     * @param values
     */
    public void append(List<T> values);

    /**
     * 往数据前面加入数据
     *
     * @param values
     */
    public void append(T values);

    void updateList(List<T> values);

    /**
     * 通知UI更新
     */
    public void notifyDataChange();

    /**
     * 用于设置在List上面有View需要额外监控触摸事件的监控器
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
}
