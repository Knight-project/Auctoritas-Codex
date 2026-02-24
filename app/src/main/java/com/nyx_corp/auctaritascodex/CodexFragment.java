package com.nyx_corp.auctaritascodex;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import im.delight.android.webview.AdvancedWebView;

public class CodexFragment extends Fragment implements AdvancedWebView.Listener {
    private AdvancedWebView mywebview;
    private FrameLayout container;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_codex, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        container = view.findViewById(R.id.codex_container);
        WebViewModel viewModel = new ViewModelProvider(requireActivity()).get(WebViewModel.class);

        mywebview = viewModel.getCodexWebView();

        mywebview.setListener(requireActivity(), this);

        mywebview.setOnLongClickListener(v -> {
            WebView.HitTestResult result = mywebview.getHitTestResult();
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

        if (mywebview.getParent() != null) {
            ((ViewGroup) mywebview.getParent()).removeView(mywebview);
        }
        container.addView(mywebview);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (mywebview != null && mywebview.canGoBack()) {

                    mywebview.goBack();
                } else {
                    this.setEnabled(false);
                    requireActivity().onBackPressed();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (mywebview != null) {
            mywebview.onActivityResult(requestCode, resultCode, intent);
        }
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {
        // Custom Download Options Dialog
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_download_options, null);

        RadioGroup radioGroupFileFormat = dialogView.findViewById(R.id.radio_group_file_format);
        RadioButton rbPdf = dialogView.findViewById(R.id.rb_pdf);
        Button btnDownload = dialogView.findViewById(R.id.btn_download);

        rbPdf.setChecked(true); // Default selection

        AlertDialog downloadOptionsDialog = new AlertDialog.Builder(requireActivity(), R.style.AlertDialog_App_Rounded)
                .setView(dialogView)
                .create();

        btnDownload.setOnClickListener(buttonView -> {
            String newExtension = (radioGroupFileFormat.getCheckedRadioButtonId() == rbPdf.getId()) ? ".pdf" : ".pptx";

            String finalFileName = suggestedFilename;
            if (finalFileName.contains(".")) {
                finalFileName = finalFileName.substring(0, finalFileName.lastIndexOf("."));
            }
            finalFileName += newExtension;

            if (AdvancedWebView.handleDownload(requireContext(), url, finalFileName)) {
                Toast.makeText(requireContext(), "Downloading as: " + finalFileName, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Download failed", Toast.LENGTH_SHORT).show();
            }
            downloadOptionsDialog.dismiss();
        });

        downloadOptionsDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mywebview != null) {
            mywebview.onResume();
            // Dismiss any lingering veil when the fragment resumes
            dismissVeil();
        }
    }

    @Override
    public void onPause() {
        if (mywebview != null) mywebview.onPause();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        if (container != null && mywebview != null) {
            container.removeView(mywebview);
        }
        super.onDestroyView();
    }

    @Override public void onPageStarted(String url, Bitmap favicon) {
        Log.d("Codex", "Started: " + url);

        getChildFragmentManager().beginTransaction()
                .add(R.id.codex_container, new LoadingVeilFragment(), LoadingVeilFragment.TAG)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();

        if (url.contains("youtube.com") || url.contains("youtu.be") || url.contains("e-book-library") || url.contains("phet.colorado.edu") || url.contains("182.160.97.198:8080") || url.contains("forms.gle") || url.contains("https://scholar.google.com/") || url.contains("github")) {
            mywebview.stopLoading();
            openInBrowser(url);
            mywebview.goBack();
//            mywebview.loadUrl("https://sites.google.com/view/auctoritas-codex/links");
        }


        if (url.contains("nyxtoolkit.web.app")) {
            dismissVeil(); // Dismiss any existing veil before launching custom tab
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            builder.setShowTitle(false);
            builder.setToolbarColor(Color.BLACK);
            builder.setNavigationBarColor(Color.BLACK);
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(requireActivity(), Uri.parse(url));
//            mywebview.loadUrl("https://sites.google.com/view/auctoritas-codex/tools");
            mywebview.goBack();

        }
    }

    @Override public void onPageFinished(String url) {
        Log.d("Codex", "Finished: " + url);
        dismissVeil(); // Dismiss veil when page is done
    }

    @Override public void onPageError(int errorCode, String description, String failingUrl) {
        Toast.makeText(getContext(), "Error: " + description, Toast.LENGTH_SHORT).show();
        dismissVeil(); // Dismiss veil on error
    }

    @Override public void onExternalPageRequest(String url) {
        openInBrowser(url);
    }

    private void dismissVeil() {
        LoadingVeilFragment veil = (LoadingVeilFragment) getChildFragmentManager().findFragmentByTag(LoadingVeilFragment.TAG);
        if (veil != null) {
            veil.dismiss();
        }
    }

    // --- HELPERS ---
    private void openInBrowser(String link) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(intent);
    }

    private void copyToClipboard(String link) {
        ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Link", link);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            Toast.makeText(requireContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
        }
    }
}