package com.cocosw.framework.view.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.cocosw.accessory.views.ViewUtils;
import com.cocosw.framework.uiquery.CocoQuery;

public class ViewHolder {

    protected CocoQuery q;
    public View contentView;
    int position;
    ViewGroup parent;

    public ViewHolder() {
    }

    public ViewHolder(final View view) {
        setContentView(view);
    }

    public void setContentView(View contentView) {
        this.contentView = contentView;
        q = new CocoQuery(contentView);
    }

    public void gone(final View view) {
        ViewUtils.setGone(view, true);
    }

    public void visible(final View view) {
        ViewUtils.setGone(view, false);
    }

    // 快速初始化的辅助接口
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

//    public void delayImage(final ImageView view, final String url) {
//		delayImage(view, url, getPlaceHolder());
//	}
//
//	public void delayImage(final ImageView view, final String url,
//			final int holderRes, final OnClickListener onViewClickInListListener) {
//		if (TextUtils.isEmpty(url)) {
//			view.setVisibility(View.GONE);
//		} else {
//			if (parent != null
//					&& q.shouldDelay(position, contentView, parent, url)) {
//				q.recycle(view).image(holderRes).visible();
//			} else {
//				q.recycle(view).image(url, true, true, 0, 0,
//						q.getCachedImage(holderRes), 0);
//			}
//			if (onViewClickInListListener != null) {
//				view.setOnClickListener(onViewClickInListListener);
//			}
//			q.recycle(contentView);
//		}
//	}
//
//	public void delayImage(final ImageView view, final String url,
//			final int holderRes) {
//		delayImage(view, url, holderRes, null);
//	}

//	protected int getPlaceHolder() {
//		return R.drawable.content_picture;
//	}

}
