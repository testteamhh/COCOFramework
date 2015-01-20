package com.cocosw.framework.syncadapter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;

/**
 * Project: cocoframework
 * Created by LiaoKai(soarcn) on 2015/1/19.
 */
public class SyncAdapterHelper {


    private final Context context;
    private String AUTHORITY;
    private Account mAccount;
    // An account type, in the form of a domain name
    private String ACCOUNT_TYPE;
    // The account name
    private String ACCOUNT = "dummyaccount";

    public SyncAdapterHelper(Context context,
                             String defaultUser,
                             String Authority,
                             String accountType) {
        ACCOUNT = defaultUser;
        this.AUTHORITY = Authority;
        this.ACCOUNT_TYPE = accountType;
        this.context = context;
        mAccount = createSyncAccount(context);
    }

    public SyncAdapterHelper(Context context, Account mAccount) {
        this.mAccount = mAccount;
        this.context = context;
    }

    /**
     * Create a new dummy account for the sync adapter
     *
     * @param context The application context
     */
    public Account createSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = mAccount == null ? new Account(
                ACCOUNT, ACCOUNT_TYPE) : mAccount;
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        Context.ACCOUNT_SERVICE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
        }
        return newAccount;
    }

    public void triggerSync() {
        // Pass the settings flags by inserting them in a bundle
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(mAccount, AUTHORITY, settingsBundle);
    }

    public void triggerSyncImmediately() {
        // Pass the settings flags by inserting them in a bundle
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        /*
         * Request the sync for the default account, authority, and
         * manual sync settings
         */
        ContentResolver.requestSync(mAccount, AUTHORITY, settingsBundle);
    }

    public void periodicSync(int minutes) {
        // Get the content resolver for your app
        context.getContentResolver();
        /*
         * Turn on periodic syncing
         */
        ContentResolver.addPeriodicSync(
                mAccount,
                AUTHORITY,
                Bundle.EMPTY,
                minutes * 3600);
    }

    public void turnOnSync() {
        context.getContentResolver();
        ContentResolver.setSyncAutomatically(mAccount, AUTHORITY, true);
    }

    public void turnOffSync() {
        context.getContentResolver();
        ContentResolver.setSyncAutomatically(mAccount, AUTHORITY, false);
    }
}
