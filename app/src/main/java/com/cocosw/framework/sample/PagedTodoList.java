package com.cocosw.framework.sample;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.cocosw.framework.core.PagedListFragment;
import com.cocosw.framework.view.adapter.CocoAdapter;
import com.cocosw.framework.view.adapter.QuickAdapter;
import com.google.common.collect.Lists;
import com.joanzapata.android.BaseAdapterHelper;

import java.util.List;

/**
 * Project: ToDoList
 * Created by LiaoKai(soarcn) on 2014/6/12.
 */
public class PagedTodoList extends PagedListFragment<Todo> implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public List<Todo> pendingPagedData(long index, int time, int size, Bundle args) throws Exception {
        Thread.sleep(3000);
        return Lists.newArrayList(new Todo("Breakfast "+time),
                new Todo("Lunch "+time),
                new Todo("Dinner "+time),
                new Todo("Shopping "+time),
                new Todo("Programming "+time),
                new Todo("Sleeping "+time),
                new Todo("Fishing "+time),
                new Todo("Sex "+time),
                new Todo("Drinking "+time),
                new Todo("Doing nothing "+time),
                new Todo("Washing "+time)
        );

    }

    @Override
    protected CocoAdapter<Todo> createEndlessAdapter(List<Todo> items) throws Exception {
        return new QuickAdapter<Todo>(context,android.R.layout.simple_list_item_1) {
            @Override
            protected void convert(BaseAdapterHelper helper, Todo item) {
                helper.setText(android.R.id.text1,item.title);
            }
        };
    }

    @Override
    public int layoutId() {
        return R.layout.ui_adv_list;
    }

    @Override
    protected void init(View view, Bundle bundle) throws Exception {
        swipeRefreshLayout = q.id(R.id.swipe_container).getView();
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected void onItemClick(Todo todo, int pos, long id, View view) {
        TodoDetail.launch(getActivity(),todo);
    }

    @Override
    public void onRefresh() {
        refresh();
        swipeRefreshLayout.setRefreshing(false);
    }
}
