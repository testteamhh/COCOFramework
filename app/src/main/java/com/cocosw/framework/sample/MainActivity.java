package com.cocosw.framework.sample;

import android.support.v4.app.Fragment;

import com.cocosw.framework.core.SinglePaneActivity;


public class MainActivity extends SinglePaneActivity<Void> {

    @Override
    protected Fragment onCreatePane() {
        return Fragment.instantiate(this,TodoList.class.getName());
    }
}
