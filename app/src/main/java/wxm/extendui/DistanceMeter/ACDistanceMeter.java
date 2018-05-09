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
        DistanceMeterTag mt_f = new DistanceMeterTag("1", 10);
        mt_f.setMCRTagColor(getColor(R.color.aqua));

        DistanceMeterTag mt_od = new DistanceMeterTag("2", 25);
        mt_od.setMCRTagColor(getColor(R.color.aquamarine));

        DistanceMeterTag mt_b = new DistanceMeterTag("3", 75);
        mt_b.setMCRTagColor(getColor(R.color.brown));

        mDMObj.addCursor(mt_f, mt_od, mt_b);
    }
}
