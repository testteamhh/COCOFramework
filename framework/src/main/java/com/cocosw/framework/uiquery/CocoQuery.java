package com.cocosw.framework.uiquery;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.util.TypedValue;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.cocosw.accessory.views.ViewUtils;
import com.cocosw.framework.R;
import com.cocosw.query.AbstractViewQuery;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;


public class CocoQuery extends com.cocosw.query.CocoQuery<CocoQuery.ExtViewQuery> {


    public CocoQuery(Activity act) {
        super(act);
    }

    public CocoQuery(View root) {
        super(root);
    }

    public CocoQuery(Activity act, View root) {
        super(act, root);
    }

    public CocoQuery(Context context) {
        super(context);
    }


    /**
     * Open a alert dialog with title and message
     *
     * @param title
     * @param message
     */
    public void alert(final String title, final CharSequence message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(
                getContext());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog,
                                        final int which) {
                        final AlertDialog ad = builder.create();
                        ad.cancel();
                    }
                });
        builder.show();
    }

    /**
     * Open a alert dialog with title and message
     *
     * @param title
     * @param message
     */
    public void alert(final int title, final int message) {
        new AlertDialog.Builder(getContext());
        alert(getContext().getString(title),
                getContext().getString(message));
    }

    /**
     * Open a confirm dialog with title and message
     *
     * @param title
     * @param message
     */
    public void confirm(final int title, final int message,
                        final DialogInterface.OnClickListener onClickListener) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(
                getContext());
        builder.setTitle(title).setIcon(android.R.drawable.ic_dialog_info).setMessage(message);
        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(final DialogInterface dialog,
                                        final int which) {
                        if (onClickListener != null) {
                            onClickListener.onClick(dialog, which);
                        }

                    }
                });
        builder.setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(final DialogInterface dialog,
                                        final int which) {
                        if (onClickListener != null) {
                            onClickListener.onClick(dialog, which);
                        }
                    }
                });
        builder.show();
    }


    /**
     * Open a alert dialog with title and message
     *
     * @param title
     * @param message
     */
    public void alert(final int title, final int message,
                      final DialogInterface.OnClickListener onClickListener) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(
                getContext());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog,
                                        final int which) {
                        if (onClickListener != null) {
                            onClickListener.onClick(dialog, which);
                        }
                        final AlertDialog ad = builder.create();
                        ad.cancel();
                    }
                });
        builder.show();
    }

    /**
     * Open a dialog with single choice list
     *
     * @param title
     * @param list
     * @param listener
     */
    public void dialog(final int title, int list, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title)
                .setItems(list, listener);
        builder.create().show();
    }

    /**
     * Open a dialog with single choice list
     *
     * @param title
     * @param list
     * @param listener
     */
    public void dialog(final int title, CharSequence[] list, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title)
                .setItems(list, listener);
        builder.create().show();
    }


    /**
     * Show huge amount info with a dialog, HTML is allowed
     */
    public void dialog(String content, CharSequence title) {
        create(content, title).show();
    }

    private AppCompatDialog create(String mLicensesText, CharSequence str) {
        //Get resources
        final WebView webView = new WebView(getContext());
        webView.loadDataWithBaseURL(null, mLicensesText, "text/html", "utf-8", null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setView(webView).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        if (str != null)
            builder.setTitle(str);
        return builder.create();
    }


    public static class ExtViewQuery extends AbstractViewQuery<ExtViewQuery> {

        public ExtViewQuery(View root) {
            super(root);
        }

        /**
         * pager的adapter
         *
         * @param mAdapter
         * @return
         */
        public ExtViewQuery adapter(final PagerAdapter mAdapter) {
            if (view instanceof ViewPager) {
                ((ViewPager) view).setAdapter(mAdapter);
            }
            return self();
        }


        @Override
        public ExtViewQuery image(@DrawableRes int imgId) {
            ((ImageView) view).setImageResource(imgId);
            return self();
        }

        /**
         * Sets a iconic image
         *
         * @param icon
         * @param color
         * @return
         */
        public ExtViewQuery image(IIcon icon, int color) {
            return background(new IconicsDrawable(context, icon).color(color));
        }

        /**
         * Set a iconic image
         *
         * @param icon
         * @param color
         * @return
         */
        public ExtViewQuery image(IIcon icon, int sizedp, @ColorRes int color) {
            IconicsDrawable draw = new IconicsDrawable(context, icon);
            draw.colorRes(color);
            draw.sizeDp(sizedp);
            return image(draw);
        }

        /**
         * Set color for iconic image
         *
         * @param color
         * @return
         */
        public ExtViewQuery iconColor(int color) {
            if (view instanceof ImageView) {
                Drawable d = ((ImageView) view).getDrawable();
                if (d != null && d instanceof IconicsDrawable) {
                    ((IconicsDrawable) d).color(color);
                }
                d = (view).getBackground();
                if (d != null && d instanceof IconicsDrawable) {
                    ((IconicsDrawable) d).color(color);
                }
            }
            return self();
        }

        /**
         * Set color for iconic image
         *
         * @param colorId
         * @return
         */
        public ExtViewQuery iconColorId(int colorId) {
            return iconColor(context.getResources().getColor(colorId));
        }


        /**
         * Sets a iconic image to left of textview/buttonview
         *
         * @param icon
         * @return
         */
        public ExtViewQuery leftDrawable(IIcon icon) {
            if (view instanceof TextView) {
                return leftDrawable(icon, -1, -1);
            }
            return self();
        }

        /**
         * Sets a iconic image to left of textview/buttonview
         *
         * @param color
         * @return
         */
        public ExtViewQuery leftDrawableColor(int color) {
            if (view instanceof TextView) {
                Drawable d = ((TextView) view).getCompoundDrawables()[0];
                if (d != null && d instanceof IconicsDrawable) {
                    ((IconicsDrawable) d).color(color);
                }
            }
            return self();
        }

        /**
         * Sets a iconic image to left of textview/buttonview
         *
         * @param colorId
         * @return
         */
        public ExtViewQuery leftDrawableColorId(int colorId) {
            return leftDrawableColor(context.getResources().getColor(colorId));
        }


        /**
         * Sets a iconic image to left of textview/buttonview
         *
         * @param color
         * @return
         */
        public ExtViewQuery rightDrawableColor(int color) {
            if (view instanceof TextView) {
                Drawable d = ((TextView) view).getCompoundDrawables()[2];
                if (d != null && d instanceof IconicsDrawable) {
                    ((IconicsDrawable) d).color(color);
                }
            }
            return self();
        }

        /**
         * Sets a iconic image to left of textview/buttonview
         *
         * @param colorId
         * @return
         */
        public ExtViewQuery rightDrawableColorId(int colorId) {
            return leftDrawableColor(context.getResources().getColor(colorId));
        }

        /**
         * Sets a iconic image to left of textview/buttonview with padding
         *
         * @param icon
         * @return
         */
        public ExtViewQuery leftDrawable(IIcon icon, int sizedp, int padding) {
            if (view instanceof TextView) {
                IconicsDrawable draw = new IconicsDrawable(context, icon);
                draw.color((((TextView) view).getCurrentTextColor()));
                if (padding < 0)
                    padding = 8;
                draw.paddingDp(padding);
                if (sizedp > 0) {
                    draw.sizeDp(sizedp);
                } else {
                    draw.sizeDp((int) ((TextView) view).getTextSize());
                }
                Drawable[] ds = ((TextView) view).getCompoundDrawables();
                ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(draw, ds[1], ds[2], ds[3]);

            }
            return self();
        }


        /**
         * Sets a iconic image to right of textview/buttonview
         *
         * @param icon
         * @return
         */
        public ExtViewQuery rightDrawable(IIcon icon) {
            if (view instanceof TextView) {
                return rightDrawable(icon, -1, -1);
            }
            return self();
        }


        /**
         * Sets a iconic image to right of textview/buttonview with padding
         *
         * @param icon
         * @return
         */
        public ExtViewQuery rightDrawable(IIcon icon, int sizedp, int padding) {
            if (view instanceof TextView) {
                IconicsDrawable draw = new IconicsDrawable(context, icon);
                draw.color((((TextView) view).getCurrentTextColor()));
                if (padding < 0)
                    padding = 8;
                draw.paddingDp(padding);
                if (sizedp > 0) {
                    draw.sizeDp(sizedp);
                } else {
                    draw.sizeDp((int) ((TextView) view).getTextSize());
                }
                Drawable[] ds = ((TextView) view).getCompoundDrawables();
                ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(ds[0], ds[1], draw, ds[3]);

            }
            return self();
        }

        /**
         * set compoundDrawablepadding
         *
         * @return
         */
        public ExtViewQuery drawablePadding(int padding) {
            if (view instanceof TextView) {
                ((TextView) view).setCompoundDrawablePadding(padding);
            }
            return self();
        }


        /**
         * Sets background
         *
         * @param draw
         * @return
         */
        public ExtViewQuery background(Drawable draw) {
            ViewUtils.setBackground(view, draw);
            return self();
        }

        private int dip2pixel(float n) {
            int value = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, n, context.getResources().getDisplayMetrics());
            return value;
        }

        public ExtViewQuery padding(int left, int top, int right, int bottom) {
            view.setPadding(dip2pixel(left), dip2pixel(top), dip2pixel(right), dip2pixel(bottom));
            return self();
        }


    }
}