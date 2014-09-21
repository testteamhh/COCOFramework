package com.cocosw.framework.core;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Project: cocoframework
 * Created by LiaoKai(soarcn) on 2014/9/8.
 */
public class Presenter {

    private Fragment from;
    private Activity fromAct;

    private Class<? extends BaseFragment> target;

    private Class<? extends Activity> container;

    private Intent extra;
    private boolean blank;

    public Presenter(Fragment from) {
        this.from = from;
    }

    public Presenter(Activity fromAct) {
        this.fromAct = fromAct;
    }

    public Presenter target(Class<? extends BaseFragment> target) {
        this.target = target;
        return this;
    }

    public Presenter container(Class<? extends Activity> container) {
        this.container = container;
        return this;
    }

    public Presenter blank() {
        blank = true;
        return this;
    }

    public Presenter extra(Intent extra) {
        this.extra = extra;
        return this;
    }

    /**
     * Open target fragment in target container
     */
    public void open() {
        if (extra == null)
            extra = new Intent();

        // start activity from fragment
        if (from!=null) {
            fromAct = from.getActivity();
            if (fromAct == null)
                return;

            if (!(fromAct instanceof DualPaneActivity) || blank) {
                // open target fragment in a new container
                from.startActivity(new Intent(fromAct, container==null?SinglePaneActivity.class:container)
                        .setAction(target.getName()).putExtras(extra));
            } else {
                // open target fragment in current DualPaneActivity
                ((DualPaneActivity)fromAct).openDetail(target,  extra);
            }
        } else {
            if (!(fromAct instanceof DualPaneActivity) || blank) {
                // open target fragment in a new container
                fromAct.startActivity(new Intent(fromAct, container==null?SinglePaneActivity.class:container)
                        .setAction(target.getName()).putExtras(extra));
            } else {
                // open target fragment in current DualPaneActivity
                ((DualPaneActivity)fromAct).openDetail(target,  extra);
            }
        }
    }

    public void openForResult(int requestCode) {
        if (extra == null)
            extra = new Intent();

        // start activity from fragment
        if (from!=null) {
            fromAct = from.getActivity();
            if (fromAct == null)
                return;

            if (!(fromAct instanceof DualPaneActivity) || blank) {
                // open target fragment in a new container
                from.startActivityForResult(new Intent(fromAct, container == null ? SinglePaneActivity.class : container)
                        .setAction(target.getName()).putExtras(extra), requestCode);
            } else {
                // open target fragment in current DualPaneActivity
                ((DualPaneActivity)fromAct).openDetail(target, from , extra, requestCode);
            }
        } else {
            if (!(fromAct instanceof DualPaneActivity) || blank) {
                // open target fragment in a new container
                fromAct.startActivityForResult(new Intent(fromAct, container == null ? SinglePaneActivity.class : container)
                        .setAction(target.getName()).putExtras(extra), requestCode);
            } else {
                // open target fragment in current DualPaneActivity
                ((DualPaneActivity)fromAct).openDetail(target, null , extra, requestCode);
            }
        }
    }
}
