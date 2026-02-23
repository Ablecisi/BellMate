package com.bellmate.ui.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bellmate.MainActivity;
import com.bellmate.R;
import com.bellmate.data.session.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

public class LoginFragment extends Fragment {

    private View loginView;
    private View registerView;
    private View forgotView;
    private View accountForm;
    private View phoneForm;

    private MaterialButton accountMethodBtn;
    private MaterialButton phoneMethodBtn;

    private TextInputLayout accountNameLayout;
    private TextInputLayout accountPwdLayout;
    private TextInputEditText accountNameInput;
    private TextInputEditText accountPwdInput;
    private MaterialButton loginAccountBtn;

    private TextInputLayout phoneNumLayout;
    private TextInputLayout phoneCodeLayout;
    private TextInputEditText phoneNumInput;
    private TextInputEditText phoneCodeInput;
    private MaterialButton sendCodeBtn;
    private MaterialButton loginPhoneBtn;

    private MaterialButton wechatAuthBtn;
    private MaterialButton qqAuthBtn;
    private MaterialButton openRegisterBtn;
    private MaterialButton openForgotBtn;
    private MaterialButton skipLoginBtn;

    private TextInputLayout regPhoneLayout;
    private TextInputLayout regPwdLayout;
    private TextInputEditText regPhoneInput;
    private TextInputEditText regPwdInput;
    private MaterialButton registerBtn;
    private MaterialButton backToLoginFromRegister;

    private TextInputLayout forgotPhoneLayout;
    private TextInputLayout forgotPwdLayout;
    private TextInputEditText forgotPhoneInput;
    private TextInputEditText forgotPwdInput;
    private MaterialButton resetPwdBtn;
    private MaterialButton backToLoginFromForgot;

    private CountDownTimer codeTimer;
    private int codeSeconds = 0;
    private String currentAuthProvider = "wechat";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        initViews(view);
        setupListeners();

        return view;
    }

    private void initViews(View view) {
        loginView = view.findViewById(R.id.loginView);
        registerView = view.findViewById(R.id.registerView);
        forgotView = view.findViewById(R.id.forgotView);
        accountForm = view.findViewById(R.id.accountForm);
        phoneForm = view.findViewById(R.id.phoneForm);

        accountMethodBtn = view.findViewById(R.id.accountMethodBtn);
        phoneMethodBtn = view.findViewById(R.id.phoneMethodBtn);

        accountNameLayout = view.findViewById(R.id.accountNameLayout);
        accountPwdLayout = view.findViewById(R.id.accountPwdLayout);
        accountNameInput = view.findViewById(R.id.accountNameInput);
        accountPwdInput = view.findViewById(R.id.accountPwdInput);
        loginAccountBtn = view.findViewById(R.id.loginAccountBtn);

        phoneNumLayout = view.findViewById(R.id.phoneNumLayout);
        phoneCodeLayout = view.findViewById(R.id.phoneCodeLayout);
        phoneNumInput = view.findViewById(R.id.phoneNumInput);
        phoneCodeInput = view.findViewById(R.id.phoneCodeInput);
        sendCodeBtn = view.findViewById(R.id.sendCodeBtn);
        loginPhoneBtn = view.findViewById(R.id.loginPhoneBtn);

        wechatAuthBtn = view.findViewById(R.id.wechatAuthBtn);
        qqAuthBtn = view.findViewById(R.id.qqAuthBtn);
        openRegisterBtn = view.findViewById(R.id.openRegisterBtn);
        openForgotBtn = view.findViewById(R.id.openForgotBtn);
        skipLoginBtn = view.findViewById(R.id.skipLoginBtn);

        regPhoneLayout = view.findViewById(R.id.regPhoneLayout);
        regPwdLayout = view.findViewById(R.id.regPwdLayout);
        regPhoneInput = view.findViewById(R.id.regPhoneInput);
        regPwdInput = view.findViewById(R.id.regPwdInput);
        registerBtn = view.findViewById(R.id.registerBtn);
        backToLoginFromRegister = view.findViewById(R.id.backToLoginFromRegister);

        forgotPhoneLayout = view.findViewById(R.id.forgotPhoneLayout);
        forgotPwdLayout = view.findViewById(R.id.forgotPwdLayout);
        forgotPhoneInput = view.findViewById(R.id.forgotPhoneInput);
        forgotPwdInput = view.findViewById(R.id.forgotPwdInput);
        resetPwdBtn = view.findViewById(R.id.resetPwdBtn);
        backToLoginFromForgot = view.findViewById(R.id.backToLoginFromForgot);

        updateMethodButtonState("account");
    }

    private void setupListeners() {
        accountMethodBtn.setOnClickListener(v -> switchMethod("account"));
        phoneMethodBtn.setOnClickListener(v -> switchMethod("phone"));

        loginAccountBtn.setOnClickListener(v -> handleAccountLogin());
        sendCodeBtn.setOnClickListener(v -> handleSendCode());
        loginPhoneBtn.setOnClickListener(v -> handlePhoneLogin());

        wechatAuthBtn.setOnClickListener(v -> openAuthDialog("wechat"));
        qqAuthBtn.setOnClickListener(v -> openAuthDialog("qq"));

        openRegisterBtn.setOnClickListener(v -> showView("register"));
        openForgotBtn.setOnClickListener(v -> showView("forgot"));
        skipLoginBtn.setOnClickListener(v -> handleGuestMode());

        registerBtn.setOnClickListener(v -> handleRegister());
        backToLoginFromRegister.setOnClickListener(v -> showView("login"));

        resetPwdBtn.setOnClickListener(v -> handleResetPassword());
        backToLoginFromForgot.setOnClickListener(v -> showView("login"));
    }

    private void showView(String viewName) {
        loginView.setVisibility(viewName.equals("login") ? View.VISIBLE : View.GONE);
        registerView.setVisibility(viewName.equals("register") ? View.VISIBLE : View.GONE);
        forgotView.setVisibility(viewName.equals("forgot") ? View.VISIBLE : View.GONE);
    }

    private void switchMethod(String method) {
        updateMethodButtonState(method);
        accountForm.setVisibility(method.equals("account") ? View.VISIBLE : View.GONE);
        phoneForm.setVisibility(method.equals("phone") ? View.VISIBLE : View.GONE);
    }

    private void updateMethodButtonState(String method) {
        if (method.equals("account")) {
            accountMethodBtn.setTextColor(getResources().getColor(R.color.switcher_text_active, requireContext().getTheme()));
            accountMethodBtn.setBackgroundColor(getResources().getColor(R.color.white, requireContext().getTheme()));
            phoneMethodBtn.setTextColor(getResources().getColor(R.color.switcher_text_inactive, requireContext().getTheme()));
            phoneMethodBtn.setBackgroundColor(getResources().getColor(android.R.color.transparent, requireContext().getTheme()));
        } else {
            phoneMethodBtn.setTextColor(getResources().getColor(R.color.switcher_text_active, requireContext().getTheme()));
            phoneMethodBtn.setBackgroundColor(getResources().getColor(R.color.white, requireContext().getTheme()));
            accountMethodBtn.setTextColor(getResources().getColor(R.color.switcher_text_inactive, requireContext().getTheme()));
            accountMethodBtn.setBackgroundColor(getResources().getColor(android.R.color.transparent, requireContext().getTheme()));
        }
    }

    private void handleAccountLogin() {
        String account = accountNameInput.getText().toString().trim();
        String password = accountPwdInput.getText().toString().trim();

        if (account.isEmpty()) {
            accountNameLayout.setError("请输入账号");
            return;
        }
        if (password.isEmpty()) {
            accountPwdLayout.setError("请输入密码");
            return;
        }

        accountNameLayout.setError(null);
        accountPwdLayout.setError(null);

        String token = "mock_token_" + System.currentTimeMillis();
        SessionManager.get().login(token, account);
        Toast.makeText(requireContext(), "登录成功", Toast.LENGTH_SHORT).show();

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).onLoginSuccess();
        }
    }

    private void handleSendCode() {
        if (codeSeconds > 0) return;

        String phone = phoneNumInput.getText().toString().trim();
        if (!isValidPhone(phone)) {
            phoneNumLayout.setError("请输入正确的手机号");
            return;
        }

        phoneNumLayout.setError(null);
        Toast.makeText(requireContext(), "验证码已发送", Toast.LENGTH_SHORT).show();

        codeSeconds = 60;
        sendCodeBtn.setEnabled(false);
        sendCodeBtn.setText(codeSeconds + "s");

        codeTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                codeSeconds--;
                sendCodeBtn.setText(codeSeconds + "s");
            }

            @Override
            public void onFinish() {
                codeSeconds = 0;
                sendCodeBtn.setEnabled(true);
                sendCodeBtn.setText("获取验证码");
            }
        }.start();
    }

    private void handlePhoneLogin() {
        String phone = phoneNumInput.getText().toString().trim();
        String code = phoneCodeInput.getText().toString().trim();

        if (!isValidPhone(phone)) {
            phoneNumLayout.setError("请输入正确的手机号");
            return;
        }
        if (!isValidCode(code)) {
            phoneCodeLayout.setError("请输入正确的验证码");
            return;
        }

        phoneNumLayout.setError(null);
        phoneCodeLayout.setError(null);

        String token = "mock_token_" + System.currentTimeMillis();
        SessionManager.get().login(token, "phone_" + phone);
        Toast.makeText(requireContext(), "登录成功", Toast.LENGTH_SHORT).show();

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).onLoginSuccess();
        }
    }

    private void openAuthDialog(String provider) {
        currentAuthProvider = provider;
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_auth, null);
        MaterialTextView authTitle = dialogView.findViewById(R.id.authTitle);
        MaterialTextView authLogo = dialogView.findViewById(R.id.authLogo);

        if (provider.equals("wechat")) {
            authTitle.setText("微信授权登录");
            authLogo.setText("chat");
        } else {
            authTitle.setText("QQ授权登录");
            authLogo.setText("forum");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView)
                .setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();

        dialogView.findViewById(R.id.cancelAuthBtn).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.confirmAuthBtn).setOnClickListener(v -> {
            dialog.dismiss();
            String token = "mock_token_" + System.currentTimeMillis();
            SessionManager.get().login(token, provider + "_user");
            Toast.makeText(requireContext(), "授权成功", Toast.LENGTH_SHORT).show();

            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).onLoginSuccess();
            }
        });
    }

    private void handleGuestMode() {
        SessionManager.get().enterGuestMode();
        Toast.makeText(requireContext(), "已进入游客模式", Toast.LENGTH_SHORT).show();

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).onLoginSuccess();
        }
    }

    private void handleRegister() {
        String phone = regPhoneInput.getText().toString().trim();
        String password = regPwdInput.getText().toString().trim();

        if (!isValidPhone(phone)) {
            regPhoneLayout.setError("请输入正确的手机号");
            return;
        }
        if (password.length() < 6) {
            regPwdLayout.setError("密码至少6位");
            return;
        }

        regPhoneLayout.setError(null);
        regPwdLayout.setError(null);

        Toast.makeText(requireContext(), "注册成功", Toast.LENGTH_SHORT).show();
        showView("login");
    }

    private void handleResetPassword() {
        String phone = forgotPhoneInput.getText().toString().trim();
        String password = forgotPwdInput.getText().toString().trim();

        if (!isValidPhone(phone)) {
            forgotPhoneLayout.setError("请输入正确的手机号");
            return;
        }
        if (password.length() < 6) {
            forgotPwdLayout.setError("密码至少6位");
            return;
        }

        forgotPhoneLayout.setError(null);
        forgotPwdLayout.setError(null);

        Toast.makeText(requireContext(), "密码重置成功", Toast.LENGTH_SHORT).show();
        showView("login");
    }

    private boolean isValidPhone(String phone) {
        return phone.matches("^\\d{11}$");
    }

    private boolean isValidCode(String code) {
        return code.matches("^\\d{6}$");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (codeTimer != null) {
            codeTimer.cancel();
            codeTimer = null;
        }
    }
}