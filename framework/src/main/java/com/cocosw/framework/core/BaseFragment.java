package com.cocosw.framework.core;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cocosw.accessory.connectivity.NetworkConnectivity;
import com.cocosw.framework.R;
import com.cocosw.framework.app.Injector;
import com.cocosw.framework.exception.CocoException;
import com.cocosw.framework.exception.ExceptionManager;
import com.cocosw.framework.loader.CocoLoader;
import com.cocosw.framework.loader.ThrowableLoader;
import com.cocosw.framework.uiquery.CocoQuery;
import com.cocosw.lifecycle.LifecycleDispatcher;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Base Fragment, all other fragments in this application should extend from this class.
 *
 * - Loader has been integrated in, so you could simply running async background task in pendingdata()
 * - ButterKnife integrated
 * - Safely launch intent
 *
 * Created by kai on 19/05/15.
 */
public abstract class BaseFragment<T> extends Fragment implements
        DialogResultListener, CocoLoader<T>, Base.OnActivityInsetsCallback {

    /**
     * Loader would not be initialized by fragment.
     */
    protected static final int NEVER = -1;
    /**
     * Loader will be initialized when fragment was created
     */
    protected static final int ONCREATE = 0;
    protected Context context;
    protected Loader<T> loader;
    protected CocoQuery q;
    protected View v;
    CocoDialog parentDialog;
    private T items;
    private boolean loaderRunning;


    /**
     * Check network connection
     *
     * @throws CocoException
     */
    protected void checkNetwork() throws CocoException {
        if (!NetworkConnectivity.getInstance().checkConnected()) {
            throw new CocoException(getString(R.string.network_error));
        }
    }

    public ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    public AppCompatActivity getActionBarActivity() {
        return ((AppCompatActivity) getActivity());
    }

    /**
     * Close current UI container(activity/dialog)
     */
    public void finish() {
        if (parentDialog != null) {
            parentDialog.dismiss();
        } else {
            getActivity().finish();
        }
    }

    protected SystemBarTintManager getTintManager() {
        if (getActivity() instanceof Base) {
            return ((Base) getActivity()).getTintManager();
        }
        return null;
    }

    /**
     * run on Ui thread
     *
     * @param runnable
     */
    protected void runOnUiThread(Runnable runnable) {
        getActivity().runOnUiThread(runnable);
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

    public ThrowableLoader<T> getLoader() {
        return (ThrowableLoader<T>) loader;
    }

    /**
     * When the loader been initialized
     */
    protected int getLoaderOn() {
        return BaseFragment.NEVER;
    }

    public CharSequence getTitle() {
        return "";
    }

    /**
     * Is this fragment still part of an activity and usable from the UI-thread?
     *
     * @return true if usable on the UI-thread, false otherwise
     */
    protected boolean isUsable() {
        return getActivity() != null;
    }

    /**
     * The layout file which will be inflated for this fragment
     * @return layout file id
     */
    public abstract int layoutId();

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LifecycleDispatcher.get().onFragmentActivityCreated(this, savedInstanceState);
        if (getLoaderOn() == ONCREATE && reloadNeeded(savedInstanceState)) {
            onStartLoading();
            getLoaderManager().initLoader(this.hashCode(), getArguments(), this);
        }
    }

    protected boolean reloadNeeded(final Bundle savedInstanceState) {
        return true;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LifecycleDispatcher.get().onFragmentCreated(this, savedInstanceState);
        context = getActivity();
        setHasOptionsMenu(true);
        q = new CocoQuery(getActivity());
    }


    protected void save(String key, Object obj) {
        if (getActivity() instanceof Base)
            ((Base) getActivity()).save(key, obj);
    }

    protected void save(Object obj) {
        if (getActivity() instanceof Base && obj != null)
            ((Base) getActivity()).save(this.getClass().getName() + obj.getClass().getName(), obj);
    }

    protected <T> T load(String key) {
        if (getActivity() instanceof Base)
            return (T) ((Base) getActivity()).load(key);
        return null;
    }

    protected Object load(Object obj) {
        if (getActivity() instanceof Base && obj != null)
            return ((Base) getActivity()).load(this.getClass().getName() + obj.getClass().getName());
        return null;
    }

    @Override
    public Loader<T> onCreateLoader(final int id, final Bundle args) {
        onStartLoading();
        return loader = new ThrowableLoader<T>(getActivity(), items) {
            @Override
            public T loadData() throws Exception {
                return pendingData(args);
            }
        };
    }

    protected void inject() {
        Injector.inject(this);
    }

    protected void onStartLoading() {
        loaderRunning = true;
    }

    protected void onStopLoading() {
        loaderRunning = false;
        hideLoading();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             final ViewGroup container, final Bundle savedInstanceState) {
        LifecycleDispatcher.get().onFragmentCreateView(this, inflater, container, savedInstanceState);
        v = inflater.inflate(layoutId(), null);
        ButterKnife.bind(this, v);
        if (q == null)
            q = new CocoQuery(v);
        else
            q.recycle(v);
        try {
            setupUI(v, savedInstanceState);
        } catch (final Exception e) {
            ExceptionManager.error(e, context, this);
        }
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        v = null;
        q = null;
        loader = null;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LifecycleDispatcher.get().onFragmentViewCreated(this, view, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        LifecycleDispatcher.get().onFragmentSaveInstanceState(this, outState);
    }

    @Override
    public void onStop() {
        super.onStop();
        LifecycleDispatcher.get().onFragmentStopped(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() instanceof Base) {
            Base activity = ((Base) getActivity());
            activity.removeInsetChangedCallback(this);
            activity.resetInsets();
        }
        LifecycleDispatcher.get().onFragmentPaused(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof Base) {
            ((Base) getActivity()).addInsetChangedCallback(this);
        }
        LifecycleDispatcher.get().onFragmentResumed(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LifecycleDispatcher.get().onFragmentDetach(this);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        LifecycleDispatcher.get().onFragmentAttach(this, activity);
    }


    @Override
    public void onDialogResult(final int requestCode, final int resultCode,
                               final Bundle arguments) {
        // Intentionally left blank
    }

    /**
     * A handy way of findViewById
     * @param resourceId view id
     * @return view instance
     */
    protected final <E extends View> E view(int resourceId) {
        return (E) v.findViewById(resourceId);
    }


    @Override
    public void onLoaderDone(final T items) {

    }

    @Override
    public void onLoaderReset(final Loader<T> loader) {

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

    @Override
    public T pendingData(final Bundle arg) throws Exception {
        return null;
    }

    /**
     * Reload current loader
     */
    public void refresh() {
        refresh(getArguments());
    }

    /**
     * Reload current loader with arguments
     *
     * @param b arguments
     */
    protected void refresh(final Bundle b) {
        onStartLoading();
        getLoaderManager().restartLoader(this.hashCode(), b, this);
    }

    /**
     * Is current loader running or not
     * @return Is current loader running or not
     */
    protected boolean isLoaderRunning() {
        return loaderRunning;
    }

    /**
     * Set up your fragment ui, which exactly like you did in {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} with inflated view
     *
     * @param view
     * @param bundle
     * @throws Exception
     */
    protected abstract void setupUI(View view, Bundle bundle) throws Exception;

    /**
     * Failback of loader, meaning there is a exception been catched in {@link #pendingData(Bundle)}
     *
     * @param e the catch exception in loader
     */
    @Override
    public void showError(final Exception e) {
        try {
            ExceptionManager.handle(e, getActivity(), this);
        } catch (final CocoException e1) {
            showRefresh(e1);
        }
    }

    protected void showRefresh(final CocoException e) {
        Snackbar.make(v, e.getMessage(), Snackbar.LENGTH_LONG);
    }

    private Base<?> getBase() {
        try {
            return (Base<?>) getActivity();
        } catch (final Exception e) {
            return null;
        }
    }

    protected void showLoading(final String str) {
        getBase().showLoading(str);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LifecycleDispatcher.get().onFragmentDestroyed(this);

    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragments = getChildFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    protected void hideLoading() {
        if (getBase() != null)
            getBase().hideLoading();
    }

    @Override
    public void onInsetsChanged(SystemBarTintManager.SystemBarConfig insets) {
        if (v != null)
            v.setPadding(
                    0, insets.getPixelInsetTop(hasActionBarBlock())
                    , insets.getPixelInsetRight(), insets.getPixelInsetBottom()
            );
    }

    protected boolean hasActionBarBlock() {
        return false;
    }

    /**
     * Safely start a intent without worry activity not found
     * @param intent the intent try to launch
     */
    public void startActivitySafely(Intent intent) {
        try {
            super.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), R.string.activity_not_found, Toast.LENGTH_SHORT).show();
        } catch (SecurityException e) {
            Toast.makeText(getActivity(), R.string.activity_not_found, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Safely start a intent for result without worry activity not found
     * @param intent the intent try to launch
     */
    public void startActivityForResultSafely(Intent intent, int requestCode) {
        try {
            super.startActivityForResult(intent, requestCode);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), R.string.activity_not_found, Toast.LENGTH_SHORT).show();
        } catch (SecurityException e) {
            Toast.makeText(getActivity(), R.string.activity_not_found, Toast.LENGTH_SHORT).show();
        }
    }

    public boolean onBackPressed() {
        return false;
    }

}
