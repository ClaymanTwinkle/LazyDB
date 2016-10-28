package com.kesar.demo.domain;

/**
 * 标签bean
 * Created by kesar on 16-10-28.
 */
public class Tag {
    private String text;
    private String time;

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
                "text='" + text + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
