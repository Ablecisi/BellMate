package com.bellmate;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bellmate.data.session.SessionManager;
import com.bellmate.ui.fragment.AlarmFragment;
import com.bellmate.ui.fragment.LoginFragment;
import com.bellmate.ui.fragment.MyFragment;
import com.bellmate.ui.fragment.PlanFragment;
import com.bellmate.ui.fragment.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SessionManager.init(this);

        bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_alarm) {
                openFragment(new AlarmFragment());
                return true;
            }
            if (id == R.id.nav_plan) {
                openFragment(new PlanFragment());
                return true;
            }
            if (id == R.id.nav_my) {
                openFragment(new MyFragment());
                return true;
            }
            if (id == R.id.nav_settings) {
                openFragment(new SettingsFragment());
                return true;
            }
            return false;
        });

        if (savedInstanceState == null) {
            if (SessionManager.get().isAuthedOrGuest()) {
                bottomNav.setSelectedItemId(R.id.nav_alarm);
            } else {
                openFragment(new LoginFragment());
                bottomNav.setVisibility(View.GONE);
            }
        }
    }

    private void openFragment(@NonNull Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    public void onLoginSuccess() {
        bottomNav.setVisibility(BottomNavigationView.VISIBLE);
        bottomNav.setSelectedItemId(R.id.nav_alarm);
    }

    public void openLoginScreen() {
        bottomNav.setVisibility(View.GONE);
        openFragment(new LoginFragment());
    }
}
