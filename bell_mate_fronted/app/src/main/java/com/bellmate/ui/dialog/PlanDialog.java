package com.bellmate.ui.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.bellmate.R;
import com.bellmate.data.plan.Plan;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import java.util.Calendar;

public class PlanDialog extends DialogFragment {

    public interface PlanDialogListener {
        void onPlanSaved(Plan plan);
    }

    private PlanDialogListener listener;
    private Plan plan;
    private boolean isEditMode;

    private TextInputEditText titleInput;
    private TextInputLayout titleLayout;
    private TextInputEditText descriptionInput;
    private MaterialTextView dateText;
    private MaterialTextView timeText;
    private MaterialButton dateButton;
    private MaterialButton timeButton;
    private MaterialButton saveButton;
    private MaterialButton generateAlarmButton;
    private MaterialButton cancelButton;

    private Calendar selectedCalendar;

    public PlanDialog() {
    }

    public static PlanDialog newInstance(Plan plan, boolean isEditMode) {
        PlanDialog dialog = new PlanDialog();
        Bundle args = new Bundle();
        args.putSerializable("plan", plan);
        args.putBoolean("isEditMode", isEditMode);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            Fragment parentFragment = getParentFragment();
            if (parentFragment instanceof PlanDialogListener) {
                listener = (PlanDialogListener) parentFragment;
            } else {
                listener = (PlanDialogListener) context;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException("Parent must implement PlanDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            plan = (Plan) args.getSerializable("plan");
            isEditMode = args.getBoolean("isEditMode", false);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_plan, null);

        selectedCalendar = Calendar.getInstance();
        initViews(view);
        setupListeners();
        if (plan != null) {
            populateFields();
        }

        builder.setView(view)
                .setTitle(isEditMode ? "编辑计划" : "新增计划");

        return builder.create();
    }

    private void initViews(View view) {
        titleInput = view.findViewById(R.id.titleInput);
        titleLayout = view.findViewById(R.id.titleLayout);
        descriptionInput = view.findViewById(R.id.descriptionInput);
        dateText = view.findViewById(R.id.dateText);
        timeText = view.findViewById(R.id.timeText);
        dateButton = view.findViewById(R.id.dateButton);
        timeButton = view.findViewById(R.id.timeButton);
        saveButton = view.findViewById(R.id.saveButton);
        generateAlarmButton = view.findViewById(R.id.generateAlarmButton);
        cancelButton = view.findViewById(R.id.cancelButton);

        updateDateTimeText();
    }

    private void setupListeners() {
        dateButton.setOnClickListener(v -> showDatePicker());
        timeButton.setOnClickListener(v -> showTimePicker());
        saveButton.setOnClickListener(v -> savePlan());
        generateAlarmButton.setOnClickListener(v -> generateAlarmFromPlan());
        cancelButton.setOnClickListener(v -> dismiss());
    }

    private void populateFields() {
        if (plan == null) return;

        titleInput.setText(plan.getTitle());
        descriptionInput.setText(plan.getDescription());

        if (plan.getDatetime() != null) {
            String[] datetimeParts = plan.getDatetime().split(" ");
            if (datetimeParts.length == 2) {
                String[] dateParts = datetimeParts[0].split("-");
                String[] timeParts = datetimeParts[1].split(":");
                if (dateParts.length == 3 && timeParts.length == 2) {
                    int year = Integer.parseInt(dateParts[0]);
                    int month = Integer.parseInt(dateParts[1]) - 1;
                    int day = Integer.parseInt(dateParts[2]);
                    int hour = Integer.parseInt(timeParts[0]);
                    int minute = Integer.parseInt(timeParts[1]);

                    selectedCalendar.set(year, month, day, hour, minute);
                    updateDateTimeText();
                }
            }
        }
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selectedCalendar.set(Calendar.YEAR, year);
                        selectedCalendar.set(Calendar.MONTH, month);
                        selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateDateTimeText();
                    }
                },
                selectedCalendar.get(Calendar.YEAR),
                selectedCalendar.get(Calendar.MONTH),
                selectedCalendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        selectedCalendar.set(Calendar.MINUTE, minute);
                        updateDateTimeText();
                    }
                },
                selectedCalendar.get(Calendar.HOUR_OF_DAY),
                selectedCalendar.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    private void updateDateTimeText() {
        int year = selectedCalendar.get(Calendar.YEAR);
        int month = selectedCalendar.get(Calendar.MONTH) + 1;
        int day = selectedCalendar.get(Calendar.DAY_OF_MONTH);
        int hour = selectedCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = selectedCalendar.get(Calendar.MINUTE);

        dateText.setText(String.format("%d-%02d-%02d", year, month, day));
        timeText.setText(String.format("%02d:%02d", hour, minute));
    }

    private void savePlan() {
        String title = titleInput.getText().toString().trim();
        if (title.isEmpty()) {
            titleLayout.setError("请输入标题");
            return;
        }

        String description = descriptionInput.getText().toString().trim();
        String datetime = dateText.getText().toString() + " " + timeText.getText().toString();

        if (plan == null) {
            plan = new Plan();
            plan.setId("plan_" + System.currentTimeMillis());
        }

        plan.setTitle(title);
        plan.setDescription(description);
        plan.setDatetime(datetime);
        plan.setDone(false);
        plan.setPriority(0);

        listener.onPlanSaved(plan);
        dismiss();
    }

    private void generateAlarmFromPlan() {
        String title = titleInput.getText().toString().trim();
        if (title.isEmpty()) {
            titleLayout.setError("请输入标题");
            return;
        }

        String time = timeText.getText().toString();
        String label = "计划: " + title;

        // 保存计划
        savePlan();

        // 生成闹钟
        com.bellmate.data.alarm.Alarm alarm = new com.bellmate.data.alarm.Alarm();
        alarm.setId("alarm_" + System.currentTimeMillis());
        alarm.setTime(time);
        alarm.setLabel(label);
        alarm.setEnabled(true);
        alarm.setRepeatMode("仅一次");
        alarm.setRingtone("默认铃声");
        alarm.setAiEnabled(false);
        alarm.setAiPlanId(plan.getId());

        com.bellmate.data.alarm.AlarmManager alarmManager = com.bellmate.data.alarm.AlarmManager.get();
        alarmManager.addAlarm(alarm);

        // 显示成功消息
        requireActivity().runOnUiThread(() -> {
            Toast.makeText(requireContext(), "已根据计划生成闹钟", Toast.LENGTH_SHORT).show();
        });
    }
}
