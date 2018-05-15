package wxm.extendui.ListView;

import android.os.Bundle;
import android.support.annotation.Nullable;

import wxm.androidutil.FrgUtility.FrgSupportBaseAdv;
import wxm.androidutil.Switcher.ACSwitcherActivity;

public class ACListView extends ACSwitcherActivity<FrgSupportBaseAdv> {
    @Override
    protected void setupFragment(@Nullable Bundle savedInstanceState) {
        addFragment(new FrgListView());
    }
}
