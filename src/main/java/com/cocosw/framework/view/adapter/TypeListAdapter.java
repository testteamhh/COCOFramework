package com.cocosw.framework.view.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import com.cocosw.framework.uiquery.CocoQuery;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.github.kevinsawicki.wishlist.TypeAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Administrator
 * Date: 13-7-17
 * Time: 下午6:32
 */
public abstract class TypeListAdapter<T> extends SingleTypeAdapter<T> implements
        CocoAdapter<T>{

    private List<T> dataList;
    protected Context context;
    protected CocoQuery q;
    protected View.OnClickListener onViewClickInListListener;
    private boolean loading = true;

    public TypeListAdapter(Context context, int layoutResourceId) {
        this(context, layoutResourceId, null);
    }

    public TypeListAdapter(Context context, int layoutResourceId, List<T> dataList) {
        super(context, layoutResourceId);
        this.context = context;
        if (dataList == null) {
            this.dataList = new ArrayList<T>();
        } else
            this.dataList = dataList;
        q = new CocoQuery(context);
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
    public void updateList(List<T> values) {
        this.dataList = values;
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
     * @param item
     *            Item to be verified whether it is in the adapter.
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
