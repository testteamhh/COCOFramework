package com.cocosw.framework.core;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.view.View;

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
    private Bundle options;

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

    public Presenter options(Bundle options) {
        this.options = options;
        return this;
    }

    public Presenter scaleUp(View v) {
        return options(ActivityOptionsCompat.makeScaleUpAnimation(v, 0, 0, v.getWidth(), v.getHeight()).toBundle());
    }

    public Presenter thumbnail(View v, Bitmap bitmap) {
        return options(ActivityOptionsCompat.makeThumbnailScaleUpAnimation(v, bitmap, 0, 0).toBundle());
    }

    /**
     * Open target fragment in target container
     */
    public void open() {
        if (extra == null)
            extra = new Intent();

        // start activity from fragment
        if (from != null) {
            fromAct = from.getActivity();
            if (fromAct == null)
                return;

            if (!(fromAct instanceof DualPaneActivity) || blank) {
                ActivityCompat.startActivity(fromAct, new Intent(fromAct, container == null ? SinglePaneActivity.class : container)
                        .setAction(target.getName()).putExtras(extra), options);
            } else {
                // open target fragment in current DualPaneActivity
                ((DualPaneActivity) fromAct).openDetail(target, extra);
            }
        } else {
            if (!(fromAct instanceof DualPaneActivity) || blank) {
                // open target fragment in a new container
                ActivityCompat.startActivity(fromAct, new Intent(fromAct, container == null ? SinglePaneActivity.class : container)
                        .setAction(target.getName()).putExtras(extra), options);
            } else {
                // open target fragment in current DualPaneActivity
                ((DualPaneActivity) fromAct).openDetail(target, extra);
            }
        }
    }


    public void openForResult(int requestCode) {
        if (extra == null)
            extra = new Intent();

        // start activity from fragment
        if (from != null) {
            fromAct = from.getActivity();
            if (fromAct == null)
                return;

            if (!(fromAct instanceof DualPaneActivity) || blank) {
                // open target fragment in a new container
                // So far, there is no way to set activity options for this.
                from.startActivityForResult(new Intent(fromAct, container == null ? SinglePaneActivity.class : container)
                        .setAction(target.getName()).putExtras(extra), requestCode);
            } else {
                // open target fragment in current DualPaneActivity
                ((DualPaneActivity) fromAct).openDetail(target, from, extra, requestCode);
            }
        } else {
            if (!(fromAct instanceof DualPaneActivity) || blank) {
                ActivityCompat.startActivityForResult(fromAct, new Intent(fromAct, container == null ? SinglePaneActivity.class : container)
                        .setAction(target.getName()).putExtras(extra), requestCode, options);
            } else {
                // open target fragment in current DualPaneActivity
                ((DualPaneActivity) fromAct).openDetail(target, null, extra, requestCode);
            }
        }
    }
}
