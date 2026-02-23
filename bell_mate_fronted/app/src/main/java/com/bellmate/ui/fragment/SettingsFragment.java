package com.bellmate.ui.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bellmate.MainActivity;
import com.bellmate.R;
import com.bellmate.data.session.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {

    private SwitchMaterial notifySwitch;
    private SwitchMaterial darkSwitch;
    private Spinner langSelect;
    private View profileSection;
    private TextInputLayout nicknameLayout;
    private TextInputLayout phoneLayout;
    private TextInputLayout bioLayout;
    private TextInputEditText nicknameInput;
    private TextInputEditText phoneInput;
    private TextInputEditText bioInput;
    private MaterialButton saveProfileBtn;
    private RecyclerView accountRecyclerView;
    private View securityItem;
    private View clearCacheItem;
    private View logoutItem;
    private MaterialButton logoutBtn;
    private View aboutItem;
    private View termsItem;
    private View privacyItem;

    private AccountAdapter accountAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        notifySwitch = view.findViewById(R.id.notifySwitch);
        darkSwitch = view.findViewById(R.id.darkSwitch);
        langSelect = view.findViewById(R.id.langSelect);
        profileSection = view.findViewById(R.id.profileSection);
        nicknameLayout = view.findViewById(R.id.nicknameLayout);
        phoneLayout = view.findViewById(R.id.phoneLayout);
        bioLayout = view.findViewById(R.id.bioLayout);
        nicknameInput = view.findViewById(R.id.nicknameInput);
        phoneInput = view.findViewById(R.id.phoneInput);
        bioInput = view.findViewById(R.id.bioInput);
        saveProfileBtn = view.findViewById(R.id.saveProfileBtn);
        accountRecyclerView = view.findViewById(R.id.accountRecyclerView);
        securityItem = view.findViewById(R.id.securityItem);
        clearCacheItem = view.findViewById(R.id.clearCacheItem);
        logoutItem = view.findViewById(R.id.logoutItem);
        logoutBtn = view.findViewById(R.id.logoutBtn);
        aboutItem = view.findViewById(R.id.aboutItem);
        termsItem = view.findViewById(R.id.termsItem);
        privacyItem = view.findViewById(R.id.privacyItem);

        setupLanguageSpinner();
        setupAccountRecyclerView();
        setupListeners();
        loadProfileData();
        applyGuestRestrictions();

        return view;
    }

    private void setupLanguageSpinner() {
        String[] languages = {"跟随系统", "中文", "English"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        langSelect.setAdapter(adapter);
    }

    private void setupAccountRecyclerView() {
        accountAdapter = new AccountAdapter();
        accountRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        accountRecyclerView.setAdapter(accountAdapter);
        loadAccounts();
    }

    private void loadAccounts() {
        List<AccountInfo> accounts = new ArrayList<>();
        String currentAccount = SessionManager.get().getCurrentAccount();
        
        if (currentAccount != null && !currentAccount.isEmpty()) {
            accounts.add(new AccountInfo(currentAccount, System.currentTimeMillis() + 86400000 * 30, true));
        }
        
        accountAdapter.setAccounts(accounts);
    }

    private void setupListeners() {
        notifySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String message = isChecked ? "消息通知已开启" : "消息通知已关闭";
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        });

        darkSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String message = isChecked ? "深色模式已开启" : "深色模式已关闭";
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        });

        langSelect.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String[] languages = {"跟随系统", "中文", "English"};
                String selectedLanguage = languages[position];
                Toast.makeText(requireContext(), "语言已设置为: " + selectedLanguage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        saveProfileBtn.setOnClickListener(v -> handleSaveProfile());

        securityItem.setOnClickListener(v -> showSecurityDialog());
        clearCacheItem.setOnClickListener(v -> handleClearCache());
        logoutItem.setOnClickListener(v -> handleLogout());
        logoutBtn.setOnClickListener(v -> handleLogout());

        aboutItem.setOnClickListener(v -> showAboutDialog());
        termsItem.setOnClickListener(v -> showTermsDialog());
        privacyItem.setOnClickListener(v -> showPrivacyDialog());
    }

    private void applyGuestRestrictions() {
        boolean isGuest = SessionManager.get().isGuestMode();
        profileSection.setVisibility(isGuest ? View.GONE : View.VISIBLE);
    }

    private void loadProfileData() {
        nicknameInput.setText(SessionManager.get().getProfileNickname());
        phoneInput.setText(SessionManager.get().getProfilePhone());
        bioInput.setText(SessionManager.get().getProfileBio());
    }

    private void handleSaveProfile() {
        String nickname = nicknameInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String bio = bioInput.getText().toString().trim();

        if (nickname.isEmpty()) {
            nicknameLayout.setError("昵称不能为空");
            return;
        }

        nicknameLayout.setError(null);
        SessionManager.get().saveProfile(nickname, phone, bio);
        Toast.makeText(requireContext(), "资料保存成功", Toast.LENGTH_SHORT).show();
    }

    private void handleClearCache() {
        SessionManager.get().clearCache();
        Toast.makeText(requireContext(), "缓存已清除", Toast.LENGTH_SHORT).show();
    }

    private void handleLogout() {
        new AlertDialog.Builder(requireContext())
                .setTitle("退出登录")
                .setMessage("确定要退出登录吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    SessionManager.get().logout();
                    Toast.makeText(requireContext(), "已退出登录", Toast.LENGTH_SHORT).show();
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).openLoginScreen();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void showSecurityDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("账号安全")
                .setMessage("您的账号已启用基本安全保护。建议定期修改密码以增强安全性。")
                .setPositiveButton("知道了", null)
                .show();
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("关于我们")
                .setMessage("BellMate 是一款智能闹钟和计划管理应用，帮助您更好地管理时间和日程。\n\n版本: v0.3.0-proto")
                .setPositiveButton("知道了", null)
                .show();
    }

    private void showTermsDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("用户协议")
                .setMessage("欢迎使用 BellMate！\n\n1. 使用本应用即表示您同意本协议。\n2. 请遵守相关法律法规。\n3. 我们保留随时修改协议的权利。\n4. 如有疑问，请联系客服。")
                .setPositiveButton("知道了", null)
                .show();
    }

    private void showPrivacyDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("隐私政策")
                .setMessage("我们重视您的隐私保护。\n\n1. 仅收集必要的用户数据。\n2. 数据加密存储，保障安全。\n3. 不会向第三方泄露个人信息。\n4. 您可以随时删除个人数据。")
                .setPositiveButton("知道了", null)
                .show();
    }

    private static class AccountInfo {
        String account;
        long expiresAt;
        boolean isCurrent;

        AccountInfo(String account, long expiresAt, boolean isCurrent) {
            this.account = account;
            this.expiresAt = expiresAt;
            this.isCurrent = isCurrent;
        }
    }

    private class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder> {
        private List<AccountInfo> accounts = new ArrayList<>();

        void setAccounts(List<AccountInfo> accounts) {
            this.accounts = accounts;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_2, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            AccountInfo account = accounts.get(position);
            holder.text1.setText(account.account);
            holder.text2.setText("过期时间: " + new java.util.Date(account.expiresAt).toLocaleString());
        }

        @Override
        public int getItemCount() {
            return accounts.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            android.widget.TextView text1;
            android.widget.TextView text2;

            ViewHolder(View itemView) {
                super(itemView);
                text1 = itemView.findViewById(android.R.id.text1);
                text2 = itemView.findViewById(android.R.id.text2);
            }
        }
    }
}