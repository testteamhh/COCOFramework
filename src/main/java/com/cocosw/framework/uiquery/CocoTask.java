package com.cocosw.framework.uiquery;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.view.View;
import com.androidquery.util.AQUtility;
import com.androidquery.util.Common;
import com.cocosw.accessory.utils.Utils;
import com.cocosw.framework.core.Base;


import java.lang.ref.WeakReference;

public abstract class CocoTask<T> implements CocoQueryCallBack<T>,
		OnCancelListener {

	private WeakReference<Object> progress;

	private T result;

	private View listprogress;

	private View view;

	public abstract T backgroundWork() throws Exception;

	private CocoQueryCallBack<T> callback;

	private AsyncTask<?, ?, ?> task;

	private ProgressDialog dialog;

	private int dialogreid = -1;

	private boolean dialogcancel = false;

	private boolean dialogdeterminate = true;

	private WeakReference<Activity> act;

	public void setCallback(final CocoQueryCallBack<T> callback) {
		this.callback = callback;
	}

	/**
	 * 无异常时的callback.
	 * 
	 * @param object
	 *            the object
	 *            the status
	 */
	@Override
	public void callback(final T object) {
		if (callback != null) {
			callback.callback(object);
		}
	}

	/**
	 * 有异常时的callback.
	 *
	 * @param object
	 *            the object
	 */
	@Override
	public void failcallback(final T object, final Exception e) {
		if (callback != null) {
			callback.failcallback(object, e);
		} else {
			if (act.get() instanceof Base<?>) {
				((Base<?>) act.get()).showError(e);
			}
		}
	}

	private boolean isActive() {

		if (act == null) {
			return true;
		}

		final Activity a = act.get();

		if (a == null || a.isFinishing()) {
			return false;
		}

		return true;
	}

	/**
	 * 开始活动，很少用
	 */
	public void pre() {

	}

	public void run() {
		try {
			result = backgroundWork();
			showProgress(false);
			callback(result);
		} catch (final Exception e) {
			e.printStackTrace();
			failcallback(result, e);
		}
	}

	public CocoTask<T> progress(final Object progress) {
		if (progress != null) {
			this.progress = new WeakReference<Object>(progress);
			if (progress instanceof ProgressDialog) {
				// if ((ProgressDialog)progress))
			}
		}
		return this;
	}

	@Override
	public void progressUpdate(final String... values) {
		if (callback != null) {
			callback.progressUpdate(values);
		}
		if (null != dialog && !dialog.isIndeterminate()) {
			dialog.setProgress(Integer.valueOf(values[0]));
		}
	}

	@Override
	public void end() {
		if (callback != null) {
			callback.end();
		}
	}

	public void async(final Activity act) {
		this.act = new WeakReference<Activity>(act);
		if (act.isFinishing()) {
			AQUtility
					.warn("Warning",
							"Possible memory leak. Calling ajax with a terminated activity.");
		}

		new AsyncTask<Void, String, T>() {

			private Exception e;

			@Override
			protected void onPostExecute(final T result) {
				try {
					if (e == null) {
						if (!isCancelled()) {
							if (isActive()) {
								callback(result);
							}

						}
					} else {
						e.printStackTrace();
						if (isActive()) {
							failcallback(result, e);
						}
					}
				} finally {
					// 无论如何，关闭progress
					showProgress(false);
					end();
				}
			}

			@Override
			protected T doInBackground(final Void... params) {
				try {
					return backgroundWork();
				} catch (final Exception e) {
					Utils.dout(e);
					this.e = e;
				}
				return null;
			}

			@Override
			protected void onPreExecute() {
				if (dialogreid != -1) {
					dialog = new ProgressDialog(act);
					dialog.setCancelable(dialogcancel);
					dialog.setIndeterminate(dialogdeterminate);
					dialog.setMessage(act.getText(dialogreid));
					if (dialogcancel) {
						dialog.setOnCancelListener(CocoTask.this);
					}
				}
				showProgress(true);
				pre();
			}

			@Override
			protected void onProgressUpdate(final String... values) {
				progressUpdate(values);
			}

		}.execute();
	}

	protected void showProgress(final boolean show) {
		if (progress != null) {
			Common.showProgress(progress.get(), null, show);
		}
		if (dialog != null) {
			if (show) {
				dialog.show();
			} else {
				dialog.dismiss();
			}
		}
		if (listprogress != null) {
			listprogress.setVisibility(show == true ? View.VISIBLE
					: View.INVISIBLE);
			if (view != null) {
				view.setVisibility(show == false ? View.VISIBLE
						: View.INVISIBLE);
			}
		}

	}

	public void listprogress(final View mProgressContainer) {
		this.listprogress = mProgressContainer;
	}

	public void view(final View view) {
		this.view = view;
	}

	public void cancle() {
		if (task != null && !task.isCancelled()
				&& task.getStatus() != Status.FINISHED) {
			task.cancel(true);
		}
	}

	@Override
	public void onCancel(final DialogInterface arg0) {
		cancle();
	}

	public void updateDialogMsg(final int resId) {
		if (dialog != null) {
			this.dialog.setMessage(dialog.getContext().getText(resId));
		}
	}

	/**
	 * 设置为有dialog的Task
	 * 
	 * @param resId
	 * @return
	 */
	public CocoTask<T> dialog(final int resId) {
		this.dialogreid = resId;
		return this;
	}

	/**
	 * 是否可以取消
	 * 
	 * @return
	 */
	public CocoTask<T> cancelable() {
		this.dialogcancel = true;
		return this;
	}

	public CocoTask<T> determinate() {
		this.dialogdeterminate = false;
		return this;
	}
}
