package wxm.extendui.TuneWheel;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import wxm.extendui.R;
import wxm.uilib.TuneWheel.TuneWheel;

/**
 * 展示TuneWheel
 */
public class ACTuneWheel extends AppCompatActivity {
    @BindView(R.id.tv_org_v_val)
    TextView mTVValV1;

    @BindView(R.id.tv_org_h_val)
    TextView mTVValH1;

    @BindView(R.id.tv_org_h_val1)
    TextView mTVValH2;

    @BindView(R.id.tv_org_h_val2)
    TextView mTVValH3;

    @BindView(R.id.tw_h_1)
    TuneWheel   mTWH1;

    @BindView(R.id.tw_h_2)
    TuneWheel   mTWH2;

    @BindView(R.id.tw_h_3)
    TuneWheel   mTWH3;

    @BindView(R.id.tw_v_1)
    TuneWheel   mTWV1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_tunewheel);

        ButterKnife.bind(this);
        initUI();
    }

    /**
     * 初始化UI控件
     */
    private void initUI()   {
        //
        mTWH1.setValueChangeListener((value, valTag) -> mTVValH1.setText(valTag));

        //
        mTWH2.setValueChangeListener((value, valTag) -> mTVValH2.setText(valTag));

        //
        final ArrayList<String> al_sz = new ArrayList<>();
        al_sz.add("星期一");
        al_sz.add("星期二");
        al_sz.add("星期三");
        al_sz.add("星期四");
        al_sz.add("星期五");
        al_sz.add("星期六");
        al_sz.add("星期七");

        mTVValH3.setText(al_sz.get(mTWH3.getCurValue()));
        mTWH3.setTranslateTag(val -> al_sz.get(val));

        mTWH3.setValueChangeListener((value, valTag) -> mTVValH3.setText(valTag));

        //
        mTWV1.setValueChangeListener((value, valTag) -> mTVValV1.setText(valTag));
    }
}
