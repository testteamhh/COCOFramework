package com.cocosw.framework.view.adapter;

import android.content.Context;
import android.support.v4.app.FixedFragmentStatePagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.ViewGroup;


import com.cocosw.framework.core.BaseFragment;

import java.util.WeakHashMap;


public class CocoFragmentStatePagerAdapter extends
        FixedFragmentStatePagerAdapter {

    // private final WeakPool<Fragment> pool = new WeakPool<Fragment>();
    private final WeakHashMap<Integer, Fragment> pool = new WeakHashMap<Integer, Fragment>();

    protected Class<? extends Fragment>[] clz;
    private final Context context;
    private Fragment selected;

    @SuppressWarnings("unchecked")
    public CocoFragmentStatePagerAdapter(final Context context,
                                         final FragmentManager fm) {
        super(fm);
        clz = new Class[]{};
        this.context = context;
    }

    public CocoFragmentStatePagerAdapter(final Context context,
                                         final FragmentManager fm, final Class<? extends Fragment>[] clz) {
        super(fm);
        this.clz = clz;
        this.context = context;
    }

    @Override
    public int getCount() {
        return clz.length;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Fragment getItem(final int i) {
        Fragment f = pool.get(i);
        if (f == null) {
            f = Fragment.instantiate(context, clz[i].getName());
            pool.put(i, f);
        }
        return f;
    }

    public int getPosition(final Class<? extends Fragment> fragment) {
        for (int i = 0; i < clz.length; i++) {
            if (clz[i].equals(fragment)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void setPrimaryItem(final ViewGroup container, final int position,
                               final Object object) {
        super.setPrimaryItem(container, position, object);

        selected = (Fragment) object;
    }

    @Override
    public CharSequence getPageTitle(final int position) {
        if (getItem(position) instanceof BaseFragment)
            return ((BaseFragment) getItem(position)).getTitle();
        return "";
    }

    public void update(final Class<? extends Fragment>[] clz) {
        this.clz = clz;
    }

    /**
     * Get all fragments in this viewpager
     *
     * @return
     */
    public Fragment[] getAllFragments() {
        if (pool.size() == 0) {
            return null;
        } else {
            final Fragment[] out = new Fragment[pool.size()];
            int i = 0;
            for (final Fragment f : pool.values()) {
                if (f != null) {
                    out[i++] = f;
                }
            }
            return out;
        }
    }
}
