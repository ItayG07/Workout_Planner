package com.example.workoutplanner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.Manifest;
import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import com.example.workoutplanner.ml.ModelUnquant;

public class StartPlanning extends AppCompatActivity {
    Button btnStart;
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
        //listener that taking the user to next activity
        btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                Intent intent = new Intent( StartPlanning.this , ExercisesYouCanDo.class);
                startActivity(intent);
            }});
        //count down timer to move to next activity and say thank you to the user
        countDownTimer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }
            @Override
            public void onFinish() {
                Intent intent = new Intent( StartPlanning.this , ExercisesYouCanDo.class);
                startActivity(intent);
                Toast.makeText(StartPlanning.this, "Hello!", Toast.LENGTH_SHORT).show();
            }
        }.start();
        initialize();
        //cool rotation and say Welcome
        tvThank.animate().rotation(360f).setDuration(4000);
        saySomething("Let's start planning your workout");
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
        tvThank = findViewById(R.id.tvThank);

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
            Intent intent = new Intent( StartPlanning.this , MainActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.goBack) {
            //Back to intro activity
            Intent intent = new Intent( StartPlanning.this , Login.class);
            startActivity(intent);
            return true;
        }
        if (id ==R.id.closeApp  ){
            finishAffinity(); // This will close all activities
        }
        return super.onOptionsItemSelected(item);
    }
}