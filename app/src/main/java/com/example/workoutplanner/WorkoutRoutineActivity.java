package com.example.workoutplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkoutRoutineActivity extends AppCompatActivity {

    private TextView routineTitleTextView;
    private ListView workoutListView;
    private Button finishRoutineButton;

    private ArrayList<String> selectedEquipment;
    private ArrayList<String> selectedWorkouts;
    private Map<String, WorkoutOption> workoutDetails = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_routine);

        // Initialize UI components
        routineTitleTextView = findViewById(R.id.routineTitleTextView);
        workoutListView = findViewById(R.id.workoutListView);
        finishRoutineButton = findViewById(R.id.finishRoutineButton);

        // Get data from intent
        selectedEquipment = getIntent().getStringArrayListExtra("SELECTED_EQUIPMENT");
        selectedWorkouts = getIntent().getStringArrayListExtra("SELECTED_WORKOUTS");

        // Build title from selected equipment
        StringBuilder titleBuilder = new StringBuilder("Your Workout Routine\n");
        if (selectedEquipment != null && !selectedEquipment.isEmpty()) {
            titleBuilder.append("Using: ");
            for (int i = 0; i < selectedEquipment.size(); i++) {
                titleBuilder.append(selectedEquipment.get(i));
                if (i < selectedEquipment.size() - 1) {
                    titleBuilder.append(", ");
                }
            }
        }
        routineTitleTextView.setText(titleBuilder.toString());

        // Initialize workout details
        initWorkoutDetails();

        // Set up the list view with selected workouts
        if (selectedWorkouts != null && !selectedWorkouts.isEmpty()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    selectedWorkouts
            );

            workoutListView.setAdapter(adapter);

            // Set click listener to start a specific workout
            workoutListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String workoutName = selectedWorkouts.get(position);
                    WorkoutOption workout = workoutDetails.get(workoutName);

                    if (workout != null) {
                        // Start the workout detail activity
                        Intent intent = new Intent(WorkoutRoutineActivity.this, WorkoutDetailActivity.class);
                        intent.putExtra("WORKOUT_NAME", workout.getName());
                        intent.putExtra("WORKOUT_DESCRIPTION", workout.getDescription());
                        intent.putExtra("WORKOUT_SETS", workout.getSets());
                        intent.putExtra("WORKOUT_REPS", workout.getReps());
                        startActivity(intent);
                    }
                }
            });
        } else {
            Toast.makeText(this, "No workouts selected", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Set listener for finish button
        finishRoutineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close all activities and return to main activity
                Intent intent = new Intent(WorkoutRoutineActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initWorkoutDetails() {
        // Here we recreate all the workout details so we can look them up by name
        // In a real app, this data should be stored in a database

        // Pull-up Bar workouts
        workoutDetails.put("Standard Pull-ups", new WorkoutOption(
                "Standard Pull-ups",
                "A classic upper body compound exercise that targets your lats, biceps, and middle back.",
                3,
                "8-12 reps"
        ));
        workoutDetails.put("Chin-ups", new WorkoutOption(
                "Chin-ups",
                "Similar to pull-ups but with palms facing you, putting more emphasis on the biceps.",
                3,
                "8-12 reps"
        ));
        workoutDetails.put("Hanging Leg Raises", new WorkoutOption(
                "Hanging Leg Raises",
                "An effective core exercise that targets the lower abs and hip flexors.",
                3,
                "10-15 reps"
        ));

        // Dips Bar workouts
        workoutDetails.put("Dips", new WorkoutOption(
                "Dips",
                "A compound exercise that targets your chest, triceps, and shoulders.",
                3,
                "8-12 reps"
        ));
        workoutDetails.put("L-sits", new WorkoutOption(
                "L-sits",
                "An isometric core exercise that also builds shoulder strength.",
                3,
                "20-30 seconds"
        ));
        workoutDetails.put("Straight Bar Dips", new WorkoutOption(
                "Straight Bar Dips",
                "A variation of dips performed on a straight bar, requiring more stabilization.",
                3,
                "8-10 reps"
        ));

        // Step workouts
        workoutDetails.put("Step-ups", new WorkoutOption(
                "Step-ups",
                "A lower body exercise targeting the quads, hamstrings, and glutes.",
                3,
                "15 reps each leg"
        ));
        workoutDetails.put("Box Jumps", new WorkoutOption(
                "Box Jumps",
                "A plyometric exercise that builds explosive power in the lower body.",
                3,
                "10 reps"
        ));
        workoutDetails.put("Lateral Step-ups", new WorkoutOption(
                "Lateral Step-ups",
                "A variation that targets the outer thighs and glutes.",
                3,
                "12 reps each side"
        ));

        // TRX workouts
        workoutDetails.put("TRX Rows", new WorkoutOption(
                "TRX Rows",
                "A back exercise that targets the middle back, lats, and biceps.",
                3,
                "12-15 reps"
        ));
        workoutDetails.put("TRX Push-ups", new WorkoutOption(
                "TRX Push-ups",
                "A chest exercise with increased instability for greater muscle engagement.",
                3,
                "10-12 reps"
        ));
        workoutDetails.put("TRX Hamstring Curls", new WorkoutOption(
                "TRX Hamstring Curls",
                "A posterior chain exercise targeting the hamstrings and glutes.",
                3,
                "10-12 reps"
        ));

        // Dumbbell workouts
        workoutDetails.put("Dumbbell Bench Press", new WorkoutOption(
                "Dumbbell Bench Press",
                "A chest exercise that also engages the shoulders and triceps.",
                3,
                "8-12 reps"
        ));
        workoutDetails.put("Dumbbell Rows", new WorkoutOption(
                "Dumbbell Rows",
                "A back exercise targeting the lats and middle back.",
                3,
                "10-12 reps per arm"
        ));
        workoutDetails.put("Dumbbell Lunges", new WorkoutOption(
                "Dumbbell Lunges",
                "A lower body exercise working the quads, hamstrings, and glutes.",
                3,
                "10 reps per leg"
        ));

        // Resistance Bands workouts
        workoutDetails.put("Band Pull-aparts", new WorkoutOption(
                "Band Pull-aparts",
                "An exercise targeting the rear deltoids and upper back.",
                3,
                "15-20 reps"
        ));
        workoutDetails.put("Banded Squats", new WorkoutOption(
                "Banded Squats",
                "A lower body exercise with added resistance for greater muscle engagement.",
                3,
                "12-15 reps"
        ));
        workoutDetails.put("Resistance Band Bicep Curls", new WorkoutOption(
                "Resistance Band Bicep Curls",
                "An isolation exercise for the biceps.",
                3,
                "12-15 reps"
        ));
    }

    // Workout option model class (duplicated from WorkoutSelectionActivity for simplicity)
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