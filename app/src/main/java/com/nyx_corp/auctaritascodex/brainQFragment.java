package com.nyx_corp.auctaritascodex;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import javax.annotation.Nullable;

public class brainQFragment extends Fragment {

    private WebView myWebView;
    private FrameLayout container ;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_brain_q, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        container = view.findViewById(R.id.brain_q_container);

        WebViewModel viewModel = new ViewModelProvider(requireActivity()).get(WebViewModel.class);
        myWebView = viewModel.getBrainQWebView();

        myWebView.setOnLongClickListener(v -> {
            WebView.HitTestResult result = myWebView.getHitTestResult();
            String link = result.getExtra();

            if (link == null || link.trim().isEmpty() || !(URLUtil.isNetworkUrl(link) || link.startsWith("mailto:") || link.startsWith("tel:"))) {
                return false;
            }

            // Custom Link Options Dialog
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_link_options, null);

            Button btnCopyLink = dialogView.findViewById(R.id.btn_copy_link);
            Button btnOpenInBrowser = dialogView.findViewById(R.id.btn_open_in_browser);

            AlertDialog linkOptionsDialog = new AlertDialog.Builder(requireActivity(), R.style.AlertDialog_App_Rounded)
                    .setView(dialogView)
                    .create();

            btnCopyLink.setOnClickListener(buttonView -> {
                copyToClipboard(link);
                linkOptionsDialog.dismiss();
            });

            btnOpenInBrowser.setOnClickListener(buttonView -> {
                openInBrowser(link);
                linkOptionsDialog.dismiss();
            });

            linkOptionsDialog.show();
            return true;
        });


        if (myWebView.getParent() != null) {
            ((ViewGroup) myWebView.getParent()).removeView(myWebView);
        }
        container.addView(myWebView);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Use the ViewModel's canGoBack and goBack methods
                if (myWebView != null && myWebView.canGoBack()) {
                    myWebView.goBack();
                } else {
                    // If WebView can't go back, allow the activity to handle back press (e.g., pop the fragment or exit app)
                    this.setEnabled(false); // Disable this callback to allow default back press behavior
                    requireActivity().onBackPressed();
                }
            }
        });

    }

    private void openInBrowser(String link) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        startActivity(intent);
    }

    private void copyToClipboard(String link) {
        ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Link", link);
        clipboard.setPrimaryClip(clip);
    }

}