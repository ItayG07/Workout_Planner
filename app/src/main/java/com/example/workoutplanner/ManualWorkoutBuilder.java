package com.example.workoutplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManualWorkoutBuilder extends AppCompatActivity {

    private LinearLayout equipmentContainer;
    private LinearLayout workoutsContainer;
    private LinearLayout noEquipmentWorkoutsContainer; // Container for no-equipment exercises
    private Button startWorkoutButton;
    private Button resetSelectionButton;
    private Button showNoEquipmentButton; // New button to show no-equipment exercises
    private boolean noEquipmentVisible = false; // Track visibility of no-equipment workouts

    private List<String> selectedEquipment = new ArrayList<>();
    private List<String> selectedWorkouts = new ArrayList<>();
    private Map<String, String> workoutToEquipmentMap = new HashMap<>();

    // Maps equipment types to their workout options
    private Map<String, List<WorkoutOption>> equipmentWorkouts = new HashMap<>();
    // List of all available equipment types (removed NoEquipment from this list)
    private String[] availableEquipment = {"PullUpBar", "DipsBar", "Step", "Trx", "Dumbbell", "ResistanceBands"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_workout_builder);

        // Initialize UI components
        equipmentContainer = findViewById(R.id.equipmentContainer);
        workoutsContainer = findViewById(R.id.workoutsContainer);
        noEquipmentWorkoutsContainer = findViewById(R.id.noEquipmentWorkoutsContainer); // Make sure to add this to your layout
        startWorkoutButton = findViewById(R.id.startWorkoutButton);
        resetSelectionButton = findViewById(R.id.resetSelectionButton);
        showNoEquipmentButton = findViewById(R.id.showNoEquipmentButton); // Make sure to add this to your layout

        // Initialize workout data
        initWorkoutData();

        // Create the equipment selection UI
        createEquipmentSelectionUI();

        // Initially disable the start button until a workout is selected
        startWorkoutButton.setEnabled(false);

        // Set listener for reset button
        resetSelectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetSelections();
            }
        });

        // Set listener for show no-equipment button
        showNoEquipmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleNoEquipmentWorkouts();
            }
        });

        // Set listener for start workout button
        startWorkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!selectedWorkouts.isEmpty()) {
                    // Start the workout routine activity with the selected workouts
                    Intent intent = new Intent(ManualWorkoutBuilder.this, WorkoutRoutineActivity.class);
                    intent.putStringArrayListExtra("SELECTED_WORKOUTS", new ArrayList<>(selectedWorkouts));
                    intent.putStringArrayListExtra("SELECTED_EQUIPMENT", new ArrayList<>(selectedEquipment));
                    startActivity(intent);
                } else {
                    Toast.makeText(ManualWorkoutBuilder.this,
                            "Please select at least one workout", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Update the UI
        updateWorkoutsUI();

        // Initially hide no-equipment workouts section
        noEquipmentWorkoutsContainer.setVisibility(View.GONE);

        // Create the no-equipment workouts section
        createNoEquipmentWorkoutsUI();
    }

    private void createEquipmentSelectionUI() {
        // Clear previous views
        equipmentContainer.removeAllViews();

        // Add header for equipment selection
        TextView instructionsView = new TextView(this);
        instructionsView.setText("Select the equipment you have access to:");
        instructionsView.setTextSize(18);
        instructionsView.setPadding(16, 24, 16, 16);
        instructionsView.setTextColor(getResources().getColor(android.R.color.black));
        equipmentContainer.addView(instructionsView);

        // Add equipment checkboxes
        for (final String equipment : availableEquipment) {
            CheckBox equipmentCheckbox = new CheckBox(this);
            equipmentCheckbox.setText(equipment);
            equipmentCheckbox.setPadding(16, 12, 16, 12);

            // Setup checkbox listener
            equipmentCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        // Add to selected equipment
                        if (!selectedEquipment.contains(equipment)) {
                            selectedEquipment.add(equipment);
                        }
                    } else {
                        // Remove from selected equipment
                        selectedEquipment.remove(equipment);

                        // Also remove any workouts for this equipment
                        List<String> workoutsToRemove = new ArrayList<>();
                        for (String workout : selectedWorkouts) {
                            if (workoutToEquipmentMap.get(workout).equals(equipment)) {
                                workoutsToRemove.add(workout);
                            }
                        }
                        selectedWorkouts.removeAll(workoutsToRemove);
                    }

                    // Update workouts UI based on selected equipment
                    updateWorkoutsUI();
                }
            });

            equipmentContainer.addView(equipmentCheckbox);
        }

        // Add divider
        View divider = new View(this);
        divider.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                2
        );
        dividerParams.setMargins(16, 16, 16, 16);
        divider.setLayoutParams(dividerParams);
        equipmentContainer.addView(divider);
    }

    private void createNoEquipmentWorkoutsUI() {
        // Clear previous views
        noEquipmentWorkoutsContainer.removeAllViews();

        // Add header for no-equipment workouts
        TextView noEquipmentHeaderView = new TextView(this);
        noEquipmentHeaderView.setText("Recommended Bodyweight Exercises");
        noEquipmentHeaderView.setTextSize(18);
        noEquipmentHeaderView.setPadding(16, 24, 16, 16);
        noEquipmentHeaderView.setTextColor(getResources().getColor(android.R.color.black));
        noEquipmentWorkoutsContainer.addView(noEquipmentHeaderView);

        // Add description
        TextView noEquipmentDescView = new TextView(this);
        noEquipmentDescView.setText("These exercises can be done anywhere and require no equipment:");
        noEquipmentDescView.setPadding(16, 8, 16, 16);
        noEquipmentWorkoutsContainer.addView(noEquipmentDescView);

        // Add no-equipment workouts
        List<WorkoutOption> bodyweightWorkouts = equipmentWorkouts.get("NoEquipment");
        if (bodyweightWorkouts != null && !bodyweightWorkouts.isEmpty()) {
            for (final WorkoutOption workout : bodyweightWorkouts) {
                // Create workout title with sets and reps
                TextView workoutTitleView = new TextView(this);
                workoutTitleView.setText(workout.getName() + " - " + workout.getSets() + " sets × " + workout.getReps());
                workoutTitleView.setTextSize(16);
                workoutTitleView.setTypeface(null, android.graphics.Typeface.BOLD);
                workoutTitleView.setPadding(16, 16, 16, 4);
                noEquipmentWorkoutsContainer.addView(workoutTitleView);

                // Create description text view
                TextView descriptionView = new TextView(this);
                descriptionView.setText(workout.getDescription());
                descriptionView.setPadding(16, 0, 16, 16);
                descriptionView.setTextSize(14);

                // Add views to container
                noEquipmentWorkoutsContainer.addView(descriptionView);

                // Add a subtle divider between exercises
                if (bodyweightWorkouts.indexOf(workout) < bodyweightWorkouts.size() - 1) {
                    View divider = new View(this);
                    divider.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                    LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            1
                    );
                    dividerParams.setMargins(16, 8, 16, 8);
                    divider.setLayoutParams(dividerParams);
                    noEquipmentWorkoutsContainer.addView(divider);
                }
            }
        }
    }

    private void toggleNoEquipmentWorkouts() {
        if (noEquipmentVisible) {
            noEquipmentWorkoutsContainer.setVisibility(View.GONE);
            showNoEquipmentButton.setText("Show Recommended Bodyweight Exercises");
            noEquipmentVisible = false;
        } else {
            noEquipmentWorkoutsContainer.setVisibility(View.VISIBLE);
            showNoEquipmentButton.setText("Hide Recommended Bodyweight Exercises");
            noEquipmentVisible = true;
        }
    }

    private void updateWorkoutsUI() {
        // Clear previous workout views
        workoutsContainer.removeAllViews();

        if (selectedEquipment.isEmpty()) {
            TextView noEquipmentView = new TextView(this);
            noEquipmentView.setText("Please select equipment to see available workouts");
            noEquipmentView.setPadding(16, 24, 16, 24);
            workoutsContainer.addView(noEquipmentView);
            startWorkoutButton.setEnabled(!selectedWorkouts.isEmpty());
            return;
        }

        // Add header for workout selection
        TextView workoutHeaderView = new TextView(this);
        workoutHeaderView.setText("Select workouts for your routine:");
        workoutHeaderView.setTextSize(18);
        workoutHeaderView.setPadding(16, 24, 16, 16);
        workoutHeaderView.setTextColor(getResources().getColor(android.R.color.black));
        workoutsContainer.addView(workoutHeaderView);

        // For each selected equipment, add its workouts
        for (String equipment : selectedEquipment) {
            // Skip NoEquipment as it's handled separately
            if (equipment.equals("NoEquipment")) {
                continue;
            }

            // Add equipment header
            TextView equipmentHeaderView = new TextView(this);
            equipmentHeaderView.setText(equipment + " Workouts");
            equipmentHeaderView.setTextSize(16);
            equipmentHeaderView.setPadding(16, 24, 16, 8);
            equipmentHeaderView.setTextColor(getResources().getColor(android.R.color.black));
            workoutsContainer.addView(equipmentHeaderView);

            // Add divider
            View divider = new View(this);
            divider.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1
            );
            dividerParams.setMargins(16, 0, 16, 8);
            divider.setLayoutParams(dividerParams);
            workoutsContainer.addView(divider);

            // Add workouts for this equipment
            List<WorkoutOption> workouts = equipmentWorkouts.get(equipment);
            if (workouts != null && !workouts.isEmpty()) {
                for (final WorkoutOption workout : workouts) {
                    // Create checkbox for each workout
                    CheckBox workoutCheckbox = new CheckBox(this);
                    workoutCheckbox.setText(workout.getName());
                    workoutCheckbox.setPadding(16, 12, 16, 12);
                    workoutCheckbox.setChecked(selectedWorkouts.contains(workout.getName()));

                    // Map workout name to its equipment
                    workoutToEquipmentMap.put(workout.getName(), equipment);

                    // Setup checkbox listener
                    workoutCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                // Add to selected workouts
                                if (!selectedWorkouts.contains(workout.getName())) {
                                    selectedWorkouts.add(workout.getName());
                                }
                            } else {
                                // Remove from selected workouts
                                selectedWorkouts.remove(workout.getName());
                            }

                            // Enable/disable start button
                            startWorkoutButton.setEnabled(!selectedWorkouts.isEmpty());
                        }
                    });

                    // Create description text view
                    TextView descriptionView = new TextView(this);
                    descriptionView.setText(workout.getDescription());
                    descriptionView.setPadding(32, 0, 16, 16);
                    descriptionView.setTextSize(14);

                    // Add views to container
                    workoutsContainer.addView(workoutCheckbox);
                    workoutsContainer.addView(descriptionView);
                }
            }

            // Add space between equipment sections
            View spacer = new View(this);
            spacer.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    16
            ));
            workoutsContainer.addView(spacer);
        }
    }

    private void resetSelections() {
        selectedEquipment.clear();
        selectedWorkouts.clear();

        // Reset all equipment checkboxes
        for (int i = 0; i < equipmentContainer.getChildCount(); i++) {
            View view = equipmentContainer.getChildAt(i);
            if (view instanceof CheckBox) {
                ((CheckBox) view).setChecked(false);
            }
        }

        // Clear workouts section
        updateWorkoutsUI();

        // Disable start button
        startWorkoutButton.setEnabled(false);
    }

    private void initWorkoutData() {
        // Initialize workout options for each equipment type

        // No Equipment workouts
        List<WorkoutOption> bodyweightWorkouts = new ArrayList<>();
        bodyweightWorkouts.add(new WorkoutOption(
                "Push-ups",
                "A fundamental upper body exercise that works your chest, shoulders, triceps, and core.",
                3,
                "10-20 reps"
        ));
        bodyweightWorkouts.add(new WorkoutOption(
                "Squats",
                "A lower body exercise that targets your quadriceps, hamstrings, and glutes.",
                3,
                "15-20 reps"
        ));
        bodyweightWorkouts.add(new WorkoutOption(
                "Lunges",
                "Works your legs one at a time, improving balance and addressing muscle imbalances.",
                3,
                "12 reps per leg"
        ));
        bodyweightWorkouts.add(new WorkoutOption(
                "Plank",
                "An isometric core exercise that also improves shoulder stability and posture.",
                3,
                "30-60 seconds"
        ));
        bodyweightWorkouts.add(new WorkoutOption(
                "Mountain Climbers",
                "A dynamic exercise that provides both strength and cardio benefits.",
                3,
                "30 seconds"
        ));
        bodyweightWorkouts.add(new WorkoutOption(
                "Burpees",
                "A full-body exercise that builds strength and cardiovascular endurance.",
                3,
                "10-15 reps"
        ));
        equipmentWorkouts.put("NoEquipment", bodyweightWorkouts);

        // Pull-up Bar workouts
        List<WorkoutOption> pullUpWorkouts = new ArrayList<>();
        pullUpWorkouts.add(new WorkoutOption(
                "Standard Pull-ups",
                "A classic upper body compound exercise that targets your lats, biceps, and middle back.",
                3,
                "8-12 reps"
        ));
        pullUpWorkouts.add(new WorkoutOption(
                "Chin-ups",
                "Similar to pull-ups but with palms facing you, putting more emphasis on the biceps.",
                3,
                "8-12 reps"
        ));
        pullUpWorkouts.add(new WorkoutOption(
                "Hanging Leg Raises",
                "An effective core exercise that targets the lower abs and hip flexors.",
                3,
                "10-15 reps"
        ));
        equipmentWorkouts.put("PullUpBar", pullUpWorkouts);

        // Dips Bar workouts
        List<WorkoutOption> dipsWorkouts = new ArrayList<>();
        dipsWorkouts.add(new WorkoutOption(
                "Dips",
                "A compound exercise that targets your chest, triceps, and shoulders.",
                3,
                "8-12 reps"
        ));
        dipsWorkouts.add(new WorkoutOption(
                "L-sits",
                "An isometric core exercise that also builds shoulder strength.",
                3,
                "20-30 seconds"
        ));
        dipsWorkouts.add(new WorkoutOption(
                "Straight Bar Dips",
                "A variation of dips performed on a straight bar, requiring more stabilization.",
                3,
                "8-10 reps"
        ));
        equipmentWorkouts.put("DipsBar", dipsWorkouts);

        // Step workouts
        List<WorkoutOption> stepWorkouts = new ArrayList<>();
        stepWorkouts.add(new WorkoutOption(
                "Step-ups",
                "A lower body exercise targeting the quads, hamstrings, and glutes.",
                3,
                "15 reps each leg"
        ));
        stepWorkouts.add(new WorkoutOption(
                "Box Jumps",
                "A plyometric exercise that builds explosive power in the lower body.",
                3,
                "10 reps"
        ));
        stepWorkouts.add(new WorkoutOption(
                "Lateral Step-ups",
                "A variation that targets the outer thighs and glutes.",
                3,
                "12 reps each side"
        ));
        equipmentWorkouts.put("Step", stepWorkouts);

        // TRX workouts
        List<WorkoutOption> trxWorkouts = new ArrayList<>();
        trxWorkouts.add(new WorkoutOption(
                "TRX Rows",
                "A back exercise that targets the middle back, lats, and biceps.",
                3,
                "12-15 reps"
        ));
        trxWorkouts.add(new WorkoutOption(
                "TRX Push-ups",
                "A chest exercise with increased instability for greater muscle engagement.",
                3,
                "10-12 reps"
        ));
        trxWorkouts.add(new WorkoutOption(
                "TRX Hamstring Curls",
                "A posterior chain exercise targeting the hamstrings and glutes.",
                3,
                "10-12 reps"
        ));
        equipmentWorkouts.put("Trx", trxWorkouts);

        // Dumbbell workouts
        List<WorkoutOption> dumbbellWorkouts = new ArrayList<>();
        dumbbellWorkouts.add(new WorkoutOption(
                "Dumbbell Bench Press",
                "A chest exercise that also engages the shoulders and triceps.",
                3,
                "8-12 reps"
        ));
        dumbbellWorkouts.add(new WorkoutOption(
                "Dumbbell Rows",
                "A back exercise targeting the lats and middle back.",
                3,
                "10-12 reps per arm"
        ));
        dumbbellWorkouts.add(new WorkoutOption(
                "Dumbbell Lunges",
                "A lower body exercise working the quads, hamstrings, and glutes.",
                3,
                "10 reps per leg"
        ));
        equipmentWorkouts.put("Dumbbell", dumbbellWorkouts);

        // Resistance Bands workouts
        List<WorkoutOption> bandsWorkouts = new ArrayList<>();
        bandsWorkouts.add(new WorkoutOption(
                "Band Pull-aparts",
                "An exercise targeting the rear deltoids and upper back.",
                3,
                "15-20 reps"
        ));
        bandsWorkouts.add(new WorkoutOption(
                "Banded Squats",
                "A lower body exercise with added resistance for greater muscle engagement.",
                3,
                "12-15 reps"
        ));
        bandsWorkouts.add(new WorkoutOption(
                "Resistance Band Bicep Curls",
                "An isolation exercise for the biceps.",
                3,
                "12-15 reps"
        ));
        equipmentWorkouts.put("ResistanceBands", bandsWorkouts);
    }

    // Workout option model class
    private static class WorkoutOption {
        private String name;
        private String description;
        private int sets;
        private String reps;

        public WorkoutOption(String name, String description, int sets, String reps) {
            this.name = name;
            this.description = description;
            this.sets = sets;
            this.reps = reps;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public int getSets() {
            return sets;
        }

        public String getReps() {
            return reps;
        }
    }
}