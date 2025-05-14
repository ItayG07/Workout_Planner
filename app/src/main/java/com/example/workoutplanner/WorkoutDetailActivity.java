package com.example.workoutplanner;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WorkoutDetailActivity extends AppCompatActivity {

    private TextView workoutNameTextView;
    private TextView workoutDescriptionTextView;
    private TextView setsTextView;
    private TextView repsTextView;
    private TextView timerTextView;
    private Button startPauseButton;
    private Button resetButton;
    private Button finishWorkoutButton;

    private CountDownTimer timer;
    private boolean isTimerRunning = false;
    private long timeLeftInMillis = 90000; // 90 seconds rest between sets
    private int currentSet = 1;
    private int totalSets = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_detail);

        // Initialize UI components
        workoutNameTextView = findViewById(R.id.workoutNameTextView);
        workoutDescriptionTextView = findViewById(R.id.workoutDescriptionTextView);
        setsTextView = findViewById(R.id.setsTextView);
        repsTextView = findViewById(R.id.repsTextView);
        timerTextView = findViewById(R.id.timerTextView);
        startPauseButton = findViewById(R.id.startPauseButton);
        resetButton = findViewById(R.id.resetButton);
        finishWorkoutButton = findViewById(R.id.finishWorkoutButton);

        // Get workout details from intent
        String workoutName = getIntent().getStringExtra("WORKOUT_NAME");
        String workoutDescription = getIntent().getStringExtra("WORKOUT_DESCRIPTION");
        totalSets = getIntent().getIntExtra("WORKOUT_SETS", 3);
        String reps = getIntent().getStringExtra("WORKOUT_REPS");

        // Set workout information
        workoutNameTextView.setText(workoutName);
        workoutDescriptionTextView.setText(workoutDescription);
        updateSetsText();
        repsTextView.setText("Reps: " + reps);

        updateTimerText();

        // Set button click listeners
        startPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTimerRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                }
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });

        finishWorkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close this activity and return to previous screen
            }
        });
    }
    //menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.main) {
            //Back to intro activity
            Intent intent = new Intent( WorkoutDetailActivity.this , MainActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.goBack) {
            //Back to intro activity
            Intent intent = new Intent( WorkoutDetailActivity.this , WorkoutRoutineActivity.class);
            startActivity(intent);
            return true;
        }
        if (id ==R.id.closeApp  ){
            finishAffinity(); // This will close all activities
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateSetsText() {
        setsTextView.setText("Set " + currentSet + " of " + totalSets);
    }

    private void startTimer() {
        timer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                startPauseButton.setText("Start");
                timeLeftInMillis = 90000;

                // Move to next set if not finished
                if (currentSet < totalSets) {
                    currentSet++;
                    updateSetsText();
                }

                updateTimerText();
            }
        }.start();

        isTimerRunning = true;
        startPauseButton.setText("Pause");
        resetButton.setVisibility(View.VISIBLE);
    }

    private void pauseTimer() {
        timer.cancel();
        isTimerRunning = false;
        startPauseButton.setText("Resume");
    }

    private void resetTimer() {
        timeLeftInMillis = 90000;
        updateTimerText();
        if (isTimerRunning) {
            timer.cancel();
            isTimerRunning = false;
            startPauseButton.setText("Start");
        }
    }

    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);
        timerTextView.setText(timeLeftFormatted);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }
}