package wxm.extendui.WebView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import wxm.androidutil.FrgWebView.FrgWebView;

/**
 * for webview
 * Created by ookoo on 2016/11/29.
 */
public class FrgWebViewImp extends FrgWebView {
    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View rootView = super.inflaterView(inflater, container, bundle);
        LOG_TAG = "FrgWebViewImp";
        return rootView;
    }

    @Override
    protected void loadUI() {
        loadPage("http://www.sina.com", null);
    }
}
