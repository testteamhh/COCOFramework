package com.cocosw.framework.sample.network;

import com.google.common.base.Objects;

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


        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                    .add("id", id)
                    .add("title", title)
                    .add("description", description)
                    .add("image_url", image_url)
                    .add("image_teaser_url", image_teaser_url)
                    .add("image_400_url", image_400_url)
                    .add("likes_count", likes_count)
                    .add("views_count", views_count)
                    .add("player", player)
                    .toString();
        }
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
