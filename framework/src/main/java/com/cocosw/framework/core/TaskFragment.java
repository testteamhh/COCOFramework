package com.cocosw.framework.core;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.cocosw.framework.R;
import com.cocosw.framework.uiquery.CocoQueryCallBack;
import com.cocosw.framework.uiquery.SimpleCallBack;


/**
 * 适用于没有UI的Fragment，可以抽象一些复杂的task
 * 
 * @author soar
 * 
 */
public abstract class TaskFragment<T, V> extends BaseFragment<T> {

	private CocoQueryCallBack<V> callback;

	@Override
	final public int layoutId() {
		// 不需要
		return R.layout.activity_singlepane_empty;
	}

	@Override
	final protected void setupUI(final View view, final Bundle bundle)
			throws Exception {
		// 不需要
	}

	@Override
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {
		return null;
	}

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getTargetFragment() != null) {
			q.recycle(getTargetFragment().getView());
		}
		work(getArguments());
	}

	protected abstract void work(final Bundle b);

	protected void run(final Fragment f, final Bundle b,
			final CocoQueryCallBack<V> callback) {
		final TaskFragment<?, ?> lasttask = TaskFragment.getTaskFragment(f
				.getFragmentManager());
		if (lasttask != null) {
			f.getFragmentManager().beginTransaction().remove(lasttask).commit();
		}
		if (b != null) {
			setArguments(b);
		}
		setCallback(callback);
		setTargetFragment(f, 0);
		f.getFragmentManager().beginTransaction().add(this, "_taskfragment_")
				.commit();
	}

	public static TaskFragment<?, ?> getTaskFragment(final FragmentManager fm) {
		return (TaskFragment<?, ?>) fm.findFragmentByTag("_taskfragment_");
	}

	private void setCallback(final CocoQueryCallBack<V> callback) {
		this.callback = callback;
	}

	protected void run(final Fragment f) {
		run(f, null, null);
	}

	public CocoQueryCallBack<V> getCallback() {
		// 空模式，防止空指针异常
		if (callback == null) {
			callback = new SimpleCallBack<V>();
		}
		return callback;
	}

}
