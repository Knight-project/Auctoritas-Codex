package com.nyx_corp.auctaritascodex;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;
import java.util.Map;

public class scheduleFragment extends Fragment {

    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private ImageView imageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // MAKE SURE: 'fragment_schedule' matches your actual XML file name
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // MAKE SURE: 'my_image_view' is the ID in your XML layout
        imageView = view.findViewById(R.id.my_image_view);

        // 1. Initialize Firebase Remote Config
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        // 2. Settings: 0 seconds for instant updates during testing
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        // 3. Set Defaults (Keys MUST match what you call in getString and what you set in Firebase Console)
        Map<String, Object> defaultMap = new HashMap<>();
        defaultMap.put("latest_routine", "https://drive.google.com/uc?export=download&id=16Hd6Cza2cdQv_fKGkcgTRoe0CIbrbQ0l");

        mFirebaseRemoteConfig.setDefaultsAsync(defaultMap);

        // 4. Run the fetch
        fetchRemoteImage();
    }

    private void fetchRemoteImage() {
        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(getActivity(), task -> {
                    // Log EVERY key available from the cloud to see what the app actually sees
                    for (String key : mFirebaseRemoteConfig.getKeysByPrefix("")) {
                        android.util.Log.d("FIREBASE_DEBUG", "Found key in cloud: " + key);
                    }

                    String url = mFirebaseRemoteConfig.getString("latest_routine");
                    android.util.Log.d("FIREBASE_DEBUG", "Final lol URL: " + url);

                    Glide.with(this).load(url).into(imageView);
                });
    }
}