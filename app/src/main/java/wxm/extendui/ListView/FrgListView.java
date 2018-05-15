package wxm.extendui.ListView;

import android.content.Context;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import wxm.androidutil.FrgUtility.FrgSupportBaseAdv;
import wxm.androidutil.MoreAdapter.MoreAdapter;
import wxm.androidutil.ViewHolder.ViewHolder;
import wxm.extendui.R;

/**
 * @author WangXM
 * @version create：2018/4/14
 */
public class FrgListView extends FrgSupportBaseAdv {
    @SuppressWarnings("FieldCanBeLocal")
    private final String KEY_TAG = "tag";

    @BindView(R.id.lv_item)
    ListView mLVList;

    @Override
    protected int getLayoutID() {
        return R.layout.frg_listview;
    }

    @Override
    protected boolean isUseEventBus() {
        return false;
    }

    @Override
    protected void initUI(Bundle bundle) {
        ArrayList<HashMap<String, String>> al_para = new ArrayList<>();
        HashMap<String, String> hm = new HashMap<>();
        hm.put(KEY_TAG, "");
        al_para.add(hm);

        hm = new HashMap<>();
        hm.put(KEY_TAG, "");
        al_para.add(hm);

        hm = new HashMap<>();
        hm.put(KEY_TAG, "");
        al_para.add(hm);

        hm = new HashMap<>();
        hm.put(KEY_TAG, "");
        al_para.add(hm);

        hm = new HashMap<>();
        hm.put(KEY_TAG, "");
        al_para.add(hm);

        mLVList.setAdapter(new ItemAdapter(getActivity(), al_para, R.layout.lv_item1));
    }


    class ItemAdapter extends MoreAdapter {
        public ItemAdapter(Context context, List<? extends Map<String, ?>> data, int mLRSelfDef) {
            super(context, data, mLRSelfDef);
        }

        @Override
        protected void loadView(int pos, ViewHolder vhHolder) {
            vhHolder.setText(R.id.tv_tag,
                    ((TextView)vhHolder.getView(R.id.tv_tag)).getText().toString()
                            + " at " + pos);
        }

        @Override
        protected int getChildViewLayout(int pos) {
            int ret = super.getChildViewLayout(pos);
            switch (pos) {
                case 0:
                    ret = R.layout.lv_item1;
                    break;

                case 1:
                    ret = R.layout.lv_item2;
                    break;

                case 2:
                    ret = R.layout.lv_item3;
                    break;

                case 3:
                    ret = R.layout.lv_item4;
                    break;
            }

            return ret;
        }
    }
}
