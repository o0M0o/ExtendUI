package wxm.androidutil.FrgWebView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import wxm.androidutil.FrgUtility.FrgUtilitySupportBase;
import wxm.androidutil.R;
import wxm.androidutil.util.UtilFun;

/**
 * fragment for webView
 * Created by ookoo on 2017/2/15.
 */
public abstract class FrgWebView extends FrgUtilitySupportBase {
    WebView mWVPage;
    ProgressBar mPBLoad;

    private Object pagePara;

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        LOG_TAG = "FrgWebView";
        View rootView = layoutInflater.inflate(R.layout.frg_webview, viewGroup, false);
        mWVPage = UtilFun.cast_t(rootView.findViewById(R.id.wv_page));
        mPBLoad = UtilFun.cast_t(rootView.findViewById(R.id.pb_load));

        mWVPage.getSettings().setDefaultTextEncodingName("utf-8");
        mWVPage.getSettings().setJavaScriptEnabled(true);
        mWVPage.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                onWVPageFinished(mWVPage, pagePara);
            }
        });
        return rootView;
    }

    /**
     * get settings for webview
     * @return  settings for webview
     */
    protected WebSettings getWebSettings()  {
        return  mWVPage.getSettings();
    }

    /**
     * when page load finish
     * @param wvPage   object for page
     * @param pagePara para for page
     */
    protected void onWVPageFinished(WebView wvPage, Object pagePara)    {
    }

    /**
     * load page
     * @param pageUrl   url for page
     * @param pagePara  para
     */
    protected void loadPage(String pageUrl, Object pagePara) {
        this.pagePara = pagePara;
        mWVPage.loadUrl(pageUrl);
    }

    /**
     * Shows the progress UI
     * @param show  if true, show progress UI
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    protected void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources()
                .getInteger(android.R.integer.config_shortAnimTime);

        mPBLoad.setVisibility(show ? View.VISIBLE : View.GONE);
        mPBLoad.animate().setDuration(shortAnimTime)
                .alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mPBLoad.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }
}
