package teamgodeater.hicarnet.LaunchMoudle;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.orhanobut.logger.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import teamgodeater.hicarnet.Data.UserCarInfoData;
import teamgodeater.hicarnet.Data.UserInfoData;
import teamgodeater.hicarnet.Data.UserPointData;
import teamgodeater.hicarnet.Fragment.SupportToolbarFragment;
import teamgodeater.hicarnet.Help.ConditionTask;
import teamgodeater.hicarnet.Help.UserHelp;
import teamgodeater.hicarnet.Help.Utils;
import teamgodeater.hicarnet.Interface.OnLocReceiverObserve;
import teamgodeater.hicarnet.MainModle.Fragment.MainFragment;
import teamgodeater.hicarnet.MainModle.Help.RoutePlanSearchHelp;
import teamgodeater.hicarnet.R;
import teamgodeater.hicarnet.RestClient.RestClient;

/**
 * Created by G on 2016/5/19 0019.
 */
public class LaunchFragment extends SupportToolbarFragment implements OnLocReceiverObserve {


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

    private UserInfoData userInfoData = null;
    private List<UserCarInfoData> userCarInfoDatas = null;
    private UserPointData userPointData = null;
    private DrivingRouteResult resultRoute = null;
    private ConditionTask routeSearchTask;

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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        routeSearchTask.cancle();
        routeSearchTask = null;
        if (routePlanSearchHelp != null)
            routePlanSearchHelp.onDestroy();
    }

    private RoutePlanSearchHelp routePlanSearchHelp = null;

    Runnable searchRoute = new Runnable() {
        @Override
        public void run() {
            routePlanSearchHelp = new RoutePlanSearchHelp(manageActivity, new RoutePlanSearchHelp.OnDrivingRouteListener() {
                @Override
                public void onSucceed(DrivingRouteResult route) {
                    resultRoute = route;
                }

                @Override
                public void onErrorRoute(int code) {

                }
            });
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        routeSearchTask = new ConditionTask(2, searchRoute);
        manageActivity.setLocReceiverObserve(this);
        loadRemoteData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootContain);

        tip.setText(getDate() + "\n让出行更美好");

        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                Logger.d("load Over");
                if (manageActivity != null) {
                    destroySelf();
                    manageActivity.createMapView();
                    MainFragment to = new MainFragment();
                    to.setUserData(userInfoData, userCarInfoDatas, userPointData,resultRoute);
                    manageActivity.switchFragment(to, false);
                }
            }
        }, 5000);

        Logger.d("create ");
        return rootContain;
    }

    private void loadRemoteData() {
        if (Utils.getNetworkType() == Utils.NETTYPE_NONET) {
            return;
        }

        if (UserHelp.Session == null) {
            if (!UserHelp.username.equals("") && !UserHelp.password.equals("")) {
                new UserHelp().login(new RestClient.OnResultListener<String>() {
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
        UserHelp userHelp = new UserHelp();
        userHelp.getUserInfo(new RestClient.OnResultListener<UserInfoData>() {
            @Override
            public void succeed(UserInfoData bean) {
                userInfoData = bean;
            }

            @Override
            public void error(int code) {

            }
        });

        userHelp.getUserCarInfo(null, new RestClient.OnResultListener<List<UserCarInfoData>>() {
            @Override
            public void succeed(List<UserCarInfoData> bean) {
                userCarInfoDatas = bean;
            }

            @Override
            public void error(int code) {

            }
        });

        userHelp.getUserPoint(new RestClient.OnResultListener<UserPointData>() {
            @Override
            public void succeed(UserPointData bean) {
                userPointData = bean;
                routeSearchTask.excute();
            }

            @Override
            public void error(int code) {

            }
        });
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

    @Override
    public void onReceiveLoc(BDLocation loc) {
        if (manageActivity.getMyLocation() != null) {
            routeSearchTask.excuteOnce();
        }
    }
}
