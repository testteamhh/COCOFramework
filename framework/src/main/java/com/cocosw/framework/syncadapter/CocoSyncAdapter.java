package com.cocosw.framework.syncadapter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import com.cocosw.framework.log.Log;

/**
 * Project: cocoframework
 * Created by LiaoKai(soarcn) on 2014/12/23.
 */
public abstract class CocoSyncAdapter extends AbstractThreadedSyncAdapter {

    protected final AccountManager mAccountManager;

    public CocoSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mAccountManager = AccountManager.get(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {

        // Building a print of the extras we got
        StringBuilder sb = new StringBuilder();
        if (extras != null) {
            for (String key : extras.keySet()) {
                sb.append(key + "[" + extras.get(key) + "] ");
            }
        }

        Log.d("> onPerformSync for account[" + account.name + "]. Extras: " + sb.toString());

        try {
            run(account, extras, authority,
                    provider, syncResult);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    protected abstract void run(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) throws Throwable;
}
