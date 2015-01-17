package com.cocosw.framework.sample;

import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.cocosw.framework.core.DualPaneActivity;
import com.cocosw.framework.core.SystemBarTintManager;

/**
 * Project: cocoframework
 * Created by LiaoKai(soarcn) on 2014/9/9.
 */
public class Main extends DualPaneActivity {

    private ColorDrawable abbg;
    //   private MaterialMenuIcon materialMenu;
    private boolean isDrawerOpened;

    @Override
    protected Fragment onCreateMasterPane() {
        return Fragment.instantiate(this, PopularRecyclerList.class.getName());
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (!getPanel().isOpen()) {
                getPanel().openPane();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onInsetsChanged(SystemBarTintManager.SystemBarConfig insets) {
        super.onInsetsChanged(insets);
    }
}
