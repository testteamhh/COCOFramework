package com.cocosw.framework.uiquery;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.androidquery.AbstractAQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.callback.ImageOptions;
import com.androidquery.util.AQUtility;
import com.androidquery.util.Common;
import com.androidquery.util.Constants;
import com.androidquery.util.XmlDom;
import com.cocosw.accessory.utils.Utils;
import com.cocosw.framework.BuildConfig;
import com.cocosw.framework.R;
import com.cocosw.framework.view.adapter.CocoAdapter;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import org.xml.sax.SAXException;

import java.io.File;
import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;

public class CocoQuery extends AbstractAQuery<CocoQuery> {

	private OnClickListener dialogClicklistener;
	private Activity act;
	private final WeakHashMap<Integer, CocoTask<?>> taskpool = new WeakHashMap<Integer, CocoTask<?>>();
	private Fragment fragment;

	public CocoQuery(final View view) {
		super(view);
	}

	public CocoQuery(final Activity act) {
		super(act);
		this.act = act;
	}

	public CocoQuery(final Activity act, final View root) {
		super(act, root);
		this.act = act;
	}

	public CocoQuery(final Activity act, final Fragment fragment,
			final View root) {
		super(act, root);
		this.act = act;
		this.fragment = fragment;
	}

	private static final int TOAST_DISPLAY_TIME = 3000;

	public CocoQuery(final Context context) {
		super(context);

	}

	public CocoQuery alert(final int title, final String message) {
		return alert(getContext().getResources().getString(title), message);
	}

	public CocoQuery alert(final String title, final CharSequence message) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(
				getContext());
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog,
							final int which) {
						if (dialogClicklistener != null) {
							dialogClicklistener.onClick(dialog, which);
						}
						final AlertDialog ad = builder.create();
						ad.cancel();
					}
				});
		builder.show();
		return this;
	}

	public CocoQuery alert(final int title, final int message) {
		new AlertDialog.Builder(getContext());
		return alert(getContext().getString(title),
				getContext().getString(message));
	}

	public CocoQuery alert(final Exception e) {
		alert(getContext().getString(R.string.info), e.getMessage());
		return this;
	}

	public CocoQuery confirm(final int title, final int message,
			final OnClickListener onClickListener) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(
				getContext());
		builder.setTitle(title);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setMessage(message);

		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(final DialogInterface dialog,
							final int which) {
						if (onClickListener != null) {
							onClickListener.onClick(dialog, which);
						}

					}
				});
		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(final DialogInterface dialog,
							final int which) {
						if (onClickListener != null) {
							onClickListener.onClick(dialog, which);
						}
					}
				});
		builder.show();
		return this;
	}

	public CocoQuery diaclicked(
			final DialogInterface.OnClickListener onClickListener) {
		dialogClicklistener = onClickListener;
		return this;
	}

	public CocoQuery alert(final int title, final int message,
			final DialogInterface.OnClickListener onClickListener) {

		final AlertDialog.Builder builder = new AlertDialog.Builder(
				getContext());
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog,
							final int which) {
						if (onClickListener != null) {
							onClickListener.onClick(dialog, which);
						}
						final AlertDialog ad = builder.create();
						ad.cancel();
					}
				});
		builder.show();
		return this;
	}

	/**
	 * 快速显示一个toast
	 * 
	 * @param message
	 *            消息内容
	 * @param duration
	 *            显示时间
	 * @return
	 */
	public CocoQuery toast(final String message, final int duration) {
		Toast.makeText(getContext(), message, duration).show();
		return this;
	}

	public CocoQuery toast(final int stringId) {
		Toast.makeText(getContext(),
				getContext().getResources().getString(stringId),
				CocoQuery.TOAST_DISPLAY_TIME).show();
		return this;
	}

	public CocoQuery toast(final String message) {
		Toast.makeText(getContext(), message, CocoQuery.TOAST_DISPLAY_TIME)
				.show();
		return this;
	}

	public CocoQuery task(final CocoTask<?> callback) {
		if (progress != null) {
			callback.progress(progress);
		}
		taskpool.put(callback.hashCode(), callback);
		callback.async(act);
		return this;
	}

	public CocoQuery task(final CocoTask<?> task,
			final CocoQueryCallBack callback) {
		if (progress != null) {
			task.progress(progress);
		}
		taskpool.put(callback.hashCode(), task);
		task.setCallback(callback);
		task.async(act);
		return this;
	}

	private CocoAdapter getAdapter(final AdapterView view) {
		final Adapter ad = view.getAdapter();
		if (ad instanceof HeaderViewListAdapter) {
			return (CocoAdapter) ((HeaderViewListAdapter) ad)
					.getWrappedAdapter();
		}
		return (CocoAdapter) ad;
	}

	@Deprecated
	/**
	 * 异步加载数据到listview
	 * 
	 * @param call
	 * @return
	 */
	public CocoQuery listadd(final Callable<List<?>> call) {
		if (call != null) {
			if (view instanceof AdapterView) {
				final CocoAdapter adapter = getAdapter((AdapterView) view);
				if (adapter != null) {
					task(new CocoTask<Boolean>() {

						@Override
						public void callback(final Boolean object) {
							adapter.notifyDataChange();
						}

						@Override
						public Boolean backgroundWork() throws Exception {
							final List<?> list = call.call();
							if (list != null) {
								adapter.add(call.call());
								return list.size() != 0;
							}
							return false;
						}
					});
				}
			}
		}
		return this;
	}

	/**
	 * pager的adapter
	 * 
	 * @param mAdapter
	 * @return
	 */
	public CocoQuery adapter(final PagerAdapter mAdapter) {
		if (view instanceof ViewPager) {
			((ViewPager) view).setAdapter(mAdapter);
		}
		return this;
	}

	public CocoQuery clicked(final CocoTask<?> callback) {

		final Common common = new Common() {
			@Override
			public void onClick(final View v) {
				CocoQuery.this.task(callback);
			}
		};
		return clicked(common);

	}

	public XmlDom xml(final int id) throws NotFoundException, SAXException {
		return new XmlDom(getContext().getResources().openRawResource(id));
	}

	public void toast(final Exception e) {
		Utils.dout(e);
		toast(e.getMessage());
	}

	public CocoQuery info(final int info) {
		if (act != null) {
			Crouton.makeText(act, info, Style.INFO).show();
		}
		return this;
	}

	public CocoQuery alert(final int info) {
		if (act != null) {
			Crouton.makeText(act, info, Style.ALERT).show();
		}
		return this;
	}

	public CocoQuery crouton(final int info, final Style style) {
		if (act != null) {
			Crouton.makeText(act, info, style).show();
		}
		return this;
	}

	public CocoQuery alert(final CharSequence info) {
		if (act != null & info != null) {
			Crouton.makeText(act, info, Style.ALERT).show();
		}
		return this;
	}

	public CocoQuery confirm(final int info) {
		if (act != null) {
			Crouton.makeText(act, info, Style.CONFIRM).show();
		}
		return this;
	}

	public CocoQuery confirm(final CharSequence info) {
		if (act != null & info != null) {
			Crouton.makeText(act, info, Style.CONFIRM).show();
		}
		return this;
	}

	public CocoQuery post(final Runnable r) {
		AQUtility.post(r);
		return this;
	}

	public CocoQuery d(final Object obj) {
		if (BuildConfig.DEBUG) {
			// Views.showToast(obj.toString());
			if (obj != null) {
				Log.d(obj.getClass().getSimpleName(), "obj>>>>>>>>>>>>>"
						+ obj.getClass().getName() + ">>" + obj.toString());
			} else {
				Log.d("[dout]", "obj>>>>>>>>>>>>>NULL");
			}
		}
		return this;
	}

	public CocoQuery v(final Object obj) {
		if (BuildConfig.DEBUG) {
			// Views.showToast(obj.toString());
			if (obj != null) {
				Log.v(obj.getClass().getSimpleName(), "obj>>>>>>>>>>>>>"
						+ obj.getClass().getName() + ">>" + obj.toString());
			} else {
				Log.v("[dout]", "obj>>>>>>>>>>>>>NULL");
			}
		}
		return this;
	}

	public CocoQuery w(final Object obj) {
		// Views.showToast(obj.toString());
		if (obj != null) {
			Log.w(obj.getClass().getSimpleName(), "obj>>>>>>>>>>>>>"
					+ obj.getClass().getName() + ">>" + obj.toString());
		} else {
			Log.w("[dout]", "obj>>>>>>>>>>>>>NULL");
		}
		return this;
	}

	public CocoQuery i(final Object obj) {
		// Views.showToast(obj.toString());
		if (obj != null) {
			Log.i(obj.getClass().getSimpleName(), "obj>>>>>>>>>>>>>"
					+ obj.getClass().getName() + ">>" + obj.toString());
		} else {
			Log.i("[dout]", "obj>>>>>>>>>>>>>NULL");
		}
		return this;
	}

	/**
	 * 终结所有的cocotask
	 * 
	 * @return
	 */
	public CocoQuery CleanAllTask() {
		for (final CocoTask<?> reference : taskpool.values()) {
			if (reference != null) {
				reference.cancle();
			}
		}
		return this;
	}

	public CocoQuery html(final String text) {
		if (TextUtils.isEmpty(text)) {
			return text("");
		}
		if (text.contains("<") && text.contains(">")) {
			getTextView().setMovementMethod(LinkMovementMethod.getInstance());
			return text(Html.fromHtml(text));
		} else {
			return text(text);
		}
	}

	public CocoQuery image(final Bitmap bm, final ImageOptions option) {
		final BitmapAjaxCallback cb = new BitmapAjaxCallback();
		cb.anchor(option.anchor).ratio(option.ratio).round(option.round)
				.bitmap(bm);
		return image(cb);
	}

	/**
	 * 同步下载文件
	 * 
	 * @param url
	 */
	public File downloadFile(final String url, final String folder) {
		v(folder + getFilenameFromUrl(url));
		final AjaxCallback<File> cb = new AjaxCallback<File>();
		cb.url(url).type(File.class)
				.targetFile(new File(folder + getFilenameFromUrl(url)));
		sync(cb);
		return cb.getResult();
	}

	private String getFilenameFromUrl(final String url) {
		return url.substring(url.lastIndexOf("/"));
	}

	public AbstractAQuery<CocoQuery> image(final String thumbnail,
			final String target, final BitmapAjaxCallback imageCallback) {
		if (TextUtils.isEmpty(thumbnail)) {
			gone();
			return this;
		} else {
			visible();
		}

		// 如果已经下载完成了
		if (getCachedFile(target) != null) {
			image(getCachedFile(target), 0);
			imageCallback.imageView(getImageView());
			imageCallback.callback(target, getCachedImage(target),
					new AjaxStatus(200, null));
		} else {
			image(getCachedImage(thumbnail));
			image(target, true, true, 0, 0, imageCallback);
		}

		return this;
	}

	public CocoQuery loader(final Loader<?> loader, final Bundle bunle) {
		if (getContext() instanceof FragmentActivity) {
			if (progress != null) {
				loader.progress(progress);
			}
			if (fragment != null) {
				loader.async(fragment, (FragmentActivity) getContext(), bunle);
			} else {
				loader.async((FragmentActivity) getContext(), bunle);
			}
		}
		return this;
	}

}