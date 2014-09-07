package com.cocosw.framework.sample;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

import com.cocosw.framework.core.BaseFragment;
import com.cocosw.framework.core.SinglePaneActivity;
import com.cocosw.framework.sample.network.Bean;

/**
 * Project: ToDoList
 * Created by LiaoKai(soarcn) on 2014/6/12.
 */
public class TodoDetail extends BaseFragment{
    private static final String TODO = "todo";

    @Override
    public int layoutId() {
        return R.layout.tododetail;
    }

    @Override
    protected void setupUI(View view, Bundle bundle) throws Exception {
        Bean.Shot todo = (Bean.Shot) getArguments().getSerializable(TODO);
        q.id(R.id.detail).text(todo.title);
    }

    public static void launch(Activity act,Bean.Shot todo) {
        SinglePaneActivity.start(TodoDetail.class,act,new Intent().putExtra(TODO,todo));
    }

    protected void populateInsets(Rect insets) {
        v.setPadding(
                insets.left,
                insets.top,
                insets.right,
                insets.bottom);
    }

}
