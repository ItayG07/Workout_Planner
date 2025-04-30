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
    private Map<String, String> equipmentInfo = new HashMap<>();
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

        if (detectedObjects != null && detectedObjects.length > 0) {
            for (String object : detectedObjects) {
                // Create a TextView for the object title
                TextView titleView = new TextView(this);
                titleView.setText(object);
                titleView.setTextSize(18);
                titleView.setPadding(16, 24, 16, 8);
                titleView.setTextColor(getResources().getColor(android.R.color.black));
                infoContainer.addView(titleView);

                // Create a TextView for the object information
                TextView infoView = new TextView(this);
                infoView.setText(equipmentInfo.getOrDefault(object,
                        "No information available for this equipment."));
                infoView.setPadding(16, 8, 16, 24);
                infoContainer.addView(infoView);
            }

            // Add button to view workouts
            Button viewWorkoutsButton = new Button(this);
            viewWorkoutsButton.setText("View Available Workouts");
            viewWorkoutsButton.setPadding(16, 16, 16, 16);
            LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            buttonParams.setMargins(16, 24, 16, 16);
            viewWorkoutsButton.setLayoutParams(buttonParams);
            infoContainer.addView(viewWorkoutsButton);

            // Set click listener for the button
            viewWorkoutsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Navigate to workout selection activity
                    Intent intent = new Intent(ObjectInfoActivity.this, WorkoutSelectionActivity.class);
                    intent.putExtra("DETECTED_OBJECTS", detectedObjects);
                    startActivity(intent);
                }
            });
        } else {
            TextView noInfoView = new TextView(this);
            noInfoView.setText("No information available for detected objects.");
            noInfoView.setPadding(16, 24, 16, 24);
            infoContainer.addView(noInfoView);
        }
    }

    private void initEquipmentInfo() {
        // Add information for each piece of equipment
        equipmentInfo.put("PullUpBar",
                "Pull-up bars are great for upper body strength. They primarily target your back, " +
                        "shoulders, and arms. You can perform exercises like pull-ups, chin-ups, hanging leg raises, " +
                        "and muscle-ups.\n\n" +
                        "Recommended exercises:\n" +
                        "- Standard pull-ups: 3 sets of 8-12 reps\n" +
                        "- Chin-ups: 3 sets of 8-12 reps\n" +
                        "- Hanging leg raises: 3 sets of 10-15 reps");

        equipmentInfo.put("DipsBar",
                "Dips bars are excellent for targeting your chest, triceps, and shoulders. They allow for a " +
                        "deep range of motion that's great for building upper body pushing strength.\n\n" +
                        "Recommended exercises:\n" +
                        "- Dips: 3 sets of 8-12 reps\n" +
                        "- L-sits: 3 sets of 20-30 seconds\n" +
                        "- Straight bar dips: 3 sets of 8-10 reps");

        equipmentInfo.put("Step",
                "Exercise steps are versatile tools for cardio and lower body workouts. They can be used for " +
                        "step-ups, plyometric exercises, and as platforms for various strength exercises.\n\n" +
                        "Recommended exercises:\n" +
                        "- Step-ups: 3 sets of 15 reps each leg\n" +
                        "- Box jumps: 3 sets of 10 reps\n" +
                        "- Lateral step-ups: 3 sets of 12 reps each side");

        equipmentInfo.put("Trx",
                "TRX suspension trainers use your body weight and gravity to build strength, balance, " +
                        "coordination, flexibility, and core stability. They're highly portable and versatile.\n\n" +
                        "Recommended exercises:\n" +
                        "- TRX rows: 3 sets of 12-15 reps\n" +
                        "- TRX push-ups: 3 sets of 10-12 reps\n" +
                        "- TRX hamstring curls: 3 sets of 10-12 reps");

        equipmentInfo.put("Dumbbell",
                "Dumbbells are versatile free weights that can be used for a wide range of exercises targeting " +
                        "virtually every muscle group. They're excellent for both beginners and advanced users.\n\n" +
                        "Recommended exercises:\n" +
                        "- Dumbbell bench press: 3 sets of 8-12 reps\n" +
                        "- Dumbbell rows: 3 sets of 10-12 reps per arm\n" +
                        "- Dumbbell lunges: 3 sets of 10 reps per leg");

        equipmentInfo.put("ResistanceBands",
                "Resistance bands provide constant tension throughout exercises and are excellent for both " +
                        "strength training and rehabilitation. They're lightweight, portable, and versatile.\n\n" +
                        "Recommended exercises:\n" +
                        "- Band pull-aparts: 3 sets of 15-20 reps\n" +
                        "- Banded squats: 3 sets of 12-15 reps\n" +
                        "- Resistance band bicep curls: 3 sets of 12-15 reps");
    }
}