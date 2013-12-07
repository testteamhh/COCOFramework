package com.cocosw.framework.core;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import com.cocosw.accessory.views.HeaderFooterListAdapter;
import com.cocosw.accessory.views.ItemViewClickLisener;
import com.cocosw.framework.R;
import com.cocosw.framework.R.id;
import com.cocosw.framework.exception.CocoException;
import com.cocosw.framework.log.Log;
import com.cocosw.framework.uiquery.CocoQuery;
import com.cocosw.framework.view.adapter.CocoAdapter;
import com.github.kevinsawicki.wishlist.ViewUtils;

import java.util.Collections;
import java.util.List;

public abstract class ListFragment<T> extends BaseFragment<List<T>> implements
		OnItemClickListener, OnScrollListener {

	private boolean updated;
	private View emptyView;
	private View footerView;
	private View headerView;
	protected List<T> items = Collections.emptyList();
	protected boolean listShown;

	/**
	 * The actual adapter without any wrapper
	 */
    BaseAdapter mAdapter;
	HeaderFooterListAdapter<BaseAdapter> wrapAdapter;

	private ListView mListContainer;
	private View progressBar;

	protected void setOnScrollListener(final OnScrollListener listener) {
		q.id(R.id.list).scrolled(listener);
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
    private HeaderFooterListAdapter<BaseAdapter> createAdapter()
            throws Exception {
        mAdapter = (BaseAdapter) createAdapter(items);
        wrapAdapter = new HeaderFooterListAdapter<BaseAdapter>(
                mListContainer, wrapperAdapter(mAdapter));
        return wrapAdapter;
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
	public View emptyView() {
		return LayoutInflater.from(context).inflate(R.layout.empty, null);
	}

	public CocoAdapter<T> getAdapter() {
		return (CocoAdapter<T>) mAdapter;
	}

	protected View getEmptyView(final int layout, final int msg,
			final int button, final OnClickListener listener) {
		final View view = LayoutInflater.from(context).inflate(layout, null);
		final CocoQuery nq = new CocoQuery(view).id(R.id.empty_msg).text(msg);
		nq.id(R.id.empty_button).text(button).clicked(listener);
		return view;
	}

	public View getFooterView() {
		return footerView;
	}

	/**
	 * Get list adapter
	 * 
	 * @return list adapter
	 */
	protected HeaderFooterListAdapter<BaseAdapter> getHeaderAdapter() {
		if (mListContainer != null) {
			return wrapAdapter;
		} else {
			return null;
		}
	}

	public View getHeaderView() {
		return headerView;
	}

	protected T getItem(final int position) {
		return (T) wrapAdapter.getItem(position);
	}

	public ListView getList() {
		return mListContainer;
	}

	private ListFragment<T> hide(final View view) {
		ViewUtils.setGone(view, true);
		return this;
	}

	@Override
	protected void showRefresh(final CocoException e) {
		setFooter(R.layout.refreshview);
		((TextView) getFooterView().findViewById(id.empty_msg))
				.setText(refreshText(e));
		getFooterView().findViewById(id.empty_image).setVisibility(View.GONE);
		getFooterView().findViewById(id.empty_button).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(final View arg0) {
						refreshAction();
						getHeaderAdapter().clearFooters();
					}
				});
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
		return R.layout.inc_progresslist;
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
		headerView = null;
		footerView = null;
		listShown = false;
		progressBar = null;
		emptyView = null;
		super.onDestroyView();
	}

	@Override
	public void onItemClick(final AdapterView<?> parent, final View view,
			final int position, final long id) {
		final T item = getItem(position);
        //q.v(item);
		if (item != null) {
			onItemClick(item, position, id, view);
		}
	}

	protected abstract void onItemClick(T item, int pos, long id, View view);

	@Override
	public void onLoadFinished(final Loader<List<T>> loader, final List<T> items) {
		final Exception exception = getException(loader);
		if (exception != null) {
			showError(exception);
			showList();
			return;
		}
		if (items != null && mAdapter != null) {
            ((CocoAdapter)mAdapter).add(items);
            ((CocoAdapter)mAdapter).notifyDataChange();
		}
		onLoaderDone(items);
		showList();
	}

	@Override
	public abstract List<T> pendingData(Bundle args) throws Exception;

	/**
	 * 刷新当前页面内容
	 */
	@Override
	public void refresh() {
		Log.d("页面有更新,刷新中");
		if (!isUsable()) {
			return;
		}
		if (getLoaderManager().hasRunningLoaders()) {
			loader.cancelLoad();
		}

		hide(emptyView);
		items.clear();
		((CocoAdapter<?>) mAdapter).refresh();
		setListShown(false);
		super.refresh();
	}

	/**
	 * 设置Footerview
	 * 
	 * @param uiRes
	 */
	protected void setFooter(final int uiRes) {
		final View view = LayoutInflater.from(context).inflate(uiRes, null);
		getHeaderAdapter().clearFooters();
		getHeaderAdapter().addFooter(view);
		footerView = view;
	}

	/**
	 * 设置Headview
	 * 
	 * @param uiRes
	 */
	protected void setHeader(final int uiRes) {
		final View view = LayoutInflater.from(context).inflate(uiRes, null);
		getHeaderAdapter().clearHeaders();
		getHeaderAdapter().addHeader(view);
		headerView = view;
	}

	/**
	 * Set list shown or progress bar show
	 * 
	 * @param shown
	 * @return this fragment
	 */
	/**
	 * Set list shown or progress bar show
	 * 
	 * @param shown
	 * @return this fragment
	 */
	protected ListFragment<T> setListShown(final boolean shown) {
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
			final OnClickListener listener = new OnClickListener() {

				@Override
				public void onClick(final View v) {
					final int position = getList().getPositionForView(v);
					((ItemViewClickLisener) ListFragment.this).onItemViewClick(
							position, v);
				}
			};
			((CocoAdapter) mAdapter).setListWatch(listener);
		}
	}

	@Override
	protected void setupUI(final View view, final Bundle bundle) {
		try {
			mListContainer = (ListView) q.id(id.list).itemClicked(this)
					.scrolled(this).getView();
			progressBar = q.id(id.listprogressBar).getView();
			emptyView = q.id(id.empty).getView();
			if (emptyView != null) {
				getList().setEmptyView(emptyView);
				((FrameLayout) emptyView).addView(emptyView());
			}

			mListContainer.setAdapter(createAdapter());
			setOnViewClickInList();
			init(view, bundle);
			q.recycle(view);
		} catch (final Exception e) {
			Log.d("exception in" + this.getClass().getSimpleName());
			e.printStackTrace();
			getActivity().finish();
		}
	}

	@Override
	public void onScrollStateChanged(final AbsListView view,
			final int scrollState) {
		// TODO 自动生成的方法存根

	}

	int lastVisibleItem = 0;

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
	}

	protected void scrollUp(final int firstItem) {

	}

	protected void scrollDown(final int firstItem) {

	}

	protected void updateList(final List<T> items) {
        ((CocoAdapter)getHeaderAdapter().getWrappedAdapter()).updateList(items);
	}

	private ListFragment<T> show(final View view) {
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
	 * 标记list的背景数据已经被更新,将会在下次被选中的时候刷新页面
	 */
	protected void listUpdated() {
		updated = true;
	}

	/**
	 * 重新加载页面的数据
	 * 
	 * @param force
	 *            是否强制刷新,否则需要判断是否有数据更新
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
