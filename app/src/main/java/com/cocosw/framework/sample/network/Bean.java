package com.cocosw.framework.sample.network;

import java.io.Serializable;

/**
 * Project: cocoframework
 * Created by LiaoKai(soarcn) on 2014/8/31.
 */
public class Bean {

    public static class Shot implements Serializable {
        public int id;
        public String title;
        public String description;
        public String image_url;
        public String image_teaser_url;
        public String image_400_url;
        public int likes_count;
        public int views_count;
        public Player player;
    }


    public static class Player implements Serializable{
        public int id;
        public String name;
        public String location;
        public String avatar_url;
        public String website_url;
        public String twitter_screen_name;
    }
}
