package com.nyx_corp.auctaritascodex;

import android.app.Application;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class WebViewModel extends AndroidViewModel {


    private WebView codexWebView;
    private WebView brainQWebView;


    public WebViewModel(@NonNull Application application) {
        super(application);
    }

    public WebView getCodexWebView () {
        if (codexWebView == null ) {
            codexWebView = createAndConfigCodexWebView("https://sites.google.com/view/auctoritas-codex/notices");
        }
        return codexWebView;
    }

    private WebView createAndConfigCodexWebView(String url) {
        WebView codexWebView = new WebView(getApplication());
        codexWebView.getSettings().setJavaScriptEnabled(true);
        codexWebView.loadUrl(url);
        return codexWebView;
    }

}

