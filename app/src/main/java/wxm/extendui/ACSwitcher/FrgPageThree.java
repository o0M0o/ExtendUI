package wxm.extendui.ACSwitcher;

import android.os.Bundle;

import butterknife.OnClick;
import wxm.androidutil.ui.frg.FrgSupportBaseAdv;
import wxm.androidutil.ui.frg.FrgSupportSwitcher;
import wxm.extendui.R;

/**
 * for webview
 * Created by WangXM on 2016/11/29.
 */
public class FrgPageThree extends FrgSupportSwitcher<FrgSupportBaseAdv> {
    public FrgPageThree()   {
        super();
        setupFrgID(R.layout.frg_page_three, R.id.fl_page);
    }

    @OnClick({R.id.button})
    public void onSwitcher() {
        switchPage();
    }

    @Override
    protected void setupFragment(Bundle savedInstanceState) {
        addChildFrg(new FrgPageOne());
        addChildFrg(new FrgPageTwo());
    }
}
