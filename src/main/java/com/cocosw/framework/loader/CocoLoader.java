package com.cocosw.framework.loader;

import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.widget.Toast;

public interface CocoLoader<T> extends LoaderCallbacks<T> {

    /**
     * 后台的操作或者数据读取
     * 
     * @param arg
     * @return
     * @throws Exception
     */
    T pendingData(Bundle arg) throws Exception;

    /**
     * 完成数据载入后的接口
     * 
     * @param items2
     */
    void onLoaderDone(final T items);

    /**
     * Show exception in a {@link Toast}
     * 
     * @param e
     * @param defaultMessage
     */
    void showError(final Exception e);

}
