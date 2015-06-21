package com.cocosw.framework.core;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cocosw.framework.app.CocoBus;
import com.cocosw.framework.exception.ExceptionManager;
import com.cocosw.framework.loader.CocoLoader;
import com.cocosw.framework.loader.ThrowableLoader;
import com.cocosw.framework.uiquery.CocoQuery;
import com.squareup.otto.Bus;

import butterknife.ButterKnife;

/**
 * Besides DialogFragment, BaseDialog integrate CocoQuery/ButterKniff/Loader, a better callback with activity.
 *
 * @param <T>
 */
public abstract class BaseDialog<T> extends DialogFragment implements
        CocoLoader<T> {

    /**
     * Dialog message
     */
    private static final String ARG_TITLE = "title";

    /**
     * Dialog message
     */
    private static final String ARG_MESSAGE = "message";

    /**
     * Request code
     */
    private static final String ARG_REQUEST_CODE = "requestCode";

    protected Bus bus = CocoBus.getInstance();

    protected Context context;
    protected View v;
    protected CocoQuery q;

    private ThrowableLoader<T> loader;

    private boolean result = false;

    /**
     * Is this fragment usable from the UI-thread
     *
     * @return true if usable, false otherwise
     */
    protected boolean isUsable() {
        return getActivity() != null;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        setStyle(DialogFragment.STYLE_NO_FRAME, getStyle());
        if (getArguments() != null) {
            result = getArguments().getInt(BaseDialog.ARG_REQUEST_CODE) > 0;
        }
    }

    public int getStyle() {
        return android.R.style.Theme_Translucent;
    }

    public ThrowableLoader<T> getLoader() {
        return loader;
    }

    @Override
    public Loader<T> onCreateLoader(final int id, final Bundle args) {
        loader = new ThrowableLoader<T>(getActivity(), null) {
            @Override
            public T loadData() throws Exception {
                return pendingData(args);
            }

        };
        return loader;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             final ViewGroup container, final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        v = inflater.inflate(layoutId(), container, false);
        bus.register(this);
        ButterKnife.inject(this, v);
        q = new CocoQuery(getActivity(), v);
        try {
            setupUI(v, savedInstanceState);
        } catch (final Exception e) {
            ExceptionManager.error(e, context, this);
        }
        return v;
    }

    public static void show(final FragmentManager fm, final DialogFragment d) {

        final FragmentTransaction ft = fm.beginTransaction();
        final Fragment prev = fm.findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        d.show(ft, null);
    }

    public static void showForResult(final FragmentManager fm,
                                     final DialogFragment d, final int requestCode) {

        final FragmentTransaction ft = fm.beginTransaction();
        final Fragment prev = fm.findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        if (d.getArguments() == null) {
            d.setArguments(BaseDialog.createArguments(null, null, requestCode));
        } else {
            d.getArguments().putInt(BaseDialog.ARG_REQUEST_CODE, requestCode);
        }

        d.show(ft, null);
    }


    public abstract int layoutId();

    protected abstract void setupUI(View view, Bundle bundle) throws Exception;

    /**
     * Create bundle with standard arguments
     *
     * @param title
     * @param message
     * @param requestCode
     * @return bundle
     */
    protected static Bundle createArguments(final String title,
                                            final String message, final int requestCode) {
        final Bundle arguments = new Bundle();
        arguments.putInt(BaseDialog.ARG_REQUEST_CODE, requestCode);
        arguments.putString(BaseDialog.ARG_TITLE, title);
        arguments.putString(BaseDialog.ARG_MESSAGE, message);
        return arguments;
    }

    /**
     * Call back to the activity with the dialog result
     *
     * @param resultCode
     */
    protected void onResult(final int resultCode) {
        if (result) {
            ((DialogResultListener) getActivity()).onDialogResult(
                    getArguments().getInt(BaseDialog.ARG_REQUEST_CODE),
                    resultCode, getArguments());
        }
    }

    /**
     * Get title
     *
     * @return title
     */
    protected String getTitle() {
        return getArguments().getString(BaseDialog.ARG_TITLE);
    }

    /**
     * Get message
     *
     * @return mesage
     */
    protected String getMessage() {
        return getArguments().getString(BaseDialog.ARG_MESSAGE);
    }

    @Override
    public void onCancel(final DialogInterface dialog) {
        onResult(Activity.RESULT_CANCELED);
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme());
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        onResult(Activity.RESULT_OK);
    }

    public static void show(final FragmentManager fm, final DialogFragment d,
                            final Bundle arg) {
        d.setArguments(arg);
        BaseDialog.show(fm, d);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bus.unregister(this);
        ButterKnife.reset(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    protected final <E extends View> E view(int resourceId) {
        return (E) v.findViewById(resourceId);
    }

    /**
     * Show exception in a {@link Toast}
     *
     * @param e
     */
    @Override
    public void showError(final Exception e) {
    }

    @Override
    public T pendingData(final Bundle args) throws Exception {
        return null;
    }

    @Override
    public void onLoaderDone(final T items) {

    }

    @Override
    public void onLoaderReset(final Loader<T> loader) {

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
    public void onLoadFinished(final Loader<T> loader, final T items) {
        final Exception exception = getException(loader);
        if (exception != null) {
            showError(exception);
            return;
        }
        onLoaderDone(items);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(this.hashCode(), getArguments(), this);
    }

    protected Base<?> getBase() {
        return (Base<?>) getActivity();
    }
}
