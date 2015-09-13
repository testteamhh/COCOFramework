package com.cocosw.framework.core;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.cocosw.framework.uiquery.CocoQuery;
import com.cocosw.lifecycle.LifecycleDispatcher;

/**
 * Preference Fragment
 */
public abstract class BasePreferenceFragment extends PreferenceFragmentCompat implements Base.OnActivityInsetsCallback {

    protected CocoQuery q;

    public static void launch(Activity context, Class<BasePreferenceFragment> pref) {
        context.startActivity(new Intent(context, SinglePaneActivity.class)
                .setAction(pref.getName()));
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        q = new CocoQuery(getActivity());
        setPreferencesFromResource(perferenceXML(), s);
    }

    protected abstract int perferenceXML();

    protected void setupTitle(int icon, int title, boolean up) {
        if (getActionBar() == null)
            return;
        getActionBar().setDisplayHomeAsUpEnabled(up);
        getActionBar().setLogo(getResources().getDrawable(icon));
        getActionBar().setTitle(title);
    }

    protected ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
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
    public void onInsetsChanged(SystemBarTintManager.SystemBarConfig insets) {
        getListView().setClipToPadding(false);
        getListView().setPadding(
                0, insets.getPixelInsetTop(hasActionBarBlock())
                , insets.getPixelInsetRight(), insets.getPixelInsetBottom()
        );
    }

    private boolean hasActionBarBlock() {
        return false;
    }
}
