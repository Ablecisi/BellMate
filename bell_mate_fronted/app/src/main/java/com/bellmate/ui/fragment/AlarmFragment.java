package com.bellmate.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bellmate.R;
import com.bellmate.data.alarm.Alarm;
import com.bellmate.data.alarm.AlarmManager;
import com.bellmate.ui.dialog.AlarmDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AlarmFragment extends Fragment implements AlarmDialog.AlarmDialogListener {

    private RecyclerView alarmRecyclerView;
    private FloatingActionButton addAlarmFab;
    private FloatingActionButton pauseTodayFab;
    private AlarmAdapter alarmAdapter;
    private List<Alarm> alarms;
    
    // 多选相关变量
    private boolean selectionMode = false;
    private List<Alarm> selectedAlarms = new ArrayList<>();
    private View multiSelectBar;
    private TextView multiSelectCount;
    private Button selectAllButton;
    private Button deselectAllButton;
    private Button restoreTodayButton;
    private Button deleteSelectedButton;
    private Button cancelSelectionButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);

        // 初始化AlarmManager
        AlarmManager.init(requireContext());

        alarmRecyclerView = view.findViewById(R.id.alarmRecyclerView);
        addAlarmFab = view.findViewById(R.id.addAlarmFab);
        pauseTodayFab = view.findViewById(R.id.pauseTodayFab);
        
        // 初始化多选栏
        multiSelectBar = view.findViewById(R.id.multiSelectBar);
        multiSelectCount = view.findViewById(R.id.multiSelectCount);
        selectAllButton = view.findViewById(R.id.selectAllButton);
        deselectAllButton = view.findViewById(R.id.deselectAllButton);
        restoreTodayButton = view.findViewById(R.id.restoreTodayButton);
        deleteSelectedButton = view.findViewById(R.id.deleteSelectedButton);
        cancelSelectionButton = view.findViewById(R.id.cancelSelectionButton);

        alarmRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        loadAlarms();

        addAlarmFab.setOnClickListener(v -> showAddAlarmDialog());
        pauseTodayFab.setOnClickListener(v -> pauseTodayAlarms());
        
        // 初始化多选栏监听器
        selectAllButton.setOnClickListener(v -> selectAllAlarms());
        deselectAllButton.setOnClickListener(v -> deselectAllAlarms());
        restoreTodayButton.setOnClickListener(v -> restoreTodayPausedAlarms());
        deleteSelectedButton.setOnClickListener(v -> deleteSelectedAlarms());
        cancelSelectionButton.setOnClickListener(v -> cancelSelection());

        return view;
    }

    public void loadAlarms() {
        alarms = AlarmManager.get().getAllAlarms();
        alarmAdapter = new AlarmAdapter(alarms);
        alarmRecyclerView.setAdapter(alarmAdapter);
    }

    private void showAddAlarmDialog() {
        Alarm alarm = new Alarm();
        AlarmDialog dialog = AlarmDialog.newInstance(alarm, false, this);
        dialog.show(getParentFragmentManager(), "AlarmDialog");
    }

    private void showEditAlarmDialog(Alarm alarm) {
        AlarmDialog dialog = AlarmDialog.newInstance(alarm, true, this);
        dialog.show(getParentFragmentManager(), "AlarmDialog");
    }

    private void pauseTodayAlarms() {
        String todayStr = getTodayString();
        AlarmManager.get().pauseAllAlarmsForToday(todayStr);
        Toast.makeText(requireContext(), "今日闹钟已暂停", Toast.LENGTH_SHORT).show();
        loadAlarms();
    }

    private String getTodayString() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return String.format("%d-%02d-%02d", year, month, day);
    }

    @Override
    public void onAlarmSaved(Alarm alarm) {
        if (alarm.getId() == null || alarm.getId().isEmpty()) {
            // 新增闹钟
            alarm.setId("alarm_" + System.currentTimeMillis());
            AlarmManager.get().addAlarm(alarm);
        } else {
            // 编辑闹钟
            AlarmManager.get().updateAlarm(alarm);
        }
        Toast.makeText(requireContext(), "闹钟已保存", Toast.LENGTH_SHORT).show();
        loadAlarms();
    }

    // 多选相关方法
    public void enterSelectionMode(Alarm alarm) {
        selectionMode = true;
        selectedAlarms.clear();
        selectedAlarms.add(alarm);
        updateSelectionBar();
        alarmAdapter.notifyDataSetChanged();
    }

    public void exitSelectionMode() {
        selectionMode = false;
        selectedAlarms.clear();
        updateSelectionBar();
        alarmAdapter.notifyDataSetChanged();
    }

    public void toggleSelection(Alarm alarm) {
        if (selectedAlarms.contains(alarm)) {
            selectedAlarms.remove(alarm);
        } else {
            selectedAlarms.add(alarm);
        }
        updateSelectionBar();
        alarmAdapter.notifyDataSetChanged();
    }

    public boolean isSelected(Alarm alarm) {
        return selectedAlarms.contains(alarm);
    }

    private void updateSelectionBar() {
        if (selectionMode) {
            multiSelectBar.setVisibility(View.VISIBLE);
            multiSelectCount.setText("已选择 " + selectedAlarms.size() + " 项");
            pauseTodayFab.setVisibility(View.VISIBLE);
        } else {
            multiSelectBar.setVisibility(View.GONE);
            pauseTodayFab.setVisibility(View.GONE);
        }
    }

    private void deleteSelectedAlarms() {
        if (selectedAlarms.isEmpty()) return;
        
        new AlertDialog.Builder(requireContext())
            .setTitle("确认删除")
            .setMessage("确定要删除选中的 " + selectedAlarms.size() + " 个闹钟吗？")
            .setPositiveButton("删除", (dialog, which) -> {
                for (Alarm alarm : selectedAlarms) {
                    AlarmManager.get().deleteAlarm(alarm.getId());
                }
                Toast.makeText(requireContext(), "已删除 " + selectedAlarms.size() + " 个闹钟", Toast.LENGTH_SHORT).show();
                exitSelectionMode();
                loadAlarms();
            })
            .setNegativeButton("取消", null)
            .show();
    }

    private void cancelSelection() {
        exitSelectionMode();
    }

    private void selectAllAlarms() {
        selectedAlarms.clear();
        selectedAlarms.addAll(alarms);
        updateSelectionBar();
        alarmAdapter.notifyDataSetChanged();
    }

    private void deselectAllAlarms() {
        selectedAlarms.clear();
        updateSelectionBar();
        alarmAdapter.notifyDataSetChanged();
    }

    private void restoreTodayPausedAlarms() {
        String todayStr = getTodayString();
        int restoredCount = 0;
        
        for (Alarm alarm : selectedAlarms) {
            if (alarm.isPausedToday(todayStr)) {
                alarm.removePausedDate(todayStr);
                AlarmManager.get().updateAlarm(alarm);
                restoredCount++;
            }
        }
        
        if (restoredCount > 0) {
            Toast.makeText(requireContext(), "已恢复 " + restoredCount + " 个闹钟的今日暂停状态", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "选中的闹钟中没有今日暂停的", Toast.LENGTH_SHORT).show();
        }
        
        loadAlarms();
    }

    private class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {

        private List<Alarm> alarms;

        public AlarmAdapter(List<Alarm> alarms) {
            this.alarms = alarms;
        }

        @NonNull
        @Override
        public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm, parent, false);
            return new AlarmViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
            Alarm alarm = alarms.get(position);
            holder.bind(alarm);
        }

        @Override
        public int getItemCount() {
            return alarms.size();
        }

        class AlarmViewHolder extends RecyclerView.ViewHolder {

            private MaterialTextView timeText;
            private MaterialTextView repeatText;
            private MaterialTextView labelText;
            private MaterialTextView ringtoneText;
            private MaterialTextView stateChip;
            private SwitchMaterial enabledSwitch;
            private View selectedBorder;
            private com.google.android.material.card.MaterialCardView alarmCard;

            public AlarmViewHolder(@NonNull View itemView) {
                super(itemView);
                timeText = itemView.findViewById(R.id.timeText);
                repeatText = itemView.findViewById(R.id.repeatText);
                labelText = itemView.findViewById(R.id.labelText);
                ringtoneText = itemView.findViewById(R.id.ringtoneText);
                stateChip = itemView.findViewById(R.id.stateChip);
                enabledSwitch = itemView.findViewById(R.id.enabledSwitch);
                selectedBorder = itemView.findViewById(R.id.selectedBorder);
                alarmCard = itemView.findViewById(R.id.alarmCard);
            }

            public void bind(Alarm alarm) {
                timeText.setText(alarm.getTime());
                labelText.setText(alarm.getLabel());
                repeatText.setText(getRepeatText(alarm));
                ringtoneText.setText("默认铃声");
                enabledSwitch.setChecked(alarm.isEnabled() && !alarm.isPausedToday(getTodayString()));

                // 更新状态芯片
                boolean paused = alarm.isPausedToday(getTodayString());
                if (paused) {
                    stateChip.setText("今日暂停");
                    stateChip.setTextColor(getResources().getColor(R.color.md_sys_color_on_surface_variant, requireContext().getTheme()));
                    stateChip.setBackgroundResource(R.drawable.state_chip_disabled);
                } else if (alarm.isEnabled()) {
                    stateChip.setText("已启用");
                    stateChip.setTextColor(getResources().getColor(R.color.md_sys_color_success, requireContext().getTheme()));
                    stateChip.setBackgroundResource(R.drawable.state_chip_enabled);
                } else {
                    stateChip.setText("已禁用");
                    stateChip.setTextColor(getResources().getColor(R.color.md_sys_color_on_surface_variant, requireContext().getTheme()));
                    stateChip.setBackgroundResource(R.drawable.state_chip_disabled);
                }

                // 根据是否选中显示不同的UI状态
                if (selectionMode) {
                    boolean selected = isSelected(alarm);
                    selectedBorder.setVisibility(selected ? View.VISIBLE : View.GONE);
                    if (selected) {
                        alarmCard.setCardBackgroundColor(getResources().getColor(R.color.md_sys_color_primary_container, requireContext().getTheme()));
                        alarmCard.setStrokeWidth(3);
                        alarmCard.setStrokeColor(getResources().getColor(R.color.md_sys_color_primary, requireContext().getTheme()));
                    } else {
                        alarmCard.setCardBackgroundColor(getResources().getColor(R.color.md_sys_color_surface, requireContext().getTheme()));
                        alarmCard.setStrokeWidth(1);
                        alarmCard.setStrokeColor(getResources().getColor(R.color.md_sys_color_surface_variant, requireContext().getTheme()));
                    }
                } else {
                    selectedBorder.setVisibility(View.GONE);
                    alarmCard.setCardBackgroundColor(getResources().getColor(R.color.md_sys_color_surface, requireContext().getTheme()));
                    alarmCard.setStrokeWidth(1);
                    alarmCard.setStrokeColor(getResources().getColor(R.color.md_sys_color_surface_variant, requireContext().getTheme()));
                }

                enabledSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    alarm.setEnabled(isChecked);
                    AlarmManager.get().updateAlarm(alarm);
                });

                // 点击事件
                itemView.setOnClickListener(v -> {
                    if (selectionMode) {
                        toggleSelection(alarm);
                    } else {
                        showEditAlarmDialog(alarm);
                    }
                });

                // 长按事件
                itemView.setOnLongClickListener(v -> {
                    if (!selectionMode) {
                        enterSelectionMode(alarm);
                    }
                    return true;
                });
            }

            private String getRepeatText(Alarm alarm) {
                String repeatMode = alarm.getRepeatMode();
                if ("每天".equals(repeatMode)) {
                    return "每天";
                } else if ("工作日".equals(repeatMode)) {
                    return "工作日";
                } else if ("周末".equals(repeatMode)) {
                    return "周末";
                } else if ("自定义".equals(repeatMode) && alarm.getWeekdays() != null) {
                    StringBuilder sb = new StringBuilder();
                    for (int day : alarm.getWeekdays()) {
                        sb.append(getWeekdayName(day));
                    }
                    return sb.toString();
                }
                return "仅一次";
            }

            private String getWeekdayName(int day) {
                String[] weekdays = {"日", "一", "二", "三", "四", "五", "六"};
                return weekdays[day];
            }
        }
    }
}