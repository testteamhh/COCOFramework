package com.cocosw.framework.core.recyclerview;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
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
public abstract class RecyclerViewFragment<T, A extends RecyclerView> extends BaseFragment<List<T>> {


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
    protected RecyclerView.LayoutManager mLayoutManager;
    private LayoutManagerType mCurrentLayoutManagerType;
    private int SPAN_COUNT = 2;
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";

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
            return getAdapter().getItem(position);
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
        return R.layout.inc_recyclerview;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (items.isEmpty()) {
            setListShown(false);
        }
    }

    protected enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER, STAGGEREDGRID_LAYOUT_MANAGER,

    }

    /**
     * Set RecyclerView's LayoutManager to the one given.
     *
     * @param layoutManagerType Type of layout manager to switch to.
     */
    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (getList().getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) getList().getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                mLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT,orientation(),false);
                mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
                break;
            case STAGGEREDGRID_LAYOUT_MANAGER:
                mLayoutManager = new StaggeredGridLayoutManager(SPAN_COUNT,orientation());
                mCurrentLayoutManagerType = LayoutManagerType.STAGGEREDGRID_LAYOUT_MANAGER;
                break;
            default:
                mLayoutManager = new LinearLayoutManager(getActivity(),orientation(),false);
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }

        getList().setLayoutManager(mLayoutManager);
        getList().scrollToPosition(scrollPosition);
    }

    protected int orientation() {
        return OrientationHelper.VERTICAL;
    }

    @Override
    public void onDestroyView() {
        mListContainer = null;
        listShown = false;
        progressBar = null;
        emptyView = null;
        super.onDestroyView();
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

    /**
     * Handle item long click event
     *
     * @param item
     * @param position
     * @param id
     * @param view
     */
    protected void onItemLongClick(T item, int position, long id, View view) {

    }

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
            ((CocoAdapter<T>) mAdapter).updateList(items);
            updateAdapter();
        }
        onLoaderDone(items);
        showList();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, mCurrentLayoutManagerType);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * notifiy adapter to update
     */
    protected void updateAdapter() {
        getAdapter().notifyDataChange();
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

    protected LayoutManagerType layoutType() {
        return  LayoutManagerType.LINEAR_LAYOUT_MANAGER;
    }

    @Override
    protected void setupUI(final View view, final Bundle bundle) {
        try {
            mListContainer = view(R.id.list);
            mListContainer.addOnItemTouchListener(new ClickItemTouchListener(mListContainer) {

                @Override
                boolean performItemClick(RecyclerView parent, View view, int position, long id) {
                    view.playSoundEffect(SoundEffectConstants.CLICK);
                    onItemClick(getItem(position), position, id, view);
                    return true;
                }


                @Override
                boolean performItemLongClick(RecyclerView parent, View view, int position, long id) {
                    if (mListContainer.isLongClickable()) {
                        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                        onItemLongClick(getItem(position),position,id,view);
                    }
                    return false;
                }
            });
            if (bundle != null) {
                // Restore saved layout manager type.
                mCurrentLayoutManagerType = (LayoutManagerType) bundle
                        .getSerializable(KEY_LAYOUT_MANAGER);
            } else
                mCurrentLayoutManagerType = layoutType();

            setRecyclerViewLayoutManager(mCurrentLayoutManagerType);
            mListContainer.setItemAnimator(new DefaultItemAnimator());
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
