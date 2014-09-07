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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import com.cocosw.accessory.connectivity.NetworkConnectivity;
import com.cocosw.accessory.utils.UIUtils;
import com.cocosw.framework.R;
import com.cocosw.framework.app.CocoBus;
import com.cocosw.framework.app.Injector;
import com.cocosw.framework.exception.CocoException;
import com.cocosw.framework.exception.ExceptionManager;
import com.cocosw.framework.loader.CocoLoader;
import com.cocosw.framework.loader.ThrowableLoader;
import com.cocosw.framework.uiquery.CocoQuery;
import com.cocosw.lifecycle.LifecycleDispatcher;
import com.cocosw.undobar.UndoBarController;
import com.cocosw.undobar.UndoBarController.UndoListener;
import com.squareup.otto.Bus;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;

import butterknife.ButterKnife;


/**
 * Activity
 *
 * @author solosky <solosky772@qq.com>
 */
public abstract class Base<T> extends ActionBarActivity implements
        DialogResultListener, CocoLoader<T>{

    protected CocoQuery q;
    private ThrowableLoader<T> loader;
    protected Bus bus = CocoBus.getInstance();
    private HashSet<OnActivityInsetsCallback> mInsetCallbacks;
    private SystemBarTintManager.SystemBarConfig mInsets;
    private SystemBarTintManager tintManager;


    @Override
    public void onDialogResult(final int requestCode, final int resultCode,
                               final Bundle arguments) {
        // Intentionally left blank
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
        } catch (Exception e) {

        }
        LifecycleDispatcher.get().onActivityCreated(this, savedInstanceState);
        q = q == null ? new CocoQuery(this) : q;
        setContentView(layoutId());
        tintManager = new SystemBarTintManager(this);

            // enable status bar tint
        tintManager.setStatusBarTintEnabled(true);
            // enable navigation bar tint
        tintManager.setNavigationBarTintEnabled(true);
        if (UIUtils.hasKitKat()) {
            TypedValue typedValue = new TypedValue();
            try {
                getTheme().resolveAttribute(getPackageManager().getActivityInfo(getComponentName(),PackageManager.GET_META_DATA).theme, typedValue, true);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            int[] attribute = new int[] {R.attr.colorPrimary,R.attr.colorPrimaryDark,R.attr.navigationBarColor,R.attr.statusBarColor};
            TypedArray array = this.obtainStyledAttributes(typedValue.resourceId, attribute);

            int colorPrimary = array.getResourceId(0, R.color.black);
            int colorPrimaryDark = array.getResourceId(1, -1);
            int navigationBarColor = array.getResourceId(2, -1);
            int statusBarColor = array.getResourceId(3, -1);

            colorPrimaryDark = colorPrimaryDark<0?colorPrimary:colorPrimaryDark;
            navigationBarColor = navigationBarColor<0? colorPrimaryDark:navigationBarColor;
            statusBarColor = statusBarColor<0?colorPrimaryDark:statusBarColor;

            tintManager.setStatusBarTintResource(statusBarColor);
            tintManager.setNavigationBarTintResource(navigationBarColor);
        }

        ButterKnife.inject(this);
        try {
            init(savedInstanceState);
        } catch (final RuntimeException e) {
            ExceptionManager.error(e, this);
            return;
        } catch (final Exception e) {
            ExceptionManager.error(e, this);
            finish();
            return;
        }
        onStartLoading();
        getSupportLoaderManager().initLoader(this.hashCode(), getIntent().getExtras(), this);
    }

    protected void inject() {
        Injector.inject(this);
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
     * Loading data in background
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
     * This interface will be triggered after background been loaded
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
     * Restart current activity
     */
    protected void restart() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();

        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LifecycleDispatcher.get().onActivityDestroyed(this);
        hideLoading();
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
        super.onStart();
        bus.register(this);
        supportInvalidateOptionsMenu();
        LifecycleDispatcher.get().onActivityStarted(this);
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
        onInsetsChanged(tintManager.getConfig());
        LifecycleDispatcher.get().onActivityResumed(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        LifecycleDispatcher.get().onActivityPaused(this);
    }

    protected void refresh() {
        onStartLoading();
        getSupportLoaderManager().restartLoader(0, new Bundle(), this);
    }


    protected final <E extends View> E view(int resourceId) {
        return (E) findViewById(resourceId);
    }


    @Override
    protected void onStop() {
        super.onStop();
        bus.unregister(this);
        LifecycleDispatcher.get().onActivityStopped(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        LifecycleDispatcher.get().onActivitySaveInstanceState(this, outState);
    }


    protected void onStartLoading() {

    }

    protected void onStopLoading() {
        hideLoading();
    }

    /**
     * Check network connection
     *
     * @throws CocoException
     */
    protected void checkNetwork() throws CocoException {
        if (!NetworkConnectivity.getInstance().isConnected()) {
            throw new CocoException(getString(R.string.network_error));
        }
    }

    /**
     * Show loading dialog, not safe for UI thread
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
     * Close loading dialog
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
            showExitConfirm();
            exitTime = System.currentTimeMillis();
            return false;
        } else {
            finish();
            return true;
        }
    }

    /**
     * UI will be shown when confirm activity finishing, call in finishWithConfirm()
     */
    protected void showExitConfirm() {
        new UndoBarController.UndoBar(this).message(R.string.confirm_opt_exit).duration(3000).show();
    }

    public void addInsetChangedCallback(OnActivityInsetsCallback callback) {
        if (mInsetCallbacks == null) {
            mInsetCallbacks = new HashSet<>();
        }
        mInsetCallbacks.add(callback);

        if (mInsets != null) {
            callback.onInsetsChanged(mInsets);
        }
    }

    public void removeInsetChangedCallback(OnActivityInsetsCallback callback) {
        if (mInsetCallbacks != null) {
            mInsetCallbacks.remove(callback);
        }
    }

    public void onInsetsChanged(SystemBarTintManager.SystemBarConfig insets) {
        mInsets = insets;

        if (mInsetCallbacks != null && !mInsetCallbacks.isEmpty()) {
            for (OnActivityInsetsCallback callback : mInsetCallbacks) {
                callback.onInsetsChanged(insets);
            }
        }
    }

    public void setInsetTopAlpha(float alpha) {
        tintManager.setStatusBarAlpha(alpha);
    }

    public void resetInsets() {
        setInsetTopAlpha(255);
    }

    public static interface OnActivityInsetsCallback {
        public void onInsetsChanged(SystemBarTintManager.SystemBarConfig insets);
    }

}
