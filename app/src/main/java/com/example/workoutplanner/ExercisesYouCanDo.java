package com.example.workoutplanner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.workoutplanner.ml.ModelUnquant;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class ExercisesYouCanDo extends AppCompatActivity {

    private static final String TAG = "ExercisesYouCanDo";

    private TextView result;
    private ImageView imageView;
    private Button pictureButton;
    private Button confirmButton;
    private Button retakeButton;
    private CardView resultCard;
    private int imageSize = 224;
    private String detectedObject = "";
    private List<String> selectedObjects = new ArrayList<>();

    private String[] classes = {"PullUpBar", "DipsBar", "Step", "Trx", "Dumbbell", "ResistanceBands"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises_you_can_do);

        // Initialize views
        result = findViewById(R.id.result);
        imageView = findViewById(R.id.imageView);
        pictureButton = findViewById(R.id.takePhotoButton);
        confirmButton = findViewById(R.id.confirmButton);
        retakeButton = findViewById(R.id.retakeButton);
        resultCard = findViewById(R.id.resultCard);

        // Initially hide the result card
        resultCard.setVisibility(View.GONE);

        // Set up click listeners
        pictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch camera if we have permission
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 1);
                } else {
                    // Request camera permission if we don't have it
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!detectedObject.isEmpty()) {
                    // Add the detected object to our list if it's not already there
                    if (!selectedObjects.contains(detectedObject)) {
                        selectedObjects.add(detectedObject);
                    }

                    // Show a toast to confirm the selection
                    Toast.makeText(ExercisesYouCanDo.this,
                            detectedObject + " added to your equipment list",
                            Toast.LENGTH_SHORT).show();

                    // Hide the result card
                    resultCard.setVisibility(View.GONE);

                    // If we have at least one object, enable the view info button
                    findViewById(R.id.viewInfoButton).setVisibility(View.VISIBLE);
                }
            }
        });

        retakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Just trigger the camera intent again
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 1);
                }
            }
        });

        // Set up the view info button
        findViewById(R.id.viewInfoButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch the ObjectInfoActivity with the selected objects
                Intent intent = new Intent(ExercisesYouCanDo.this, ObjectInfoActivity.class);
                intent.putExtra("DETECTED_OBJECTS", selectedObjects.toArray(new String[0]));
                startActivity(intent);
            }
        });

        // Initially hide the view info button
        findViewById(R.id.viewInfoButton).setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                if (image != null) {
                    int dimension = Math.min(image.getWidth(), image.getHeight());
                    image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
                    imageView.setImageBitmap(image);

                    image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                    classifiedImage(image);
                } else {
                    Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error processing camera result", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void classifiedImage(Bitmap image) {
        ModelUnquant model = null;

        try {
            model = ModelUnquant.newInstance(getApplicationContext());

            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

            int pixel = 0;
            for (int i = 0; i < imageSize; i++) {
                for (int j = 0; j < imageSize; j++) {
                    int val = intValues[pixel++];
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Run model inference
            ModelUnquant.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
            float[] confidences = outputFeature0.getFloatArray();

            // Find highest confidence class
            int maxPos = 0;
            float maxConfidence = 0;

            for (int i = 0; i < Math.min(confidences.length, classes.length); i++) {
                if (confidences[i] > maxConfidence && !classes[i].isEmpty()) {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }

            // Display only the top result without confidence
            if (maxPos < classes.length && !classes[maxPos].isEmpty()) {
                detectedObject = classes[maxPos];
                result.setText("Is this " + detectedObject + "?");

                // Show the result card
                resultCard.setVisibility(View.VISIBLE);
            } else {
                detectedObject = "";
                result.setText("No equipment detected");

                // Show the result card but disable the confirm button
                resultCard.setVisibility(View.VISIBLE);
                confirmButton.setEnabled(false);
            }

        } catch (IOException e) {
            Log.e(TAG, "Error loading model", e);
            result.setText("Error: Model loading failed");
            resultCard.setVisibility(View.GONE);
        } catch (Exception e) {
            Log.e(TAG, "Error in classification", e);
            result.setText("Error classifying");
            resultCard.setVisibility(View.GONE);
        } finally {
            if (model != null) {
                try {
                    model.close();
                } catch (Exception e) {
                    Log.e(TAG, "Error closing model", e);
                }
            }
        }
    }
}