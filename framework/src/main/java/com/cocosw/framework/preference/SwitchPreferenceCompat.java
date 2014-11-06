package com.cocosw.framework.preference;

import android.annotation.TargetApi;
import android.content.Context;
import android.preference.CheckBoxPreference;
import android.util.AttributeSet;

import com.cocosw.framework.R;

/**
 * Project: cocoframework
 * Created by LiaoKai(soarcn) on 2014/11/6.
 */
public class SwitchPreferenceCompat extends CheckBoxPreference {

    public SwitchPreferenceCompat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SwitchPreferenceCompat(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public SwitchPreferenceCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SwitchPreferenceCompat(Context context) {
        super(context);
        init();
    }

    private void init() {
        setWidgetLayoutResource(R.layout.preference_switch_layout);
    }
}
