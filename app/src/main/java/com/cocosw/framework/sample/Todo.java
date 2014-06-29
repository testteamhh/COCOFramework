package com.cocosw.framework.sample;

import java.io.Serializable;

/**
 * Project: ToDoList
 * Created by LiaoKai(soarcn) on 2014/6/12.
 */
public class Todo implements Serializable{

    public String title;

    public Todo(String title) {
        this.title = title;
    }
}
