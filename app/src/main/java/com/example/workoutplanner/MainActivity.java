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

public class MainActivity extends AppCompatActivity {
    Button startButton;
    CountDownTimer countDownTimer;
    private Context context;
    TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Listener for Button to go from Main to Login
        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                Intent intent = new Intent( MainActivity.this , Login.class);
                startActivity(intent);
                Toast.makeText(MainActivity.this, "Hello!", Toast.LENGTH_SHORT).show();
            }});
        countDownTimer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }
            @Override
            public void onFinish() {
                saySomething("Welcome to the Workout Planner app");

            }
        }.start();
        initialize();
        //cool rotation for the text and say Welcome to user
        tvTitle.animate().rotation(360f).setDuration(4000);
        saySomething("Welcome to the Workout Planner App!");
    }
    //Method to speak text, gets text and sends it to service
    public void saySomething(String text){
        Intent intent = new Intent(context, TextToSpeechService.class);
        intent.putExtra("text", text);
        startService(intent);
    }
    //init all elements
    public void initialize(){
        context = this;
        tvTitle = findViewById(R.id.tvTitle);

    }
}