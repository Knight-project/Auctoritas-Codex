package com.nyx_corp.auctaritascodex;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Application;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import im.delight.android.webview.AdvancedWebView; // Added import

public class WebViewModel extends AndroidViewModel {


    private AdvancedWebView codexWebView; //
    private AdvancedWebView brainQWebView;


    public WebViewModel(@NonNull Application application) {
        super(application);
    }

    public AdvancedWebView getCodexWebView () { // Changed return type
        if (codexWebView == null ) {
            codexWebView = createAndConfigCodexWebView("https://sites.google.com/view/auctoritas-codex/index");
        }
        return codexWebView;
    }

    public AdvancedWebView getBrainQWebView () {
        if (brainQWebView == null ) {
            brainQWebView = createAndConfigBrainQWebView("https://notifierninth.web.app/");
        }
        return brainQWebView;
    }

    private AdvancedWebView createAndConfigBrainQWebView(String url) {
        AdvancedWebView brainQWebView = new AdvancedWebView(getApplication()); // Instantiated AdvancedWebView
        brainQWebView.getSettings().setJavaScriptEnabled(true);
        brainQWebView.getSettings().setDomStorageEnabled(true);
        brainQWebView.getSettings().setDatabaseEnabled(true);
        brainQWebView.getSettings().setSupportZoom(false);
        brainQWebView.getSettings().setBuiltInZoomControls(false);
        brainQWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        brainQWebView.getSettings().setJavaScriptEnabled(true);
        brainQWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        brainQWebView.getSettings().setLoadsImagesAutomatically(true);
        brainQWebView.clearCache(false);
        brainQWebView.setLongClickable(true);
        brainQWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        brainQWebView.setVerticalScrollBarEnabled(false);


        brainQWebView.loadUrl(url);
        return brainQWebView;
    }


    private AdvancedWebView createAndConfigCodexWebView(String url) { // Changed return type and parameter type
        AdvancedWebView codexWebView = new AdvancedWebView(getApplication()); // Instantiated AdvancedWebView
        codexWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        codexWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        codexWebView.getSettings().setDomStorageEnabled(true);
        codexWebView.getSettings().setDatabaseEnabled(true);
        codexWebView.getSettings().setSupportZoom(false);
        codexWebView.getSettings().setBuiltInZoomControls(false);
        codexWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        codexWebView.getSettings().setJavaScriptEnabled(true);
        codexWebView.getSettings().setLoadsImagesAutomatically(true);
        codexWebView.clearCache(false);
        codexWebView.setLongClickable(true);
        codexWebView.getSettings().setAllowFileAccess(true);
        codexWebView.getSettings().setAllowContentAccess(true);
        codexWebView.getSettings().setAllowFileAccessFromFileURLs(true);
        codexWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            codexWebView.setRendererPriorityPolicy(WebView.RENDERER_PRIORITY_IMPORTANT, true);
        }
        codexWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);


        codexWebView.setVerticalScrollBarEnabled(false);
        codexWebView.setHorizontalScrollBarEnabled(false);

        // The WebViewClient will be set by the Fragment, which also acts as the AdvancedWebView.Listener
        // However, we still need a default client for initial load, or if the fragment doesn't set one.
        codexWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                view.evaluateJavascript(
                        "(function() {" +
                                "   var css = '* { -webkit-tap-highlight-color: transparent !important; } ' + " +
                                "             ' div[aria-label=\"Open search bar\"]{ display: none !important; }' + " +
                                "             ' div[aria-label=\"Site actions\"]{ display: none !important; }' + " +
                                "             'html { touch-action: manipulation !important; } '; " +
                                "   var styleId = 'global-web-style';" +
                                "   var style = document.getElementById(styleId);" +
                                "   /* Create the style element only if it doesn\'t exist */" +
                                "   if (!style) {" +
                                "       style = document.createElement('style');" +
                                "       style.id = styleId;" +
                                "       style.type = 'text/css';" +
                                "       document.head.appendChild(style);" +
                                "   }" +
                                "   /* Only update innerHTML if the content is different to save CPU */" +
                                "   if (style.innerHTML !== css) {" +
                                "       style.innerHTML = css;" +
                                "       console.log('Global styles injected/updated');" +
                                "   }" +
                                "})();", null);
            }

        });
        codexWebView.loadUrl(url);
        return codexWebView;
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        if (codexWebView != null) {
            codexWebView.removeAllViews();
            codexWebView.destroy();
            // Destroy the WebView to prevent memory leaks
            codexWebView = null;
        }
    }
    public void resetCodex() {
        if (codexWebView != null) {
            codexWebView.clearHistory();
            codexWebView.loadUrl("https://sites.google.com/view/auctoritas-codex/index"); // Clear current state
//            codexWebView.reload(); // Or reload your initial URL
        }
    }
    public void resetBrainq() {
        if (brainQWebView != null) {
            brainQWebView.clearHistory();
            brainQWebView.loadUrl("https://notifierninth.web.app");
        }
    }
}
