package com.cocosw.framework.view;

import android.content.Context;
import android.support.v4.app.FixedFragmentStatePagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.ViewGroup;

import com.cocosw.framework.core.Carousel;
import com.cocosw.framework.core.Pager;

import java.util.WeakHashMap;

/**
 * 对应用中广泛使用的pager的adapter的封装
 *
 * @author kaliao
 */
public class CocoFragmentStatePagerAdapter extends
        FixedFragmentStatePagerAdapter {

    // 利用弱引用池进行fragment实例的管理

    private Pager<?> selected;
    // private final WeakPool<Fragment> pool = new WeakPool<Fragment>();
    private final WeakHashMap<Integer, Fragment> pool = new WeakHashMap<Integer, Fragment>();

    protected Class<? extends Fragment>[] clz;
    private final Context context;
    private Carousel source;

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
            if (f instanceof Pager) {
                ((Pager) f).setSource(source);
            }
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

    public Pager getSelected() {
        return selected;
    }

    public Pager getPager(final int index) {
        return (Pager) getItem(index);
    }

    @Override
    public void setPrimaryItem(final ViewGroup container, final int position,
                               final Object object) {
        super.setPrimaryItem(container, position, object);

        if (object instanceof Pager) {
            selected = (Pager<?>) object;
            selected.setPager(source.getViewPager());
        } else {
            selected = null;
        }
    }

    @Override
    public CharSequence getPageTitle(final int position) {
        return context.getText(((Pager<?>) getItem(position)).title());
    }

    public void update(final Class<? extends Fragment>[] clz) {
        this.clz = clz;
    }

    /**
     * 返回当前所有的Fragment
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

    public void setSource(final Carousel obj) {
        source = obj;
    }

}
