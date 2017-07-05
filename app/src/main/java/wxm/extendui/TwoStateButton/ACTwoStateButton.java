package wxm.extendui.TwoStateButton;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wxm.extendui.R;
import wxm.uilib.TwoStateButton.TwoStateButton;

public class ACTwoStateButton extends AppCompatActivity {
    @BindView(R.id.tv_def_tag)
    TextView            mTVTag;

    @BindView(R.id.tv_bts_tag1)
    TextView            mTVTag1;

    @BindView(R.id.tb_def)
    TwoStateButton      mTBDef;

    @BindView(R.id.tb_one_background)
    TwoStateButton      mTBOneBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_two_state_button);

        ButterKnife.bind(this);
        initUI();
    }

    @OnClick({R.id.tb_def, R.id.tb_one_background})
    void tbOnClick(View v)  {
        switch (v.getId())  {
            case R.id.tb_def :  {
                mTVTag.setText(mTBDef.getCurTxt());
            }
            break;

            case R.id.tb_one_background:  {
                mTVTag1.setText(mTBOneBackground.getCurTxt());
            }
            break;
        }
    }

    private void initUI()   {
        mTVTag.setText(mTBDef.getCurTxt());
        mTVTag1.setText(mTBOneBackground.getCurTxt());
    }
}
