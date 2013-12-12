package com.cocosw.framework.view.adapter;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import com.cocosw.framework.uiquery.CocoQuery;
import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Administrator
 * Date: 13-7-17
 * Time: 下午6:32
 */
public abstract class MultiTypeListAdatper extends MultiTypeAdapter implements
        CocoAdapter<Object>{

    private List<Object> dataList;
    protected Context context;
    protected CocoQuery q;
    protected View.OnClickListener onViewClickInListListener;
    private boolean loading = true;

    public MultiTypeListAdatper(Context context) {
        this(context, null);
    }

    public MultiTypeListAdatper(Context context, List<Object> dataList) {
        super(context);
        this.context = context;
        if (dataList == null) {
            this.dataList = new ArrayList<Object>();
        } else
            this.dataList = dataList;
        q = new CocoQuery(context);
    }

    /**
     * get object item type
     * @param obj
     * @return
     */
    public abstract int getType(Object obj);


    /**
     * 往数据后面加入数据
     *
     * @param values
     */
    @Override
    public void add(final List<Object> values) {
        if (values != null&&values.size()>0) {
            this.dataList.addAll(values);
            for (Object obj:values) {
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
    public void add(final Object value) {
        if (value != null) {
            this.dataList.add(value);
            addItem(getType(value),value);
        }
    }

    /**
     * 往数据前面加入数据
     *
     * @param values
     */
    @Override
    public void append(final List<Object> values) {
        this.dataList.addAll(values);
    }

    /**
     * 往数据前面加入数据
     *
     * @param values
     */
    @Override
    public void append(final Object values) {
        this.dataList.add(values);
    }

    @Override
    public void updateList(List<Object> values) {
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
    public boolean contains(final Object item) {
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

    protected List<Object> getDataList() {
        return dataList;
    }

}
