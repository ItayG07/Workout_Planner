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

public class WorkoutSelectionActivity extends AppCompatActivity {

    private LinearLayout workoutsContainer;
    private Button startWorkoutButton;

    private String[] detectedObjects;
    private List<String> selectedWorkouts = new ArrayList<>();
    private Map<String, String> workoutToEquipmentMap = new HashMap<>();

    // Maps equipment types to their workout options
    private Map<String, List<WorkoutOption>> equipmentWorkouts = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_selection);

        // Initialize UI components
        workoutsContainer = findViewById(R.id.workoutsContainer);
        startWorkoutButton = findViewById(R.id.startWorkoutButton);

        // Initialize workout data
        initWorkoutData();

        // Get detected equipment from intent
        detectedObjects = getIntent().getStringArrayExtra("DETECTED_OBJECTS");

        if (detectedObjects != null && detectedObjects.length > 0) {
            // Build the UI dynamically for all detected equipment
            createWorkoutSelectionUI();
        } else {
            Toast.makeText(this, "No equipment detected", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initially disable the start button until a workout is selected
        startWorkoutButton.setEnabled(false);

        // Set listener for start workout button
        startWorkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!selectedWorkouts.isEmpty()) {
                    // Start the workout routine activity with the selected workouts
                    Intent intent = new Intent(WorkoutSelectionActivity.this, WorkoutRoutineActivity.class);
                    intent.putStringArrayListExtra("SELECTED_WORKOUTS", new ArrayList<>(selectedWorkouts));

                    // Pass all equipment types that have selected workouts
                    List<String> selectedEquipment = new ArrayList<>();
                    for (String workout : selectedWorkouts) {
                        String equipment = workoutToEquipmentMap.get(workout);
                        if (equipment != null && !selectedEquipment.contains(equipment)) {
                            selectedEquipment.add(equipment);
                        }
                    }
                    intent.putStringArrayListExtra("SELECTED_EQUIPMENT", new ArrayList<>(selectedEquipment));

                    startActivity(intent);
                } else {
                    Toast.makeText(WorkoutSelectionActivity.this,
                            "Please select at least one workout", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createWorkoutSelectionUI() {
        // Clear previous views
        workoutsContainer.removeAllViews();

        // For each detected equipment, add its workouts
        for (String equipment : detectedObjects) {
            // Add equipment header
            TextView equipmentHeaderView = new TextView(this);
            equipmentHeaderView.setText(equipment + " Workouts");
            equipmentHeaderView.setTextSize(18);
            equipmentHeaderView.setPadding(16, 24, 16, 8);
            equipmentHeaderView.setTextColor(getResources().getColor(android.R.color.black));
            workoutsContainer.addView(equipmentHeaderView);

            // Add divider
            View divider = new View(this);
            divider.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    2
            );
            dividerParams.setMargins(16, 0, 16, 16);
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
            } else {
                // Add message if no workouts available
                TextView noWorkoutsView = new TextView(this);
                noWorkoutsView.setText("No workouts available for " + equipment);
                noWorkoutsView.setPadding(16, 8, 16, 24);
                workoutsContainer.addView(noWorkoutsView);
            }

            // Add space between equipment sections
            View spacer = new View(this);
            spacer.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    32
            ));
            workoutsContainer.addView(spacer);
        }
    }

    private void initWorkoutData() {
        // Initialize workout options for each equipment type

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