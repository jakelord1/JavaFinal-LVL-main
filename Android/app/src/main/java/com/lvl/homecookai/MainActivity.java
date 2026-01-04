package com.lvl.homecookai;

import android.content.Intent;
import android.graphics.Outline;
import android.os.Bundle;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private FragmentManager fragmentManager;

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

        bottomNavigation = findViewById(R.id.bottom_navigation);
        fragmentManager = getSupportFragmentManager();

        // Make category images circular
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
                    (itemId == R.id.nav_camera && currentFragment instanceof CameraFragment) ||
                    (itemId == R.id.nav_history && currentFragment instanceof HistoryFragment)) {
                    return true;
                }
            }
            
            Fragment fragment = null;
            if (itemId == R.id.nav_home) {
                fragment = new HomeFragment();
            } else if (itemId == R.id.nav_camera) {
                fragment = new CameraFragment();
            } else if (itemId == R.id.nav_history) {
                fragment = new HistoryFragment();
            }

            if (fragment != null) {
                fragmentManager.beginTransaction()
                        .setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .replace(R.id.fragment_container, fragment)
                        .commit();
                return true;
            }
            return false;
        });
        Button btnOpen = findViewById(R.id.fab_scan_food);

        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. Создаем Intent (откуда, куда)
                Intent intent = new Intent(MainActivity.this, RecipeDetailActivity.class);

                // 3. Запускаем
                startActivity(intent);
            }
        });
    }



    private void makeImageViewsCircular() {
        // Find all category ImageViews and make them circular
        View rootView = findViewById(R.id.popular_categories_container);
        if (rootView != null) {
            findAndMakeCircular(rootView);
        }
    }

    private void findAndMakeCircular(View view) {
        if (view instanceof ImageView) {
            ImageView imageView = (ImageView) view;
            // Set outline provider for circular clipping after view is measured
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
