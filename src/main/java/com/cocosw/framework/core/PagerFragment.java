package com.cocosw.framework.core;

import android.support.v4.app.Fragment;
import com.actionbarsherlock.app.ActionBar;
import com.cocosw.accessory.views.CocoPager;
import com.cocosw.framework.R;

/**
 * 用于在主Main act上使用的Fragment 共享了一部分Main的资源
 * 
 * @author Administrator
 */
public abstract class PagerFragment<T, K extends Carousel> extends
		BaseFragment<T> implements Pager<Carousel> {

	private CocoPager pager;
	int id;
	private K source;

	@Override
	public void onShake() {
		// TODO Auto-generated method stub

	}

	@Override
	public void editMode() {
		// q.toast("编辑模式");
	}

	@Override
	public void viewMode() {
		// q.toast("浏览模式");
	}

	@Override
	public void onPagerSelected() {
		q.v(this.getClass().getSimpleName() + " onPagerSelected");
		// q.toast(this);
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

	@Override
	public ActionBar actionBar() {
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

	@Override
	public Pager<?> nextPager() {
		return source.nextPager(id);
	}

	@Override
	public Pager<?> prevPager() {
		return source.prevPager(id);
	}

}
