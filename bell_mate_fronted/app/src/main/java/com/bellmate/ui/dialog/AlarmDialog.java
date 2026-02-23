package com.bellmate.ui.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bellmate.R;
import com.bellmate.data.alarm.Alarm;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AlarmDialog extends DialogFragment {

    public interface AlarmDialogListener {
        void onAlarmSaved(Alarm alarm);
    }

    private AlarmDialogListener listener;
    private Alarm alarm;
    private boolean isEditMode;

    private TimePicker timePicker;
    private TextInputEditText labelInput;
    private TextInputLayout labelLayout;
    private Spinner repeatModeSpinner;
    private LinearLayout weekdaysLayout;
    private List<CheckBox> weekdayCheckboxes;
    private Spinner ringtoneSpinner;
    private Switch aiSwitch;
    private LinearLayout aiOptionsLayout;
    private Spinner aiStyleSpinner;
    private Spinner aiPlanSpinner;
    private MaterialButton saveButton;
    private MaterialButton cancelButton;
    
    // 批量生成相关变量
    private Switch batchSwitch;
    private LinearLayout batchOptionsLayout;
    private TextInputEditText intervalInput;
    private TextInputLayout intervalLayout;
    private Spinner intervalUnitSpinner;
    private Spinner batchModeSpinner;
    private LinearLayout repeatCountContainer;
    private TextInputEditText repeatCountInput;
    private TextInputLayout repeatCountLayout;
    private LinearLayout endDateLayout;
    private Button endDateButton;
    private TextView endDateText;
    private Calendar endDateCalendar;

    public AlarmDialog() {
    }

    public static AlarmDialog newInstance(Alarm alarm, boolean isEditMode, AlarmDialogListener listener) {
        AlarmDialog dialog = new AlarmDialog();
        dialog.listener = listener;
        Bundle args = new Bundle();
        args.putSerializable("alarm", alarm);
        args.putBoolean("isEditMode", isEditMode);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // 如果没有通过构造函数设置监听器，尝试从上下文中获取
        if (listener == null) {
            try {
                // 尝试将context转换为AlarmDialogListener
                if (context instanceof AlarmDialogListener) {
                    listener = (AlarmDialogListener) context;
                    return;
                }
                
                // 尝试获取父Fragment
                Fragment parentFragment = getParentFragment();
                if (parentFragment instanceof AlarmDialogListener) {
                    listener = (AlarmDialogListener) parentFragment;
                    return;
                }
                
                // 尝试从FragmentManager中找到实现了AlarmDialogListener接口的Fragment
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragmentContainer);
                if (currentFragment instanceof AlarmDialogListener) {
                    listener = (AlarmDialogListener) currentFragment;
                    return;
                }
                
                // 如果都没有找到，抛出异常
                throw new ClassCastException("No parent found that implements AlarmDialogListener");
            } catch (ClassCastException e) {
                throw new ClassCastException("No parent found that implements AlarmDialogListener");
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            alarm = (Alarm) args.getSerializable("alarm");
            isEditMode = args.getBoolean("isEditMode", false);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_alarm, null);

        initViews(view);
        setupListeners();
        if (alarm != null) {
            populateFields();
        }
        
        // 在编辑模式下禁用批量生成功能
        if (isEditMode) {
            batchSwitch.setVisibility(View.GONE);
            batchOptionsLayout.setVisibility(View.GONE);
        }

        builder.setView(view)
                .setTitle(isEditMode ? "编辑闹钟" : "新增闹钟");

        return builder.create();
    }

    private void initViews(View view) {
        timePicker = view.findViewById(R.id.timePicker);
        labelInput = view.findViewById(R.id.labelInput);
        labelLayout = view.findViewById(R.id.labelLayout);
        repeatModeSpinner = view.findViewById(R.id.repeatModeSpinner);
        weekdaysLayout = view.findViewById(R.id.weekdaysLayout);
        ringtoneSpinner = view.findViewById(R.id.ringtoneSpinner);
        aiSwitch = view.findViewById(R.id.aiSwitch);
        aiOptionsLayout = view.findViewById(R.id.aiOptionsLayout);
        aiStyleSpinner = view.findViewById(R.id.aiStyleSpinner);
        aiPlanSpinner = view.findViewById(R.id.aiPlanSpinner);
        saveButton = view.findViewById(R.id.saveButton);
        cancelButton = view.findViewById(R.id.cancelButton);
        
        // 初始化批量生成相关UI元素
        batchSwitch = view.findViewById(R.id.batchSwitch);
        batchOptionsLayout = view.findViewById(R.id.batchOptionsLayout);
        intervalInput = view.findViewById(R.id.intervalInput);
        intervalLayout = view.findViewById(R.id.intervalLayout);
        intervalUnitSpinner = view.findViewById(R.id.intervalUnitSpinner);
        batchModeSpinner = view.findViewById(R.id.batchModeSpinner);
        repeatCountContainer = view.findViewById(R.id.repeatCountContainer);
        repeatCountInput = view.findViewById(R.id.repeatCountInput);
        repeatCountLayout = view.findViewById(R.id.repeatCountLayout);
        endDateLayout = view.findViewById(R.id.endDateLayout);
        endDateButton = view.findViewById(R.id.endDateButton);
        endDateText = view.findViewById(R.id.endDateText);
        
        // 初始化结束日期日历
        endDateCalendar = Calendar.getInstance();

        weekdayCheckboxes = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            CheckBox checkBox = new CheckBox(requireContext());
            checkBox.setText(getWeekdayName(i));
            checkBox.setLayoutParams(new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            weekdaysLayout.addView(checkBox);
            weekdayCheckboxes.add(checkBox);
        }

        setupSpinners();
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> repeatAdapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.repeat_modes, android.R.layout.simple_spinner_item);
        repeatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        repeatModeSpinner.setAdapter(repeatAdapter);

        ArrayAdapter<CharSequence> ringtoneAdapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.ringtones, android.R.layout.simple_spinner_item);
        ringtoneAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ringtoneSpinner.setAdapter(ringtoneAdapter);

        ArrayAdapter<CharSequence> aiStyleAdapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.ai_styles, android.R.layout.simple_spinner_item);
        aiStyleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        aiStyleSpinner.setAdapter(aiStyleAdapter);

        ArrayAdapter<CharSequence> aiPlanAdapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.ai_plans, android.R.layout.simple_spinner_item);
        aiPlanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        aiPlanSpinner.setAdapter(aiPlanAdapter);
        
        // 添加批量生成相关的Spinner适配器
        // 间隔时间单位适配器
        ArrayAdapter<CharSequence> intervalUnitAdapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.interval_units, android.R.layout.simple_spinner_item);
        intervalUnitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        intervalUnitSpinner.setAdapter(intervalUnitAdapter);
        
        // 批量生成方式适配器
        ArrayAdapter<CharSequence> batchModeAdapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.batch_modes, android.R.layout.simple_spinner_item);
        batchModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        batchModeSpinner.setAdapter(batchModeAdapter);
    }

    private void setupListeners() {
        repeatModeSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                weekdaysLayout.setVisibility(selected.equals("自定义") ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        aiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                aiOptionsLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });
        
        // 批量生成相关监听器
        batchSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                batchOptionsLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });
        
        batchModeSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                if (selected.equals("按重复次数")) {
                    repeatCountContainer.setVisibility(View.VISIBLE);
                    endDateLayout.setVisibility(View.GONE);
                } else if (selected.equals("按结束日期")) {
                    repeatCountContainer.setVisibility(View.GONE);
                    endDateLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });
        
        endDateButton.setOnClickListener(v -> showDatePicker());

        saveButton.setOnClickListener(v -> saveAlarm());
        cancelButton.setOnClickListener(v -> dismiss());
    }

    private void populateFields() {
        if (alarm == null) return;

        if (alarm.getTime() != null) {
            String[] timeParts = alarm.getTime().split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);
            timePicker.setHour(hour);
            timePicker.setMinute(minute);
        }

        if (alarm.getLabel() != null) {
            labelInput.setText(alarm.getLabel());
        }

        String repeatMode = alarm.getRepeatMode();
        ArrayAdapter<CharSequence> repeatAdapter = (ArrayAdapter<CharSequence>) repeatModeSpinner.getAdapter();
        int repeatPosition = repeatAdapter.getPosition(repeatMode);
        repeatModeSpinner.setSelection(repeatPosition >= 0 ? repeatPosition : 0);

        if (alarm.getWeekdays() != null) {
            for (int i = 0; i < weekdayCheckboxes.size(); i++) {
                weekdayCheckboxes.get(i).setChecked(alarm.getWeekdays().contains(i));
            }
        }

        String ringtone = alarm.getRingtone();
        ArrayAdapter<CharSequence> ringtoneAdapter = (ArrayAdapter<CharSequence>) ringtoneSpinner.getAdapter();
        int ringtonePosition = ringtoneAdapter.getPosition(ringtone);
        ringtoneSpinner.setSelection(ringtonePosition >= 0 ? ringtonePosition : 0);

        aiSwitch.setChecked(alarm.isAiEnabled());
        aiOptionsLayout.setVisibility(alarm.isAiEnabled() ? View.VISIBLE : View.GONE);

        String aiStyle = alarm.getAiStyle();
        ArrayAdapter<CharSequence> aiStyleAdapter = (ArrayAdapter<CharSequence>) aiStyleSpinner.getAdapter();
        int aiStylePosition = aiStyleAdapter.getPosition(aiStyle);
        aiStyleSpinner.setSelection(aiStylePosition >= 0 ? aiStylePosition : 0);

        String aiPlanId = alarm.getAiPlanId();
        if (aiPlanId != null) {
            ArrayAdapter<CharSequence> aiPlanAdapter = (ArrayAdapter<CharSequence>) aiPlanSpinner.getAdapter();
            int aiPlanPosition = aiPlanAdapter.getPosition(aiPlanId);
            aiPlanSpinner.setSelection(aiPlanPosition >= 0 ? aiPlanPosition : 0);
        }
    }

    private void saveAlarm() {
        String label = labelInput.getText().toString().trim();

        if (label.isEmpty()) {
            labelLayout.setError("请输入标签");
            return;
        }

        String repeatMode = repeatModeSpinner.getSelectedItem().toString();
        List<Integer> weekdays = new ArrayList<>();
        if (repeatMode.equals("自定义")) {
            for (int i = 0; i < weekdayCheckboxes.size(); i++) {
                if (weekdayCheckboxes.get(i).isChecked()) {
                    weekdays.add(i);
                }
            }
        } else if (repeatMode.equals("每天")) {
            for (int i = 0; i < 7; i++) {
                weekdays.add(i);
            }
        } else if (repeatMode.equals("工作日")) {
            for (int i = 1; i <= 5; i++) {
                weekdays.add(i);
            }
        } else if (repeatMode.equals("周末")) {
            weekdays.add(0);
            weekdays.add(6);
        }

        String ringtone = ringtoneSpinner.getSelectedItem().toString();
        boolean aiEnabled = aiSwitch.isChecked();
        String aiStyle = aiEnabled ? aiStyleSpinner.getSelectedItem().toString() : null;
        String aiPlanId = aiEnabled ? aiPlanSpinner.getSelectedItem().toString() : null;

        // 检查是否启用了批量生成
        if (batchSwitch.isChecked()) {
            // 批量生成模式
            String intervalStr = intervalInput.getText().toString().trim();
            if (intervalStr.isEmpty()) {
                intervalLayout.setError("请输入间隔值");
                return;
            }
            
            int interval;
            try {
                interval = Integer.parseInt(intervalStr);
            } catch (NumberFormatException e) {
                intervalLayout.setError("请输入有效的间隔值");
                return;
            }
            
            if (interval <= 0) {
                intervalLayout.setError("间隔值必须大于0");
                return;
            }
            
            String intervalUnit = intervalUnitSpinner.getSelectedItem().toString();
            String batchMode = batchModeSpinner.getSelectedItem().toString();
            
            // 计算间隔时间（分钟）
            int intervalMinutes = 0;
            switch (intervalUnit) {
                case "分钟":
                    intervalMinutes = interval;
                    break;
                case "小时":
                    intervalMinutes = interval * 60;
                    break;
                case "天":
                    intervalMinutes = interval * 24 * 60;
                    break;
            }
            
            // 获取开始时间
            int startHour = timePicker.getHour();
            int startMinute = timePicker.getMinute();
            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.set(Calendar.HOUR_OF_DAY, startHour);
            currentCalendar.set(Calendar.MINUTE, startMinute);
            currentCalendar.set(Calendar.SECOND, 0);
            currentCalendar.set(Calendar.MILLISECOND, 0);
            
            // 生成多个闹钟
            List<Alarm> alarms = new ArrayList<>();
            
            if (batchMode.equals("按重复次数")) {
                // 按重复次数生成
                String repeatCountStr = repeatCountInput.getText().toString().trim();
                if (repeatCountStr.isEmpty()) {
                    repeatCountLayout.setError("请输入重复次数");
                    return;
                }
                
                int repeatCount;
                try {
                    repeatCount = Integer.parseInt(repeatCountStr);
                } catch (NumberFormatException e) {
                    repeatCountLayout.setError("请输入有效的重复次数");
                    return;
                }
                
                if (repeatCount <= 0) {
                    repeatCountLayout.setError("重复次数必须大于0");
                    return;
                }
                
                // 生成指定次数的闹钟
                for (int i = 0; i <= repeatCount; i++) {
                    Alarm newAlarm = new Alarm();
                    newAlarm.setId("alarm_" + System.currentTimeMillis() + "_" + i);
                    newAlarm.setTime(String.format("%02d:%02d", currentCalendar.get(Calendar.HOUR_OF_DAY), currentCalendar.get(Calendar.MINUTE)));
                    newAlarm.setLabel(label + (i > 0 ? " " + i : ""));
                    newAlarm.setEnabled(true);
                    newAlarm.setRepeatMode(repeatMode);
                    newAlarm.setWeekdays(weekdays);
                    newAlarm.setRingtone(ringtone);
                    newAlarm.setAiEnabled(aiEnabled);
                    newAlarm.setAiStyle(aiStyle);
                    newAlarm.setAiPlanId(aiPlanId);
                    alarms.add(newAlarm);
                    
                    // 增加间隔时间
                    currentCalendar.add(Calendar.MINUTE, intervalMinutes);
                }
            } else if (batchMode.equals("按结束日期")) {
                // 按结束日期生成
                Calendar endCalendar = endDateCalendar;
                endCalendar.set(Calendar.HOUR_OF_DAY, 23);
                endCalendar.set(Calendar.MINUTE, 59);
                endCalendar.set(Calendar.SECOND, 59);
                endCalendar.set(Calendar.MILLISECOND, 999);
                
                // 生成到结束日期的闹钟
                int count = 0;
                while (currentCalendar.before(endCalendar)) {
                    Alarm newAlarm = new Alarm();
                    newAlarm.setId("alarm_" + System.currentTimeMillis() + "_" + count);
                    newAlarm.setTime(String.format("%02d:%02d", currentCalendar.get(Calendar.HOUR_OF_DAY), currentCalendar.get(Calendar.MINUTE)));
                    newAlarm.setLabel(label + (count > 0 ? " " + count : ""));
                    newAlarm.setEnabled(true);
                    newAlarm.setRepeatMode(repeatMode);
                    newAlarm.setWeekdays(weekdays);
                    newAlarm.setRingtone(ringtone);
                    newAlarm.setAiEnabled(aiEnabled);
                    newAlarm.setAiStyle(aiStyle);
                    newAlarm.setAiPlanId(aiPlanId);
                    alarms.add(newAlarm);
                    
                    // 增加间隔时间
                    currentCalendar.add(Calendar.MINUTE, intervalMinutes);
                    count++;
                    
                    // 防止无限循环
                    if (count > 1000) {
                        break;
                    }
                }
            }
            
            // 保存所有生成的闹钟
            for (Alarm newAlarm : alarms) {
                listener.onAlarmSaved(newAlarm);
            }
        } else {
            // 单个闹钟模式
            String time = String.format("%02d:%02d", timePicker.getHour(), timePicker.getMinute());
            
            if (alarm == null) {
                alarm = new Alarm();
                alarm.setId("alarm_" + System.currentTimeMillis());
            }

            alarm.setTime(time);
            alarm.setLabel(label);
            alarm.setEnabled(true);
            alarm.setRepeatMode(repeatMode);
            alarm.setWeekdays(weekdays);
            alarm.setRingtone(ringtone);
            alarm.setAiEnabled(aiEnabled);
            alarm.setAiStyle(aiStyle);
            alarm.setAiPlanId(aiPlanId);

            listener.onAlarmSaved(alarm);
        }
        
        dismiss();
    }

    private String getWeekdayName(int day) {
        String[] weekdays = {"日", "一", "二", "三", "四", "五", "六"};
        return weekdays[day];
    }
    
    // 显示日期选择器
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    endDateCalendar.set(selectedYear, selectedMonth, selectedDay);
                    updateEndDateText();
                },
                year,
                month,
                day
        );
        
        datePickerDialog.show();
    }
    
    // 更新结束日期文本
    private void updateEndDateText() {
        int year = endDateCalendar.get(Calendar.YEAR);
        int month = endDateCalendar.get(Calendar.MONTH) + 1;
        int day = endDateCalendar.get(Calendar.DAY_OF_MONTH);
        endDateText.setText(String.format("%d-%02d-%02d", year, month, day));
    }
}
