package com.cocosw.framework.uiquery;

/**
 * 回调接口
 *
 * @param <T>
 * @author kaliao
 */
public interface CocoQueryCallBack<T> {

    /**
     * 无异常时的callback.
     *
     * @param url    the url
     * @param object the object
     * @param status the status
     */
    public void callback(T object);

    /**
     * 有异常时的callback.
     *
     * @param url    the url
     * @param object the object
     * @param status the status
     */
    public void failcallback(T object, Exception status);

    /**
     * 状态更新
     *
     * @param values
     */
    public void progressUpdate(String... values);

    /**
     * 无论成功失败都要做的善后
     */
    public void end();

}
