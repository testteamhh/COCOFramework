package com.cocosw.framework.uiquery;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.cocosw.query.AbstractViewQuery;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

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
            Crouton.makeText(act, info, Style.INFO).show();
        }
        return this;
    }

    public CocoQuery alert(final int info) {
        if (act != null) {
            Crouton.makeText(act, info, Style.ALERT).show();
        }
        return this;
    }

    public CocoQuery crouton(final int info, final Style style) {
        if (act != null) {
            Crouton.makeText(act, info, style).show();
        }
        return this;
    }

    public CocoQuery alert(final CharSequence info) {
        if (act != null & info != null) {
            Crouton.makeText(act, info, Style.ALERT).show();
        }
        return this;
    }

    public CocoQuery confirm(final int info) {
        if (act != null) {
            Crouton.makeText(act, info, Style.CONFIRM).show();
        }
        return this;
    }

    public CocoQuery confirm(final CharSequence info) {
        if (act != null & info != null) {
            Crouton.makeText(act, info, Style.CONFIRM).show();
        }
        return this;
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

        /**
         * Load network image to current ImageView with cache
         *
         * @param url
         * @return
         */
        public ExtViewQuery image(String url) {
            if (!TextUtils.isEmpty(url) && view instanceof ImageView) {
                Picasso.with(context).load(url).into((ImageView) view);
            }
            return self();
        }

        @Override
        public ExtViewQuery image(int imgId) {
            if (view instanceof ImageView) {
                Picasso.with(context).load(imgId).into((ImageView) view);
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
                Picasso.with(context).load(url).skipMemoryCache().into((ImageView) view);
            }
            return this;
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
                Picasso.with(context).load(url).placeholder(holder).into((ImageView) view);
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
                Picasso.with(context).load(url).placeholder(holder).into((ImageView) view);
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
                Picasso.with(context).load(url).error(fallbackId).placeholder(holder).into((ImageView) view);
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
                Picasso.with(context).load(url).error(fallbackId).placeholder(holder).into((ImageView) view);
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
                Picasso.with(context).load(url).into((ImageView) view, callback);
            }
            return self();
        }
    }
}