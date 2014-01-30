package com.cocosw.framework.core;

import com.actionbarsherlock.app.ActionBar;
import com.cocosw.accessory.views.CocoPager;

public interface Pager<K extends Carousel> {

	public int title();

	public void setPager(final CocoPager pager);

	public CocoPager getPager();

	public void first();

	/**
	 * 获得当前fragment的id,可以表示其所在的位置
	 * 
	 * @return
	 */
	public int getID();

	public void setID(int id);

	/**
	 * 下一个Pager
	 * 
	 * @return
	 */
	public Pager<?> nextPager();

	/**
	 * 上一个pager
	 * 
	 * @return
	 */
	public Pager<?> prevPager();

	K getSource();

	void setSource(K s);

	public void onPagerSelected();

}
