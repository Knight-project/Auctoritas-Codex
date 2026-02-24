package com.nyx_corp.auctaritascodex;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.WindowCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.concurrent.TimeUnit;

import im.delight.android.webview.AdvancedWebView;

public class MainActivity extends AppCompatActivity {
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private NavController navController;
    private WebViewModel viewModel;

//    @SuppressLint("SetJavaScriptEnabled")
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
//        super.onCreate(savedInstanceState);
//        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
//        setContentView(R.layout.activity_main);
//
//        // Find the NavHostFragment
//        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.fragmentContainerView);
//
//        navController = navHostFragment.getNavController();
//        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
//
//        // This links the Bottom Nav to the NavHost automatically
//        NavigationUI.setupWithNavController(bottomNav, navController);
//
//        // Show loading veil on tab changes by adding the LoadingVeilFragment
//        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
//            // Check if a veil fragment is already added to prevent duplicates
//            if (getSupportFragmentManager().findFragmentByTag(LoadingVeilFragment.TAG) == null) {
//                getSupportFragmentManager().beginTransaction()
//                        .add(R.id.fragmentContainerView, new LoadingVeilFragment(), LoadingVeilFragment.TAG)
//                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE) // Optional: Use a standard fade transition
//                        .commit();
//            }
//        });
//
//        WebViewModel viewModel = new ViewModelProvider(this).get(WebViewModel.class);
//        viewModel.getBrainQWebView();
//        viewModel.getCodexWebView();
//
//        PeriodicWorkRequest pollRequest = new PeriodicWorkRequest.Builder(FirestoreWorker.class, 15, TimeUnit.MINUTES)
//                .setConstraints(new Constraints.Builder()
//                        .setRequiredNetworkType(NetworkType.CONNECTED)
//                        .build())
//                .build();
//
//        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
//                "FirestorePolling",
//                ExistingPeriodicWorkPolicy.KEEP,
//                pollRequest
//        );
//        WorkManager.getInstance(this).enqueue(new OneTimeWorkRequest.Builder(FirestoreWorker.class).build());
//    }
@SuppressLint("SetJavaScriptEnabled")
@Override
protected void onCreate(Bundle savedInstanceState) {
    SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
    super.onCreate(savedInstanceState);
    WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
    setContentView(R.layout.activity_main);


    mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

    FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(0)
            .build();
    mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);

    String app_version = mFirebaseRemoteConfig.getString("app_version");
    String current_version = BuildConfig.VERSION_NAME;

    if (!app_version.equals(current_version)) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.update_app_context, null);

        Button btn_download_app = dialogView.findViewById(R.id.btn_download_app);
        Button not_now = dialogView.findViewById(R.id.not_now);


        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialog_App_Rounded);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        btn_download_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Knight-project/Auctoritas-Codex/releases/latest/download/app-release.apk"));
                startActivity(intent);
            }
        });

        not_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

        }



    // 1. Initialize ViewModel early
    viewModel = new ViewModelProvider(this).get(WebViewModel.class);

    // 2. Setup Navigation
    NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
            .findFragmentById(R.id.fragmentContainerView);
    navController = navHostFragment.getNavController();
    BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

    // Link NavigationUI (Handles the first click/fragment switching)
    NavigationUI.setupWithNavController(bottomNav, navController);

    // 3. DOUBLE-TAP LOGIC (Handles the second click on an active tab)
    bottomNav.setOnItemReselectedListener(item -> {
        int itemId = item.getItemId();

        // 1. Filter: Only run logic for the tabs we care about
        if (itemId == R.id.codex || itemId == R.id.brain_q) {
            long currentTime = System.currentTimeMillis();

            // 2. Get the specific tag for THIS item
            Object lastClickObj = bottomNav.getTag(itemId);
            long lastClickTime = (lastClickObj instanceof Long) ? (long) lastClickObj : 0L;

            if (currentTime - lastClickTime < 500) {
                // 3. SUCCESS: Handle based on which ID was double-tapped
                if (itemId == R.id.codex) {
                    viewModel.resetCodex();
                    Toast.makeText(this, "Codex Reset", Toast.LENGTH_SHORT).show();
                } else {
                    viewModel.resetBrainq();
                    Toast.makeText(this, "Notice Reset", Toast.LENGTH_SHORT).show();
                }

                // 4. Feedback
                bottomNav.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP);

                // 5. Reset timing tag
                bottomNav.setTag(itemId, 0L);
            } else {
                // First tap: save time for this specific ID
                bottomNav.setTag(itemId, currentTime);
            }
        }
    });;

    // 4. Destination Change Listener (Loading Veil)
    navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
        if (getSupportFragmentManager().findFragmentByTag(LoadingVeilFragment.TAG) == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainerView, new LoadingVeilFragment(), LoadingVeilFragment.TAG)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }
    });

    // 5. Initialize WebViews and WorkManager
    viewModel.getBrainQWebView();
    viewModel.getCodexWebView();
    setupWorkManager();
}

    private void setupWorkManager() {
        PeriodicWorkRequest pollRequest = new PeriodicWorkRequest.Builder(FirestoreWorker.class, 15, TimeUnit.MINUTES)
                .setConstraints(new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build())
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "FirestorePolling",
                ExistingPeriodicWorkPolicy.KEEP,
                pollRequest
        );
        WorkManager.getInstance(this).enqueue(new OneTimeWorkRequest.Builder(FirestoreWorker.class).build());
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}