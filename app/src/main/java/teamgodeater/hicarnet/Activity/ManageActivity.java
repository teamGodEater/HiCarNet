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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.orhanobut.logger.Logger;

import butterknife.Bind;
import butterknife.ButterKnife;
import teamgodeater.hicarnet.Fragment.DrawerFragment;
import teamgodeater.hicarnet.Fragment.SupportToolbarFragment;
import teamgodeater.hicarnet.Help.LocationHelp;
import teamgodeater.hicarnet.Help.UserHelp;
import teamgodeater.hicarnet.Help.Utils;
import teamgodeater.hicarnet.Interface.OnReceiverObserve;
import teamgodeater.hicarnet.LaunchMoudle.LaunchFragment;
import teamgodeater.hicarnet.R;

public class ManageActivity extends AppCompatActivity implements BDLocationListener {

    @Bind(R.id.MainContain)
    FrameLayout mainContain;
    @Bind(R.id.DrawerContain)
    FrameLayout drawerContain;
    @Bind(R.id.DrawerLayout)
    DrawerLayout drawerLayout;

    MapView mainMapView;


    BDLocation myLocation;
    OnReceiverObserve receiverObserve;
    SDKReceiver mapSdkReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //不允许旋转
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //设置状态栏
        setSystemBar();
        setContentView(R.layout.activity);
        ButterKnife.bind(this);
        FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
        beginTransaction.replace(R.id.MainContain, new LaunchFragment());
        beginTransaction.replace(R.id.DrawerContain, new DrawerFragment()).commit();
        //mapSdk状态
        registerReceiver();
        //加载本地数据
        loadLocalData();
        //加载用户数据

    }

    private void loadLocalData() {
        SharedPreferences sharedPreferences = getSharedPreferences(UserHelp.SHARE_USER_HELP, MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        String password = sharedPreferences.getString("password", "");
        String session = sharedPreferences.getString("session", "");
        UserHelp.username = username;
        UserHelp.password = password;
        UserHelp.Session = session;
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
    public void setReceiverObserve(OnReceiverObserve observe) {
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
    public int getStatuBarHeight() {
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
            Logger.d("onMapPause");
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
            Logger.d("create map" + Utils.getLatLng(myLocation));
            BaiduMapOptions op = new BaiduMapOptions();
            MapStatus status = new MapStatus.Builder().target(Utils.getLatLng(myLocation)).zoom(11f).build();
            op.logoPosition(LogoPosition.logoPostionCenterBottom);
            op.mapStatus(status);
            op.compassEnabled(false);
            mainMapView = new MapView(this, op);
            mainMapView.onResume();
        }
    }

    public void switchFragment(Fragment to) {
        switchFragment(to, true);
    }

    public void switchFragment(Fragment to, boolean addBack) {
        FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
        if (addBack) {
            beginTransaction.addToBackStack(null);
        }
        beginTransaction.add(R.id.MainContain, to).commitAllowingStateLoss();
    }

    long backPressPrevious = 0;

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
            return;
        }
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        Fragment currentFragment = supportFragmentManager.findFragmentById(R.id.MainContain);
        if (currentFragment instanceof SupportToolbarFragment
                && ((SupportToolbarFragment) currentFragment).isInterceptBack()) {
            return;
        }
        if (supportFragmentManager.getBackStackEntryCount() > 1) {
            supportFragmentManager.popBackStackImmediate();
            return;
        }
        if (System.currentTimeMillis() - backPressPrevious < 2000) {
            finish();
        } else {
            Toast.makeText(this, "在按一次退出程序", Toast.LENGTH_SHORT).show();
        }

        backPressPrevious = System.currentTimeMillis();
    }
}