package com.example.workoutplanner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class StartPlanning extends AppCompatActivity {
    Button btnAiWorkout, btnManualWorkout;
    CountDownTimer countDownTimer;
    private Context context;
    TextView tvThank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start_planning);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initialize();

        // AI-assisted workout planning button
        btnAiWorkout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                Intent intent = new Intent(StartPlanning.this, ExercisesYouCanDo.class);
                startActivity(intent);
            }
        });

        // Manual workout building button
        btnManualWorkout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                // Replace ManualWorkoutBuilder with your actual activity name for manual workout building
                Intent intent = new Intent(StartPlanning.this, ManualWorkoutBuilder.class);
                startActivity(intent);
            }
        });



        // Animation and welcome message
        tvThank.animate().rotation(360f).setDuration(4000);

    }

    // Method to speak text, gets text and sends it to service
    public void saySomething(String text) {
        Intent intent = new Intent(context, TextToSpeechService.class);
        intent.putExtra("text", text);
        startService(intent);
    }

    // Initialize all elements
    public void initialize() {
        context = this;
        tvThank = findViewById(R.id.tvThank);
        btnAiWorkout = findViewById(R.id.btnAiWorkout);
        btnManualWorkout = findViewById(R.id.btnManualWorkout);
    }
}