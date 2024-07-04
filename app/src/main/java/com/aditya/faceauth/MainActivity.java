//package com.aditya.faceauth;
//import android.os.Bundle;
//import android.os.Environment;
//import android.widget.Button;
//import android.widget.Toast;
//import androidx.activity.EdgeToEdge;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.camera.core.CameraSelector;
//import androidx.camera.core.ImageCapture;
//import androidx.camera.core.ImageCaptureException;
//import androidx.camera.core.Preview;
//import androidx.camera.lifecycle.ProcessCameraProvider;
//import androidx.camera.view.PreviewView;
//import androidx.core.content.ContextCompat;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//import androidx.lifecycle.LifecycleOwner;
//import com.google.common.util.concurrent.ListenableFuture;
//import java.io.File;
//import java.io.IOException;
//import java.util.Date;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.Executor;
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.MediaType;
//import okhttp3.MultipartBody;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//
//public class MainActivity extends AppCompatActivity {
//
//    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
//    private PreviewView previewView;
//    private ImageCapture imageCapture;
//    private Button captureBtn;
//    private OkHttpClient client;
//    private static final String SERVER_URL = "http://127.0.0.1:5000";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_main);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        previewView = findViewById(R.id.previewView);
//        captureBtn = findViewById(R.id.captureBtn);
//        client = new OkHttpClient();
//
//        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
//        cameraProviderFuture.addListener(() -> {
//            try {
//                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
//                startCameraX(cameraProvider);
//            } catch (ExecutionException | InterruptedException e) {
//                e.printStackTrace();
//            }
//        }, getExecutor());
//
//        captureBtn.setOnClickListener(v -> capturePhoto());
//    }
//
//    private Executor getExecutor() {
//        return ContextCompat.getMainExecutor(this);
//    }
//
//    private void startCameraX(ProcessCameraProvider cameraProvider) {
//        cameraProvider.unbindAll();
//
//        CameraSelector cameraSelector = new CameraSelector.Builder()
//                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
//                .build();
//
//        Preview preview = new Preview.Builder().build();
//        preview.setSurfaceProvider(previewView.getSurfaceProvider());
//
//        imageCapture = new ImageCapture.Builder()
//                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
//                .build();
//
//        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageCapture);
//    }
//
//    private void capturePhoto() {
//        File photoDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Faces");
//        if (!photoDir.exists()) {
//            photoDir.mkdir();
//        }
//
//        Date date = new Date();
//        String timeStamp = String.valueOf(date.getTime());
//        String photoFilePath = photoDir.getAbsolutePath() + "/" + timeStamp + ".jpg";
//        File photoFile = new File(photoFilePath);
//
//        imageCapture.takePicture(
//                new ImageCapture.OutputFileOptions.Builder(photoFile).build(),
//                getExecutor(),
//                new ImageCapture.OnImageSavedCallback() {
//                    @Override
//                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
//                        Toast.makeText(MainActivity.this, "Photo Saved", Toast.LENGTH_SHORT).show();
//                        uploadImageToServer(photoFile);
//                    }
//
//                    @Override
//                    public void onError(@NonNull ImageCaptureException exception) {
//                        Toast.makeText(MainActivity.this, "Error: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//        );
//    }
//
//    private void uploadImageToServer(File photoFile) {
//        RequestBody requestBody = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("file", photoFile.getName(),
//                        RequestBody.create(photoFile, MediaType.parse("image/jpeg")))
//                .build();
//
//        Request request = new Request.Builder()
//                .url(SERVER_URL)
//                .post(requestBody)
//                .build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show());
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    String responseBody = response.body().string();
//                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Response: " + responseBody, Toast.LENGTH_SHORT).show());
//                } else {
//                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to get a valid response", Toast.LENGTH_SHORT).show());
//                }
//            }
//        });
//    }
//}





























//
//package com.aditya.faceauth;
//
//import android.os.Bundle;
//import android.os.Environment;
//import android.util.Log;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.camera.core.CameraSelector;
//import androidx.camera.core.ImageCapture;
//import androidx.camera.core.ImageCaptureException;
//import androidx.camera.core.Preview;
//import androidx.camera.lifecycle.ProcessCameraProvider;
//import androidx.camera.view.PreviewView;
//import androidx.core.content.ContextCompat;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//import androidx.lifecycle.LifecycleOwner;
//
//import com.google.common.util.concurrent.ListenableFuture;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.Date;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.Executor;
//
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.MediaType;
//import okhttp3.MultipartBody;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//
//public class MainActivity extends AppCompatActivity {
//
//    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
//    private PreviewView previewView;
//    private ImageCapture imageCapture;
//    private Button captureBtn, uploadBtn;
//    private TextView percentageTextView;
//    private OkHttpClient client;
//    private static final String SERVER_URL = "http://192.168.91.117:5000/upload"; // Update with your server's IP address
//    private String currentPhotoPath;  // Declaration of currentPhotoPath
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_main);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        previewView = findViewById(R.id.previewView);
//        captureBtn = findViewById(R.id.captureBtn);
//        uploadBtn = findViewById(R.id.uploadBtn);
//        percentageTextView = findViewById(R.id.percentage);
//        client = new OkHttpClient();
//
//        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
//        cameraProviderFuture.addListener(() -> {
//            try {
//                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
//                startCameraX(cameraProvider);
//            } catch (ExecutionException | InterruptedException e) {
//                e.printStackTrace();
//            }
//        }, getExecutor());
//
//        captureBtn.setOnClickListener(v -> capturePhoto());
//
//        uploadBtn.setOnClickListener(v -> {
//            File photoFile = new File(currentPhotoPath);
//            if (photoFile.exists()) {
//                uploadImageToServer(photoFile);
//            } else {
//                Toast.makeText(MainActivity.this, "No photo to upload", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private Executor getExecutor() {
//        return ContextCompat.getMainExecutor(this);
//    }
//
//    private void startCameraX(ProcessCameraProvider cameraProvider) {
//        cameraProvider.unbindAll();
//
//        CameraSelector cameraSelector = new CameraSelector.Builder()
//                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
//                .build();
//
//        Preview preview = new Preview.Builder().build();
//        preview.setSurfaceProvider(previewView.getSurfaceProvider());
//
//        imageCapture = new ImageCapture.Builder()
//                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
//                .build();
//
//        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageCapture);
//    }
//
//    private void capturePhoto() {
//        File photoDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Faces");
//        if (!photoDir.exists()) {
//            photoDir.mkdir();
//        }
//
//        Date date = new Date();
//        String timeStamp = String.valueOf(date.getTime());
//        String photoFilePath = photoDir.getAbsolutePath() + "/" + timeStamp + ".jpg";
//        currentPhotoPath = photoFilePath;
//        File photoFile = new File(photoFilePath);
//
//        imageCapture.takePicture(
//                new ImageCapture.OutputFileOptions.Builder(photoFile).build(),
//                getExecutor(),
//                new ImageCapture.OnImageSavedCallback() {
//                    @Override
//                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
//                        Toast.makeText(MainActivity.this, "Photo Saved", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onError(@NonNull ImageCaptureException exception) {
//                        Toast.makeText(MainActivity.this, "Error: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//        );
//    }
//
//    private void uploadImageToServer(File photoFile) {
//        RequestBody requestBody = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("file", photoFile.getName(),
//                        RequestBody.create(photoFile, MediaType.parse("image/jpeg")))
//                .build();
//
//        Request request = new Request.Builder()
//                .url(SERVER_URL)
//                .post(requestBody)
//                .build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to upload image " + e.getMessage(), Toast.LENGTH_SHORT).show());
//                Log.e("FAILED TO UPLOAD IMAGE", e.getMessage());
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    String responseBody = response.body().string();
//                    runOnUiThread(() -> updatePercentageTextView(responseBody));
//                } else {
//                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to get a valid response " + response.message(), Toast.LENGTH_SHORT).show());
//                }
//            }
//        });
//    }
//
//    private void updatePercentageTextView(String responseBody) {
//        try {
//            JSONObject jsonObject = new JSONObject(responseBody);
//            String matchingPercentage = jsonObject.getString("matching_percentage");
//            percentageTextView.setText("Percentage: " + matchingPercentage);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Failed to parse response", Toast.LENGTH_SHORT).show();
//        }
//    }
//}


package com.aditya.faceauth;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private PreviewView previewView;
    private ImageCapture imageCapture;
    private Button captureBtn, uploadBtn;
    private TextView percentageTextView;
    private OkHttpClient client;
    private static final String SERVER_URL = "http://192.168.91.117:5000/upload"; // Update with your server's IP address
    private String currentPhotoPath;  // Declaration of currentPhotoPath

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

        previewView = findViewById(R.id.previewView);
        captureBtn = findViewById(R.id.captureBtn);
        uploadBtn = findViewById(R.id.uploadBtn);
        percentageTextView = findViewById(R.id.percentage);
        client = new OkHttpClient();

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                startCameraX(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, getExecutor());

        captureBtn.setOnClickListener(v -> capturePhoto());
        uploadBtn.setOnClickListener(v -> uploadPhoto());
    }

    private Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }

    private void startCameraX(ProcessCameraProvider cameraProvider) {
        cameraProvider.unbindAll();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();

        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageCapture);
    }

    private void capturePhoto() {
        File photoDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Faces");
        if (!photoDir.exists()) {
            photoDir.mkdir();
        }

        Date date = new Date();
        String timeStamp = String.valueOf(date.getTime());
        String photoFilePath = photoDir.getAbsolutePath() + "/" + timeStamp + ".jpg";
        currentPhotoPath = photoFilePath;
        File photoFile = new File(photoFilePath);

        imageCapture.takePicture(
                new ImageCapture.OutputFileOptions.Builder(photoFile).build(),
                getExecutor(),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Toast.makeText(MainActivity.this, "Photo Saved", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(MainActivity.this, "Error: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void uploadPhoto() {
        File file = new File(currentPhotoPath);
        if (!file.exists()) {
            Toast.makeText(MainActivity.this, "No photo to upload", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/*"), file))
                .build();

        Request request = new Request.Builder()
                .url(SERVER_URL)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response.body().string());
                        String matchPercentage = jsonResponse.getString("match_percentage");
                        runOnUiThread(() -> percentageTextView.setText("Percentage: " + matchPercentage));
                    } catch (JSONException e) {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error parsing response: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Upload failed: " + response.message(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}


