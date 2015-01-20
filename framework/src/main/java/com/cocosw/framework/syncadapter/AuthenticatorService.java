package com.cocosw.framework.syncadapter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Project: cocoframework
 * Created by LiaoKai(soarcn) on 2015/1/19.
 */
public class AuthenticatorService extends Service {

    // Instance field that stores the authenticator object
    private StubAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new StubAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}