package com.cocosw.framework.view.adapter;

import android.content.Context;
import android.view.View;

import java.util.List;

/**
 * QuickAdapter
 * <p/>
 * Project: cocoframework
 * User: Liao Kai(soarcn@gmail.com)
 * Date: 14-1-14
 */
public abstract class QuickAdapter<T> extends com.joanzapata.android.QuickAdapter<T> implements CocoAdapter<T> {

    protected View.OnClickListener onViewClickInListListener;
    private boolean loading = true;

    public QuickAdapter(Context context, int layoutResId) {
        super(context, layoutResId);
    }

    public QuickAdapter(Context context, int layoutResId, List<T> data) {
        super(context, layoutResId, data);
    }


    @Override
    public void add(final List<T> values) {
        if (values != null) {
            addAll(values);
        }
    }


    @Override
    public void add(final T value) {
        if (value != null) {
            add(value);
        }
    }


    @Override
    public void append(final List<T> values) {
        addAll(values);
    }

    @Override
    public void append(final T values) {
        add(values);
    }

    @Override
    public void remove(int position) {
        remove(position);
    }

    @Override
    public void notifyDataChange() {
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        this.loading = false;
        super.notifyDataSetChanged();
    }

    public void updateList(final List<T> values) {
        clear();
        addAll(values);
    }


    @Override
    public void setListWatch(final View.OnClickListener listener) {
        this.onViewClickInListListener = listener;
    }

    @Override
    public void refresh() {
        clear();
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty() & !loading;
    }
}
