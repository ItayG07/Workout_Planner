package com.example.workoutplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class ObjectInfoActivity extends AppCompatActivity {

    // Map containing information about each type of exercise equipment
    private Map<String, EquipmentDetails> equipmentInfo = new HashMap<>();
    private String[] detectedObjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_info);

        // Initialize equipment information
        initEquipmentInfo();

        // Get detected objects from intent
        detectedObjects = getIntent().getStringArrayExtra("DETECTED_OBJECTS");

        // Display information for each detected object
        LinearLayout infoContainer = findViewById(R.id.infoContainer);

        // Add default bodyweight exercises recommendation
        TextView bodyweightHeaderView = new TextView(this);
        bodyweightHeaderView.setText("Exercises You Can Do Anywhere");
        bodyweightHeaderView.setTextSize(20);
        bodyweightHeaderView.setPadding(16, 24, 16, 8);
        bodyweightHeaderView.setTextColor(getResources().getColor(android.R.color.black));
        infoContainer.addView(bodyweightHeaderView);

        TextView bodyweightInfoView = new TextView(this);
        bodyweightInfoView.setText(
                "No equipment? No problem! Here are exercises you can do anywhere:\n\n" +
                        "• Push-ups: 3 sets of 10-20 reps\n" +
                        "  Works chest, shoulders, triceps, and core\n\n" +
                        "• Squats: 3 sets of 15-20 reps\n" +
                        "  Targets quadriceps, hamstrings, and glutes\n\n" +
                        "• Lunges: 3 sets of 12 reps per leg\n" +
                        "  Improves balance and addresses muscle imbalances\n\n" +
                        "• Plank: 3 sets of 30-60 seconds\n" +
                        "  Strengthens core, improves posture\n\n" +
                        "• Mountain Climbers: 3 sets of 30 seconds\n" +
                        "  Provides both strength and cardio benefits\n\n" +
                        "• Burpees: 3 sets of 10-15 reps\n" +
                        "  Full-body exercise for strength and endurance"
        );
        bodyweightInfoView.setPadding(16, 8, 16, 24);
        infoContainer.addView(bodyweightInfoView);

        // Add a divider
        View divider = new View(this);
        divider.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                2
        );
        dividerParams.setMargins(16, 16, 16, 16);
        divider.setLayoutParams(dividerParams);
        infoContainer.addView(divider);

        if (detectedObjects != null && detectedObjects.length > 0) {
            TextView detectedHeaderView = new TextView(this);
            detectedHeaderView.setText("Detected Equipment");
            detectedHeaderView.setTextSize(20);
            detectedHeaderView.setPadding(16, 24, 16, 8);
            detectedHeaderView.setTextColor(getResources().getColor(android.R.color.black));
            infoContainer.addView(detectedHeaderView);

            for (String object : detectedObjects) {
                // Create a TextView for the object title
                TextView titleView = new TextView(this);
                titleView.setText(object);
                titleView.setTextSize(18);
                titleView.setPadding(16, 24, 16, 8);
                titleView.setTextColor(getResources().getColor(android.R.color.black));
                infoContainer.addView(titleView);

                // Get equipment details
                EquipmentDetails details = equipmentInfo.get(object);
                if (details != null) {
                    // Create a TextView for the object information
                    TextView infoView = new TextView(this);
                    infoView.setText(details.getDescription());
                    infoView.setPadding(16, 8, 16, 16);
                    infoContainer.addView(infoView);

                    // Create a TextView for recommended exercises
                    TextView exercisesView = new TextView(this);
                    exercisesView.setText("Recommended Exercises:\n\n" + details.getExercises());
                    exercisesView.setPadding(16, 8, 16, 24);
                    infoContainer.addView(exercisesView);
                } else {
                    // Create a TextView for unknown equipment
                    TextView noInfoView = new TextView(this);
                    noInfoView.setText("No information available for this equipment.");
                    noInfoView.setPadding(16, 8, 16, 24);
                    infoContainer.addView(noInfoView);
                }
            }

            // Add button to customize workout
            Button customizeWorkoutButton = new Button(this);
            customizeWorkoutButton.setText("Customize Your Workout");
            customizeWorkoutButton.setPadding(16, 16, 16, 16);
            LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            buttonParams.setMargins(16, 24, 16, 16);
            customizeWorkoutButton.setLayoutParams(buttonParams);
            infoContainer.addView(customizeWorkoutButton);

            // Set click listener for the button
            customizeWorkoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Navigate to workout selection with detected objects
                    Intent intent = new Intent(ObjectInfoActivity.this, WorkoutSelectionActivity.class);
                    intent.putExtra("DETECTED_OBJECTS", detectedObjects); // Pass the detected objects
                    startActivity(intent);
                }
            });
        } else {
            TextView noDetectedView = new TextView(this);
            noDetectedView.setText("No equipment detected. You can still do bodyweight exercises or manually select equipment in the workout builder.");
            noDetectedView.setPadding(16, 24, 16, 24);
            infoContainer.addView(noDetectedView);

            // Add button to go to manual workout builder
            Button manualButton = new Button(this);
            manualButton.setText("Go to Workout Builder");
            manualButton.setPadding(16, 16, 16, 16);
            LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            buttonParams.setMargins(16, 24, 16, 16);
            manualButton.setLayoutParams(buttonParams);
            infoContainer.addView(manualButton);

            // Set click listener for the button
            manualButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Navigate to manual workout builder
                    Intent intent = new Intent(ObjectInfoActivity.this, ManualWorkoutBuilder.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void initEquipmentInfo() {
        // Add information for each piece of equipment
        equipmentInfo.put("PullUpBar", new EquipmentDetails(
                "Pull-up bars are great for upper body strength. They primarily target your back, " +
                        "shoulders, and arms. Regular training on a pull-up bar can significantly improve " +
                        "your upper body strength and muscle definition.",

                "• Standard pull-ups: 3 sets of 8-12 reps\n" +
                        "  Classic exercise for back, biceps, and middle back\n\n" +
                        "• Chin-ups: 3 sets of 8-12 reps\n" +
                        "  Palms facing you, emphasizes biceps more\n\n" +
                        "• Hanging leg raises: 3 sets of 10-15 reps\n" +
                        "  Great for core and hip flexors\n\n" +
                        "• Dead hangs: 3 sets of 30-60 seconds\n" +
                        "  Improves grip strength and decompresses spine\n\n" +
                        "• Scapular pulls: 3 sets of 10-15 reps\n" +
                        "  Strengthens scapular muscles for better shoulder health"
        ));

        equipmentInfo.put("DipsBar", new EquipmentDetails(
                "Dips bars are excellent for targeting your chest, triceps, and shoulders. They allow for a " +
                        "deep range of motion that's great for building upper body pushing strength. They're especially " +
                        "effective for triceps development.",

                "• Dips: 3 sets of 8-12 reps\n" +
                        "  Targets chest, triceps, and shoulders\n\n" +
                        "• L-sits: 3 sets of 20-30 seconds\n" +
                        "  Isometric core and shoulder exercise\n\n" +
                        "• Straight bar dips: 3 sets of 8-10 reps\n" +
                        "  Requires more stabilization than parallel bars\n\n" +
                        "• Assisted dips: 3 sets of 10-12 reps\n" +
                        "  Uses band assistance for beginners\n\n" +
                        "• Modified L-sit: 3 sets of 15-20 seconds\n" +
                        "  Easier variation with bent knees"
        ));

        equipmentInfo.put("Step", new EquipmentDetails(
                "Exercise steps are versatile tools for cardio and lower body workouts. They can be used for " +
                        "step-ups, plyometric exercises, and as platforms for various strength exercises. They're " +
                        "excellent for building lower body endurance and power.",

                "• Step-ups: 3 sets of 15 reps each leg\n" +
                        "  Works quads, hamstrings, and glutes\n\n" +
                        "• Box jumps: 3 sets of 10 reps\n" +
                        "  Plyometric exercise for explosive power\n\n" +
                        "• Lateral step-ups: 3 sets of 12 reps each side\n" +
                        "  Targets outer thighs and glutes\n\n" +
                        "• Step-up with knee drive: 3 sets of 10 reps per leg\n" +
                        "  Adds core engagement and balance challenge\n\n" +
                        "• Bulgarian split squats: 3 sets of 10 reps per leg\n" +
                        "  Uses step as rear foot support for single-leg exercise"
        ));

        equipmentInfo.put("Trx", new EquipmentDetails(
                "TRX suspension trainers use your body weight and gravity to build strength, balance, " +
                        "coordination, flexibility, and core stability. They're highly portable and versatile, " +
                        "allowing for a full-body workout in limited space.",

                "• TRX rows: 3 sets of 12-15 reps\n" +
                        "  Targets back, lats, and biceps\n\n" +
                        "• TRX push-ups: 3 sets of 10-12 reps\n" +
                        "  Unstable variation engages more stabilizer muscles\n\n" +
                        "• TRX hamstring curls: 3 sets of 10-12 reps\n" +
                        "  Works posterior chain - hamstrings and glutes\n\n" +
                        "• TRX pikes: 3 sets of 10-12 reps\n" +
                        "  Advanced core exercise targeting shoulders and abs\n\n" +
                        "• TRX fallouts: 3 sets of 10-12 reps\n" +
                        "  Challenges core stability and shoulder strength"
        ));

        equipmentInfo.put("Dumbbell", new EquipmentDetails(
                "Dumbbells are versatile free weights that can be used for a wide range of exercises targeting " +
                        "virtually every muscle group. They're excellent for both beginners and advanced users, allowing " +
                        "for progressive overload and unilateral training.",

                "• Dumbbell bench press: 3 sets of 8-12 reps\n" +
                        "  Works chest, shoulders, and triceps\n\n" +
                        "• Dumbbell rows: 3 sets of 10-12 reps per arm\n" +
                        "  Targets back, lats, and rhomboids\n\n" +
                        "• Dumbbell lunges: 3 sets of 10 reps per leg\n" +
                        "  Works quads, hamstrings, and glutes\n\n" +
                        "• Dumbbell shoulder press: 3 sets of 10-12 reps\n" +
                        "  Targets deltoids and triceps\n\n" +
                        "• Dumbbell goblet squats: 3 sets of 12-15 reps\n" +
                        "  Full lower body exercise with core engagement"
        ));

        equipmentInfo.put("ResistanceBands", new EquipmentDetails(
                "Resistance bands provide constant tension throughout exercises and are excellent for both " +
                        "strength training and rehabilitation. They're lightweight, portable, and versatile, making them " +
                        "ideal for home workouts or travel.",

                "• Band pull-aparts: 3 sets of 15-20 reps\n" +
                        "  Targets rear deltoids and upper back\n\n" +
                        "• Banded squats: 3 sets of 12-15 reps\n" +
                        "  Adds resistance to regular squats\n\n" +
                        "• Resistance band bicep curls: 3 sets of 12-15 reps\n" +
                        "  Isolates and works the biceps\n\n" +
                        "• Banded lateral raises: 3 sets of 12-15 reps\n" +
                        "  Targets medial deltoids for shoulder width\n\n" +
                        "• Banded face pulls: 3 sets of 15 reps\n" +
                        "  Works rear deltoids and upper back posture muscles"
        ));
    }

    // Class to hold equipment details and recommended exercises
    private static class EquipmentDetails {
        private String description;
        private String exercises;

        public EquipmentDetails(String description, String exercises) {
            this.description = description;
            this.exercises = exercises;
        }

        public String getDescription() {
            return description;
        }

        public String getExercises() {
            return exercises;
        }
    }
}