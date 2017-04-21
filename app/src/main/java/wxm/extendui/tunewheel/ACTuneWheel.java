package wxm.extendui.tunewheel;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import wxm.extendui.R;
import wxm.uilib.tunewheel.TuneWheel;

/**
 * 展示TuneWheel
 */
public class ACTuneWheel extends AppCompatActivity {

    @BindView(R.id.tv_org_v_val)
    TextView mTVValV1;

    @BindView(R.id.tv_org_h_val)
    TextView mTVValH1;

    @BindView(R.id.tw_h_1)
    TuneWheel   mTWH1;

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
        mTWH1.setValueChangeListener(new TuneWheel.OnValueChangeListener() {
            @Override
            public void onValueChange(int value, String valTag) {
                mTVValH1.setText(valTag);
            }
        });

        mTWV1.setValueChangeListener(new TuneWheel.OnValueChangeListener() {
            @Override
            public void onValueChange(int value, String valTag) {
                mTVValV1.setText(valTag);
            }
        });
    }
}
