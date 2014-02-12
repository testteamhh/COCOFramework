package com.cocosw.framework.core;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cocosw.framework.R;


/**
 * 适用于没有UI的Fragment，可以抽象一些复杂的task
 *
 * @author soar
 */
public abstract class TaskFragment<T, V> extends BaseFragment<T> {

    private Callback<V> callback;

    @Override
    final public int layoutId() {
        // empty
        return R.layout.activity_singlepane_empty;
    }

    @Override
    final protected void setupUI(final View view, final Bundle bundle)
            throws Exception {
        // empty
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
                       final Callback<V> callback) {
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

    private void setCallback(final Callback<V> callback) {
        this.callback = callback;
    }

    protected void run(final Fragment f) {
        run(f, null, null);
    }

    public Callback<V> getCallback() {
        // empty object pattern
        if (callback == null) {
            callback = new Callback<V>() {

                @Override
                public void callback(V result) {

                }

                @Override
                public void fallback(V result, Exception status) {

                }

            };
        }
        return callback;
    }


    public static interface Callback<V> {

        void callback(V result);

        public void fallback(V result, Exception status);
    }

}
