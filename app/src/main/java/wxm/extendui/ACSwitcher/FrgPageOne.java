package wxm.extendui.ACSwitcher;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import wxm.androidutil.FrgUtility.FrgSupportBaseAdv;
import wxm.extendui.R;

/**
 * for webview
 * Created by ookoo on 2016/11/29.
 */
public class FrgPageOne extends FrgSupportBaseAdv {
    @BindView(R.id.tw_tag)
    TextView mTVTag;

    private Timer mTimer;
    private long mLiveSeconds;
    private long mStartSeconds;

    @Override
    protected int getLayoutID() {
        return R.layout.frg_page_one;
    }

    @Override
    protected boolean isUseEventBus() {
        return false;
    }

    @Override
    protected void initUI(Bundle bundle)    {
        mLiveSeconds = 0;
        mStartSeconds = Calendar.getInstance().getTimeInMillis() / 1000;
    }

    @Override
    protected void loadUI(Bundle savedInstanceState) {
        mLiveSeconds = Calendar.getInstance().getTimeInMillis() / 1000 - mStartSeconds;
        mTVTag.setText(String.format(Locale.CHINA,
                "live %d seconds", mLiveSeconds));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // update jobs info every 3 seconds
        FrgPageOne h = this;
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                h.getActivity().runOnUiThread(() -> {
                    loadUI(null);
                });
            }
        }, 1000, 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
    }
}
