package com.cocosw.framework.uiquery;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.util.Log;
import android.view.View;
import com.androidquery.util.Common;
import com.cocosw.framework.exception.ExceptionManager;
import com.cocosw.framework.loader.ThrowableLoader;

import java.lang.ref.WeakReference;

//import com.umeng.analytics.MobclickAgent;

public abstract class Loader<T> implements CocoQueryCallBack<T>,
		OnCancelListener, LoaderCallbacks<T> {

	private WeakReference<Object> progress;

	private View listprogress;

	private View view;

	public abstract T backgroundWork(Bundle arg1) throws Exception;

	private CocoQueryCallBack<T> callback;

	private ProgressDialog dialog;

	private int dialogreid = -1;

	private boolean dialogcancel = false;

	private boolean dialogdeterminate = true;

	private FragmentActivity context;

	private ThrowableLoader<T> loader;

	public void setCallback(final CocoQueryCallBack<T> callback) {
		this.callback = callback;
	}

	/**
	 * 无异常时的callback.
	 * 

	 * @param object
	 *            the object
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
	@SuppressWarnings("deprecation")
	@Override
	public void failcallback(final T object, final Exception e) {
		if (callback != null) {
			callback.failcallback(object, e);
		} else {
            ExceptionManager.handle(e,context);
		}
	}

	/**
	 * 开始活动，很少用
	 */
	public void pre() {

	}

	public Loader<T> progress(final Object progress) {
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
		if (loader.isStarted()) {
			loader.stopLoading();
		}
		loader.abandon();
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
	public Loader<T> dialog(final int resId) {
		this.dialogreid = resId;
		return this;
	}

	/**
	 * 是否可以取消
	 * 
	 * @return
	 */
	public Loader<T> cancelable() {
		this.dialogcancel = true;
		return this;
	}

	public Loader<T> determinate() {
		this.dialogdeterminate = false;
		return this;
	}

	public void async(final FragmentActivity context, final Bundle arg) {
		this.context = context;
		context.getSupportLoaderManager().initLoader(dialogreid, arg, this);
	}

	public void async(final Fragment fragment, final FragmentActivity context,
			final Bundle arg) {
		this.context = context;
		if (!fragment.isRemoving()) {
			fragment.getLoaderManager().initLoader(dialogreid, arg, this);
		}
	}

	@Override
	public android.support.v4.content.Loader<T> onCreateLoader(final int arg0,
			final Bundle arg1) {
		loader = new ThrowableLoader<T>(context, null) {
			@Override
			public T loadData() throws Exception {
				return backgroundWork(arg1);
			}
		};
		if (dialogreid != -1) {
			dialog = new ProgressDialog(context);
			dialog.setCancelable(dialogcancel);
			dialog.setIndeterminate(dialogdeterminate);
			dialog.setMessage(context.getText(dialogreid));
			if (dialogcancel) {
				dialog.setOnCancelListener(Loader.this);
			}
		}
		showProgress(true);
		pre();
		return loader;
	}

	@Override
	public void onLoadFinished(final android.support.v4.content.Loader<T> arg0,
			final T arg1) {
		final Exception exception = getException(loader);
		if (exception != null) {
			failcallback(arg1, exception);
			showProgress(false);
			end();
			return;
		}

		callback(arg1);
		showProgress(false);
		end();
	}

	/**
	 * Get exception from loader if it provides one by being a
	 * {@link ThrowableLoader}
	 * 
	 * @param loader
	 * @return exception or null if none provided
	 */
	protected Exception getException(
			final android.support.v4.content.Loader<T> loader) {
		if (loader instanceof ThrowableLoader) {
			return ((ThrowableLoader<T>) loader).clearException();
		} else {
			return null;
		}
	}

	@Override
	public void onLoaderReset(final android.support.v4.content.Loader<T> arg0) {
		// TODO Auto-generated method stub

	}

}
