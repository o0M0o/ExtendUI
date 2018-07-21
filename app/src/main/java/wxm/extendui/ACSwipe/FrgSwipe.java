package wxm.extendui.ACSwipe;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import wxm.androidutil.log.TagLog;
import wxm.androidutil.ui.frg.FrgSupportBaseAdv;
import wxm.androidutil.ui.moreAdapter.MoreAdapter;
import wxm.androidutil.ui.view.ViewHolder;
import wxm.extendui.R;
import wxm.uilib.SwipeLayout.SwipeLayout;

/**
 * @author WangXM
 * @version create：2018/4/14
 */
public class FrgSwipe extends FrgSupportBaseAdv {
    @BindView(R.id.swipe1)
    SwipeLayout mSL1;

    @BindView(R.id.swipe2)
    SwipeLayout mSL2;

    @BindView(R.id.swipe3)
    SwipeLayout mSL3;

    @BindView(R.id.lv_swipe)
    ListView mLVSwipe;

    @Override
    protected int getLayoutID() {
        return R.layout.frg_swipe;
    }

    @Override
    protected boolean isUseEventBus() {
        return false;
    }

    @Override
    protected void initUI(Bundle bundle) {
        mSL1.setOnSlideListener((view, status) -> {
            TagLog.INSTANCE.i("swipe1 status : " + status, null);
        });

        mSL2.setOnSlideListener((view, status) -> {
            TagLog.INSTANCE.i("swipe2 status : " + status, null);
        });

        mSL3.setOnSlideListener((view, status) -> {
            TagLog.INSTANCE.i("swipe3 status : " + status, null);
        });

        ArrayList<HashMap<String, String>> al_para = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            HashMap<String, String> hm = new HashMap<>();
            hm.put("tag", "");
            al_para.add(hm);
        }
        mLVSwipe.setAdapter(new ItemAdapter(getActivity(), al_para));
    }


    class ItemAdapter extends MoreAdapter {
        ItemAdapter(Context context, List<? extends Map<String, ?>> data) {
            super(context, data, R.layout.lv_swipe, new String[0], new int[0]);
        }

        protected void loadView(int pos, @NonNull ViewHolder vhHolder) {
        }
    }
}
