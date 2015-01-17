package com.cocosw.framework.core.recyclerview;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import com.cocosw.accessory.views.CocoBundle;
import com.cocosw.framework.R;
import com.cocosw.framework.exception.CocoException;
import com.cocosw.framework.view.adapter.CocoAdapter;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Paged ListFragment
 * <p/>
 * With a endless listview within, progress view and empty view is also supported.
 * All your code in listFragment can be easily used in this class.
 * <p/>
 * <p/>
 * Project: cocoframework
 * User: Liao Kai(soarcn@gmail.com)
 * Date: 13-12-19
 * Time: 下午9:35
 */
public abstract class PagedRecyclerViewFragment<T, A extends RecyclerView> extends RecyclerViewFragment<T, A> {

    private static final String TIME = "_pagedlist_time";
    private static final String ENDED = "_pagedlist_ended";
    protected AtomicBoolean ended = new AtomicBoolean(false);
    protected int time = 0;
    private View loadview = null;

    @Override
    protected void setupUI(final View view, final Bundle bundle) {
        super.setupUI(view, bundle);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        save(TIME, time);
        save(ENDED, ended);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ended = load(ENDED);
        if (ended == null) {
            ended = new AtomicBoolean(false);
            time = 0;
        } else {
            time = load(TIME);
        }
    }

    @Override
    public void onLoadFinished(final Loader<List<T>> loader, final List<T> items) {
        final Exception exception = getException(loader);
        onStopLoading();
        if (refresh != null) {
            MenuItemCompat.setActionView(refresh,null);
        }
        if (exception != null) {
            ended.set(true);
            showError(exception);
            showList();
            return;
        }
        if (items != null && mAdapter != null && !items.isEmpty()) {
            if (time == 0)
                getAdapter().refresh();
            getAdapter().add(items);
        }
        showList();
        updateAdapter();
        onLoaderDone(items);

        showLoading();
        if (items == null || items.size() < pagedSize(time)) {
            ended.set(true);
            onAllDataLoaded();
            hideLoading();
        }

        time++;
    }


    @Override
    public final List<T> pendingData(Bundle args) throws Exception {
        return pendingPagedData(getIndex(args), time, pagedSize(time), args);
    }

    /**
     * Data will be load in each pages
     *
     * @param index current index, used for start point, can be changed in getLastIndex()
     * @param time  batch number, or the page number.
     * @param size  how many items need to be load, can be changed in pagedSize()
     * @param args  @return
     * @throws Exception
     */
    public abstract List<T> pendingPagedData(long index, int time, int size, Bundle args) throws Exception;

    @Override
    protected void showRefresh(final CocoException e) {
        super.showRefresh(e);
    }

    @Override
    protected CocoAdapter<T> createAdapter(final List<T> items)
            throws Exception {
        return createEndlessAdapter(items);
    }

    protected abstract CocoAdapter<T> createEndlessAdapter(List<T> items)
            throws Exception;

    private final Bundle bundel = new Bundle();
    protected MenuItem refresh;

    @Override
    protected void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (ended.get()) {
            return;
        }
        if (isLoaderRunning()) {
            return;
        }
        if (getList() != null) {
            if (getLastVisiblePostion(mLayoutManager)+ 1 >= getAdapter().getCount()) {
                loadmore();
            }
        }
    }

    /**
     * Get the position of last visible view item
     *
     * @param mLayoutManager
     * @return
     */
    protected int getLastVisiblePostion(RecyclerView.LayoutManager mLayoutManager) {
        if (mLayoutManager instanceof LinearLayoutManager) {
            return ((LinearLayoutManager)mLayoutManager).findLastVisibleItemPosition();
        } else
        if (mLayoutManager instanceof StaggeredGridLayoutManager) {
            int[] out = ((StaggeredGridLayoutManager)mLayoutManager).findLastVisibleItemPositions(null);
            if (out.length>0) return out[0];
        }
        return -1;
    }


    private void loadmore() {
        //q.v("start loading more");
        ended.set(false);
        onStartLoading();
        getLoaderManager()
                .restartLoader(
                        this.hashCode(),
                        CocoBundle.builder(bundel).setIndex(getLastIndex())
                                .getBundle(), this
                );
    }

    @Override
    protected void refreshAction() {
        getList().smoothScrollToPosition(0);
        showLoading();
        loadmore();
    }

    @Override
    public void refresh() {
        time = 0;
        super.refresh();
    }

    /**
     * This will be called when all data has been loaded
     */
    protected void onAllDataLoaded() {
    }

    protected void showLoading() {
//        if (loadview == null && getHeaderAdapter() != null) {
//            loadview = getLoadingView();
//            getHeaderAdapter().addFooter(loadview);
//        }
    }

    @Override
    protected void hideLoading() {
//        if (loadview != null) {
//            getHeaderAdapter().removeFooter(loadview);
//        }
        loadview = null;
    }

    protected Long getLastIndex() {
        return (long) getAdapter().getCount();
    }

    protected int pagedSize(final int time) {
        return 10;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (refresh != null) {
            MenuItemCompat.setActionView(refresh,R.layout.indeterminate_progress_action);
        }
    }

    /**
     * helper method for getting index
     *
     * @param bundel
     * @return
     */
    protected long getIndex(Bundle bundel) {
        return CocoBundle.builder(bundel).getIndex();
    }

    /**
     * the loading view in footer area
     *
     * @return
     */
    protected View getLoadingView() {
        return LayoutInflater.from(context).inflate(R.layout.footer, null);
    }
}
