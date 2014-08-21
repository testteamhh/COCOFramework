/*
 * Copyright (c) 2014. www.cocosw.com
 */

package com.cocosw.framework.core;

import android.content.Context;
import android.os.Bundle;
//import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.cocosw.framework.R;
import com.cocosw.framework.view.CollectionView;
import com.cocosw.framework.view.CollectionViewCallbacks;
import com.cocosw.framework.view.adapter.CocoAdapter;
import com.cocosw.framework.view.adapter.TypeListAdapter;

import java.util.List;

/**
 * Project: insight
 * Created by LiaoKai(soarcn) on 2014/7/31.
 */
public abstract class CollectFragment<T> extends ListFragment<T> {

    private CollectAdapter adapter;

    @Override
    protected CocoAdapter<T> createAdapter(List<T> items) throws Exception {
        return null;
    }

    @Override
    protected void init(final View view, final Bundle bundle) throws Exception {
        getList().notifyAdapterDataSetChanged();
        getList().setCollectionAdapter(adapter);
      //  getList().updateInventory(adapter.getInventory());
    }

    @Override
    public CollectionView getList() {
        return (CollectionView) super.getList();
    }

    @Override
    public int layoutId() {
        return R.layout.inc_progress_collectlist;
    }

    @Override
    protected void onItemClick(T item, int pos, long id, View view) {

    }

    abstract class CollectAdapter extends TypeListAdapter<Void> implements CollectionViewCallbacks {

        public CollectAdapter(Context context,int layout) {
            super(context, layout);
        }

//
//        public CollectionView.Inventory getInventory() {
//            CollectionView.Inventory inventory = new CollectionView.Inventory();
//            // setup hero hashtag
//            inventory.addGroup(new CollectionView.InventoryGroup(HERO_GROUP_ID)
//                    .setDisplayCols(1)
//                    .setItemCount(1)
//                    .setShowHeader(false));
//
//            // setup other hashtags
//            inventory.addGroup(new CollectionView.InventoryGroup(HashtagsQuery.TOKEN)
//                    .setDisplayCols(context.getResources().getInteger(R.integer.grid_col_num))
//                    .setItemCount(getCount() - 1)
//                    .setDataIndexStart(1)
//                    .setShowHeader(false));
//            return inventory;
//        }
//
//        private boolean isHeroView(int groupId) {
//            return groupId == HERO_GROUP_ID;
//        }
//
//        @Override
//        public View newCollectionHeaderView(Context context, ViewGroup parent) {
//            return null;
//        }
//
//        @Override
//        public void bindCollectionHeaderView(Context context, View view, int groupId, String headerLabel) {
//        }
//
//        @Override
//        public View newCollectionItemView(Context context, int groupId, ViewGroup parent) {
//
//            if (convertView == null)
//                convertView = initialize(inflater.inflate(layout, null));
//            update(position, convertView, getItem(position));
//            return convertView;
//
//            return isHeroView(groupId) ? newHeroView(context, parent) : newView(context, null, parent);
//        }
//
//        @Override
//        public void bindCollectionItemView(Context context, View view, int groupId, int indexInGroup, int dataIndex, Object tag) {
//            if (isHeroView(groupId)) {
//                bindHeroView(view, context);
//            } else {
//                getView(dataIndex,view, context);
//            }
//        }
//
//        private View newHeroView(Context context, ViewGroup parent) {
//            return LayoutInflater.from(context).inflate(R.layout.li_menu_item, parent, false);
//        }
//
//
//        public void bindHeroView(View view, Context context) {
//            final String hashtag = cursor.getString(HashtagsQuery.HASHTAG_NAME);
//            ((TextView) view.findViewById(R.id.name)).setText(hashtag);
//        }
    }

}
