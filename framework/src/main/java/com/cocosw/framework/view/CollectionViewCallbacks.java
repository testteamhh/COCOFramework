/*
 * Copyright (c) 2014. www.cocosw.com
 */

package com.cocosw.framework.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public interface CollectionViewCallbacks {
    View newCollectionHeaderView(Context context, ViewGroup parent);
    void bindCollectionHeaderView(Context context, View view, int groupId, String headerLabel);

    View newCollectionItemView(Context context, int groupId, ViewGroup parent);
    void bindCollectionItemView(Context context, View view, int groupId, int indexInGroup, int dataIndex, Object tag);
}
