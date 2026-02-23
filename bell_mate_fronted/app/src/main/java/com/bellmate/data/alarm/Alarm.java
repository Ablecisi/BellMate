package com.bellmate.data.alarm;

import java.io.Serializable;
import java.util.List;

public class Alarm implements Serializable {

    private String id;
    private String time;
    private String label;
    private boolean enabled;
    private String repeatMode;
    private List<Integer> weekdays;
    private String ringtone;
    private boolean aiEnabled;
    private String aiStyle;
    private String aiPlanId;
    private String todayPausedDate;

    public Alarm() {
    }

    public Alarm(String id, String time, String label, boolean enabled, String repeatMode, List<Integer> weekdays, String ringtone, boolean aiEnabled, String aiStyle, String aiPlanId) {
        this.id = id;
        this.time = time;
        this.label = label;
        this.enabled = enabled;
        this.repeatMode = repeatMode;
        this.weekdays = weekdays;
        this.ringtone = ringtone;
        this.aiEnabled = aiEnabled;
        this.aiStyle = aiStyle;
        this.aiPlanId = aiPlanId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getRepeatMode() {
        return repeatMode;
    }

    public void setRepeatMode(String repeatMode) {
        this.repeatMode = repeatMode;
    }

    public List<Integer> getWeekdays() {
        return weekdays;
    }

    public void setWeekdays(List<Integer> weekdays) {
        this.weekdays = weekdays;
    }

    public String getRingtone() {
        return ringtone;
    }

    public void setRingtone(String ringtone) {
        this.ringtone = ringtone;
    }

    public boolean isAiEnabled() {
        return aiEnabled;
    }

    public void setAiEnabled(boolean aiEnabled) {
        this.aiEnabled = aiEnabled;
    }

    public String getAiStyle() {
        return aiStyle;
    }

    public void setAiStyle(String aiStyle) {
        this.aiStyle = aiStyle;
    }

    public String getAiPlanId() {
        return aiPlanId;
    }

    public void setAiPlanId(String aiPlanId) {
        this.aiPlanId = aiPlanId;
    }

    public String getTodayPausedDate() {
        return todayPausedDate;
    }

    public void setTodayPausedDate(String todayPausedDate) {
        this.todayPausedDate = todayPausedDate;
    }

    public boolean isPausedToday(String todayStr) {
        return todayPausedDate != null && todayPausedDate.equals(todayStr);
    }

    public void removePausedDate(String dateStr) {
        if (todayPausedDate != null && todayPausedDate.equals(dateStr)) {
            todayPausedDate = null;
        }
    }
}
