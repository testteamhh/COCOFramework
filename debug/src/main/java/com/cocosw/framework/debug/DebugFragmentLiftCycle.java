/*
 * Copyright (c) 2014. www.cocosw.com
 */

package com.cocosw.framework.debug;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cocosw.lifecycle.FragmentLifecycleCallbacks;

/**
 * Project: insight
 * Created by LiaoKai(soarcn) on 14-3-18.
 */
public class DebugFragmentLiftCycle implements FragmentLifecycleCallbacks {
    @Override
    public void onFragmentCreated(final Fragment fragment, final Bundle savedInstanceState) {
        Log.d(fragment.getClass().getSimpleName(),"onFragmentCreated fragment" + fragment);
    }

    @Override
    public void onFragmentStarted(final Fragment fragment) {
        Log.d(fragment.getClass().getSimpleName(),"onFragmentStarted fragment" + fragment);
    }

    @Override
    public void onFragmentResumed(final Fragment fragment) {
        Log.d(fragment.getClass().getSimpleName(),"onFragmentResumed fragment" + fragment);
    }

    @Override
    public void onFragmentPaused(final Fragment fragment) {
        Log.d(fragment.getClass().getSimpleName(),"onFragmentPaused fragment" + fragment);
    }

    @Override
    public void onFragmentStopped(final Fragment fragment) {
        Log.d(fragment.getClass().getSimpleName(),"onFragmentStopped fragment" + fragment);
    }

    @Override
    public void onFragmentSaveInstanceState(final Fragment fragment, final Bundle outState) {
        Log.d(fragment.getClass().getSimpleName(),"onFragmentSaveInstanceState fragment" + fragment);
    }

    @Override
    public void onFragmentDestroyed(final Fragment fragment) {
        Log.d(fragment.getClass().getSimpleName(),"onFragmentDestroyed fragment" + fragment);
    }

    @Override
    public void onFragmentAttach(final Fragment fragment, final Activity activity) {
        Log.d(fragment.getClass().getSimpleName(),"onFragmentAttach fragment" + fragment);
    }

    @Override
    public void onFragmentDetach(final Fragment fragment) {
        Log.d(fragment.getClass().getSimpleName(),"onFragmentDetach fragment" + fragment);
    }

    @Override
    public void onFragmentActivityCreated(final Fragment fragment, final Bundle savedInstanceState) {
        Log.d(fragment.getClass().getSimpleName(),"onFragmentActivityCreated fragment" + fragment);
    }

    @Override
    public void onFragmentCreateView(final Fragment fragment, final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        Log.d(fragment.getClass().getSimpleName(),"onFragmentCreateView fragment" + fragment);
    }

    @Override
    public void onFragmentViewCreated(final Fragment fragment, final View view, final Bundle savedInstanceState) {
        Log.d(fragment.getClass().getSimpleName(),"onFragmentViewCreated fragment" + fragment);
    }
}
