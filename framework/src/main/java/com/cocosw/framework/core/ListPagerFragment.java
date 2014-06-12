package com.cocosw.framework.core;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.actionbarsherlock.app.ActionBar;
import com.cocosw.accessory.views.CocoPager;
import com.cocosw.framework.R;


/**
 * 适用于主体是ListView的pagerFragment
 *
 * @author Administrator
 *
 */

/**
 * @param <K>
 * @author Administrator
 */
public abstract class ListPagerFragment<T, K extends Carousel> extends
        ListFragment<T> implements Pager<Carousel> {

    private CocoPager pager;
    int id;
    private K source;

    protected static final int ONSELECTED = 1;
    protected boolean updated;

    @Override
    public void onPagerSelected() {
        if (getLoaderOn() == ListPagerFragment.ONSELECTED) {
            if (getLoader() == null) {
                onStartLoading();
                getLoaderManager().initLoader(this.hashCode(), getArguments(), this);
            }
        }
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        if (updated) {
            refresh();
            updated = false;
        }
    }

    @Override
    protected void setupUI(final View view, final Bundle bundle) {
        super.setupUI(view, bundle);
        if (actionBarAutoHide() == false
                && getSherlockActivity().getSupportActionBar() != null
                && !getSherlockActivity().getSupportActionBar().isShowing()) {
            getSherlockActivity().getSupportActionBar().show();
        }
    }

    protected void navigate(final Class<? extends Fragment> clz) {
        getSource().nav(clz);
    }

    @Override
    public CocoPager getPager() {
        return pager;
    }

    @Override
    public void setPager(final CocoPager pager) {
        this.pager = pager;
    }

    private ActionBar actionBar() {
        return getSherlockActivity().getSupportActionBar();
    }

    /**
     * 首次使用本应用的独立入口，page被select的时候触发，可以用于帮助
     */
    @Override
    public void first() {
        // TODO Auto-generated method stub

    }

    @Override
    public int title() {
        return R.string.homepage;
    }

    /**
     * 判断自身是否是当前正在被Main使用的Fragment
     *
     * @return
     */
    public boolean isCurrentFragment() {
        return this == getSource().getCurrentPager();
    }

    /**
     * 获得当前fragment的id,可以表示其所在的位置
     *
     * @return
     */
    @Override
    public int getID() {
        return id;
    }

    @Override
    public void setID(final int id) {
        this.id = id;
    }

    /**
     * 获得pager的共享资源
     *
     * @return
     */
    @Override
    public K getSource() {
        return source;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setSource(final Carousel s) {
        source = (K) s;
    }

    protected boolean actionBarAutoHide() {
        return false;
    }

    @Override
    protected void scrollUp(final int firstItem) {
        if (actionBar() == null || !actionBarAutoHide()) {
            return;
        }
        if (actionBar().isShowing() && firstItem >= 1) {
            actionBar().hide();
        }
    }

    @Override
    protected void scrollDown(final int firstItem) {
        if (actionBar() == null || !actionBarAutoHide()) {
            return;
        }
        if (!actionBar().isShowing()) {
            actionBar().show();
        }
    }

    @Override
    public Pager<?> nextPager() {
        return source.nextPager(id);
    }

    @Override
    public Pager<?> prevPager() {
        return source.prevPager(id);
    }
}