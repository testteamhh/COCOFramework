package com.cocosw.framework.sample.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.util.LruCache;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Project: cocoframework
 * Created by LiaoKai(soarcn) on 2014/9/21.
 */
public class PaletteManager {
    private LruCache<String, Palette.Swatch> cache = new LruCache<>(150);

    public interface Callback {
        void onPaletteReady(Palette.Swatch palette);
    }

    public void getPalette(final String key, Bitmap bitmap, final Callback callback) {
        Palette.Swatch palette = cache.get(key);
        if (palette != null)
            callback.onPaletteReady(palette);
        else
            Palette.generateAsync(bitmap, 24, new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    Palette.Swatch swatch = palette.getDarkVibrantSwatch();
                    if (swatch != null) {
                        for (Palette.Swatch swatch1 : palette.getSwatches()) {
                            if (swatch1 != null)
                                swatch = swatch1;
                        }
                    }
                    if (swatch != null)
                        cache.put(key, swatch);
                    callback.onPaletteReady(swatch);
                }
            });
    }

    private static int setColorAlpha(int color, int alpha) {
        return (alpha << 24) | (color & 0x00ffffff);
    }

    public void updatePalette(Picasso picasso, String url, final ImageView imageView, final TextView textView, final View block) {
        final String key = url;
        picasso.load(url).into(imageView, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                getPalette(key, bitmap, new PaletteManager.Callback() {
                    @Override
                    public void onPaletteReady(Palette.Swatch palette) {
                        if (palette != null) {
                            if (block != null)
                                block.setBackgroundColor(palette.getRgb());
                            if (textView != null)
                                textView.setTextColor(palette.getTitleTextColor());
                        }
                    }
                });
            }

            @Override
            public void onError() {

            }
        });

    }
}
