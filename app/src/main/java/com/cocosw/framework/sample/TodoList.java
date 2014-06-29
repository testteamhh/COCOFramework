package com.cocosw.framework.sample;

import android.os.Bundle;
import android.view.View;

import com.cocosw.framework.core.ListFragment;
import com.cocosw.framework.view.adapter.CocoAdapter;
import com.cocosw.framework.view.adapter.QuickAdapter;
import com.google.common.collect.Lists;
import com.joanzapata.android.BaseAdapterHelper;

import java.util.List;

/**
 * Project: ToDoList
 * Created by LiaoKai(soarcn) on 2014/6/9.
 */
public class TodoList extends ListFragment<Todo> {

    @Override
    protected CocoAdapter<Todo> createAdapter(List<Todo> todos) throws Exception {
        return new QuickAdapter<Todo>(context,android.R.layout.simple_list_item_1) {
            @Override
            protected void convert(BaseAdapterHelper helper, Todo item) {
                helper.setText(android.R.id.text1,item.title);
            }
        };
    }

    @Override
    protected void init(View view, Bundle bundle) throws Exception {

    }

    @Override
    protected void onItemClick(Todo todo, int i, long l, View view) {
        TodoDetail.launch(getActivity(),todo);
    }

    @Override
    public List<Todo> pendingData(Bundle bundle) throws Exception {
        return Lists.newArrayList(new Todo("Breakfast"),
                new Todo("Lunch"),
                new Todo("Dinner"));
    }

}
