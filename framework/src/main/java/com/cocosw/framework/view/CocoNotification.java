package com.cocosw.framework.view;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat.Builder;
import android.widget.RemoteViews;

import com.cocosw.framework.R;


public class CocoNotification {

    private NotificationManager notificationManager = null;
    private final Context context;
    private int id = 0x12456872;
    private Handler handler;
    private Notification progNotification;

    public CocoNotification(final Context ctx, final int id) {
        context = ctx;
        notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        this.id = id;
    }

    /**
     * 带进度条的Notification 实例
     *
     * @return
     */
    private Notification createProgNoti(final String title, final int size,
                                        final Intent intent) {
        progNotification = new Builder(context)
                .setAutoCancel(true).setSmallIcon(context.getApplicationInfo().icon)
                .setContentTitle(title).setProgress(size, 0, false).build();

        // 通过RemoteViews 设置notification中View 的属性
        progNotification.contentView = new RemoteViews(
                context.getPackageName(), R.layout.notification_progress);
        progNotification.contentView.setProgressBar(R.id.pb, size, 0, false);
        progNotification.contentView.setTextViewText(R.id.tv, title);
        progNotification.contentIntent = PendingIntent.getActivity(context, 0,
                intent, 0);
        if (handler == null) {
            handler = new Handler() {
                @Override
                public void handleMessage(final Message msg) {
                    progNotification.contentView.setProgressBar(R.id.pb, size,
                            msg.arg1, false);
                    notificationManager.notify(id, progNotification);
                    super.handleMessage(msg);
                }
            };
        }

        return progNotification;
    }

    /**
     * 普通Notification 实例
     *
     * @param text
     * @param title
     * @param intent
     * @return
     */
    private Builder createNotification(final String title, final String text,
                                       final Intent intent) {
        final PendingIntent contentIntent = PendingIntent
                .getActivity(context, 0, intent, 0);

        return new Builder(context).setAutoCancel(true)
                .setSmallIcon(context.getApplicationInfo().icon).setContentTitle(title)
                .setContentIntent(contentIntent).setContentText(text)
                .setTicker(title);
    }

    /**
     * 显示一个普通文字的notification
     *
     * @param title
     * @param text
     */
    public void notifyText(final String title, final String text, int num) {
        final Notification notification = createNotification(title, text, null)
                .setNumber(num).build();
        notificationManager.notify(id, notification);
    }

    /**
     * 显示一个普通文字的notification
     *
     * @param title
     * @param text
     * @param icon
     */
    public void notifyText(final String title, final String text, int num,
                           Bitmap icon) {
        final Notification notification = createNotification(title, text, null)
                .setNumber(num).setLargeIcon(icon).build();
        notificationManager.notify(id, notification);
    }

    /**
     * 显示一个普通文字的notification
     *
     * @param title
     * @param text
     */
    public void notifyText(int id, final String title, final String text,
                           int num) {
        final Notification notification = createNotification(title, text, null)
                .setNumber(num).build();
        notificationManager.notify(id, notification);
    }

    /**
     * 显示一个普通文字的notification
     *
     * @param title
     * @param text
     */
    public void notifyText(final String title, final String text) {
        final Notification notification = createNotification(title, text, null)
                .build();
        notificationManager.notify(id, notification);
    }

    /**
     * 显示一个普通文字的notification 点击后跳转
     *
     * @param title
     * @param text
     */
    public void notifyText(final String title, final String text,
                           final Intent intent) {
        final Notification notification = createNotification(title, text,
                intent).build();
        notificationManager.notify(id, notification);
    }

    /**
     * 显示一个带进度条的notification 点击后跳转
     *
     * @param title
     */
    public void notifyProgress(final String title, final int size,
                               final Intent intent) {
        final Notification notification = createProgNoti(title, size, intent);
        notificationManager.notify(id, notification);
    }

    /**
     * 更新进度
     *
     * @param pro
     */
    public void updateProgress(final int pro) {
        if (progNotification == null)
            return;
        final Message msg = handler.obtainMessage();
        msg.arg1 = pro;
        msg.sendToTarget();
    }

    public void notifyProgress(final int title, final int size,
                               final Intent intent) {
        notifyProgress(context.getString(title), size, intent);
    }
}
