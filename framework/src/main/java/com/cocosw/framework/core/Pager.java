package com.cocosw.framework.core;


import com.cocosw.accessory.views.complex.CocoPager;

public interface Pager<K extends Carousel> {

    public int title();

    public void setPager(final CocoPager pager);

    public CocoPager getPager();

    public void first();

    /**
     * get Id of current pager
     *
     * @return
     */
    public int getID();

    public void setID(int id);

    /**
     * next pager
     *
     * @return
     */
    public Pager<?> nextPager();

    /**
     * previous pager
     *
     * @return
     */
    public Pager<?> prevPager();

    K getSource();

    void setSource(K s);

    public void onPagerSelected();

}
