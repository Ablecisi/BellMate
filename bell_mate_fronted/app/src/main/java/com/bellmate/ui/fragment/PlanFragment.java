package com.bellmate.ui.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bellmate.R;
import com.bellmate.data.plan.Plan;
import com.bellmate.data.plan.PlanManager;
import com.bellmate.ui.dialog.PlanDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.HashSet;

public class PlanFragment extends Fragment implements PlanDialog.PlanDialogListener {

    private TabLayout tabLayout;
    private RecyclerView planRecyclerView;
    private FloatingActionButton addPlanFab;
    private View multiSelectBar;
    private TextView multiSelectCount;
    private Button selectAllButton;
    private Button deleteSelectedButton;
    private Button cancelSelectionButton;

    private PlanAdapter planAdapter;
    private List<Plan> plans;
    private List<Plan> filteredPlans;

    private int currentFilter = 0;

    private boolean selectionMode = false;
    private Set<String> selectedPlanIds = new HashSet<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plan, container, false);

        tabLayout = view.findViewById(R.id.tabLayout);
        planRecyclerView = view.findViewById(R.id.planRecyclerView);
        addPlanFab = view.findViewById(R.id.addPlanFab);
        multiSelectBar = view.findViewById(R.id.multiSelectBar);
        multiSelectCount = view.findViewById(R.id.multiSelectCount);
        selectAllButton = view.findViewById(R.id.selectAllButton);
        deleteSelectedButton = view.findViewById(R.id.deleteSelectedButton);
        cancelSelectionButton = view.findViewById(R.id.cancelSelectionButton);

        planRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        loadPlans();

        tabLayout.addTab(tabLayout.newTab().setText("今日计划"));
        tabLayout.addTab(tabLayout.newTab().setText("未来计划"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentFilter = tab.getPosition();
                exitSelectionMode();
                filterPlans();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        addPlanFab.setOnClickListener(v -> showAddPlanDialog());

        selectAllButton.setOnClickListener(v -> toggleSelectAll());
        deleteSelectedButton.setOnClickListener(v -> deleteSelectedPlans());
        cancelSelectionButton.setOnClickListener(v -> exitSelectionMode());

        return view;
    }

    private void loadPlans() {
        plans = PlanManager.get().getAllPlans();
        filterPlans();
    }

    private void filterPlans() {
        filteredPlans = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = sdf.format(new Date());

        for (Plan plan : plans) {
            String planDate = plan.getDatetime().substring(0, 10);
            if (currentFilter == 0) {
                if (planDate.equals(today)) {
                    filteredPlans.add(plan);
                }
            } else {
                if (!planDate.equals(today)) {
                    filteredPlans.add(plan);
                }
            }
        }

        if (planAdapter == null) {
            planAdapter = new PlanAdapter(filteredPlans);
            planRecyclerView.setAdapter(planAdapter);
        } else {
            planAdapter.updatePlans(filteredPlans);
        }
    }

    private void showAddPlanDialog() {
        showPlanDialog(null);
    }

    private void showEditPlanDialog(Plan plan) {
        showPlanDialog(plan);
    }

    private void showPlanDialog(Plan plan) {
        PlanDialog dialog = PlanDialog.newInstance(plan, plan != null);
        dialog.show(getChildFragmentManager(), "PlanDialog");
    }

    private void deleteSelectedPlans() {
        if (selectedPlanIds.isEmpty()) return;

        new AlertDialog.Builder(requireContext())
                .setTitle("确认删除")
                .setMessage("确定要删除选中的 " + selectedPlanIds.size() + " 个计划吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    for (String id : selectedPlanIds) {
                        PlanManager.get().deletePlan(id);
                    }
                    Toast.makeText(requireContext(), "已删除 " + selectedPlanIds.size() + " 个计划", Toast.LENGTH_SHORT).show();
                    exitSelectionMode();
                    loadPlans();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void enterSelectionMode(String planId) {
        selectionMode = true;
        if (planId != null) {
            selectedPlanIds.add(planId);
        }
        updateSelectionBar();
        planAdapter.notifyDataSetChanged();
    }

    private void toggleSelection(String planId) {
        if (planId == null) return;
        if (selectedPlanIds.contains(planId)) {
            selectedPlanIds.remove(planId);
        } else {
            selectedPlanIds.add(planId);
        }
        if (selectedPlanIds.isEmpty()) {
            selectionMode = false;
        }
        updateSelectionBar();
        planAdapter.notifyDataSetChanged();
    }

    private void exitSelectionMode() {
        selectionMode = false;
        selectedPlanIds.clear();
        updateSelectionBar();
        planAdapter.notifyDataSetChanged();
    }

    private void updateSelectionBar() {
        if (selectionMode) {
            multiSelectBar.setVisibility(View.VISIBLE);
            multiSelectCount.setText("已选择 " + selectedPlanIds.size() + " 项");
            
            boolean allSelected = !filteredPlans.isEmpty() && selectedPlanIds.size() == filteredPlans.size();
            if (allSelected) {
                selectAllButton.setText("全不选");
            } else {
                selectAllButton.setText("全选");
            }
        } else {
            multiSelectBar.setVisibility(View.GONE);
        }
    }

    private void toggleSelectAll() {
        boolean allSelected = !filteredPlans.isEmpty() && selectedPlanIds.size() == filteredPlans.size();
        
        if (allSelected) {
            selectedPlanIds.clear();
        } else {
            selectedPlanIds.clear();
            for (Plan plan : filteredPlans) {
                selectedPlanIds.add(plan.getId());
            }
        }
        
        if (selectedPlanIds.isEmpty()) {
            selectionMode = false;
        }
        
        updateSelectionBar();
        planAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPlanSaved(Plan plan) {
        if (plan != null) {
            if (plans.contains(plan)) {
                PlanManager.get().updatePlan(plan);
            } else {
                PlanManager.get().addPlan(plan);
            }
            loadPlans();
        }
    }

    private class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.PlanViewHolder> {

        private List<Plan> plans;

        public PlanAdapter(List<Plan> plans) {
            this.plans = plans;
        }

        public void updatePlans(List<Plan> plans) {
            this.plans = plans;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plan, parent, false);
            return new PlanViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
            Plan plan = plans.get(position);
            holder.bind(plan);
        }

        @Override
        public int getItemCount() {
            return plans.size();
        }

        class PlanViewHolder extends RecyclerView.ViewHolder {

            private TextView titleText;
            private TextView timeText;
            private TextView priorityText;
            private TextView remarkText;
            private Button markDoneButton;
            private View selectedBorder;
            private com.google.android.material.card.MaterialCardView planCard;

            public PlanViewHolder(@NonNull View itemView) {
                super(itemView);
                titleText = itemView.findViewById(R.id.titleText);
                timeText = itemView.findViewById(R.id.timeText);
                priorityText = itemView.findViewById(R.id.priorityText);
                remarkText = itemView.findViewById(R.id.remarkText);
                markDoneButton = itemView.findViewById(R.id.markDoneButton);
                selectedBorder = itemView.findViewById(R.id.selectedBorder);
                planCard = itemView.findViewById(R.id.planCard);
            }

            public void bind(Plan plan) {
                titleText.setText(plan.getTitle());
                timeText.setText(plan.getDatetime());

                String priorityTextStr;
                int priorityBg;
                int priorityColor;
                switch (plan.getPriority()) {
                    case 0:
                        priorityTextStr = "高优先级";
                        priorityBg = R.drawable.priority_chip_high;
                        priorityColor = R.color.md_sys_color_error;
                        break;
                    case 1:
                        priorityTextStr = "中优先级";
                        priorityBg = R.drawable.priority_chip_medium;
                        priorityColor = R.color.md_sys_color_warning;
                        break;
                    default:
                        priorityTextStr = "低优先级";
                        priorityBg = R.drawable.priority_chip_low;
                        priorityColor = R.color.md_sys_color_success;
                        break;
                }
                priorityText.setText(priorityTextStr);
                priorityText.setBackgroundResource(priorityBg);
                priorityText.setTextColor(getResources().getColor(priorityColor, requireContext().getTheme()));

                if (plan.getDescription() != null && !plan.getDescription().isEmpty()) {
                    remarkText.setText(plan.getDescription());
                    remarkText.setVisibility(View.VISIBLE);
                } else {
                    remarkText.setVisibility(View.GONE);
                }

                if (plan.isDone()) {
                    titleText.setPaintFlags(titleText.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                    titleText.setTextColor(getResources().getColor(android.R.color.darker_gray, requireContext().getTheme()));
                } else {
                    titleText.setPaintFlags(titleText.getPaintFlags() & ~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                    titleText.setTextColor(getResources().getColor(R.color.md_sys_color_on_surface, requireContext().getTheme()));
                }

                if (selectionMode) {
                    boolean selected = selectedPlanIds.contains(plan.getId());
                    selectedBorder.setVisibility(selected ? View.VISIBLE : View.GONE);
                    if (selected) {
                        planCard.setCardBackgroundColor(getResources().getColor(R.color.md_sys_color_primary_container, requireContext().getTheme()));
                        planCard.setStrokeWidth(3);
                        planCard.setStrokeColor(getResources().getColor(R.color.md_sys_color_primary, requireContext().getTheme()));
                    } else {
                        planCard.setCardBackgroundColor(getResources().getColor(R.color.md_sys_color_surface, requireContext().getTheme()));
                        planCard.setStrokeWidth(1);
                        planCard.setStrokeColor(getResources().getColor(R.color.md_sys_color_surface_variant, requireContext().getTheme()));
                    }
                } else {
                    selectedBorder.setVisibility(View.GONE);
                    planCard.setCardBackgroundColor(getResources().getColor(R.color.md_sys_color_surface, requireContext().getTheme()));
                    planCard.setStrokeWidth(1);
                    planCard.setStrokeColor(getResources().getColor(R.color.md_sys_color_surface_variant, requireContext().getTheme()));
                }

                markDoneButton.setOnClickListener(v -> {
                    plan.setDone(!plan.isDone());
                    PlanManager.get().updatePlan(plan);
                    notifyDataSetChanged();
                });

                itemView.setOnClickListener(v -> {
                    if (selectionMode) {
                        toggleSelection(plan.getId());
                    } else {
                        showEditPlanDialog(plan);
                    }
                });

                itemView.setOnLongClickListener(v -> {
                    if (!selectionMode) {
                        enterSelectionMode(plan.getId());
                    }
                    return true;
                });
            }
        }
    }
}