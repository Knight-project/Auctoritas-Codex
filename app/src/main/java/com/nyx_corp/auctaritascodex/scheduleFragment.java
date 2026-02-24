package com.nyx_corp.auctaritascodex;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;


import java.util.HashMap;
import java.util.Map;

public class scheduleFragment extends Fragment {

    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private PhotoView routineView;
    private PhotoView calanderView;


    private MaterialCardView site_btn;

    private MaterialCardView app_download_btn;

    private MaterialCardView brain_quarry_btn;

    private MaterialCardView discord_btn;
    private MaterialCardView telegram_btn;

    private MaterialCardView github_btn;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // MAKE SURE: 'fragment_schedule' matches your actual XML file name
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


// Inside your Fragment's onViewCreated or wherever you trigger the end of loading
//        if (getView() != null) {
//            getView().postDelayed(() -> {
//                LoadingVeilFragment veil = (LoadingVeilFragment) getChildFragmentManager()
//                        .findFragmentByTag(LoadingVeilFragment.TAG);
//
//                if (veil != null && isAdded()) { // isAdded() ensures fragment is still active
//                    veil.dismiss();
//                }
//            }, 2000); // 1000ms = 1 second delay
//        }


        routineView = view.findViewById(R.id.routine_view);
        calanderView = view.findViewById(R.id.calender_view);


        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        fetchCalenderImage();
        fetchRoutineImage();
//        fetchPromoImage();

        site_btn = view.findViewById(R.id.site_btn);




        site_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/auctoritas-codex/schedule"));
                startActivity(intent);
            }
        });

        app_download_btn = view.findViewById(R.id.app_download_btn);

        app_download_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Knight-project/Auctoritas-Codex/releases/latest/download/app-release.apk"));
                startActivity(intent);
            }
        });

        brain_quarry_btn = view.findViewById(R.id.brain_quarry_btn);

        brain_quarry_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/osl6/e-book-library"));
                startActivity(intent);
            }
        });

        discord_btn = view.findViewById(R.id.discord_btn);

        discord_btn.setOnClickListener(new View.OnClickListener() {
            String url;
            @Override
            public void onClick(View v) {
                mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(getActivity() , task -> {
                    url = mFirebaseRemoteConfig.getString("discord_invite");

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                });
            }
        });

        telegram_btn = view.findViewById(R.id.telegram_btn);

        telegram_btn.setOnClickListener(new View.OnClickListener() {
            String url;
            @Override
            public void onClick(View v) {
                mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(getActivity() , task -> {
                    url = mFirebaseRemoteConfig.getString("telegram_invite");

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                });
            }
        });

        github_btn = view.findViewById(R.id.github_btn);

        github_btn.setOnClickListener(new View.OnClickListener() {
            String url;
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW , Uri.parse("https://github.com/Knight-project/Auctoritas-Codex"));
                startActivity(intent);
            }
        });

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

//    private void fetchPromoImage() {
//        mFirebaseRemoteConfig.fetchAndActivate()
//                .addOnCompleteListener(getActivity(), task -> {
//
//                    String url = mFirebaseRemoteConfig.getString("promo_image");
//
//                    Glide.with(this).load(url).into(promoView);
//                });
//    }


}
