package com.lvl.homecookai;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class CameraFragment extends Fragment {

    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;
    private static final int PERMISSION_CAMERA = 100;
    private static final int PERMISSION_READ_STORAGE = 101;
    private static final int PERMISSION_POST_NOTIFICATIONS = 102;

    private ImageView cameraPreview;
    private MaterialButton takePhotoBtn;
    private MaterialButton uploadFromGalleryBtn;
    private ProgressBar progressBar;
    private TextView resultText;
    private View resultCard;
    private String currentPhotoPath;
    private Bitmap currentBitmap;
    private String currentImageUri;
    private GeminiService geminiService;
    private PromptManager promptManager;
    private String pendingNotificationSummary;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    private void showResultDialog(String text) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.ready))
                .setMessage(getString(R.string.products_found))
                .setPositiveButton(getString(R.string.ok), null)
                .show();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        geminiService = new GeminiService();
        promptManager = new PromptManager(requireContext());
        NotificationUtils.ensureChannel(requireContext());

        setupButtonAnimations();
        
        takePhotoBtn.setOnClickListener(v -> {
            openCamera();
        });
        
        uploadFromGalleryBtn.setOnClickListener(v -> {
            openGallery();
        });
    }
    
    private void setupButtonAnimations() {
        View.OnTouchListener buttonTouchListener = (v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                    break;
            }
            return false;
        };
        
        takePhotoBtn.setOnTouchListener(buttonTouchListener);
        uploadFromGalleryBtn.setOnTouchListener(buttonTouchListener);
    }

    private void initializeViews(View view) {
        cameraPreview = view.findViewById(R.id.camera_preview);
        takePhotoBtn = view.findViewById(R.id.take_photo_btn);
        uploadFromGalleryBtn = view.findViewById(R.id.upload_from_gallery_btn);
        progressBar = view.findViewById(R.id.progress_bar);
        resultText = view.findViewById(R.id.result_text);
        resultCard = view.findViewById(R.id.result_card);
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA);
            return;
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireContext().getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(requireContext(),
                        "com.lvl.homecookai.provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_CAMERA);
            }
        }
    }

    private void openGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_GALLERY);
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_READ_STORAGE);
                return;
            }
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_GALLERY);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                handleCameraImage();
            } else if (requestCode == REQUEST_GALLERY && data != null) {
                handleGalleryImage(data.getData());
            }
        }
    }

    private void handleCameraImage() {
        if (currentPhotoPath != null) {
            File file = new File(currentPhotoPath);
            currentBitmap = BitmapFactory.decodeFile(currentPhotoPath);
            if (currentBitmap != null) {
                cameraPreview.setImageBitmap(currentBitmap);
                currentImageUri = "file://" + currentPhotoPath;
                sendImageToGemini(currentBitmap);
            }
        }
    }

    private void handleGalleryImage(Uri imageUri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
            currentBitmap = BitmapFactory.decodeStream(inputStream);
            if (inputStream != null) {
                inputStream.close();
            }
            if (currentBitmap != null) {
                cameraPreview.setImageBitmap(currentBitmap);
                currentImageUri = persistImageToCache(imageUri);
                sendImageToGemini(currentBitmap);
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), getString(R.string.error_loading_image, e.getMessage()),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            if (show) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.clearAnimation();
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    private void showResult(String result) {
        if (resultText != null && resultCard != null) {
            resultText.setText(result);
            AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
            fadeIn.setDuration(500);
            resultCard.setVisibility(View.VISIBLE);
            resultCard.startAnimation(fadeIn);
            
            resultCard.postDelayed(() -> {
                AlphaAnimation fadeOut = new AlphaAnimation(1f, 0f);
                fadeOut.setDuration(500);
                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        resultCard.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
                resultCard.startAnimation(fadeOut);
            }, 5000);
        }
    }

    private void sendImageToGemini(Bitmap bitmap) {
        showLoading(true);
        if (resultCard != null) {
            resultCard.setVisibility(View.GONE);
        }

        String prompt = promptManager.getDefaultPrompt();

        Futures.addCallback(
                geminiService.generateContentWithImage(bitmap, prompt),
                new FutureCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        if (isAdded()) {
                            showLoading(false);
                            String cleanedJson = extractJson(result);
                            java.util.LinkedHashMap<String, Integer> parsed = parseIngredientQuantities(cleanedJson);
                            String summary = parseSummary(cleanedJson, parsed);
                            saveRecentScan(currentImageUri, summary);
                            showProcessingDialog(summary,
                                    new ArrayList<>(parsed.keySet()),
                                    new ArrayList<>(parsed.values()));
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        if (isAdded()) {
                            if (progressBar != null) {
                                progressBar.setVisibility(View.GONE);
                            }
                            if (resultText != null && resultCard != null) {
                                resultText.setText(getString(R.string.error, t.getMessage()));
                                resultCard.setVisibility(View.VISIBLE);
                            }
                            new MaterialAlertDialogBuilder(requireContext())
                                    .setTitle(getString(R.string.recognition_failed))
                                    .setMessage(getString(R.string.error_occurred, String.valueOf(t.getMessage())))
                                    .setPositiveButton(getString(R.string.ok), null)
                                    .show();
                        }
                    }
                },
                ContextCompat.getMainExecutor(requireContext())
        );
    }

    private void tryShowProcessingNotification(String summary) {
        if (NotificationUtils.canPostNotifications(requireContext())) {
            NotificationUtils.showProcessingComplete(requireContext(), summary);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pendingNotificationSummary = summary;
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    PERMISSION_POST_NOTIFICATIONS);
        }
    }

    private String extractJson(String input) {
        if (input == null) return "";

        int start = input.indexOf('{');
        int end = input.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return input.substring(start, end + 1).trim();
        }
        return input.trim();
    }


    private void openIngredientConfirm(ArrayList<String> ingredients, ArrayList<Integer> quantities) {
        Intent intent = new Intent(requireContext(), IngredientConfirmActivity.class);
        intent.putStringArrayListExtra(IngredientConfirmActivity.EXTRA_PREFILL_INGREDIENTS, ingredients);
        intent.putIntegerArrayListExtra(IngredientConfirmActivity.EXTRA_PREFILL_QUANTITIES, quantities);
        startActivity(intent);
    }

    private void showProcessingDialog(String summary,
                                      ArrayList<String> ingredients,
                                      ArrayList<Integer> quantities) {
        String message;
        if (summary == null || summary.trim().isEmpty()) {
            message = getString(R.string.dialog_photo_processed_message_fallback);
        } else {
            message = getString(R.string.dialog_photo_processed_message, summary.trim());
        }
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.dialog_photo_processed_title))
                .setMessage(message)
                .setPositiveButton(getString(R.string.dialog_photo_processed_positive),
                        (dialog, which) -> openIngredientConfirm(ingredients, quantities))
                .setNegativeButton(getString(R.string.dialog_photo_processed_negative), null)
                .setOnDismissListener(dialog -> tryShowProcessingNotification(summary))
                .show();
    }

    private java.util.LinkedHashMap<String, Integer> parseIngredientQuantities(String result) {
        java.util.LinkedHashMap<String, Integer> parsed = new java.util.LinkedHashMap<>();
        if (result == null || result.isEmpty()) {
            return parsed;
        }
        try {
            JSONObject root = new JSONObject(result);
            JSONArray array = root.optJSONArray("ingredients");
            if (array != null) {
                for (int i = 0; i < array.length(); i++) {
                    Object item = array.opt(i);
                    String name = "";
                    int quantity = 1;
                    if (item instanceof JSONObject) {
                        JSONObject obj = (JSONObject) item;
                        name = obj.optString("name", "").trim();
                        quantity = obj.optInt("quantity", 1);
                    } else if (item != null) {
                        name = String.valueOf(item).trim();
                    }
                    if (!name.isEmpty()) {
                        if (quantity <= 0) {
                            quantity = 1;
                        }
                        String key = name.toLowerCase(Locale.US);
                        int current = parsed.getOrDefault(key, 0);
                        parsed.put(key, current + quantity);
                    }
                }
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), getString(R.string.error_parsing_json),
                    Toast.LENGTH_SHORT).show();
        }
        return parsed;
    }

    private String parseSummary(String result, java.util.LinkedHashMap<String, Integer> ingredients) {
        String summary = "";
        if (result != null && !result.isEmpty()) {
            try {
                JSONObject root = new JSONObject(result);
                summary = root.optString("summary", "").trim();
            } catch (Exception ignored) {
            }
        }
        if (summary.isEmpty()) {
            summary = buildSummaryFromIngredients(ingredients);
        }
        if (summary.isEmpty()) {
            summary = "Scan";
        }
        summary = trimSummary(summary);
        return summary;
    }

    private String buildSummaryFromIngredients(java.util.LinkedHashMap<String, Integer> ingredients) {
        if (ingredients == null || ingredients.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        int count = 0;
        for (String name : ingredients.keySet()) {
            if (name == null || name.trim().isEmpty()) {
                continue;
            }
            if (count > 0) {
                builder.append(", ");
            }
            builder.append(name.trim());
            count++;
            if (count >= 2) {
                break;
            }
        }
        String summary = builder.toString().trim();
        if (summary.length() > 32) {
            summary = summary.substring(0, 32).trim();
        }
        return summary;
    }

    private String trimSummary(String summary) {
        if (summary == null) {
            return "";
        }
        String[] parts = summary.trim().split("\\s+");
        if (parts.length <= 3) {
            return summary.trim();
        }
        return (parts[0] + " " + parts[1] + " " + parts[2]).trim();
    }

    private void saveRecentScan(String imageUri, String summary) {
        if (imageUri == null || imageUri.trim().isEmpty()) {
            return;
        }
        com.lvl.homecookai.database.AppDatabase db =
                com.lvl.homecookai.database.AppDatabase.getDatabase(requireContext());
        com.lvl.homecookai.database.RecentScanDao dao = db.recentScanDao();
        new Thread(() -> {
            dao.insert(new com.lvl.homecookai.database.RecentScan(imageUri, summary, System.currentTimeMillis()));
            dao.trimToLimit(20);
        }).start();
    }

    private String persistImageToCache(Uri imageUri) {
        if (imageUri == null) {
            return null;
        }
        try (InputStream inputStream =
                     requireContext().getContentResolver().openInputStream(imageUri)) {
            if (inputStream == null) {
                return null;
            }
            File cacheDir = new File(requireContext().getCacheDir(), "recent_scans");
            if (!cacheDir.exists() && !cacheDir.mkdirs()) {
                return null;
            }
            File outFile = File.createTempFile("scan_", ".jpg", cacheDir);
            try (FileOutputStream outputStream = new FileOutputStream(outFile)) {
                byte[] buffer = new byte[8192];
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
            }
            return "file://" + outFile.getAbsolutePath();
        } catch (Exception e) {
            Log.e("CameraFragment", "Failed to cache image", e);
            return imageUri.toString();
        }
    }

    private File createImageFile() {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(imageFileName, ".jpg", storageDir);
            currentPhotoPath = image.getAbsolutePath();
            return image;
        } catch (Exception e) {
            Toast.makeText(requireContext(), getString(R.string.error_creating_file, e.getMessage()),
                    Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(requireContext(), getString(R.string.camera_permission_denied), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PERMISSION_READ_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(requireContext(), getString(R.string.storage_permission_denied), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PERMISSION_POST_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                NotificationUtils.showProcessingComplete(requireContext(), pendingNotificationSummary);
            }
            pendingNotificationSummary = null;
        }
    }
}
