package com.cocosw.framework.uiquery;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.atermenji.android.iconicdroid.IconicFontDrawable;
import com.atermenji.android.iconicdroid.icon.Icon;
import com.cocosw.accessory.views.ViewUtils;
import com.cocosw.accessory.views.textview.StyledText;
import com.cocosw.query.AbstractViewQuery;
import com.cocosw.undobar.UndoBarController;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


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


    public CocoQuery info(final int info) {
        if (act != null) {
            UndoBarController.show(act, new StyledText().append(getContext().getResources().getDrawable(android.R.drawable.ic_dialog_info)).append(getContext().getString(info)));
        }
        return this;
    }

    public CocoQuery alert(final int info) {
        if (act != null) {
            UndoBarController.show(act, new StyledText().append(getContext().getResources().getDrawable(android.R.drawable.ic_dialog_alert)).append(getContext().getString(info)));
        }
        return this;
    }

    public CocoQuery alert(final CharSequence info) {
        if (act != null & info != null) {
            UndoBarController.show(act, new StyledText().append(getContext().getResources().getDrawable(android.R.drawable.ic_dialog_info)).append(info));
        }
        return this;
    }


    public static class ExtViewQuery extends AbstractViewQuery<ExtViewQuery> {

        Picasso picasso;

        public ExtViewQuery(View root) {
            super(root);
            picasso = Picasso.with(root.getContext());
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

        /**
         * Load network image to current ImageView with cache
         *
         * @param url
         * @return
         */
        public ExtViewQuery image(String url) {
            if (!TextUtils.isEmpty(url) && view instanceof ImageView) {
                picasso.load(url).into((ImageView) view);
            }
            return self();
        }

        @Override
        public ExtViewQuery image(int imgId) {
            if (view instanceof ImageView) {
                picasso.load(imgId).into((ImageView) view);
            }
            return self();
        }

        /**
         * Load network image to current ImageView with cache control
         *
         * @param url
         * @param cache
         * @return
         */
        public ExtViewQuery image(String url, boolean cache) {
            if (cache) {
                image(url);
            } else {
                picasso.load(url).skipMemoryCache().into((ImageView) view);
            }
            return this;
        }

        /**
         * Load image to current ImageView
         *
         * @param uri
         * @return
         */
        public ExtViewQuery image(Uri uri) {
            picasso.load(uri).into((ImageView) view);
            return this;
        }

        /**
         * Load image to current ImageView with holder image
         *
         * @param uri
         * @param holder
         * @return
         */
        public ExtViewQuery image(Uri uri, int holder) {
            picasso.load(uri).placeholder(holder).into((ImageView) view);
            return self();
        }


        /**
         * Load network image to current ImageView with holder image
         *
         * @param url
         * @param holder
         * @return
         */
        public ExtViewQuery image(String url, int holder) {
            if (!TextUtils.isEmpty(url) && view instanceof ImageView) {
                picasso.load(url).placeholder(holder).into((ImageView) view);
            }
            return self();
        }

        /**
         * Load network image to current ImageView with holder image
         *
         * @param url
         * @param holder
         * @return
         */
        public ExtViewQuery image(String url, Drawable holder) {
            if (!TextUtils.isEmpty(url) && view instanceof ImageView) {
                picasso.load(url).placeholder(holder).into((ImageView) view);
            }
            return self();
        }

        /**
         * Load network image to current ImageView with holder image and fallback image
         *
         * @param url
         * @param holder
         * @param fallbackId
         * @return
         */
        public ExtViewQuery image(String url, Drawable holder, int fallbackId) {
            if (!TextUtils.isEmpty(url) && view instanceof ImageView) {
                picasso.load(url).error(fallbackId).placeholder(holder).into((ImageView) view);
            }
            return self();
        }

        /**
         * Load network image to current ImageView with holder image and fallback image
         *
         * @param url
         * @param holder
         * @param fallbackId
         * @return
         */
        public ExtViewQuery image(String url, Drawable holder, Drawable fallbackId) {
            if (!TextUtils.isEmpty(url) && view instanceof ImageView) {
                picasso.load(url).error(fallbackId).placeholder(holder).into((ImageView) view);
            }
            return self();
        }


        /**
         * Load network image to current ImageView with callback
         *
         * @param url
         * @param callback
         * @return
         */
        public ExtViewQuery image(String url, Callback callback) {
            if (!TextUtils.isEmpty(url) && view instanceof ImageView) {
                picasso.load(url).into((ImageView) view, callback);
            }
            return self();
        }

        /**
         * Sets a iconic image
         *
         * @param icon
         * @param color
         * @return
         */
        public ExtViewQuery image(Icon icon, int color) {
            IconicFontDrawable draw = new IconicFontDrawable(context, icon);
            draw.setIconColor(context.getResources().getColor(color));
            return background(draw);
        }

        /**
         * Set a iconic image
         *
         * @param icon
         * @param color
         * @return
         */
        public ExtViewQuery image(Icon icon, int sizedp, int color) {
            IconicFontDrawable draw = new IconicFontDrawable(context, icon);
            draw.setIconColor(context.getResources().getColor(color));
            draw.setIntrinsicHeight(dip2pixel(sizedp));
            draw.setIntrinsicWidth(dip2pixel(sizedp));
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
                if (d != null && d instanceof IconicFontDrawable) {
                    ((IconicFontDrawable) d).setIconColor(color);
                }
                d = (view).getBackground();
                if (d != null && d instanceof IconicFontDrawable) {
                    ((IconicFontDrawable) d).setIconColor(color);
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
        public ExtViewQuery leftDrawable(Icon icon) {
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
                if (d != null && d instanceof IconicFontDrawable) {
                    ((IconicFontDrawable) d).setIconColor(color);
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
                if (d != null && d instanceof IconicFontDrawable) {
                    ((IconicFontDrawable) d).setIconColor(color);
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
        public ExtViewQuery leftDrawable(Icon icon, int sizedp, int padding) {
            if (view instanceof TextView) {
                IconicFontDrawable draw = new IconicFontDrawable(context, icon);
                draw.setIconColor((((TextView) view).getCurrentTextColor()));
                if (padding < 0)
                    padding = dip2pixel(8);
                else
                    padding = dip2pixel(padding);
                draw.setIconPadding(padding);
                if (sizedp > 0) {
                    draw.setIntrinsicHeight(dip2pixel(sizedp));
                    draw.setIntrinsicWidth(dip2pixel(sizedp));
                } else {
                    draw.setIntrinsicWidth((int) ((TextView) view).getTextSize());
                    draw.setIntrinsicHeight((int) ((TextView) view).getTextSize());
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
        public ExtViewQuery rightDrawable(Icon icon) {
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
        public ExtViewQuery rightDrawable(Icon icon, int sizedp, int padding) {
            if (view instanceof TextView) {
                IconicFontDrawable draw = new IconicFontDrawable(context, icon);
                draw.setIntrinsicHeight((int) ((TextView) view).getTextSize());
                draw.setIntrinsicWidth((int) ((TextView) view).getTextSize());
                draw.setIconColor((((TextView) view).getCurrentTextColor()));
                if (padding < 0)
                    padding = dip2pixel(8);
                else
                    padding = dip2pixel(padding);
                draw.setIconPadding(padding);
                if (sizedp > 0) {
                    draw.setIntrinsicHeight(dip2pixel(sizedp));
                    draw.setIntrinsicWidth(dip2pixel(sizedp));
                } else {
                    draw.setIntrinsicWidth((int) ((TextView) view).getTextSize());
                    draw.setIntrinsicHeight((int) ((TextView) view).getTextSize());
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