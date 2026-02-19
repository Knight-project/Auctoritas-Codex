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
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;
import java.util.Map;

public class scheduleFragment extends Fragment {

    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private PhotoView routineView;
    private PhotoView calanderView;
    
    private PhotoView promoView;

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
        routineView = view.findViewById(R.id.routine_view);
        calanderView = view.findViewById(R.id.calender_view);
        promoView = view.findViewById(R.id.promo_view);





        // 1. Initialize Firebase Remote Config
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        // 2. Settings: 0 seconds for instant updates during testing
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        // 3. Set Defaults (Keys MUST match what you call in getString and what you set in Firebase Console)
//        Map<String, Object> defaultMap = new HashMap<>();
//        defaultMap.put("latest_routine", "https://drive.google.com/uc?export=download&id=16Hd6Cza2cdQv_fKGkcgTRoe0CIbrbQ0l");
//
//        mFirebaseRemoteConfig.setDefaultsAsync(defaultMap);

        // 4. Run the fetch

        fetchCalenderImage();
        fetchRoutineImage();
        fetchPromoImage();
    }

    private void fetchRoutineImage() {
        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(getActivity(), task -> {

                    String url = mFirebaseRemoteConfig.getString("latest_routine");

                    Glide.with(this).load(url).into(routineView);
                });
    }

    private void fetchCalenderImage() {
        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(getActivity(), task -> {

                    String url = mFirebaseRemoteConfig.getString("academic_calender");

                    Glide.with(this).load(url).into(calanderView);
                });
    }

    private void fetchPromoImage() {
        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(getActivity(), task -> {

                    String url = mFirebaseRemoteConfig.getString("promo_image");

                    Glide.with(this).load(url).into(promoView);
                });
    }


}
