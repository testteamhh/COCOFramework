package com.cocosw.framework.core;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.cocosw.accessory.views.CocoBundle;
import com.cocosw.framework.R;
import com.cocosw.framework.exception.CocoException;
import com.cocosw.framework.view.adapter.CocoAdapter;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Paged ListFragment
 * <p/>
 * Project: cocoframework
 * User: Liao Kai(soarcn@gmail.com)
 * Date: 13-12-19
 * Time: 下午9:35
 */
public abstract class PagedListFragment<T> extends ListFragment<T> {

    protected final AtomicBoolean ended = new AtomicBoolean(false);
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
        if (items != null && mAdapter != null) {
            getAdapter().add(items);
        }
        updateAdapter();
        onLoaderDone(items);

        showList();

        showLoading();
        if (items == null || items.size() < pagedSize(time)) {
            ended.set(true);
            hideLoading();
        }
        time++;
    }

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
        if (getList().getLastVisiblePosition() + 1 >= wrapAdapter.getCount()) {
            loadmore();
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
                                .getBundle(), this);
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

    protected void showLoading() {
        if (loadview == null && getHeaderAdapter() != null) {
            loadview = LayoutInflater.from(context).inflate(R.layout.footer,
                    null);
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

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        refresh = menu.add(0, 999, 0, R.string.refresh).setIcon(
                R.drawable.ic_action_ic_action_refresh);
        refresh.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == 999) {
            refresh();
            return true;
        }
        return false;
    }

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
}
