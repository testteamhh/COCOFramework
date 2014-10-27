package com.cocosw.framework.sample.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.util.LruCache;
import android.support.v4.view.ViewCompat;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Project: cocoframework
 * Created by LiaoKai(soarcn) on 2014/9/21.
 */
public class PaletteManager {
    private LruCache<String, Palette> cache = new LruCache<>(150);

    public interface Callback {
        void onPaletteReady(Palette palette);
    }

    public void getPalette(final String key, Bitmap bitmap, final Callback callback) {
        Palette palette = cache.get(key);
        if (palette != null)
            callback.onPaletteReady(palette);
        else
            Palette.generateAsync(bitmap, 24, new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    cache.put(key, palette);
                    callback.onPaletteReady(palette);
                }
            });
    }

    private static int setColorAlpha(int color, int alpha) {
        return (alpha << 24) | (color & 0x00ffffff);
    }

    public void updatePalette(ImageView imageView, final TextView textView) {
        String key = imageView.toString();
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        getPalette(key, bitmap, new PaletteManager.Callback() {
            @Override
            public void onPaletteReady(Palette palette) {
                int bgColor = palette.getDarkVibrantColor().getRgb();
                textView.setBackgroundColor(setColorAlpha(bgColor, 192));
                textView.setTextColor(palette.getLightMutedColor().getRgb());
            }
        });
    }

    public void updatePalette(Picasso picasso,String url, final ImageView imageView, final TextView textView,final View block) {
        final String key = url;
        picasso.load(url).into(imageView,new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                getPalette(key, bitmap, new PaletteManager.Callback() {
                    @Override
                    public void onPaletteReady(Palette palette) {
                        if (palette.getDarkVibrantColor()!=null) {
                            int bgColor = palette.getDarkVibrantColor().getRgb();
                            block.setBackgroundColor(bgColor);
                        }
                        if (palette.getLightMutedColor()!=null)
                        textView.setTextColor(palette.getLightMutedColor().getRgb());
                    }
                });
            }

            @Override
            public void onError() {

            }
        });

    }
}
