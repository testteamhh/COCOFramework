package com.cocosw.framework.sample.network;

import com.cocosw.framework.exception.CocoException;
import com.cocosw.framework.network.Network;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

/**
 * Project: cocoframework
 * Created by LiaoKai(soarcn) on 2014/8/31.
 */
public class DataSource extends Network {

    private static final String PRODUCEURL = "http://api.dribbble.com/";

    private static String APISITE = PRODUCEURL;

    public static void setApiUrl(String url) {
        APISITE = url;
    }

    public static ShotWrap getShots(long page) {
        return request(APISITE+"shots/popular?page="+page,ShotWrap.class);
    }

    protected static <T extends Object> T fromRequest(HttpRequest request, Class<T> target) {
        Reader reader = request.bufferedReader();
        try {
            return GSON.fromJson(request.body(), target);
        } catch (JsonParseException e) {
            e.printStackTrace();
            throw new CocoException("当前服务器出了一些问题，请稍后重试", e);
        } finally {
            try {
                reader.close();
            } catch (IOException ignored) {
                // Ignored
            }
        }
    }

    public static class ShotWrap {
        public int page;
        public int per_page;
        public int pages;
        public ArrayList<Bean.Shot> shots;
    }
}
