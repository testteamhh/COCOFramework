package com.cocosw.framework.core;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.HashMap;

/**
* Project: cocoframework
* Created by LiaoKai(soarcn) on 2014/9/21.
*/
public class RetainedFragment extends Fragment {

    HashMap<String,Object> data = new HashMap<>();

    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((Base) activity).retainedFragment = this;
    }

    void put(String key, Object obj) {
        data.put(key,obj);
    }

    void put(Object obj) {
        put(obj.getClass().getName(),obj);
    }

    <T> T get(String key) {
        return (T) data.get(key);
    }
}
