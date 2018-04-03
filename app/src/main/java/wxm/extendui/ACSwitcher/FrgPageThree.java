package wxm.extendui.ACSwitcher;

import butterknife.OnClick;
import wxm.androidutil.FrgUtility.FrgSupportSwitcher;
import wxm.androidutil.FrgUtility.FrgUtilitySupportBase;
import wxm.extendui.R;

/**
 * for webview
 * Created by ookoo on 2016/11/29.
 */
public class FrgPageThree extends FrgSupportSwitcher<FrgUtilitySupportBase> {
    public FrgPageThree()   {
        super();
        setupFrgID(R.layout.frg_page_three, R.id.fl_page);
        addChildFrg(new FrgPageOne());
        addChildFrg(new FrgPageTwo());
    }

    @OnClick({R.id.button})
    public void onSwitcher() {
        switchPage();
    }
}
