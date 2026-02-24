package com.bellmate.data.plan;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PlanManager {

    private static final String PREF_NAME = "bellmate_plans";
    private static final String KEY_PLANS = "plans";

    private static PlanManager instance;
    private final SharedPreferences prefs;
    private final Gson gson;

    private PlanManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new PlanManager(context.getApplicationContext());
        }
    }

    public static PlanManager get() {
        if (instance == null) {
            throw new IllegalStateException("PlanManager not initialized");
        }
        return instance;
    }

    public List<Plan> getAllPlans() {
        String json = prefs.getString(KEY_PLANS, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<Plan>>(){}.getType();
        return gson.fromJson(json, type);
    }

    public void savePlans(List<Plan> plans) {
        String json = gson.toJson(plans);
        prefs.edit().putString(KEY_PLANS, json).apply();
    }

    public void addPlan(Plan plan) {
        List<Plan> plans = getAllPlans();
        plans.add(plan);
        savePlans(plans);
    }

    public void updatePlan(Plan plan) {
        List<Plan> plans = getAllPlans();
        for (int i = 0; i < plans.size(); i++) {
            if (plans.get(i).getId().equals(plan.getId())) {
                plans.set(i, plan);
                break;
            }
        }
        savePlans(plans);
    }

    public void deletePlan(String planId) {
        List<Plan> plans = getAllPlans();
        plans.removeIf(plan -> plan.getId().equals(planId));
        savePlans(plans);
    }

    public Plan getPlanById(String planId) {
        List<Plan> plans = getAllPlans();
        for (Plan plan : plans) {
            if (plan.getId().equals(planId)) {
                return plan;
            }
        }
        return null;
    }

    public void markPlanAsDone(String planId, boolean done) {
        Plan plan = getPlanById(planId);
        if (plan != null) {
            plan.setDone(done);
            updatePlan(plan);
        }
    }

    public List<Plan> getUpcomingPlans() {
        List<Plan> plans = getAllPlans();
        List<Plan> upcoming = new ArrayList<>();
        for (Plan plan : plans) {
            if (!plan.isDone()) {
                upcoming.add(plan);
            }
        }
        // 按日期时间排序
        upcoming.sort((p1, p2) -> p1.getDatetime().compareTo(p2.getDatetime()));
        return upcoming;
    }

    public List<Plan> getCompletedPlans() {
        List<Plan> plans = getAllPlans();
        List<Plan> completed = new ArrayList<>();
        for (Plan plan : plans) {
            if (plan.isDone()) {
                completed.add(plan);
            }
        }
        // 按日期时间排序
        completed.sort((p1, p2) -> p2.getDatetime().compareTo(p1.getDatetime()));
        return completed;
    }
}
