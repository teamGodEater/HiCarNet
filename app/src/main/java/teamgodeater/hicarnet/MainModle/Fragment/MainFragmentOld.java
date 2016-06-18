package teamgodeater.hicarnet.MainModle.Fragment;

import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.lapism.searchview.SearchView;
import com.orhanobut.logger.Logger;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import teamgodeater.hicarnet.Activity.ManageActivity;
import teamgodeater.hicarnet.Adapter.BaseItem2LineAdapter;
import teamgodeater.hicarnet.CarManageModle.Fragment.CarManageFragmentOld;
import teamgodeater.hicarnet.Data.BaseItem2LineData;
import teamgodeater.hicarnet.Fragment.OldBaseFragment;
import teamgodeater.hicarnet.Help.ConditionTask;
import teamgodeater.hicarnet.Help.RestClientHelp;
import teamgodeater.hicarnet.Help.UserDataHelp;
import teamgodeater.hicarnet.Help.Utils;
import teamgodeater.hicarnet.Interface.OnLocReceiverObserve;
import teamgodeater.hicarnet.LoginModle.Fragment.LoginFragmentOld;
import teamgodeater.hicarnet.MainModle.Help.BottomHelp;
import teamgodeater.hicarnet.MainModle.Help.GasStationHelp;
import teamgodeater.hicarnet.MainModle.Help.MapHelp;
import teamgodeater.hicarnet.MainModle.Help.RouteHelp;
import teamgodeater.hicarnet.MainModle.Help.SearchHelp;
import teamgodeater.hicarnet.MainModle.MapOverlay.DrivingRouteOverlay;
import teamgodeater.hicarnet.R;
import teamgodeater.hicarnet.WeizhangModle.Fragment.WeizhangFragmentOld;
import teamgodeater.hicarnet.Widget.RippleBackGroundView;

/**
 * Created by G on 2016/5/20 0020.
 */
public class MainFragmentOld extends OldBaseFragment
        implements View.OnClickListener, BaiduMap.OnMapLoadedCallback, BaiduMap.OnMapTouchListener
        , OnLocReceiverObserve, SearchHelp.OnSearchRouteListener, SearchView.OnBackClickListener {


    @Bind(R.id.bottomViewPager)
    public ViewPager bottomViewPager;
    @Bind(R.id.zoomLocButton)
    RippleBackGroundView zoomLocButton;
    @Bind(R.id.searchView)
    public SearchView searchView;


    public MapHelp mapHelp;
    Handler handler;

    //综合条件满足 定位到我的位置
    ConditionTask fristLocTask;
    SearchHelp searchHelp;
    public BottomHelp bottomHelp;


    private DrivingRouteOverlay routeOverlay;
    private GasStationHelp gasStationHelp;

    public static MainFragmentOld getInstans() {
        List<Fragment> fragments = ManageActivity.manageActivity.getSupportFragmentManager().getFragments();
        for (Fragment f : fragments) {
            if (f instanceof MainFragmentOld) {
                return (MainFragmentOld) f;
            }
        }
        return new MainFragmentOld();
    }

    //--------------------------------------Parent Implement Begin----------------------------------

    @NonNull
    @Override
    protected SupportWindowsParams onCreateSupportViewParams() {
        SupportWindowsParams params = new SupportWindowsParams();
        params.rootLayoutId = R.layout.frgm_main_main;
        params.isHasToolBar = false;
        params.isNoFullScreen = false;
        return params;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        createFirstLocTask();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        //绑定视图
        ButterKnife.bind(this, rootContain);
        //设置searchHelp
        searchHelp = new SearchHelp(manageActivity, searchView, getChildFragmentManager());
        searchHelp.setOnSearchRouteListener(this);
        searchView.setOnBackClickListener(this);
        //设置bottomHelp
        bottomHelp = new BottomHelp(manageActivity, bottomViewPager, rootContain);
        setFirstBottomPagerDefault();
        //添加地图
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rootContain.addView(manageActivity.getMainMapView(), 0, params);
        //创建地图管理者
        mapHelp = new MapHelp(manageActivity);
        //隐藏地图控制按钮
        hideMapControlButton();
        if (manageActivity.getMyLocation() != null) {
            Logger.d("loc no null task");
            fristLocTask.excute();
        }
        //关闭Poi
        mapHelp.map.showMapPoi(false);
        //监听
        zoomLocButton.getDrawable().mutate().setColorFilter(getResources().getColor(R.color.colorBlack54), PorterDuff.Mode.SRC_IN);
        zoomLocButton.setOnClickListener(this);

        mapHelp.map.setOnMapLoadedCallback(this);
        mapHelp.map.setOnMapTouchListener(this);

        gasStationHelp = new GasStationHelp(this);

        return rootView;
    }


    @Override
    protected void onOnceGlobalLayoutListen() {
        Logger.d("onOnceGlobalLayoutListen");
        fristLocTask.excute();
    }

    @Override
    public void onResume() {
        super.onResume();
        manageActivity.setLocReceiverObserve(this);
        manageActivity.onMapResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        manageActivity.removeReceiverObserve();
        manageActivity.onMapPause();
        handler.removeCallbacks(hideMapRunnable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        bottomHelp.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        searchHelp.onDestroy();
    }

    @Override
    public boolean onInterceptBack() {
        return searchHelp.isInterceptBack();
    }

    @Override
    public String getType() {
        return "main";
    }

    //--------------------------------------Parent Implement End------------------------------------


    //-------------------------------------Implements Begin-----------------------------------------

    //--------------------------------ViewClickListen Begin-----------------------------------------

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.zoomLocButton) {
            if (gasStationHelp.zoomToMLocInterceptor())
                return;
            if (routeOverlay != null && routeOverlay.zoomToSpan()) {
                return;
            }
            boolean zoomLocResult = mapHelp.zoomToMyLoc(getMapCenterPoint());
            if (!zoomLocResult) {
                Toast.makeText(Utils.getContext(), "定位失败 请检查你的网络和Gps设置后重试", Toast.LENGTH_SHORT).show();
            }
            Logger.d("zoomLocResult  " + zoomLocResult);
        }
    }

    //--------------------------------ViewClickListener End-----------------------------------------

    //--------------------------------MapListen Begin-----------------------------------------------

    @Override
    public void onMapLoaded() {
        Logger.d("onMapLoaded");
        //设置元素位置
        requestMapControlPosition();
        fristLocTask.excute();
    }

    /**
     * 隐藏地图控制器Runnable
     */
    Runnable hideMapRunnable = new Runnable() {
        @Override
        public void run() {
            hideMapControlButton();
        }
    };

    @Override
    public void onTouch(MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            handler.removeCallbacks(hideMapRunnable);
            showMapControlButton();
        }
        if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
            handler.removeCallbacks(hideMapRunnable);
            handler.postDelayed(hideMapRunnable, 3000);
        }
    }

    //----------------------------------MapListen End-----------------------------------------------

    //---------------------------------LocReceiveListen Begin---------------------------------------
    @Override
    public void onReceiveLoc(BDLocation loc) {
        mapHelp.onReceiverLoc(loc);
        if (manageActivity.getMyLocation() != null) {
            fristLocTask.excuteOnce();
        }
    }
    //---------------------------------LocReceiveListen Begin---------------------------------------

    //-------------------------------SearchHelpListener---------------------------------------------

    @Override
    public void onRouteResult(DrivingRouteResult result) {
        if (!RouteHelp.isLegalRoute(result)) {
            Toast.makeText(Utils.getContext(), "错误: 获取结果无效", Toast.LENGTH_SHORT).show();
            searchView.close();
            return;
        }

        List<BaseItem2LineData> list = new ArrayList<>();
        for (DrivingRouteLine line : result.getRouteLines()) {
            BaseItem2LineData item = new BaseItem2LineData();
            item.icoLeft = R.drawable.ic_location;
            item.title = RouteHelp.getTrafficSuggest(line);
            item.tipRight = "查看";
            item.icoRight = R.drawable.ic_keyboard_arrow_right;
            String dis;
            String dur;

            if (line.getDistance() < 1000) {
                dis = line.getDistance() + " m";
            } else {
                DecimalFormat format = new DecimalFormat("#.0");
                dis = format.format((line.getDistance() / 1000f)) + " Km";
            }

            int min = line.getDuration() / 60;
            min = min > 1 ? min : 1;
            if (min > 60) {
                int hour = min / 60;
                min = min - hour * 60;
                dur = hour + " 小时 " + min + " 分钟";
            } else {
                dur = min + " 分钟";
            }

            item.tip = dis + " - 预计 " + dur;
            list.add(item);
        }

        BaseItem2LineAdapter adapter = new BaseItem2LineAdapter(list);
        adapter.setOnClickListener(new BaseItem2LineAdapter.OnItemClickListener() {
            @Override
            public void onClick(BaseItem2LineData data, int position) {
                if (routeOverlay != null) {
                    routeOverlay.changeFocusPolyLine(position);
                    RecyclerView.Adapter rvAdapter = bottomHelp.getRvAdapter(1);
                    if (rvAdapter instanceof BaseItem2LineAdapter) {
                        ((BaseItem2LineAdapter) rvAdapter).setFocusItem(position);
                        routeOverlay.zoomToSpan();
                    }
                }
            }
        });
        bottomHelp.setRvAdapter(1, adapter);
        mapHelp.setLocVisible(false);

        existRouteOverlay();
        routeOverlay.setShowMarket(true);
        routeOverlay.setData(result);
        routeOverlay.setOnPolyLineChangeListener(new DrivingRouteOverlay.OnPolyLineChangeListener() {
            @Override
            public void onChangePolyLine(int index) {
                Logger.d("onChangePolyLine index " + index);
                RecyclerView.Adapter rvAdapter = bottomHelp.getRvAdapter(1);
                if (rvAdapter instanceof BaseItem2LineAdapter) {
                    ((BaseItem2LineAdapter) rvAdapter).setFocusItem(index);
                    routeOverlay.zoomToSpan();
                }
            }
        });
        routeOverlay.addToMap();
        routeOverlay.zoomToSpan();
    }

    @Override
    public void onRouteStart(String title) {
        searchView.itemClickClose(title);
        bottomHelp.setCurrentItem(1);
        Toast.makeText(Utils.getContext(), "搜索路线 " + title, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRouteError() {
        searchView.close();
    }

    //--------------------------------------SearchViewBackClickListener-----------------------------
    @Override
    public void onBackClick() {
        Logger.d("search BackClick");
        if (routeOverlay != null) {
            routeOverlay.removeFromMap();
        }
        bottomHelp.setRvAdapter(1, null);
        mapHelp.setLocVisible(true);
        mapHelp.zoomToMyLoc(getMapCenterPoint());
    }


    //---------------------------------------Implements Eed-----------------------------------------


    //----------------------------------------Method Begin------------------------------------------


    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            mapHelp.setLocVisible(true);
        } else {
            mapHelp.setLocVisible(false);
        }
    }

    private void setFirstBottomPagerDefault() {
        List<BaseItem2LineData> datas = new ArrayList<>();

        BaseItem2LineData trafficData = null;
        trafficData = bottomHelp.getFBPTrafficData();
        if (trafficData != null) {
            datas.add(trafficData);
        }

        List<BaseItem2LineData> carDatas = null;
        carDatas = bottomHelp.getFBPCarInfoData();
        datas.addAll(carDatas);

        BaseItem2LineAdapter baseItem2LineAdapter = new BaseItem2LineAdapter(datas);
        baseItem2LineAdapter.setOnClickListener(new BaseItem2LineAdapter.OnItemClickListener() {
            @Override
            public void onClick(BaseItem2LineData data, int position) {
                //点击回调
                String tag = (String) data.tag;
                switch (tag) {
                    case "获取车辆数据失败":
                        if (Utils.getNetworkType() == Utils.NETTYPE_NONET) {
                            Toast.makeText(getActivity(), "你好像没有打开网络哦", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        bottomHelp.setRotation(true, "车辆数据\n加载中....");
                        UserDataHelp.getUserCarInfoDatas(new UserDataHelp.OnDoneListener() {
                            @Override
                            public void onDone(int code) {
                                if (code == RestClientHelp.HTTP_UNAUTHORIZED) {
                                    manageActivity.switchFragment(new LoginFragmentOld());
                                    hideSelf(400L);
                                    Utils.toast("请先登录");
                                } else {
                                    setFirstBottomPagerDefault();
                                }
                                bottomHelp.setRotation(false, null);
                            }
                        });
                        break;
                    case "获取路况数据失败":
                        if (Utils.getNetworkType() == Utils.NETTYPE_NONET) {
                            Toast.makeText(getActivity(), "你好像没有打开网络哦", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        bottomHelp.setRotation(true, "路况数据\n加载中....");
                        UserDataHelp.getUserPointData(new UserDataHelp.OnDoneListener() {
                            @Override
                            public void onDone(int code) {
                                if (code == RestClientHelp.HTTP_UNAUTHORIZED) {
                                    manageActivity.switchFragment(new LoginFragmentOld());
                                    hideSelf(400L);
                                    Utils.toast("请先登录");
                                } else {
                                    setFirstBottomPagerDefault();
                                }
                                bottomHelp.setRotation(false, null);

                            }
                        });
                        break;
                    case "管理车辆":
                        manageActivity.switchFragment(CarManageFragmentOld.getInstans());
                        hideSelf(400L);
                        break;
                    case "违章查询":
                        manageActivity.switchFragment(WeizhangFragmentOld.getInstans());
                        hideSelf(400L);
                        break;
                    case "没有设置车辆":
                        manageActivity.switchFragment(CarManageFragmentOld.getInstans());
                        hideSelf(400L);
                        break;
                    case "附近的4S店":
                        break;
                    case "没有设置路况":
                        break;
                    case "规划新路线":
                        break;
                }
            }
        });
        bottomHelp.setRvAdapter(0, baseItem2LineAdapter);
    }


    private void createFirstLocTask() {
        fristLocTask = new ConditionTask(3, new Runnable() {
            @Override
            public void run() {
                Logger.d("task Runnable");
                mapHelp.requestLocMarket();
                mapHelp.zoomToMyLoc(getMapCenterPoint());
            }
        });
    }

    public Point getMapCenterPoint() {
        Logger.d(rootContain.getHeight() + "  " + bottomViewPager.getTop());
        return new Point(rootContain.getWidth() / 2
                , searchView.getBottom() + (bottomViewPager.getTop()
                - searchView.getBottom()) / 2);
    }

    private void existRouteOverlay() {
        if (routeOverlay == null) {
            routeOverlay = new DrivingRouteOverlay(mapHelp.map);
            routeOverlay.setZoomWH(rootContain.getWidth() - Utils.dp2Px(32)
                    , bottomViewPager.getTop() - searchView.getBottom() - Utils.dp2Px(32));
            mapHelp.map.setOnPolylineClickListener(routeOverlay);
        } else {
            routeOverlay.removeFromMap();
        }
    }


    /**
     * 改变地图控制器位置 适应当前屏幕
     */
    public void requestMapControlPosition() {
        int left = bottomViewPager.getLeft();
        int top = searchView.getBottom();
        int bottom = bottomViewPager.getTop() + (int) bottomViewPager.getTranslationY();
        int right = bottomViewPager.getRight();
        MapView m = mapHelp.mainMapView;
        Point scaleControlPoint = new Point(left, bottom - m.getScaleControlViewHeight() - Utils.dp2Px(6));
        Point ZoomControlsPoint = new Point(right - Utils.dp2Px(40), top - Utils.dp2Px(60) + (bottom - top) / 2);
        m.setZoomControlsPosition(ZoomControlsPoint);
        m.setScaleControlPosition(scaleControlPoint);
    }

    private void hideMapControlButton() {
        zoomLocButton.setVisibility(View.INVISIBLE);
        mapHelp.mainMapView.showZoomControls(false);
    }

    private void showMapControlButton() {
        zoomLocButton.setVisibility(View.VISIBLE);
        mapHelp.mainMapView.showZoomControls(true);
    }

    //----------------------------------------Method End------------------------------------------


}
