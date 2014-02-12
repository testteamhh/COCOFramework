package com.cocosw.framework.extend;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.cocosw.accessory.utils.FragmentUtils;
import com.cocosw.framework.R;
import com.cocosw.framework.core.Base;

import net.simonvt.menudrawer.MenuDrawer;

/**
 * This is design for list+detail mode activity
 * <p/>
 * list/detail fragment can be set up separately
 * detail fragment will slide from right with animation if you try to open it and can be closed by swipe right
 * <p/>
 * you have to add dependency in your pom to enable this feature
 * <p/>
 * User: Administrator
 * Date: 13-11-15
 * Time: 下午7:08
 */
public abstract class DetailActivity<T> extends Base<T> {

    // private ActionsContentView mDrawer;
    private Fragment contentFragment;
    private Fragment actionFragment;
    private MenuDrawer mDrawer;

    @Override
    public int layoutId() {
        return R.layout.ui_slide;
    }

    @Override
    protected void init(Bundle saveBundle) throws Exception {
        mDrawer = (MenuDrawer) q.id(R.id.md__drawer).getView();
        mDrawer.setEnabled(false);
        mDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_FULLSCREEN);
        mDrawer.setOffsetMenuEnabled(false);
        mDrawer.setMenuSize(getWindowManager().getDefaultDisplay().getWidth());
        mDrawer.setDropShadowEnabled(false);
        mDrawer.openMenu();
        setup(saveBundle);
    }

    protected abstract void setup(Bundle saveBundle);

    public MenuDrawer getContentView() {
        return mDrawer;
    }


    @Override
    public void onBackPressed() {
        if (!mDrawer.isMenuVisible()) {
            mDrawer.openMenu();
        } else {
            super.onBackPressed();
        }
    }


    protected void setActionFragment(Fragment fragment) {
        actionFragment = fragment;
        FragmentUtils.replaceFragment(this, R.id.mdMenu, fragment);
    }

    protected void setContentFragment(Fragment fragment) {
        contentFragment = fragment;
        FragmentUtils.replaceFragment(this, R.id.mdContent, fragment);
    }

    public Fragment getContentFragment() {
        return contentFragment;
    }

    public Fragment getActionFragment() {
        return actionFragment;
    }
}
