package wxm.extendui.ACSwitcher;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
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
        setFrgID(R.layout.frg_page_three, R.id.fl_page);
        addChildFrg(new FrgPageOne());
        addChildFrg(new FrgPageTwo());
    }

    @OnClick({R.id.button})
    public void onSwitcher() {
        switchPage();
    }
}
