package com.cocosw.framework.syncadapter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Project: cocoframework
 * Created by LiaoKai(soarcn) on 2014/12/23.
 */
public abstract class SyncService extends Service {
    // Storage for an instance of the sync adapter
    private static CocoSyncAdapter sSyncAdapter = null;
    // Object to use as a thread-safe lock
    private static final Object sSyncAdapterLock = new Object();
    /*
     * Instantiate the sync adapter object.
     */
    @Override
    public void onCreate() {
        /*
         * Create the sync adapter as a singleton.
         * Set the sync adapter as syncable
         * Disallow parallel syncs
         */
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = getSyncAdatper();
            }
        }
    }

    protected abstract CocoSyncAdapter getSyncAdatper();

    /**
     * Return an object that allows the system to invoke
     * the sync adapter.
     *
     */
    @Override
    public IBinder onBind(Intent intent) {
        /*
         * Get the object that allows external processes
         * to call onPerformSync(). The object is created
         * in the base class code when the SyncAdapter
         * constructors call super()
         */
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
