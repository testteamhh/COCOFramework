package com.cocosw.framework.debug;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ceelites.sharedpreferenceinspector.SharedPrefsBrowser;
import com.cocosw.accessory.utils.IntentUtil;
import com.cocosw.framework.core.BasePreferenceFragment;
import com.cocosw.framework.core.SinglePaneActivity;
import com.github.pedrovgs.lynx.LynxActivity;

/**
 * Project: cocoframework
 * Created by LiaoKai(soarcn) on 2015/5/11.
 */
public class DevPreference extends BasePreferenceFragment {


    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setBackgroundResource(R.color.holo_light_background);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findPreference("reboot").setOnPreferenceClickListener(new android.preference.Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(android.preference.Preference preference) {
                IntentUtil.doRestart(getActivity());
                return false;
            }
        });

        findPreference("dbinspector").setOnPreferenceClickListener(new android.preference.Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(android.preference.Preference preference) {
                startActivitySafely(LynxActivity.getIntent(getActivity()));
                return false;
            }
        });

        findPreference("pref").setOnPreferenceClickListener(new android.preference.Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(android.preference.Preference preference) {
                startActivitySafely(new Intent(getActivity(), SharedPrefsBrowser.class));
                return false;
            }
        });

        findPreference("logcat").setOnPreferenceClickListener(new android.preference.Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(android.preference.Preference preference) {
                startActivitySafely(new Intent(getActivity(), CatLogWindow.class));
                return false;
            }
        });

        findPreference("alarm").setOnPreferenceClickListener(new android.preference.Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(android.preference.Preference preference) {
                SinglePaneActivity.start(AlarmFragment.class, getActivity());
                return false;
            }
        });
    }

    @Override
    protected int perferenceXML() {
        return R.xml.coco_debug_pref_dev;
    }
}
