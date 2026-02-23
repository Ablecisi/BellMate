package com.bellmate.ui.fragment;

import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bellmate.R;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyFragment extends Fragment {

    private ImageView avatarImage;
    private TextView accountName;
    private TextView accountIdLine;
    private TextView profileBioLine;
    private TextView deviceIdText;
    private MaterialButton syncButton;
    private MaterialButton pullButton;
    private MaterialButton goSettingButton;
    private TextView syncHint;
    private RecyclerView logRecyclerView;

    private LogAdapter logAdapter;
    private List<LogEntry> logEntries;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my, container, false);

        avatarImage = view.findViewById(R.id.avatarImage);
        accountName = view.findViewById(R.id.accountName);
        accountIdLine = view.findViewById(R.id.accountIdLine);
        profileBioLine = view.findViewById(R.id.profileBioLine);
        deviceIdText = view.findViewById(R.id.deviceIdText);
        syncButton = view.findViewById(R.id.syncButton);
        pullButton = view.findViewById(R.id.pullButton);
        goSettingButton = view.findViewById(R.id.goSettingButton);
        syncHint = view.findViewById(R.id.syncHint);
        logRecyclerView = view.findViewById(R.id.logRecyclerView);

        logRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        loadLogs();

        String deviceId = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        deviceIdText.setText(deviceId);

        syncButton.setOnClickListener(v -> syncToCloud());
        pullButton.setOnClickListener(v -> pullFromCloud());
        goSettingButton.setOnClickListener(v -> navigateToSettings());

        return view;
    }

    private void loadLogs() {
        logEntries = new ArrayList<>();
        logAdapter = new LogAdapter(logEntries);
        logRecyclerView.setAdapter(logAdapter);
    }

    private void syncToCloud() {
        addLogEntry("同步到云端", "成功", "同步了 0 个闹钟和 0 个计划");
        Toast.makeText(requireContext(), "同步到云端成功", Toast.LENGTH_SHORT).show();
    }

    private void pullFromCloud() {
        addLogEntry("从云端拉取", "成功", "拉取了 0 个闹钟和 0 个计划");
        Toast.makeText(requireContext(), "从云端拉取成功", Toast.LENGTH_SHORT).show();
    }

    private void navigateToSettings() {
        Toast.makeText(requireContext(), "前往设置页面", Toast.LENGTH_SHORT).show();
    }

    private void addLogEntry(String action, String status, String details) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String time = sdf.format(new Date());

        LogEntry entry = new LogEntry(time, action, status, details);
        logEntries.add(0, entry);
        logAdapter.notifyDataSetChanged();
    }

    private class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {

        private List<LogEntry> logs;

        public LogAdapter(List<LogEntry> logs) {
            this.logs = logs;
        }

        @NonNull
        @Override
        public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_log, parent, false);
            return new LogViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
            LogEntry log = logs.get(position);
            holder.bind(log);
        }

        @Override
        public int getItemCount() {
            return logs.size();
        }

        class LogViewHolder extends RecyclerView.ViewHolder {

            private TextView logTime;
            private TextView logAction;
            private TextView logStatus;
            private TextView logDetails;

            public LogViewHolder(@NonNull View itemView) {
                super(itemView);
                logTime = itemView.findViewById(R.id.logTime);
                logAction = itemView.findViewById(R.id.logAction);
                logStatus = itemView.findViewById(R.id.logStatus);
                logDetails = itemView.findViewById(R.id.logDetails);
            }

            public void bind(LogEntry log) {
                logTime.setText(log.getTime());
                logAction.setText(log.getAction());
                logStatus.setText(log.getStatus());
                logDetails.setText(log.getDetails());

                if (log.getStatus().equals("成功")) {
                    logStatus.setTextColor(getResources().getColor(R.color.md_sys_color_success, requireContext().getTheme()));
                } else {
                    logStatus.setTextColor(getResources().getColor(R.color.md_sys_color_error, requireContext().getTheme()));
                }
            }
        }
    }

    private static class LogEntry {
        private String time;
        private String action;
        private String status;
        private String details;

        public LogEntry(String time, String action, String status, String details) {
            this.time = time;
            this.action = action;
            this.status = status;
            this.details = details;
        }

        public String getTime() {
            return time;
        }

        public String getAction() {
            return action;
        }

        public String getStatus() {
            return status;
        }

        public String getDetails() {
            return details;
        }
    }
}