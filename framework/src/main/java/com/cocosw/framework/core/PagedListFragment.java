package com.cocosw.framework.core;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;

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
public abstract class PagedListFragment<T> extends ListFragment<T> {

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
    public CocoAdapter<T> getAdapter() {
        return (CocoAdapter<T>) mAdapter;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TIME, time);
        outState.putSerializable(ENDED, ended);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState!=null) {
            time = savedInstanceState.getInt(TIME);
            ended = (AtomicBoolean) savedInstanceState.getSerializable(ENDED);
            if (ended==null)
                ended = new AtomicBoolean(false);
        }
    }

    @Override
    public void onLoadFinished(final Loader<List<T>> loader, final List<T> items) {
        final Exception exception = getException(loader);
        if (refresh != null) {
            refresh.setActionView(null);
        }
        if (exception != null) {
            ended.set(true);
            showError(exception);
            showList();
            return;
        }
        if (items != null && mAdapter != null && !items.isEmpty()) {
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
    public List<T> pendingData(Bundle args) throws Exception {
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
    private MenuItem refresh;

    @Override
    public void onScroll(final AbsListView view, final int firstVisibleItem,
                         final int visibleItemCount, final int totalItemCount) {
        super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        if (ended.get()) {
            return;
        }
        if (getLoaderManager().hasRunningLoaders()) {
            return;
        }
        if (getList() != null && wrapAdapter != null) {
            if (getList().getLastVisiblePosition() + 1 >= wrapAdapter.getCount()) {
                loadmore();
            }
        }
    }

    private void loadmore() {
        //q.v("start loading more");
        ended.set(false);
        onStartLoading();
        getLoaderManager()
                .restartLoader(
                        0,
                        CocoBundle.builder(bundel).setIndex(getLastIndex())
                                .getBundle(), this
                );
    }

    @Override
    protected void refreshAction() {
        // ended.set(true);
        getList().setSelection(0);
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
        if (loadview == null && getHeaderAdapter() != null) {
            loadview = getLoadingView();
            getHeaderAdapter().addFooter(loadview);
        }
    }

    @Override
    protected void hideLoading() {
        if (loadview != null) {
            getHeaderAdapter().removeFooter(loadview);
        }
        loadview = null;
    }

    protected Long getLastIndex() {
        return (long) getAdapter().getCount();
    }

    protected int pagedSize(final int time) {
        return 10;
    }

//    @Override
//    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
//        refresh = menu.add(0, 999, 0, R.string.refresh).setIcon(
//                R.drawable.ic_action_refresh);
//        refresh.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(final MenuItem item) {
//        if (item.getItemId() == 999) {
//            refresh();
//            return true;
//        }
//        return false;
//    }

    @Override
    protected void onStartLoading() {
        if (refresh != null) {
            refresh.setActionView(R.layout.indeterminate_progress_action);
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
