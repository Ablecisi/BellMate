package com.bellmate.data.alarm;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AlarmManager {

    private static final String PREF_NAME = "bellmate_alarms";
    private static final String KEY_ALARMS = "alarms";

    private static AlarmManager instance;
    private final SharedPreferences prefs;
    private final Gson gson;

    private AlarmManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new AlarmManager(context.getApplicationContext());
        }
    }

    public static AlarmManager get() {
        if (instance == null) {
            throw new IllegalStateException("AlarmManager not initialized");
        }
        return instance;
    }

    public List<Alarm> getAllAlarms() {
        String json = prefs.getString(KEY_ALARMS, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<Alarm>>(){}.getType();
        return gson.fromJson(json, type);
    }

    public void saveAlarms(List<Alarm> alarms) {
        String json = gson.toJson(alarms);
        prefs.edit().putString(KEY_ALARMS, json).apply();
    }

    public void addAlarm(Alarm alarm) {
        List<Alarm> alarms = getAllAlarms();
        alarms.add(alarm);
        saveAlarms(alarms);
    }

    public void updateAlarm(Alarm alarm) {
        List<Alarm> alarms = getAllAlarms();
        for (int i = 0; i < alarms.size(); i++) {
            if (alarms.get(i).getId().equals(alarm.getId())) {
                alarms.set(i, alarm);
                break;
            }
        }
        saveAlarms(alarms);
    }

    public void deleteAlarm(String alarmId) {
        List<Alarm> alarms = getAllAlarms();
        alarms.removeIf(alarm -> alarm.getId().equals(alarmId));
        saveAlarms(alarms);
    }

    public Alarm getAlarmById(String alarmId) {
        List<Alarm> alarms = getAllAlarms();
        for (Alarm alarm : alarms) {
            if (alarm.getId().equals(alarmId)) {
                return alarm;
            }
        }
        return null;
    }

    public void pauseAlarmForToday(String alarmId, String todayStr) {
        Alarm alarm = getAlarmById(alarmId);
        if (alarm != null) {
            alarm.setTodayPausedDate(todayStr);
            updateAlarm(alarm);
        }
    }

    public void resumeAlarm(String alarmId) {
        Alarm alarm = getAlarmById(alarmId);
        if (alarm != null) {
            alarm.setTodayPausedDate(null);
            updateAlarm(alarm);
        }
    }

    public void pauseAllAlarmsForToday(String todayStr) {
        List<Alarm> alarms = getAllAlarms();
        for (Alarm alarm : alarms) {
            if (alarm.isEnabled()) {
                alarm.setTodayPausedDate(todayStr);
            }
        }
        saveAlarms(alarms);
    }

    public void resumeAllAlarms() {
        List<Alarm> alarms = getAllAlarms();
        for (Alarm alarm : alarms) {
            alarm.setTodayPausedDate(null);
        }
        saveAlarms(alarms);
    }
}
