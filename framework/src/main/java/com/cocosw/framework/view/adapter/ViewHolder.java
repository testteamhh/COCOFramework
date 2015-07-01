package com.cocosw.framework.view.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.cocosw.accessory.views.ViewUtils;
import com.cocosw.framework.uiquery.CocoQuery;

import butterknife.ButterKnife;

public class ViewHolder {

    public View contentView;
    protected CocoQuery q;
    int position;
    ViewGroup parent;

    public ViewHolder(final View view) {
        setContentView(view);
        ButterKnife.bind(this, view);
    }

    private void setContentView(View contentView) {
        this.contentView = contentView;
        q = new CocoQuery(contentView);
    }

    public void gone(final View view) {
        ViewUtils.setGone(view, true);
    }

    public void visible(final View view) {
        ViewUtils.setGone(view, false);
    }

    protected TextView textView(final int id) {
        return view(id);
    }

    protected ImageView imageView(final int id) {
        return view(id);
    }

    protected CheckBox checkBox(final int id) {
        return view(id);
    }

    protected final <E extends View> E view(int resourceId) {
        return (E) contentView.findViewById(resourceId);
    }

}
