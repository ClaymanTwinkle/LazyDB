package com.kesar.demo.domain;

import org.kesar.lazy.lazydb.annotate.ID;

/**
 * 标签bean
 * Created by kesar on 16-10-28.
 */
public class Tag {
    @ID
    private String id;
    private String text;
    private String time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id='" + id + '\'' +
                ", text='" + text + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
