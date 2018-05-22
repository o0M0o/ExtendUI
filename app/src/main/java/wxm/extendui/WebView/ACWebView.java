package wxm.extendui.WebView;


import android.os.Bundle;

import wxm.androidutil.switcher.ACSwitcherActivity;

/**
 * test webview frg
 */
public class ACWebView extends ACSwitcherActivity<FrgWebViewImp> {
    @Override
    protected void setupFragment(Bundle savedInstanceState) {
        addFragment(new FrgWebViewImp());
    }
}
