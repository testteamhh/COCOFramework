package com.cocosw.framework.core.cursor;

import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
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
import com.cocosw.framework.core.BaseFragment;
import com.cocosw.framework.core.SystemBarTintManager;
import com.cocosw.framework.exception.CocoException;
import com.cocosw.framework.exception.ExceptionManager;
import com.cocosw.framework.log.Log;
import com.cocosw.framework.uiquery.CocoQuery;
import com.cocosw.framework.view.adapter.CocoAdapter;

/**
 * Base Fragment with GridView, same as ListFragment with less functions(because of the limitation of GridView)
 * Might combine with ListFragment someday. Let's see.
 * <p/>
 * <p/>
 * Project: cocoframework
 * User: Liao Kai(soarcn@gmail.com)
 * Date: 13-11-28
 * Time: 下午12:31
 */
public abstract class AdapterViewFragment<A extends AdapterView> extends BaseFragment<Cursor> implements
        AdapterView.OnItemClickListener, AbsListView.OnScrollListener {

    protected boolean listShown;

    View emptyView;
    Rect mInsets;
    /**
     * The actual adapter without any wrapper
     */
    CursorAdapter mAdapter;
    int lastVisibleItem = 0;
    private boolean updated;

    private A mListContainer;
    private View progressBar;

    private AbsListView.OnScrollListener externalListener;
    private Cursor cursor;

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
    public void onDestroy() {
        super.onDestroy();
        mAdapter = null;
    }

    protected boolean reloadNeeded(final Bundle savedInstanceState) {
        return savedInstanceState == null;
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
     * @return adapter
     * @throws Exception
     */
    protected abstract CursorAdapter createAdapter()
            throws Exception;

    /**
     * 设置如果列表为空时显示的View
     *
     * @return
     */
    protected View emptyView() {
        return LayoutInflater.from(context).inflate(R.layout.empty, null);
    }

    public CursorAdapter getAdapter() {
        return mAdapter;
    }

    protected View getEmptyView(final int layout, final int msg,
                                final int button, final View.OnClickListener listener) {
        final View view = LayoutInflater.from(context).inflate(layout, null);
        final CocoQuery nq = new CocoQuery(view);
        nq.id(R.id.empty_msg).text(msg);
        nq.id(R.id.empty_button).text(button).clicked(listener);
        return view;
    }


    public A getList() {
        return mListContainer;
    }

    protected AdapterViewFragment<A> hide(final View view) {
        ViewUtils.setGone(view, true);
        return this;
    }


    /**
     * 内容为空时显示的文字消息
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
     * 按了refresh后的反应
     */
    protected void refreshAction() {
        refresh();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {
        onStartLoading();
        return loader = new CursorLoader(context){
            @Override
            public Cursor loadInBackground() {
                cursor = pendingData(args);
                if (cursor!=null) {
                    cursor.getCount();
                    cursor.registerContentObserver(new ForceLoadContentObserver());
                }
                return cursor;
            }
        };
    }

    @Override
    public abstract Cursor pendingData(Bundle arg);

    /**
     * 初始化界面
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
            if (cursor.moveToPosition(position)) {
                onItemClick(cursor, position, id, view);
            }
        } catch (Exception e) {
            // for possible Cast exception, this will at least ensure the UI would not crash.
            Log.e(e);
        }
    }

    protected abstract void onItemClick(Cursor cursor, int pos, long id, View view);

    @Override
    public void onLoadFinished(final Loader<Cursor> loader, Cursor items) {
        mAdapter.swapCursor(items);
        cursor = items;
        onLoaderDone(items);
        showList();
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
        if (adapter instanceof BaseAdapter) {
            ((BaseAdapter) adapter).notifyDataSetChanged();
        }
    }


    /**
     * Set list shown or progress bar show
     *
     * @param shown
     * @return this fragment
     */

    /**
     * 刷新当前页面内容
     */
    @Override
    public void refresh(final Bundle b) {
        Log.i("页面有更新,刷新中");
        if (!isUsable()) {
            return;
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
    protected AdapterViewFragment<A> setListShown(final boolean shown) {
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

    @SuppressWarnings("rawtypes")
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
        mAdapter = createAdapter();
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

    protected AdapterViewFragment<A> show(final View view) {
        ViewUtils.setGone(view, false);
        return this;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (updated) {
            refresh();
            updated = false;
        }
    }

    /**
     * Set the list to be shown
     */
    protected void showList() {
        setListShown(true);
    }


    /**
     * 重新加载页面的数据
     *
     * @param force 是否强制刷新,否则需要判断是否有数据更新
     */
    public void reload(final boolean force) {
        if (force || updated) {
            refresh();
        }
    }

    @Override
    protected int getLoaderOn() {
        return BaseFragment.ONCREATE;
    }

    public static interface ItemViewClickLisener {

        void onItemViewClick(int position, View v);
    }
}