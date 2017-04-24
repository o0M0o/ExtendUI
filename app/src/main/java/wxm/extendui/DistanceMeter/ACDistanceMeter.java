package wxm.extendui.DistanceMeter;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import butterknife.BindView;
import butterknife.ButterKnife;
import wxm.extendui.R;
import wxm.uilib.DistanceMeter.DistanceMeter;
import wxm.uilib.DistanceMeter.DistanceMeterTag;

@RequiresApi(api = Build.VERSION_CODES.M)
public class ACDistanceMeter extends AppCompatActivity {
    @BindView(R.id.dm_obj)
    DistanceMeter   mDMObj;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_distance_meter);

        ButterKnife.bind(this);
        initUI();
    }

    private void initUI() {
        mDMObj.clearValueTag();

        DistanceMeterTag mt_f = new DistanceMeterTag();
        mt_f.mSZTagName = "1";
        mt_f.mCRTagColor = getColor(R.color.aqua);
        mt_f.mTagVal = 10;
        mDMObj.addValueTag(mt_f);

        DistanceMeterTag mt_od = new DistanceMeterTag();
        mt_od.mSZTagName = "2";
        mt_od.mCRTagColor = getColor(R.color.aquamarine);
        mt_od.mTagVal = 25;
        mDMObj.addValueTag(mt_od);

        DistanceMeterTag mt_b = new DistanceMeterTag();
        mt_b.mSZTagName = "3";
        mt_b.mCRTagColor = getColor(R.color.brown);
        mt_b.mTagVal = 75;
        mDMObj.addValueTag(mt_b);
    }
}
