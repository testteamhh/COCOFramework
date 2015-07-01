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

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Toast;

import com.cocosw.accessory.connectivity.NetworkConnectivity;
import com.cocosw.accessory.utils.UIUtils;
import com.cocosw.accessory.views.QuickReturn;
import com.cocosw.framework.R;
import com.cocosw.framework.app.Injector;
import com.cocosw.framework.exception.CocoException;
import com.cocosw.framework.exception.ExceptionManager;
import com.cocosw.framework.loader.CocoLoader;
import com.cocosw.framework.loader.ThrowableLoader;
import com.cocosw.framework.uiquery.CocoQuery;
import com.cocosw.lifecycle.LifecycleDispatcher;
import com.cocosw.undobar.UndoBarController;
import com.cocosw.undobar.UndoBarController.UndoListener;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;

import butterknife.ButterKnife;


/**
 * Base activity for all other activities
 * We could put some shared methods and properties into this class to simply the UI development
 *
 * Created by kai on 19/05/15.
 */
public abstract class Base<T> extends AppCompatActivity implements
        DialogResultListener, CocoLoader<T> {

    private static final String TAG_RETAINED_STATE_FRAGMENT = "_retainedStateFragment";
    /**
     * Flag for a window belonging to an activity that responds to {@link KeyEvent#KEYCODE_MENU}
     * and therefore needs a Menu key. For devices where Menu is a physical button this flag is
     * ignored, but on devices where the Menu key is drawn in software it may be hidden unless
     * this flag is set.
     * <p/>
     * (Note that Action Bars, when available, are the preferred way to offer additional
     * functions otherwise accessed via an options menu.)
     * <p/>
     * {@hide}
     */
    private static final int FLAG_NEEDS_MENU_KEY = 0x08000000;
    protected CocoQuery q;
    RetainedFragment retainedFragment;
    private ThrowableLoader<T> loader;
    private HashSet<OnActivityInsetsCallback> mInsetCallbacks;
    private SystemBarTintManager.SystemBarConfig mInsets;
    private SystemBarTintManager tintManager;
    private QuickReturn qr;
    private ProgressDialog dialog;
    private boolean mCompatMenuKeyEnabled = false;
    private long exitTime;

    @Override
    public void onDialogResult(final int requestCode, final int resultCode,
                               final Bundle arguments) {
        // Intentionally left blank
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
        } catch (Exception e) {
            //workround for V7 appcompact
        }
        LifecycleDispatcher.get().onActivityCreated(this, savedInstanceState);
        q = q == null ? new CocoQuery(this) : q;
        setContentView(layoutId());
        tintManager = new SystemBarTintManager(this);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        // enable status bar tint
        tintManager.setStatusBarTintEnabled(true);
        // enable navigation bar tint
        tintManager.setNavigationBarTintEnabled(true);
        if (UIUtils.hasKitKat()) {
            initTint(tintManager);
        }

        ButterKnife.bind(this);
        // use Retained Fragment to handle runtime changes
        if (hasRetainData()) {
            FragmentManager fm = getSupportFragmentManager();
            retainedFragment = (RetainedFragment) fm.findFragmentByTag(TAG_RETAINED_STATE_FRAGMENT);

            // create the fragment and data the first time
            if (retainedFragment == null) {
                // add the fragment
                retainedFragment = new RetainedFragment();
                fm.beginTransaction().add(retainedFragment, TAG_RETAINED_STATE_FRAGMENT).commit();
            }
        }
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

    @TargetApi(19)
    private void initTint(SystemBarTintManager tintManager) {
        TypedValue typedValue = new TypedValue();
        try {
            getTheme().resolveAttribute(getPackageManager().getActivityInfo(getComponentName(), PackageManager.GET_META_DATA).theme, typedValue, true);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        int[] attribute = new int[]{R.attr.colorPrimary, R.attr.colorPrimaryDark, R.attr.navigationBarColor, R.attr.statusBarColor};
        TypedArray array = this.obtainStyledAttributes(typedValue.resourceId, attribute);

        int colorPrimary = array.getResourceId(0, R.color.black);
        int colorPrimaryDark = array.getResourceId(1, -1);
        int navigationBarColor = array.getResourceId(2, -1);
        int statusBarColor = array.getResourceId(3, -1);

        colorPrimaryDark = colorPrimaryDark < 0 ? colorPrimary : colorPrimaryDark;
        navigationBarColor = navigationBarColor < 0 ? colorPrimaryDark : navigationBarColor;
        statusBarColor = statusBarColor < 0 ? colorPrimaryDark : statusBarColor;

        tintManager.setStatusBarTintResource(statusBarColor);
        tintManager.setNavigationBarTintResource(navigationBarColor);
    }

    protected SystemBarTintManager getTintManager() {
        return tintManager;
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
     * Set up your fragment ui, which exactly like you did in {@link #onCreate(Bundle)} with inflated view
     *
     * @param saveBundle
     */
    protected abstract void init(Bundle saveBundle) throws Exception;

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

    /**
     * Enable the compatibility menu button on devices that don't have
     * a permanent menu key.
     *
     * @param enabled true to enable the compatibility menu button, false to disable it.
     */
    protected void setCompatMenuKeyEnabled(boolean enabled) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            ViewConfiguration vc = ViewConfiguration.get(this);
            if (!vc.hasPermanentMenuKey()) {
                if (enabled) {
                    getWindow().addFlags(FLAG_NEEDS_MENU_KEY);
                } else {
                    getWindow().clearFlags(FLAG_NEEDS_MENU_KEY);
                }
                mCompatMenuKeyEnabled = enabled;
            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (mCompatMenuKeyEnabled) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_UP) {
                onCompatMenuKeyPressed();
                return false;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * Override to implement a compatibility menu key press action.
     */
    protected void onCompatMenuKeyPressed() {

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
        getSupportLoaderManager().restartLoader(this.hashCode(), new Bundle(), this);
    }

    protected final <E extends View> E view(int resourceId) {
        return (E) findViewById(resourceId);
    }

    @Override
    protected void onStop() {
        super.onStop();
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

    /**
     * Finish acitivity with confirm
     *
     * @return return true if activity got be closed.
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

    protected void addInsetChangedCallback(OnActivityInsetsCallback callback) {
        if (mInsetCallbacks == null) {
            mInsetCallbacks = new HashSet<>();
        }
        mInsetCallbacks.add(callback);

        if (mInsets != null) {
            callback.onInsetsChanged(mInsets);
        }
    }

    protected void removeInsetChangedCallback(OnActivityInsetsCallback callback) {
        if (mInsetCallbacks != null) {
            mInsetCallbacks.remove(callback);
        }
    }

    protected void onInsetsChanged(SystemBarTintManager.SystemBarConfig insets) {
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

    protected void resetInsets() {
        setInsetTopAlpha(255);
    }

    protected boolean hasRetainData() {
        return true;
    }

    protected void save(String key, Object obj) {
        if (retainedFragment != null)
            retainedFragment.put(key, obj);
    }

    protected void save(Object obj) {
        if (retainedFragment != null)
            retainedFragment.put(obj.getClass().getName(), obj);
    }

    protected <T> T load(String key) {
        if (retainedFragment != null)
            return (T) retainedFragment.get(key);
        return null;
    }

    public void startActivitySafely(Intent intent) {
        try {
            super.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
        } catch (SecurityException e) {
            Toast.makeText(this, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
        }
    }

    public void startActivityForResultSafely(Intent intent, int requestCode) {
        try {
            super.startActivityForResult(intent, requestCode);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
        } catch (SecurityException e) {
            Toast.makeText(this, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
        }
    }

    protected Object load(Object obj) {
        return obj = load(obj.getClass().getName());
    }

    protected interface OnActivityInsetsCallback {
        void onInsetsChanged(@NonNull SystemBarTintManager.SystemBarConfig insets);
    }

}
