package teamgodeater.hicarnet.LaunchMoudle;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import teamgodeater.hicarnet.Fragment.OldBaseFragment;
import teamgodeater.hicarnet.Help.RestClientHelp;
import teamgodeater.hicarnet.Help.UserDataHelp;
import teamgodeater.hicarnet.Help.Utils;
import teamgodeater.hicarnet.MainModle.Fragment.MainFragmentOld;
import teamgodeater.hicarnet.R;
import teamgodeater.hicarnet.RestClient.RestClient;

/**
 * Created by G on 2016/5/19 0019.
 */
public class LaunchFragmentOld extends OldBaseFragment {


    @Bind(R.id.tip)
    TextView tip;
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

    @Override
    public String getType() {
        return "";
    }

    private String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        return format.format(new Date());
    }

    boolean hasDestory = false;
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hasDestory = true;
        ButterKnife.unbind(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manageActivity.setDrawerLayoutAllow(false);
        loadRemoteData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootContain);
        tip.setText(getDate() + "\n让出行更美好");
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                manageActivity.setDrawerLayoutAllow(true);
                manageActivity.createMapView();
                MainFragmentOld to = new MainFragmentOld();
                manageActivity.switchFragment(to);
            }
        }, 5000);

        Logger.d("create ");
        return rootview;
    }

    private void loadRemoteData() {
        if (Utils.getNetworkType() == Utils.NETTYPE_NONET) {
            return;
        }

        if (RestClientHelp.Session.equals("")) {
            if (!RestClientHelp.username.equals("") && !RestClientHelp.password.equals("")) {
                new RestClientHelp().login(new RestClient.OnResultListener<String>() {
                    @Override
                    public void succeed(String bean) {
                        loadUserData();
                    }

                    @Override
                    public void error(int code) {
                    }
                });
            }
        } else {
            loadUserData();
        }
    }


    private void loadUserData() {
        UserDataHelp.getUserPointData(null);
        UserDataHelp.getUserCarInfoDatas(null);
        UserDataHelp.getUserInfoData(null);
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
        tip.setAlpha(0f);
        tip.animate().alpha(a).setDuration(700L).setStartDelay(1000L).start();

        brandContain.setAlpha(0f);
        brandContain.animate().alpha(1f).start();

    }

}
