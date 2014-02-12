package com.cocosw.framework.app;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * TODO Desc
 * <p/>
 * Project: cocoframework
 * User: Liao Kai(soarcn@gmail.com)
 * Date: 13-12-25
 * Time: 下午8:25
 */
public class CocoBus {

    private static Bus bus;

    public static Bus getInstance() {
        if (bus == null)
            bus = new Bus(ThreadEnforcer.ANY);
        return bus;
    }

}
