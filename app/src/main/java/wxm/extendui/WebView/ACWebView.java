package wxm.extendui.WebView;


import wxm.androidutil.ExActivity.BaseAppCompatActivity;

/**
 * test webview frg
 */
public class ACWebView extends BaseAppCompatActivity {
    @Override
    protected void leaveActivity() {
        finish();
    }

    @Override
    protected void initFrgHolder() {
        LOG_TAG = "ACWebView";
        mFGHolder = new FrgWebViewImp();
    }
}
