package wxm.extendui.WebView;

import android.os.Bundle;

import wxm.androidutil.FrgWebView.FrgSupportWebView;

/**
 * for webview
 * Created by ookoo on 2016/11/29.
 */
public class FrgWebViewImp extends FrgSupportWebView {
    @Override
    protected void loadUI(Bundle savedInstanceState) {
        loadPage("http://www.sina.com", null);
    }
}
