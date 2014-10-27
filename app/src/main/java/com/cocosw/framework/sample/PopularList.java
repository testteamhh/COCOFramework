package com.cocosw.framework.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.GridView;

import com.cocosw.framework.core.PagedListFragment;
import com.cocosw.framework.core.Presenter;
import com.cocosw.framework.sample.network.Bean;
import com.cocosw.framework.sample.network.DataSource;
import com.cocosw.framework.sample.utils.PaletteManager;
import com.cocosw.framework.view.adapter.CocoAdapter;
import com.cocosw.framework.view.adapter.TypeListAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;

/**
 * Project: ToDoList
 * Created by LiaoKai(soarcn) on 2014/6/9.
 */
public class PopularList extends PagedListFragment<Bean.Shot, GridView> implements SwipeRefreshLayout.OnRefreshListener {

    @InjectView(R.id.swipe)
    SwipeRefreshLayout mSwipe;

    @Inject
    Picasso picasso;

    @Inject
    PaletteManager pm;

    @Override
    public List<Bean.Shot> pendingPagedData(long index, int time, int size, Bundle args) throws Exception {
        return DataSource.getShots(index < 0 ? 1 : time + 1).shots;
    }

    @Override
    protected CocoAdapter<Bean.Shot> createEndlessAdapter(List<Bean.Shot> items) throws Exception {
        return new ShotAdapter(context, items);
    }

    @Override
    protected void init(View view, Bundle bundle) throws Exception {
        inject();
        mSwipe.setColorSchemeResources(R.color.themecolor, R.color.transparent, R.color.themecolor, R.color.transparent);
        mSwipe.setOnRefreshListener(this);
    }

    @Override
    public int layoutId() {
        return R.layout.ui_popular;
    }

    @Override
    protected void onItemClick(Bean.Shot item, int pos, long id, View view) {
        new Presenter(this).target(ShotDetail.class).extra(new Intent().putExtra(ShotDetail.TODO, item)).openForResult(1);
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    @Override
    public void onLoaderDone(List<Bean.Shot> items) {
        mSwipe.setRefreshing(false);

    }

    class ShotAdapter extends TypeListAdapter<Bean.Shot> {

        public ShotAdapter(Context context, List<Bean.Shot> items) {
            super(context, R.layout.li_shot, items);
        }

        @Override
        protected int[] getChildViewIds() {
            return new int[]{R.id.title, R.id.image, R.id.subtitle, R.id.info_box};
        }

        @Override
        protected void update(int position, Bean.Shot item) {
            textView(0).setText(item.title);
            textView(2).setText(item.player.name);
            pm.updatePalette(picasso, item.image_teaser_url, imageView(1), textView(0), view(3));
        }
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        final SubMenu subMenu1 = menu.addSubMenu(R.string.more);
        menu.add(Menu.NONE, 3, Menu.NONE, R.string.about);
        subMenu1.add(Menu.NONE, 2, Menu.NONE, R.string.about);

        final MenuItem subMenu1Item = subMenu1.getItem();
        subMenu1Item.setIcon(R.drawable.abc_ic_menu_moreoverflow_mtrl_alpha);
        MenuItemCompat.setShowAsAction(subMenu1Item, MenuItem.SHOW_AS_ACTION_ALWAYS);
        super.onCreateOptionsMenu(menu, inflater);
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 2:
                q.alert(R.string.about, R.string.about);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
