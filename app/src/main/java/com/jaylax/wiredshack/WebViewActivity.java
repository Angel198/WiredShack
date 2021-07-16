package com.jaylax.wiredshack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jaylax.wiredshack.databinding.ActivityWebViewBinding;

public class WebViewActivity extends AppCompatActivity {

    ActivityWebViewBinding mBinding;
    String title, url;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_web_view);

        title = getIntent().getStringExtra("title") == null ? "" : getIntent().getStringExtra("title");
        url = getIntent().getStringExtra("url") == null ? "" : getIntent().getStringExtra("url");

        mBinding.txtTitle.setText(title);
        mBinding.imgBack.setOnClickListener(view -> {
            onBackPressed();
        });

        mBinding.webView.getSettings().setLoadsImagesAutomatically(true);
        mBinding.webView.getSettings().setJavaScriptEnabled(true);
        mBinding.webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mBinding.webView.loadUrl(url);
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}