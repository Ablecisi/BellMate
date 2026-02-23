package com.bellmate;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bellmate.data.session.SessionManager;
import com.bellmate.data.plan.PlanManager;
import com.bellmate.ui.fragment.AlarmFragment;
import com.bellmate.ui.fragment.LoginFragment;
import com.bellmate.ui.fragment.MyFragment;
import com.bellmate.ui.fragment.PlanFragment;
import com.bellmate.ui.fragment.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.appbar.MaterialToolbar;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private MaterialToolbar topToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SessionManager.init(this);
        PlanManager.init(this);

        topToolbar = findViewById(R.id.topToolbar);
        setSupportActionBar(topToolbar);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_top_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            openFragment(new SettingsFragment());
            return true;
        }
        return super.onOptionsItemSelected(item);
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
