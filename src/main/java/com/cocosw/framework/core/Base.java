/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cocosw.framework.core;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.cocosw.accessory.connectivity.NetworkConnectivity;
import com.cocosw.framework.BuildConfig;
import com.cocosw.framework.R;
import com.cocosw.framework.app.CocoBus;
import com.cocosw.framework.exception.CocoException;
import com.cocosw.framework.exception.ErrorCode;
import com.cocosw.framework.exception.ExceptionManager;
import com.cocosw.framework.loader.CocoLoader;
import com.cocosw.framework.loader.ThrowableLoader;
import com.cocosw.framework.uiquery.CocoQuery;
import com.cocosw.undobar.UndoBarController;
import com.cocosw.undobar.UndoBarController.UndoListener;
import com.jakewharton.scalpel.ScalpelFrameLayout;
import com.squareup.otto.Bus;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import org.jraf.android.util.activitylifecyclecallbackscompat.ApplicationHelper;
import org.jraf.android.util.activitylifecyclecallbackscompat.MainLifecycleDispatcher;

import java.lang.reflect.Field;
import java.util.Map;


/**
 * 所有Activity的抽象父类，提供一些共同的操作
 *
 * @author solosky <solosky772@qq.com>
 */
public abstract class Base<T> extends SherlockFragmentActivity implements
        DialogResultListener, CocoLoader<T> {

    protected CocoQuery q;
    private ThrowableLoader<T> loader;
    protected Bus bus = CocoBus.getInstance();

    @Override
    public void onDialogResult(final int requestCode, final int resultCode,
                               final Bundle arguments) {
        // Intentionally left blank
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bus.register(this);
        if (ApplicationHelper.PRE_ICS) MainLifecycleDispatcher.get().onActivityCreated(this, savedInstanceState);
        q = q == null ? new CocoQuery(this) : q;

        setContentView(layoutId());

        try {
            init(savedInstanceState);
        } catch (final RuntimeException e) {
            ExceptionManager.handle(e, this);
            return;
        } catch (final Exception e) {
            ExceptionManager.handle(e, this);
            finish();
            return;
        }
        onStartLoading();
        getSupportLoaderManager().initLoader(0, getIntent().getExtras(), this);

    }

    @Override
    public void setContentView(int layoutResId) {
        if (BuildConfig.DEBUG) {
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            ScalpelFrameLayout contentView = (ScalpelFrameLayout) layoutInflater.inflate(R.layout.ui_scalpel, null);
            contentView.addView(layoutInflater.inflate(layoutResId, null));
            super.setContentView(contentView);
        } else {
            super.setContentView(layoutResId);
        }
    }

    @Override
    public Loader<T> onCreateLoader(final int arg0, final Bundle arg) {
        loader = new ThrowableLoader<T>(this, null) {
            @Override
            public T loadData() throws Exception {
                return pendingData(arg);
            }

        };

        return loader;
    }

    /**
     * 后台的操作或者数据读取
     *
     * @param arg
     * @return
     * @throws Exception
     */
    @Override
    public T pendingData(final Bundle arg) throws Exception {
        return null;
    }

    @Override
    public void onLoadFinished(final Loader<T> loader, final T items) {
        final Exception exception = getException(loader);
        onStopLoading();
        if (exception != null) {
            showError(exception);
            return;
        }
        onLoaderDone(items);
    }

    /**
     * 完成数据载入后的接口
     *
     * @param items
     */
    @Override
    public void onLoaderDone(final T items) {

    }

    /**
     * Show exception
     *
     * @param e
     */
    @Override
    public void showError(final Exception e) {
        try {
            ExceptionManager.handle(e, this);
        } catch (final CocoException e1) {
            q.toast(e1.getMessage());
            showRefresh(e1);
        }
    }

    protected void showRefresh(final CocoException e) {
        UndoBarController.show(this, e.getMessage(), new UndoListener() {

            @Override
            public void onUndo(final Parcelable token) {
                refresh();
            }
        }, UndoBarController.RETRYSTYLE);
    }

    /**
     * Get exception from loader if it provides one by being a
     * {@link ThrowableLoader}
     *
     * @param loader
     * @return exception or null if none provided
     */
    protected Exception getException(final Loader<T> loader) {
        if (loader instanceof ThrowableLoader) {
            return ((ThrowableLoader<T>) loader).clearException();
        } else {
            return null;
        }
    }

    @Override
    public void onLoaderReset(final Loader<T> arg0) {

    }

    public abstract int layoutId();

    /**
     * 初始化完成后执行的方法
     *
     * @param saveBundle
     */
    protected abstract void init(Bundle saveBundle) throws Exception;

    private ProgressDialog dialog;

    /**
     * 重启本activity
     */
    public void restart() {
        // TweetUtils.restartActivity(this, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bus.register(this);
        if (ApplicationHelper.PRE_ICS) MainLifecycleDispatcher.get().onActivityDestroyed(this);
        hideLoading();
        q.CleanAllTask();
        // hack for null point exception
        try {
            final Field INSTANCES_MAP = LayoutInflater.class
                    .getDeclaredField("INSTANCES_MAP");
            INSTANCES_MAP.setAccessible(true);
            ((Map<?, ?>) INSTANCES_MAP.get(null)).remove(this);
        } catch (final Exception e) {
            // e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        invalidateOptionsMenu();
        super.onStart();
        if (ApplicationHelper.PRE_ICS) MainLifecycleDispatcher.get().onActivityStarted(this);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ApplicationHelper.PRE_ICS) MainLifecycleDispatcher.get().onActivityResumed(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (ApplicationHelper.PRE_ICS) MainLifecycleDispatcher.get().onActivityPaused(this);
    }

    protected void refresh() {
        onStartLoading();
        getSupportLoaderManager().restartLoader(0, new Bundle(), this);
    }

    protected ScalpelFrameLayout getScalpel() {
        if (!BuildConfig.DEBUG)
            throw new IllegalStateException("Only in DEBUG Mode!");
        return view(R.id.scalpel);
    }

    protected final <E extends View> E view(int resourceId) {
        return (E) findViewById(resourceId);
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (ApplicationHelper.PRE_ICS) MainLifecycleDispatcher.get().onActivityStopped(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (ApplicationHelper.PRE_ICS) MainLifecycleDispatcher.get().onActivitySaveInstanceState(this, outState);
    }


    protected void onStartLoading() {

    }

    protected void onStopLoading() {
        hideLoading();
    }

    /**
     * 用于检查网络情况
     *
     * @throws CocoException
     */
    protected void checkNetwork() throws CocoException {
        if (!NetworkConnectivity.getInstance().isConnected()) {
            throw new CocoException(ErrorCode.NETWORK_ERROR,
                    getString(R.string.network_error));
        }
    }

    /**
     * 显示一个loading的dialog, 非UI线程安全
     */
    protected void showLoading(final String str) {
        if (dialog == null) {
            dialog = new ProgressDialog(this);
            dialog.setCancelable(false);
        }
        dialog.setMessage(str);
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    /**
     * 关闭loading的dialog
     */
    protected void hideLoading() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private long exitTime;

    /**
     * 带有确认的退出，需要的时候，在onBackPressed中调用
     *
     * @return 如果确实退出了, 返回ture
     */
    protected boolean finishWithConfirm() {
        if (System.currentTimeMillis() - exitTime > 3000) {
            Crouton.cancelAllCroutons();
            q.crouton(R.string.confirm_opt_exit, new Style.Builder().setBackgroundColor(R.color.purered)
                    .setHeight(LayoutParams.WRAP_CONTENT).build());
            exitTime = System.currentTimeMillis();
            return false;
        } else {
            finish();
            return true;
        }
    }
}
