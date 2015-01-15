package com.cocosw.framework.core.recyclerview;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.cocosw.accessory.views.ViewUtils;
import com.cocosw.framework.R;
import com.cocosw.framework.core.AdapterViewFragment;
import com.cocosw.framework.core.BaseFragment;
import com.cocosw.framework.core.SystemBarTintManager;
import com.cocosw.framework.exception.CocoException;
import com.cocosw.framework.exception.ExceptionManager;
import com.cocosw.framework.log.Log;
import com.cocosw.framework.uiquery.CocoQuery;
import com.cocosw.framework.view.adapter.CocoAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Project: cocoframework
 * Created by LiaoKai(soarcn) on 2015/1/15.
 */
public abstract class RecyclerViewFragment<T, A extends RecyclerView> extends BaseFragment<List<T>> implements
        RecyclerView.OnItemTouchListener {


    private final static String DATA = "_adatperview_data";
    protected List<T> items;
    protected boolean listShown;

    View emptyView;
    Rect mInsets;
    /**
     * The actual adapter without any wrapper
     */
    RecyclerView.Adapter mAdapter;
    private boolean updated;

    private A mListContainer;
    private View progressBar;
    private RecyclerView.OnScrollListener externalListener;

    protected void setOnScrollListener(@NonNull final RecyclerView.OnScrollListener listener) {
        externalListener = listener;
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
        save(DATA, items);
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
    protected RecyclerView.Adapter wrapperAdapter(RecyclerView.Adapter adapter) {
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
     * 设置如果列表为空时显示的View
     *
     * @return
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
            return (T) getAdapter().getItem(position);
        } catch (Exception e) {
            return null;
        }
    }

    public A getList() {
        return mListContainer;
    }

    protected RecyclerViewFragment<T, A> hide(final View view) {
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
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (items.isEmpty()) {
            setListShown(false);
        }
    }

    @Override
    public void onDestroyView() {
        mAdapter = null;
        mListContainer = null;
        listShown = false;
        progressBar = null;
        emptyView = null;
        super.onDestroyView();
    }

    @Override
    public void onTouchEvent(RecyclerView recyclerView, MotionEvent e) {
        try {
            View view = mListContainer.findChildViewUnder(e.getX(), e.getY());
            int position = mListContainer.getChildPosition(view);
            final T item = getItem(position);
            if (item != null) {
                onItemClick(item, position, mAdapter.getItemId(position), view);
            }
        } catch (Exception ex) {
            // for possible Cast exception, this will at least ensure the UI would not crash.
            Log.e(ex);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
        return false;
    }

    /**
     * Handle item click event
     *
     * @param item
     * @param position
     * @param id
     * @param view
     */
    protected abstract void onItemClick(T item, int position, long id, View view);

    @Override
    public void onLoadFinished(final Loader<List<T>> loader, List<T> items) {
        final Exception exception = getException(loader);
        if (exception != null) {
            showError(exception);
            showList();
            return;
        }

        if (items == null)
            items = Collections.EMPTY_LIST;

        if (items != null && mAdapter != null) {
            ((CocoAdapter<?>) mAdapter).refresh();
            ((CocoAdapter<T>) mAdapter).add(items);
            updateAdapter();
        }
        onLoaderDone(items);
        showList();
    }

    /**
     * notifiy adapter to update
     */
    protected void updateAdapter() {
        RecyclerView.Adapter adapter = mListContainer.getAdapter();
        adapter.notifyDataSetChanged();
    }

    @Override
    public abstract List<T> pendingData(Bundle args) throws Exception;


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
        if (getLoaderManager().hasRunningLoaders() && loader != null) {
            loader.cancelLoad();
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
    protected RecyclerViewFragment<T, A> setListShown(final boolean shown) {
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
        if (this instanceof AdapterViewFragment.ItemViewClickLisener) {
            final View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (getList() == null) return;
                    final int position = getList().getChildPosition(v);
                    ((AdapterViewFragment.ItemViewClickLisener) RecyclerViewFragment.this).onItemViewClick(
                            position, v);
                }
            };
            ((CocoAdapter) mAdapter).setListWatch(listener);
        }
    }

    @Override
    protected void setupUI(final View view, final Bundle bundle) {
        try {
            mListContainer = view(R.id.listcontainer);
            mListContainer.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(final RecyclerView view,
                                                 final int scrollState) {
                    RecyclerViewFragment.this.onScrollStateChanged(view, scrollState);
                    if (externalListener != null) {
                        externalListener.onScrollStateChanged(view, scrollState);
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    RecyclerViewFragment.this.onScrolled(recyclerView, dx, dy);
                    if (externalListener != null) {
                        externalListener.onScrolled(recyclerView, dx, dy);
                    }
                }
            });
            progressBar = q.id(R.id.listprogressBar).getView();
            emptyView = q.id(R.id.empty).getView();
            if (emptyView != null) {
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
        mAdapter = (RecyclerView.Adapter) createAdapter(items);
        getList().setAdapter(wrapperAdapter(mAdapter));
    }

    protected void onScrollStateChanged(final RecyclerView view,
                                        final int scrollState) {

    }

    protected void onScrolled(RecyclerView recyclerView, int dx, int dy) {

    }

    protected void scrollUp(final int firstItem) {

    }

    protected void scrollDown(final int firstItem) {

    }

    protected void updateList(final List<T> items) {
        getAdapter().updateList(items);
    }


    protected RecyclerViewFragment<T, A> show(final View view) {
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

}
