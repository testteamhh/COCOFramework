package com.cocosw.framework.preference;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.cocosw.framework.core.SinglePaneActivity;
import com.cocosw.framework.uiquery.CocoQuery;

/**
 * Preference Fragment
 */
public abstract class BasePreferenceFragment extends PreferenceFragment {

    protected CocoQuery q;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        q = new CocoQuery(getActivity());
        // Load the preferences from an XML resource
        addPreferencesFromResource(perferenceXML());
    }

    protected abstract int perferenceXML();

    protected void setupTitle(int icon, int title, boolean up) {
        ((SherlockFragmentActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(up);
        ((SherlockFragmentActivity) getActivity()).getSupportActionBar().setLogo(getResources().getDrawable(icon));
        ((SherlockFragmentActivity) getActivity()).getSupportActionBar().setTitle(title);
    }

    public static void launch(Activity context, Class<BasePreferenceFragment> pref) {
        context.startActivity(new Intent(context, SinglePaneActivity.class)
                .setAction(pref.getName()));
    }
}
