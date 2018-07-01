package wxm.extendui.WebView;


import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import wxm.androidutil.ui.activity.ACSwitcherActivity;

/**
 * test webview frg
 */
public class ACWebView extends ACSwitcherActivity<FrgWebViewImp> {
    @Override
    protected List<FrgWebViewImp> setupFragment() {
        ArrayList<FrgWebViewImp> ret = new ArrayList<>();
        ret.add(new FrgWebViewImp());
        return ret;
    }
}
