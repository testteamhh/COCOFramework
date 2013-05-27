package com.cocosw.framework.view.adapter;

import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.cocosw.framework.R;
import com.cocosw.framework.uiquery.CocoQuery;
import com.github.kevinsawicki.wishlist.ViewUtils;

public class ViewHolder {

	protected final CocoQuery q;
	public View contentView;
	int position;
	ViewGroup parent;

	public ViewHolder(final CocoQuery q) {
		this.q = q;
	}

	public ViewHolder(final CocoQuery q, final View view) {
		this.q = q;
		contentView = view;
	}

	public void gone(final View view) {
		ViewUtils.setGone(view, true);
	}

	public void visible(final View view) {
		ViewUtils.setGone(view, false);
	}

	// 快速初始化的辅助接口
	protected TextView textView(final int id) {
		return (TextView) contentView.findViewById(id);
	}

	protected ImageView imageView(final int id) {
		return (ImageView) contentView.findViewById(id);
	}

	protected CheckBox checkBox(final int id) {
		return (CheckBox) contentView.findViewById(id);
	}

	protected View view(final int id) {
		return contentView.findViewById(id);
	}

	public void delayImage(final ImageView view, final String url) {
		delayImage(view, url, getPlaceHolder());
	}

	public void delayImage(final ImageView view, final String url,
			final int holderRes, final OnClickListener onViewClickInListListener) {
		if (TextUtils.isEmpty(url)) {
			view.setVisibility(View.GONE);
		} else {
			if (parent != null
					&& q.shouldDelay(position, contentView, parent, url)) {
				q.recycle(view).image(holderRes).visible();
			} else {
				q.recycle(view).image(url, true, true, 0, 0,
						q.getCachedImage(holderRes), 0);
			}
			if (onViewClickInListListener != null) {
				view.setOnClickListener(onViewClickInListListener);
			}
			q.recycle(contentView);
		}
	}

	public void delayImage(final ImageView view, final String url,
			final int holderRes) {
		delayImage(view, url, holderRes, null);
	}

	protected int getPlaceHolder() {
		return R.drawable.content_picture;
	}

}
