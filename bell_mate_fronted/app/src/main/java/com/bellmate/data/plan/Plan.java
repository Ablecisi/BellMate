package com.bellmate.data.plan;

import java.io.Serializable;

public class Plan implements Serializable {

    private String id;
    private String title;
    private String description;
    private String datetime;
    private boolean done;
    private int priority;

    public Plan() {
    }

    public Plan(String id, String title, String description, String datetime, boolean done, int priority) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.datetime = datetime;
        this.done = done;
        this.priority = priority;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
