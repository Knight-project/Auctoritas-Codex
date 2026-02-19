package com.nyx_corp.auctaritascodex;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import javax.annotation.Nullable;

public class brainQFragment extends Fragment {

    private WebView myWebView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_brain_q, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        myWebView = view.findViewById(R.id.brain_q_webview);
        myWebView.getSettings().setJavaScriptEnabled(true);

        myWebView.loadUrl("https://sites.google.com/view/osl6/e-book-library");
    }

}