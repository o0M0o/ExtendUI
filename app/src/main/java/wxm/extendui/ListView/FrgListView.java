package wxm.extendui.ACSwipe;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import butterknife.BindView;
import wxm.androidutil.FrgUtility.FrgSupportBaseAdv;
import wxm.extendui.R;
import wxm.uilib.SwipeLayout.SwipeLayout;

/**
 * @author WangXM
 * @version create：2018/4/14
 */
public class FrgSwipe extends FrgSupportBaseAdv {
    @BindView(R.id.swipe1)
    SwipeLayout     mSL1;

    @BindView(R.id.swipe2)
    SwipeLayout     mSL2;

    @BindView(R.id.swipe3)
    SwipeLayout     mSL3;

    @Override
    protected int getLayoutID() {
        return R.layout.frg_swipe;
    }

    @Override
    protected boolean isUseEventBus() {
        return false;
    }

    @Override
    protected void initUI(Bundle bundle)    {
        mSL1.setOnSlideListener((view, status) -> {
            Log.d("swipe1", "status : " + status);
        });

        mSL2.setOnSlideListener((view, status) -> {
            Log.d("swipe2", "status : " + status);
        });

        mSL3.setOnSlideListener((view, status) -> {
            Log.d("swipe3", "status : " + status);
        });
    }
}
