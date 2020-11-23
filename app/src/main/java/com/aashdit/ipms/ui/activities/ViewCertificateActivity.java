package com.aashdit.ipms.ui.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.aashdit.ipms.R;

public class ViewCertificateActivity extends AppCompatActivity {

    private static final String TAG = "ViewCertificateActivity";
    private String pdfUrl = "http://www.pdf995.com/samples/pdf.pdf";
    private String url = "http://drive.google.com/viewerng/viewer?embedded=true&url="+pdfUrl;
//    private String url = "http://docs.google.com/gview?embedded=true&url="+pdfUrl;

    private WebView webView;
    private ProgressBar mProgressBar;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_certificate);

        webView = findViewById(R.id.webview);
        mProgressBar = findViewById(R.id.progressBar);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setSupportZoom(true);
        webView.invalidate();
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mProgressBar.setVisibility(View.GONE);
            }
        });

//        webView.setWebViewClient(new Callback());
    }
    private class Callback extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(
                WebView view, String url) {
            mProgressBar.setVisibility(View.GONE);
            return(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
