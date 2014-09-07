package com.cocosw.framework.sample;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.cocosw.framework.core.ListFragment;
import com.cocosw.framework.core.PagedListFragment;
import com.cocosw.framework.core.SinglePaneActivity;
import com.cocosw.framework.sample.network.Bean;
import com.cocosw.framework.sample.network.DataSource;
import com.cocosw.framework.view.adapter.CocoAdapter;
import com.cocosw.framework.view.adapter.QuickAdapter;
import com.cocosw.framework.view.adapter.TypeListAdapter;
import com.cocosw.query.CocoTask;
import com.google.common.collect.Lists;
import com.joanzapata.android.BaseAdapterHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Project: ToDoList
 * Created by LiaoKai(soarcn) on 2014/6/9.
 */
public class TodoList extends PagedListFragment<Bean.Shot> {

    @Override
    public List<Bean.Shot> pendingPagedData(long index, int time, int size, Bundle args) throws Exception {
        return DataSource.getShots(index<0?0:index).shots;
    }

    @Override
    protected CocoAdapter<Bean.Shot> createEndlessAdapter(List<Bean.Shot> items) throws Exception {
        return new ShotAdapter(context);
    }

    @Override
    protected void init(View view, Bundle bundle) throws Exception {

    }

    @Override
    protected void onItemClick(Bean.Shot item, int pos, long id, View view) {
        TodoDetail.launch(getActivity(),item);
    }

    class ShotAdapter extends TypeListAdapter<Bean.Shot> {

        public ShotAdapter(Context context) {
            super(context, R.layout.li_shot);
        }

        @Override
        protected int[] getChildViewIds() {
            return new int[]{R.id.title,R.id.image};
        }

        @Override
        protected void update(int position, Bean.Shot item) {
            textView(0).setText(item.title);
            Picasso.with(context).load(item.image_teaser_url).into(imageView(1));
        }
    }

}
