package com.cocosw.framework.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.cocosw.framework.uiquery.CocoQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for viewpager with view recycle
 * <p/>
 * Project: app-parent
 * User: Liao Kai(soarcn@gmail.com)
 * Date: 13-12-12
 * Time: 下午4:41
 */
public abstract class CocoPagerAdapter<T> extends RecyclingPagerAdapter implements CocoAdapter<T> {

    private final LayoutInflater mInflater;
    private List<T> dataList;
    protected Context context;
    protected CocoQuery q;
    protected View.OnClickListener onViewClickInListListener;
    private boolean loading = true;

    /**
     * 默认会构造为arraylist以后可以换
     */
    public CocoPagerAdapter(final Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.dataList = new ArrayList<T>();
        q = new CocoQuery(context);
    }

    /**
     * @param dataList
     */
    public CocoPagerAdapter(final Context context, List<T> dataList) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        if (dataList == null) {
            dataList = new ArrayList<T>(0);
        }
        this.dataList = dataList;
        q = new CocoQuery(context);
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
        ViewHolder holder = null;
        if (convertView == null) {
            holder = newView(position);
            holder.contentView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        q.recycle(holder.contentView);
        fillView(holder, position, parent);
        return holder.contentView;
    }

    protected Context getContext() {
        return context;
    }

    // 创建一个新的View
    public abstract ViewHolder newView(int position);

    // 填充这个View
    public abstract void fillView(ViewHolder viewHolder, int position,
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

    /**
     * 通知UI更新
     */
    @Override
    public void notifyDataChange() {
        this.loading = false;
        notifyDataSetChanged();
    }

    @Override
    public void remove(int position) {
        this.dataList.remove(position);
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
        getDataList().clear();
    }

    @Override
    public boolean isEmpty() {
        return getDataList().size() == 0 & !loading;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
