package wxm.extendui.IconButton;

import android.graphics.drawable.Icon;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import wxm.extendui.R;
import wxm.uilib.IconButton.IconButton;

public class ACIconButton extends AppCompatActivity {
    @BindView(R.id.ib_show)
    IconButton  mIBShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_icon_button);

        ButterKnife.bind(this);

        mIBShow.setOnClickListener(v -> {
            IconButton ib = (IconButton) v;
            ib.setColdOrHot(!ib.isHot());
        });
    }
}
