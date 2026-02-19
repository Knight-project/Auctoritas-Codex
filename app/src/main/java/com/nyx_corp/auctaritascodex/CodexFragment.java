package com.nyx_corp.auctaritascodex;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import javax.annotation.Nullable;

public class CodexFragment extends Fragment {
    private WebView mywebview;
    private FrameLayout container;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // XML should have a FrameLayout with id: codex_container
        return inflater.inflate(R.layout.fragment_codex, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        container = view.findViewById(R.id.codex_container);

        // Scope to 'requireActivity()' so the VM lives as long as the App/Activity
        WebViewModel viewModel = new ViewModelProvider(requireActivity()).get(WebViewModel.class);

        // Grab the specific one for this fragment
        mywebview = viewModel.getCodexWebView();

        // Check if it's still attached to an old version of this fragment
        if (mywebview.getParent() != null) {
            ((ViewGroup) mywebview.getParent()).removeView(mywebview);
        }

        // Stick it in the UI
        container.addView(mywebview);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Remove it so the Fragment doesn't take the WebView down with it
        if (container != null) {
            container.removeAllViews();
        }
    }
}