package teamgodeater.hicarnet.LaunchMoudle;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import teamgodeater.hicarnet.Fragment.SupportToolbarFragment;
import teamgodeater.hicarnet.MainModle.Fragment.MainFragment;
import teamgodeater.hicarnet.R;

/**
 * Created by G on 2016/5/19 0019.
 */
public class LaunchFragment extends SupportToolbarFragment {


    @Bind(R.id.tip)
    TextView tip;
    @Bind(R.id.brandLogo)
    ImageView brandLogo;
    @Bind(R.id.brandName)
    TextView brandName;
    @Bind(R.id.brandVersion)
    TextView brandVersion;
    @Bind(R.id.brandContain)
    RelativeLayout brandContain;


    @Override
    protected SupportWindowsParams onCreateSupportViewParams() {
        SupportWindowsParams params = new SupportWindowsParams();
        params.rootLayoutId = R.layout.frgm_launch;
        params.isNoFullScreen = false;
        params.statusAlpha = 0f;
        params.primaryColor = Color.BLACK;
        params.isHasToolBar = false;
        return params;
    }


    private String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        return format.format(new Date());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        Logger.d("onDestroyView ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.d("onDestroy ");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Logger.d("onDetach ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.d("onResume ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view

        super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this,rootContain);

        tip.setText(getDate() + "\n让出行更美好");

        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                Logger.d("load Over");
                if (manageActivity != null) {
                    destroySelf();
                    manageActivity.createMapView();
                    manageActivity.switchFragment(MainFragment.newInstance(1                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    ), false);
                }
            }
        }, 5000);

        Logger.d("create ");
        return rootContain;
    }

    @Override
    protected void onOnceGlobalLayoutListen() {
        starAnimation();
    }

    /**
     * 入场动画
     */
    private void starAnimation() {
        Logger.d("starAnimation ");

        float a = tip.getAlpha();
        tip.setAlpha(0);
        tip.animate().alpha(a).setDuration(500).setStartDelay(1500).start();

        brandContain.setAlpha(0);
        brandContain.animate().alpha(1f)
                .setDuration(500).setStartDelay(0).start();

    }

}
