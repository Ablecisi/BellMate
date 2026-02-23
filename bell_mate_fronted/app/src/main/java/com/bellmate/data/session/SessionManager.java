package com.bellmate.data.session;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "bellmate_session";
    private static final String KEY_AUTH_TOKEN = "auth_token";
    private static final String KEY_GUEST_MODE = "guest_mode";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_PROFILE_NICKNAME = "profile_nickname";
    private static final String KEY_PROFILE_PHONE = "profile_phone";
    private static final String KEY_PROFILE_BIO = "profile_bio";

    private static SessionManager instance;
    private final SharedPreferences prefs;

    private SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new SessionManager(context.getApplicationContext());
        }
    }

    public static SessionManager get() {
        if (instance == null) {
            throw new IllegalStateException("SessionManager not initialized");
        }
        return instance;
    }

    public void login(String token, String userId) {
        prefs.edit()
                .putString(KEY_AUTH_TOKEN, token)
                .putString(KEY_USER_ID, userId)
                .putBoolean(KEY_GUEST_MODE, false)
                .apply();
    }

    public void enterGuestMode() {
        prefs.edit()
                .putBoolean(KEY_GUEST_MODE, true)
                .apply();
    }

    public void logout() {
        prefs.edit()
                .remove(KEY_AUTH_TOKEN)
                .remove(KEY_USER_ID)
                .remove(KEY_GUEST_MODE)
                .apply();
    }

    public boolean isGuest() {
        return prefs.getBoolean(KEY_GUEST_MODE, false);
    }

    public boolean isAuthedOrGuest() {
        return isAuthed() || isGuest();
    }

    public String getAuthToken() {
        return prefs.getString(KEY_AUTH_TOKEN, null);
    }

    public String getUserId() {
        return prefs.getString(KEY_USER_ID, null);
    }

    public boolean isAuthed() {
        return getAuthToken() != null;
    }

    public String getCurrentAccount() {
        return getUserId();
    }

    public boolean isGuestMode() {
        return isGuest();
    }

    public void saveProfile(String nickname, String phone, String bio) {
        prefs.edit()
                .putString(KEY_PROFILE_NICKNAME, nickname)
                .putString(KEY_PROFILE_PHONE, phone)
                .putString(KEY_PROFILE_BIO, bio)
                .apply();
    }

    public String getProfileNickname() {
        return prefs.getString(KEY_PROFILE_NICKNAME, "");
    }

    public String getProfilePhone() {
        return prefs.getString(KEY_PROFILE_PHONE, "");
    }

    public String getProfileBio() {
        return prefs.getString(KEY_PROFILE_BIO, "");
    }

    public void clearCache() {
        prefs.edit()
                .remove(KEY_PROFILE_NICKNAME)
                .remove(KEY_PROFILE_PHONE)
                .remove(KEY_PROFILE_BIO)
                .apply();
    }
}