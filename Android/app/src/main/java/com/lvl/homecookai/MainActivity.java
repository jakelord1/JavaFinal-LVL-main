package com.lvl.homecookai;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Outline;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_START_TAB = "extra_start_tab";
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;
    private static final int PERMISSION_CAMERA = 100;
    private static final int PERMISSION_READ_STORAGE = 101;
    private static final int PERMISSION_POST_NOTIFICATIONS = 102;

    private BottomNavigationView bottomNavigation;
    private FragmentManager fragmentManager;
    private View scanFoodButton;
    private View processingOverlay;
    private String currentPhotoPath;
    private Bitmap currentBitmap;
    private String currentImageUri;
    private GeminiService geminiService;
    private PromptManager promptManager;
    private String pendingNotificationSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (controller != null) {
            controller.setAppearanceLightStatusBars(true);
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        bottomNavigation = findViewById(R.id.bottom_navigation);
        fragmentManager = getSupportFragmentManager();
        geminiService = new GeminiService();
        promptManager = new PromptManager(this);
        scanFoodButton = findViewById(R.id.fab_scan_food);
        processingOverlay = findViewById(R.id.home_processing_overlay);
        NotificationUtils.ensureChannel(this);

        makeImageViewsCircular();

        if (savedInstanceState == null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commitNow();
            bottomNavigation.setSelectedItemId(R.id.nav_home);
        }

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);
            
            if (currentFragment != null) {
                if ((itemId == R.id.nav_home && currentFragment instanceof HomeFragment) ||
                    (itemId == R.id.nav_history && currentFragment instanceof HistoryFragment) ||
                    (itemId == R.id.nav_profile && currentFragment instanceof ProfileFragment)) {
                    return true;
                }
            }
            
            Fragment fragment = null;
            if (itemId == R.id.nav_home) {
                fragment = new HomeFragment();
            } else if (itemId == R.id.nav_history) {
                fragment = new HistoryFragment();
            } else if (itemId == R.id.nav_profile) {
                fragment = new ProfileFragment();
            }

            if (fragment != null) {
                fragmentManager.beginTransaction()
                        .setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .replace(R.id.fragment_container, fragment)
                        .commit();
                updateScanFoodVisibility(itemId);
                return true;
            }
            return false;
        });
        Button btnOpen = findViewById(R.id.fab_scan_food);
        btnOpen.setOnClickListener(v -> showImageSourceDialog());
        updateScanFoodVisibility(bottomNavigation.getSelectedItemId());
        handleStartTab(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleStartTab(intent);
    }

    private void handleStartTab(Intent intent) {
        if (intent == null || bottomNavigation == null) {
            return;
        }
        int tabId = intent.getIntExtra(EXTRA_START_TAB, 0);
        if (tabId != 0) {
            bottomNavigation.setSelectedItemId(tabId);
        }
    }

    public void showProfile() {
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_profile);
        }
    }

    private void updateScanFoodVisibility(int itemId) {
        if (scanFoodButton == null) {
            return;
        }
        scanFoodButton.setVisibility(itemId == R.id.nav_home ? View.VISIBLE : View.GONE);
    }

    private void showImageSourceDialog() {
        CharSequence[] options = new CharSequence[] {
                getString(R.string.camera),
                getString(R.string.choose_from_gallery)
        };
        new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.scan_food))
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        openCamera();
                    } else {
                        openGallery();
                    }
                })
                .show();
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA);
            return;
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(this,
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
            if (ContextCompat.checkSelfPermission(this,
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
            currentBitmap = BitmapFactory.decodeFile(currentPhotoPath);
            if (currentBitmap != null) {
                currentImageUri = "file://" + currentPhotoPath;
                sendImageToGemini(currentBitmap);
            }
        }
    }

    private void handleGalleryImage(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            currentBitmap = BitmapFactory.decodeStream(inputStream);
            if (inputStream != null) {
                inputStream.close();
            }
            if (currentBitmap != null) {
                currentImageUri = persistImageToCache(imageUri);
                sendImageToGemini(currentBitmap);
            }
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.error_loading_image, e.getMessage()),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void sendImageToGemini(Bitmap bitmap) {
        String prompt = promptManager.getDefaultPrompt();
        setProcessing(true);

        Futures.addCallback(
                geminiService.generateContentWithImage(bitmap, prompt),
                new FutureCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        String cleanedJson = extractJson(result);
                        java.util.LinkedHashMap<String, Integer> parsed = parseIngredientQuantities(cleanedJson);
                        String summary = parseSummary(cleanedJson, parsed);
                        saveRecentScan(currentImageUri, summary);
                        setProcessing(false);
                        showProcessingDialog(summary,
                                new ArrayList<>(parsed.keySet()),
                                new ArrayList<>(parsed.values()));
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        setProcessing(false);
                        new MaterialAlertDialogBuilder(MainActivity.this)
                                .setTitle(getString(R.string.recognition_failed))
                                .setMessage(getString(R.string.error_occurred, String.valueOf(t.getMessage())))
                                .setPositiveButton(getString(R.string.ok), null)
                                .show();
                    }
                },
                ContextCompat.getMainExecutor(this)
        );
    }

    private void tryShowProcessingNotification(String summary) {
        if (NotificationUtils.canPostNotifications(this)) {
            NotificationUtils.showProcessingComplete(this, summary);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pendingNotificationSummary = summary;
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    PERMISSION_POST_NOTIFICATIONS);
        }
    }

    private void setProcessing(boolean show) {
        if (processingOverlay == null) {
            return;
        }
        processingOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void openIngredientConfirm(ArrayList<String> ingredients, ArrayList<Integer> quantities) {
        Intent intent = new Intent(this, IngredientConfirmActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
        new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.dialog_photo_processed_title))
                .setMessage(message)
                .setPositiveButton(getString(R.string.dialog_photo_processed_positive),
                        (dialog, which) -> openIngredientConfirm(ingredients, quantities))
                .setNegativeButton(getString(R.string.dialog_photo_processed_negative), null)
                .setOnDismissListener(dialog -> tryShowProcessingNotification(summary))
                .show();
    }

    private void openMatchResults(List<String> ingredients) {
        Intent intent = new Intent(this, MatchResultsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putStringArrayListExtra(MatchResultsActivity.EXTRA_INGREDIENTS,
                new ArrayList<>(ingredients));
        startActivity(intent);
    }

    private java.util.LinkedHashMap<String, Integer> parseIngredientQuantities(String result) {
        java.util.LinkedHashMap<String, Integer> parsed = new java.util.LinkedHashMap<>();
        if (result == null || result.isEmpty()) {
            return parsed;
        }
        String json = result;
        try {
            org.json.JSONObject root = new org.json.JSONObject(json);
            org.json.JSONArray array = root.optJSONArray("ingredients");
            if (array != null) {
                for (int i = 0; i < array.length(); i++) {
                    Object item = array.opt(i);
                    String name = "";
                    int quantity = 1;
                    if (item instanceof org.json.JSONObject) {
                        org.json.JSONObject obj = (org.json.JSONObject) item;
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
            Toast.makeText(this, getString(R.string.error_parsing_json),
                    Toast.LENGTH_SHORT).show();
        }
        return parsed;
    }

    private String parseSummary(String result, java.util.LinkedHashMap<String, Integer> ingredients) {
        String summary = "";
        if (result != null && !result.isEmpty()) {
            try {
                org.json.JSONObject root = new org.json.JSONObject(result);
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
                com.lvl.homecookai.database.AppDatabase.getDatabase(this);
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
        try (InputStream inputStream = getContentResolver().openInputStream(imageUri)) {
            if (inputStream == null) {
                return null;
            }
            File cacheDir = new File(getCacheDir(), "recent_scans");
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
            Log.e("MainActivity", "Failed to cache image", e);
            return imageUri.toString();
        }
    }

    private String extractJson(String input) {
        if (input == null) {
            return "";
        }
        int start = input.indexOf('{');
        int end = input.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return input.substring(start, end + 1).trim();
        }
        return input.trim();
    }

    private File createImageFile() {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(imageFileName, ".jpg", storageDir);
            currentPhotoPath = image.getAbsolutePath();
            return image;
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.error_creating_file, e.getMessage()),
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
                Toast.makeText(this, getString(R.string.camera_permission_denied),
                        Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PERMISSION_READ_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, getString(R.string.storage_permission_denied),
                        Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PERMISSION_POST_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                NotificationUtils.showProcessingComplete(this, pendingNotificationSummary);
            }
            pendingNotificationSummary = null;
        }
    }



    private void makeImageViewsCircular() {
        
        View rootView = findViewById(R.id.popular_categories_container);
        if (rootView != null) {
            findAndMakeCircular(rootView);
        }
    }

    private void findAndMakeCircular(View view) {
        if (view instanceof ImageView) {
            ImageView imageView = (ImageView) view;
            
            imageView.post(() -> {
                int size = Math.min(imageView.getWidth(), imageView.getHeight());
                imageView.setOutlineProvider(new ViewOutlineProvider() {
                    @Override
                    public void getOutline(View view, Outline outline) {
                        int left = (view.getWidth() - size) / 2;
                        int top = (view.getHeight() - size) / 2;
                        outline.setOval(left, top, left + size, top + size);
                    }
                });
                imageView.setClipToOutline(true);
            });
        } else if (view instanceof android.view.ViewGroup) {
            android.view.ViewGroup group = (android.view.ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                findAndMakeCircular(group.getChildAt(i));
            }
        }
    }
}
