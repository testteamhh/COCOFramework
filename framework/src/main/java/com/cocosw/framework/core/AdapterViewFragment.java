package com.cocosw.framework.core;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

import com.cocosw.accessory.views.ViewUtils;
import com.cocosw.accessory.views.adapter.HeaderFooterListAdapter;
import com.cocosw.framework.R;
import com.cocosw.framework.exception.CocoException;
import com.cocosw.framework.exception.ExceptionManager;
import com.cocosw.framework.loader.ThrowableLoader;
import com.cocosw.framework.log.Log;
import com.cocosw.framework.uiquery.CocoQuery;
import com.cocosw.framework.view.adapter.CocoAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base Fragment with GridView, same as ListFragment with less functions(because of the limitation of GridView)
 * Might combine with ListFragment someday. Let's see.
 * <p/>
 * <p/>
 * Project: cocoframework
 * User: Liao Kai(soarcn@gmail.com)
 * Date: 13-11-28
 */
public abstract class AdapterViewFragment<T, A extends AdapterView> extends BaseFragment<List<T>> implements
        AdapterView.OnItemClickListener, AbsListView.OnScrollListener {


    private final static String DATA = "_adatperview_data";
    protected List<T> items;
    protected boolean listShown;

    View emptyView;
    /**
     * The actual adapter without any wrapper
     */
    BaseAdapter mAdapter;
    int lastVisibleItem = 0;

    private A mListContainer;
    private View progressBar;

    private AbsListView.OnScrollListener externalListener;

    protected void setOnScrollListener(@NonNull final AbsListView.OnScrollListener listener) {
        this.externalListener = listener;
    }


    @Override
    public void onInsetsChanged(SystemBarTintManager.SystemBarConfig insets) {
        if (mListContainer == null) return;
        mListContainer.setClipToPadding(false);
        mListContainer.setPadding(
                0, insets.getPixelInsetTop(hasActionBarBlock())
                , insets.getPixelInsetRight(), insets.getPixelInsetBottom()
        );
    }

    @Override
    public Loader<List<T>> onCreateLoader(int id, Bundle args) {
        setListShown(false);
        return super.onCreateLoader(id, args);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        save(DATA, getAdapter().getItems());
        mAdapter = null;
    }

    protected boolean reloadNeeded(final Bundle savedInstanceState) {
        return savedInstanceState == null;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        items = load(DATA);
        if (items == null)
            items = new ArrayList<>();
    }

    /**
     * If you need to wrap the adpter, this is the interface you are looking for
     *
     * @param adapter
     * @return
     */
    protected BaseAdapter wrapperAdapter(BaseAdapter adapter) {
        return adapter;
    }

    /**
     * Create adapter to display items
     *
     * @param items
     * @return adapter
     * @throws Exception
     */
    protected abstract CocoAdapter<T> createAdapter(final List<T> items)
            throws Exception;

    /**
     * The emptyview which will be shown if list is empty
     *
     * @return emptyview
     */
    protected View emptyView() {
        return LayoutInflater.from(context).inflate(R.layout.empty, null);
    }

    public CocoAdapter<T> getAdapter() {
        return (CocoAdapter<T>) mAdapter;
    }

    protected View getEmptyView(final int layout, final int msg,
                                final int button, final View.OnClickListener listener) {
        final View view = LayoutInflater.from(context).inflate(layout, null);
        final CocoQuery nq = new CocoQuery(view);
        nq.id(R.id.empty_msg).text(msg);
        nq.id(R.id.empty_button).text(button).clicked(listener);
        return view;
    }


    protected T getItem(final int position) {
        try {
            return (T) getList().getAdapter().getItem(position);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public A getList() {
        return mListContainer;
    }

    protected AdapterViewFragment<T, A> hide(final View view) {
        ViewUtils.setGone(view, true);
        return this;
    }


    /**
     * Text will be shown if exception is got or list is empty
     *
     * @param e
     * @return
     */
    protected String refreshText(final CocoException e) {
        if (e != null) {
            return e.getMessage();
        }
        return getString(R.string.empty_list);
    }

    /**
     * Action when user press refresh button
     */
    protected void refreshAction() {
        refresh();
    }

    /**
     * Ui init
     *
     * @param view
     * @param bundle
     * @throws Exception
     */
    protected abstract void init(View view, Bundle bundle) throws Exception;

    @Override
    public int layoutId() {
        return R.layout.inc_progressgrid;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (items.isEmpty()) {
            setListShown(false);
        }
    }

    @Override
    public void onDestroyView() {
        mListContainer = null;
        listShown = false;
        progressBar = null;
        emptyView = null;
        super.onDestroyView();
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, final View view,
                            final int position, final long id) {
        try {
            final T item = getItem(position);
            if (item != null) {
                onItemClick(item, position, id, view);
            }
        } catch (Exception e) {
            // for possible Cast exception, this will at least ensure the UI would not crash.
            Log.e(e);
        }
    }

    protected abstract void onItemClick(T item, int pos, long id, View view);

    @Override
    public void onLoadFinished(final Loader<List<T>> loader, List<T> items) {
        final Exception exception = getException(loader);
        if (exception != null) {
            showError(exception);
            showList();
            onStopLoading();
            return;
        }

        if (items == null)
            items = Collections.EMPTY_LIST;

        if (items != null && mAdapter != null) {
            ((CocoAdapter<T>) mAdapter).updateList(items);
            updateAdapter();
        }
        onLoaderDone(items);
        showList();
        onStopLoading();
    }

    /**
     * notifiy adapter to update
     */
    protected void updateAdapter() {
        Adapter adapter = mListContainer.getAdapter();
        if (adapter instanceof HeaderFooterListAdapter) {
            ((HeaderFooterListAdapter) adapter).getWrappedAdapter().notifyDataSetChanged();
            return;
        }
        if (adapter instanceof CocoAdapter) {
            ((CocoAdapter) adapter).notifyDataChange();
            return;
        }
    }

    @Override
    public abstract List<T> pendingData(Bundle args) throws Exception;


    /**
     * Reload current loader with arguments
     *
     * @param b arguments
     */
    @Override
    public void refresh(final Bundle b) {
        if (!isUsable()) {
            return;
        }
        if (isLoaderRunning() && loader != null) {
            if (loader instanceof ThrowableLoader) {
                ((ThrowableLoader<T>) loader).cancelLoad();
            }
        }
        if (hideListWhenRefreshing()) {
            hide(emptyView);
            setListShown(false);
        }
        super.refresh(b);
    }

    protected boolean hideListWhenRefreshing() {
        return true;
    }


    /**
     * Set list shown or progress bar show
     *
     * @param shown
     * @return this fragment
     */
    protected AdapterViewFragment<T, A> setListShown(final boolean shown) {
        if (!isUsable()) {
            return this;
        }
        listShown = shown;
        if (shown) {
            hide(progressBar).show(mListContainer);
        } else {
            hide(mListContainer).show(progressBar);
        }

        return this;
    }

    protected void setOnViewClickInList() {
        if (this instanceof ItemViewClickLisener) {
            final View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (getList() == null) return;
                    final int position = getList().getPositionForView(v);
                    ((ItemViewClickLisener) AdapterViewFragment.this).onItemViewClick(
                            position, v);
                }
            };
            ((CocoAdapter) mAdapter).setListWatch(listener);
        }
    }

    @Override
    protected void setupUI(final View view, final Bundle bundle) {
        try {
            mListContainer = q.id(R.id.list).itemClicked(this).scrolled(this).getView();
            progressBar = q.id(R.id.listprogressBar).getView();
            emptyView = q.id(R.id.empty).getView();
            if (emptyView != null) {
                getList().setEmptyView(emptyView);
                ((FrameLayout) emptyView).addView(emptyView());
            }
            constractAdapter();
            setOnViewClickInList();
            init(view, bundle);
            q.recycle(view);
        } catch (final Exception e) {
            ExceptionManager.error(e, context, this);
            getActivity().finish();
        }
    }

    protected void constractAdapter() throws Exception {
        mAdapter = (BaseAdapter) createAdapter(items);
        getList().setAdapter(wrapperAdapter(mAdapter));
    }

    @Override
    public void onScrollStateChanged(final AbsListView view,
                                     final int scrollState) {
        if (externalListener != null) {
            externalListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(final AbsListView view, final int firstVisibleItem,
                         final int visibleItemCount, final int totalItemCount) {
        if (visibleItemCount > 0) {
            if (firstVisibleItem < lastVisibleItem) {
                scrollDown(firstVisibleItem);
            } else if (firstVisibleItem > lastVisibleItem) {
                scrollUp(totalItemCount);
            }
            lastVisibleItem = firstVisibleItem;
        }
        if (externalListener != null) {
            externalListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    protected void scrollUp(final int firstItem) {

    }

    protected void scrollDown(final int firstItem) {

    }

    protected AdapterViewFragment<T, A> show(final View view) {
        ViewUtils.setGone(view, false);
        return this;
    }


    /**
     * Set the list to be shown
     */
    protected void showList() {
        setListShown(true);
    }



    @Override
    protected int getLoaderOn() {
        return BaseFragment.ONCREATE;
    }

    public static interface ItemViewClickLisener {

        void onItemViewClick(int position, View v);
    }
}
