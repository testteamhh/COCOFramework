package com.cocosw.framework.core;

import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

import com.cocosw.accessory.connectivity.NetworkConnectivity;
import com.cocosw.framework.R;
import com.cocosw.framework.app.CocoApp;
import com.cocosw.framework.exception.CocoException;
import com.cocosw.framework.exception.ExceptionManager;
import com.cocosw.framework.uiquery.CocoQuery;

public abstract class CocoIntentService extends IntentService {

    protected final CocoQuery q;


    public CocoIntentService(final String name) {
        super(name);
        q = new CocoQuery(CocoApp.getApp());
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        try {
            run(intent);
        } catch (final Exception e) {
            onError(e);
        }
    }

    /**
     * Check network connection
     *
     * @throws CocoException
     */
    protected void checkNetwork() throws CocoException {
        if (!NetworkConnectivity.getInstance().isConnected()) {
            throw new CocoException(
                    getString(R.string.network_error));
        }
    }

    protected void onError(final Exception e) {
        try {
            ExceptionManager.handle(e, this);
        } catch (final CocoException e1) {
            e1.printStackTrace();
            Toast.makeText(this, e1.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    protected abstract void run(Intent intent) throws Exception;

}
