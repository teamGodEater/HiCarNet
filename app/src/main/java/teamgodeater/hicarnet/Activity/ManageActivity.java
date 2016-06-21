package teamgodeater.hicarnet.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.orhanobut.logger.Logger;

import butterknife.Bind;
import butterknife.ButterKnife;
import teamgodeater.hicarnet.Drawer.Fragment.DrawerFragment;
import teamgodeater.hicarnet.Help.LocationHelp;
import teamgodeater.hicarnet.Help.RestClientHelp;
import teamgodeater.hicarnet.Help.SharedPreferencesHelp;
import teamgodeater.hicarnet.Help.UserDataHelp;
import teamgodeater.hicarnet.MVP.Base.BaseFragmentManage;
import teamgodeater.hicarnet.MVP.Ui.Main.MainFragment;
import teamgodeater.hicarnet.R;
import teamgodeater.hicarnet.Utils.Utils;

public class ManageActivity extends AppCompatActivity implements BDLocationListener {

    @Bind(R.id.MainContain)
    FrameLayout mainContain;
    @Bind(R.id.DrawerContain)
    FrameLayout drawerContain;
    @Bind(R.id.DrawerLayout)
    DrawerLayout drawerLayout;

    public static ManageActivity manageActivity;

    MapView mainMapView;


    BDLocation myLocation;
    OnLocReceiverObserve receiverObserve;
    SDKReceiver mapSdkReceiver;
    private DrawerFragment drawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manageActivity = this;
        //不允许旋转
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //设置状态栏
        setSystemBar();
        setContentView(R.layout.activity);
        ButterKnife.bind(this);
        //侧滑
        drawerFragment = new DrawerFragment();
        drawerFragment.setDrawerLayout(drawerLayout);
        //----
        setFragment();
        //mapSdk状态
        registerReceiver();
        //加载本地数据
        loadLocalData();
    }

    public void setDrawerLayoutAllow(boolean allow) {
        if (allow) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED); //打开手势滑
        } else {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED); //关闭
        }
    }

    private void setFragment() {
        BaseFragmentManage.mFragmentManage = getSupportFragmentManager();
        getSupportFragmentManager().beginTransaction().add(R.id.DrawerContain, drawerFragment).commitAllowingStateLoss();
        // TODO: 2016/6/18 0018 启动
        BaseFragmentManage.switchFragment(new MainFragment());
    }

    private void loadLocalData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferencesHelp.CLIENT_INFO, MODE_PRIVATE);
        String username = sharedPreferences.getString(SharedPreferencesHelp.CLIENT_INFO_USERNAME, "");
        String password = sharedPreferences.getString(SharedPreferencesHelp.CLIENT_INFO_PASSWORD, "");
        String session = sharedPreferences.getString(SharedPreferencesHelp.CLIENT_INFO_SESSION, "");
        RestClientHelp.username = username;
        RestClientHelp.password = password;
        RestClientHelp.Session = session;
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocationHelp.Star(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocationHelp.Stop(this);
        onMapPause();
    }

    /**
     * 销毁地图
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mainMapView != null) {
            Logger.d("onDestroy");
            mainMapView.onDestroy();
        }
        if (mapSdkReceiver != null) {
            unregisterReceiver(mapSdkReceiver);
        }
    }


    public BDLocation getMyLocation() {
        return myLocation;
    }

    /**
     * 添加数据观察者
     */
    public void setLocReceiverObserve(OnLocReceiverObserve observe) {
        receiverObserve = observe;
    }

    public void removeReceiverObserve() {
        receiverObserve = null;
    }

    /*
    注册百度地图SDk状态广播接受者
    */
    private void registerReceiver() {
        // 注册 SDK 广播监听者
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        if (mapSdkReceiver == null) {
            mapSdkReceiver = new SDKReceiver();
        }
        registerReceiver(mapSdkReceiver, iFilter);
    }

    //SDK授权状态广播接收者
    public class SDKReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            Logger.d("action" + s);
            if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
                Logger.d("key 验证出错! 请在 AndroidManifest.xml 文件中检查 key 设置");
            } else if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK)) {
                Logger.d("key 验证成功! 功能可以正常使用");
            } else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
                Logger.d("网络出错");
            }
        }
    }


    /**
     * 地图监听回调-*-*
     *
     * @param bdLocation
     */
    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        if (bdLocation.getLocType() == BDLocation.TypeGpsLocation
                || bdLocation.getLocType() == BDLocation.TypeNetWorkLocation
                || bdLocation.getLocType() == BDLocation.TypeOffLineLocation) {
            myLocation = bdLocation;
            createMapView();
        }
        if (myLocation != null
                && (UserDataHelp.gasstationData == null || !UserDataHelp.gasstationData.getResultcode().equals("200"))) {
            LatLng latLng = Utils.getLatLng(myLocation);
//            UserDataHelp.getGasstation(latLng, null);
        }
        if (receiverObserve != null)
            receiverObserve.onReceiveLoc(bdLocation);
    }

    /**
     * 5.0以上 StatusBar 不透明度20% 以下跟随系统(默认40%）
     */
    private void setSystemBar() {
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= 21) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    public void setStatusBarAlpha(float f) {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.argb((int) (255 * f), 0, 0, 0));
        }
    }

    /**
     * @return状态栏高度
     */
    public int getStatusBarHeight() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        return getResources().getDimensionPixelSize(resourceId);
    }

    /**
     * 打开侧滑
     */
    public void openDrawer() {
        drawerLayout.openDrawer(Gravity.LEFT);
    }

    /**
     * 关闭侧滑
     */
    public void closeDrawer() {
        drawerLayout.closeDrawer(Gravity.LEFT);
    }

    public MapView getMainMapView() {
        createMapView();
        return mainMapView;
    }

    /**
     * @return 地图没有创建返回假
     */
    public boolean onMapResume() {
        if (mainMapView != null) {
            mainMapView.onResume();
            return true;
        }
        return false;
    }

    /**
     * @return 地图没有创建返回假
     */
    public boolean onMapPause() {
        if (mainMapView != null) {
            mainMapView.onPause();
            return true;
        }
        return false;
    }

    /**
     * 如果地图为空,创建一个地图实例
     */
    public void createMapView() {
        if (mainMapView == null) {
            BaiduMapOptions op = new BaiduMapOptions();
            MapStatus status = new MapStatus.Builder().target(Utils.getLatLng(myLocation)).zoom(11f).build();
            op.logoPosition(LogoPosition.logoPostionCenterBottom);
            op.mapStatus(status);
            op.compassEnabled(false);
            mainMapView = new MapView(this, op);
            mainMapView.onResume();
        }
    }

    long backPressPrevious = 0;

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
            return;
        }
        if (BaseFragmentManage.getTopFragment().onInterceptBack()) {
            return;
        }

        if (System.currentTimeMillis() - backPressPrevious < 2000) {
            finish();
        } else {
            Toast.makeText(this, "在按一次退出程序", Toast.LENGTH_SHORT).show();
        }

        backPressPrevious = System.currentTimeMillis();
    }

    public interface OnLocReceiverObserve {
        void onReceiveLoc(BDLocation loc);
    }
}
