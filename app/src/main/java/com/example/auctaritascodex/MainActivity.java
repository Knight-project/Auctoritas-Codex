package com.example.auctaritascodex;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.splashscreen.SplashScreen;

import im.delight.android.webview.AdvancedWebView;

public class MainActivity extends AppCompatActivity implements AdvancedWebView.Listener {

    private AdvancedWebView mWebView;
    private ProgressBar mProgressBar;
    private boolean keepSplashScreen = true;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // --- SPLASH SCREEN ---
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        splashScreen.setKeepOnScreenCondition(() -> keepSplashScreen);
        new Handler(Looper.getMainLooper()).postDelayed(() -> keepSplashScreen = false, 2000);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = findViewById(R.id.progressBar);
        mWebView = findViewById(R.id.webview);

        // 1. Basic Setup
        mWebView.setListener(this, this);
        checkRequiredPermissions();

        // 2. Settings (Crucial for Google Sites + Iframes)
        WebSettings s = mWebView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(true);
        s.setDatabaseEnabled(true);
        s.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        s.setUserAgentString("Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36 (HTML, like Gecko) Chrome/116.0.0.0 Mobile Safari/537.36");


        s.setSupportZoom(true);
        s.setBuiltInZoomControls(true);
        s.setDisplayZoomControls(false);



        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);

        // 3. The "Black Screen" Fix
        // We keep the WebView VISIBLE but use the ProgressBar on top of it.
        mWebView.setVisibility(View.VISIBLE);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mProgressBar.setVisibility(View.GONE);

                // Injecting JS to hide both Search and Site Actions
                view.loadUrl("javascript:(function() { " +
                        "var search = document.querySelector('[aria-label=\"Open search bar\"]'); " +
                        "if (search) { search.style.display = 'none'; } " +
                        "var actions = document.querySelector('[aria-label=\"Site actions\"]'); " +
                        "if (actions) { actions.style.display = 'none'; } " +
                        "})();");
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("intent://") || url.contains("youtube.com") || url.contains("youtu.be")) {
                    try {
                        Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                        startActivity(intent);
                        return true;
                    } catch (Exception e) { return false; }
                }
                return false;
            }
        });

        // 4. Back Button logic
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (mWebView.canGoBack()) { mWebView.goBack(); } else { finish(); }
            }
        });

        mWebView.loadUrl("https://sites.google.com/view/auctoritas-codex/schedule");
    }

    // --- UPLOAD / DOWNLOAD LOGIC ---

    private void checkRequiredPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_AUDIO
            }, 101);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, 101);
        }
    }

    @Override
    public void onDownloadRequested(String url, String filename, String mimeType, long contentLength, String contentDisposition, String userAgent) {
        // This handles all file formats (PDF, APK, PNG, etc.)
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setMimeType(mimeType);
        request.addRequestHeader("User-Agent", userAgent);
        request.setTitle(filename);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);

        DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        try {
            dm.enqueue(request);
            Toast.makeText(this, "Downloading file...", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            AdvancedWebView.handleDownload(this, url, filename);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // CRITICAL: This connects the "Choose File" button in the web page to Android
        mWebView.onActivityResult(requestCode, resultCode, data);
    }

    // --- INTERFACE LISTENERS (Required by Library) ---
    @Override public void onPageStarted(String url, Bitmap favicon) {}
    @Override public void onPageFinished(String url) {}
    @Override public void onPageError(int errorCode, String description, String failingUrl) {
        mProgressBar.setVisibility(View.GONE);
    }
    @Override public void onExternalPageRequest(String url) {}
    @Override protected void onResume() { super.onResume(); mWebView.onResume(); }
    @Override protected void onPause() { mWebView.onPause(); super.onPause(); }
    @Override protected void onDestroy() { mWebView.onDestroy(); super.onDestroy(); }
}