package com.nyx_corp.auctaritascodex;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

// Renamed from LoadingVeilDialogFragment
public class LoadingVeilFragment extends Fragment {

    public static final String TAG = "LoadingVeilFragment";
    private static final long DISPLAY_DURATION = 600; // 1.5 seconds
    private static final int FADE_DURATION = 600; // 500ms for a gentler animation

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable removeRunnable;

    public LoadingVeilFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout and set alpha to 0 for fade-in
        View view = inflater.inflate(R.layout.loading_veil, container, false);
        view.setAlpha(0f);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Fade-in animation
        view.animate()
                .alpha(1f)
                .setDuration(0)
                .setListener(null); // No listener needed for fade-in

        // Runnable to fade-out and remove the fragment
        removeRunnable = () -> {
            if (getView() != null && isAdded()) {
                getView().animate()
                        .alpha(0f)
                        .setDuration(FADE_DURATION)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                // Once faded out, remove the fragment
                                if (isAdded()) {
                                    getParentFragmentManager().beginTransaction().remove(LoadingVeilFragment.this).commitAllowingStateLoss();
                                }
                            }
                        });
            }
        };
        // Schedule the removal
        handler.postDelayed(removeRunnable, DISPLAY_DURATION);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up handler callbacks
        handler.removeCallbacks(removeRunnable);
    }

    // Public method to manually trigger dismissal (e.g. in onPageFinished)
    public void dismiss() {
        // To avoid race conditions, only proceed if the view and fragment are valid.
        if (getView() == null || !isAdded()) {
            return;
        }

        // Cancel the scheduled auto-removal
        handler.removeCallbacks(removeRunnable);

        // Start fade-out animation and remove fragment
        getView().animate()
                .alpha(0f)
                .setDuration(FADE_DURATION) // Use the same gentle duration
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (isAdded()) {
                           getParentFragmentManager().beginTransaction().remove(LoadingVeilFragment.this).commitAllowingStateLoss();
                        }
                    }
                });
    }
}
