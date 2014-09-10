package com.cocosw.framework.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

import com.cocosw.framework.core.PagedListFragment;
import com.cocosw.framework.sample.network.Bean;
import com.cocosw.framework.sample.network.DataSource;
import com.cocosw.framework.view.adapter.CocoAdapter;
import com.cocosw.framework.view.adapter.TypeListAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Project: ToDoList
 * Created by LiaoKai(soarcn) on 2014/6/9.
 */
public class TodoList extends PagedListFragment<Bean.Shot> {

    @Override
    public List<Bean.Shot> pendingPagedData(long index, int time, int size, Bundle args) throws Exception {
        return DataSource.getShots(index < 0 ? 0 : index).shots;
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
        new Presenter(this).target(TodoDetail.class).extra(new Intent().putExtra(TodoDetail.TODO,item)).openForResult(1);
    }

    class ShotAdapter extends TypeListAdapter<Bean.Shot> {

        public ShotAdapter(Context context) {
            super(context, R.layout.li_shot);
        }

        @Override
        protected int[] getChildViewIds() {
            return new int[]{R.id.title, R.id.image};
        }

        @Override
        protected void update(int position, Bean.Shot item) {
            textView(0).setText(item.title);
            Picasso.with(context).load(item.image_teaser_url).into(imageView(1));
        }
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        final SubMenu subMenu1 = menu.addSubMenu(R.string.more);
        menu.add(Menu.NONE, 3, Menu.NONE, R.string.about);
        subMenu1.add(Menu.NONE, 2, Menu.NONE, R.string.about);

        final MenuItem subMenu1Item = subMenu1.getItem();
        subMenu1Item.setIcon(R.drawable.abc_ic_menu_moreoverflow_normal_holo_dark);
        MenuItemCompat.setShowAsAction(subMenu1Item, MenuItem.SHOW_AS_ACTION_ALWAYS);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 2:
                q.alert(R.string.about, R.string.about);
        }
        return false;
    }
}
