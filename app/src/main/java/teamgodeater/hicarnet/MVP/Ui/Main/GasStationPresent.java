package teamgodeater.hicarnet.MVP.Ui.Main;

import android.graphics.Point;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.PlanNode;

import java.util.ArrayList;
import java.util.List;

import teamgodeater.hicarnet.Adapter.BaseItem2LineAdapter;
import teamgodeater.hicarnet.C;
import teamgodeater.hicarnet.Data.BaseItem2LineData;
import teamgodeater.hicarnet.Data.GasStationData;
import teamgodeater.hicarnet.Data.UserCarInfoData;
import teamgodeater.hicarnet.Help.UserDataHelp;
import teamgodeater.hicarnet.MapHelp.RouteHelp;
import teamgodeater.hicarnet.MapHelp.RoutePlanSearchHelp;
import teamgodeater.hicarnet.MapOverlay.DrivingRouteOverlay;
import teamgodeater.hicarnet.MapOverlay.MarketOverlay;
import teamgodeater.hicarnet.R;
import teamgodeater.hicarnet.Utils.Utils;

/**
 * Created by G on 2016/6/21 0021.
 */

public class GasStationPresent extends MainContractor.GasStationPresent {
    RoutePlanSearchHelp routePlanSearchHelp;
    DrivingRouteOverlay routeOverlay;
    MarketOverlay gasStationOverlay;
    MainPresent mDspPresenter;
    List<LatLng> pointList;
    boolean marketVisible = false;

    @Override
    protected void addToPager(List<BaseItem2LineData> item2LineDatas) {
        BaseItem2LineAdapter baseItem2LineAdapter = new BaseItem2LineAdapter(item2LineDatas);
        baseItem2LineAdapter.setOnClickListener(new BaseItem2LineAdapter.OnItemClickListener() {
            @Override
            public void onClick(BaseItem2LineData data, int position) {
                if (position == 0) {
                    String tag = (String) data.tag;
                    if (tag.equals("error")) {
                        marketVisible = false;
                        getData();
                    } else if (tag.equals("open")) {
                        marketVisible = true;
                        showAllGasStation();
                    } else if (tag.equals("close")) {
                        marketVisible = false;
                        mDspPresenter.mapPresent.zoomToFit();
                        showShortGasStation();
                    }
                } else {
                    itemClick(position - 1);
                }
            }
        });
        mView.setSecondViewPager(baseItem2LineAdapter);
    }

    @Override
    public boolean interceptZoomToFit() {
        return (routeOverlay != null && routeOverlay.zoomToSpan()) || (marketVisible
                && gasStationOverlay != null
                && gasStationOverlay.zoomToSpan());
    }

    @Override
    public void showGasMarket() {
        marketVisible = true;
        if (gasStationOverlay == null || !gasStationOverlay.setVisible(true)) {
            gasStationOverlay = new MarketOverlay(mDspPresenter.mapPresent.baiduMap);
            gasStationOverlay.setPointList(pointList);
            gasStationOverlay.setMarketClickListen(new MarketOverlay.OnGasMarketClickListener() {
                @Override
                public void onGasMarketClick(int index) {
                    itemClick(index);
                }
            });
            gasStationOverlay.addToMap();
            gasStationOverlay.setZoomWH(mView.mRootContain.getWidth() - Utils.dp2Px(32)
                    , mView.viewPagerFramelayout.getTop() - mView.searchView.getBottom() - Utils.dp2Px(32));
            gasStationOverlay.zoomToSpan();
        }
    }

    @Override
    public void hideGasMarket() {
        marketVisible = false;
        if (gasStationOverlay != null)
            gasStationOverlay.setVisible(false);
    }

    @Override
    protected void getData() {
        if (Utils.getNetworkType() == C.NETTYPE_NONET) {
            Utils.toast("你好像没有连接到网络哦");
            return;
        }
        BDLocation myLocation = mView.manageActivity.getMyLocation();
        if (myLocation == null) {
            Utils.toast("定位失败,请检查 网络和Gps 设置后重试");
            return;
        }
        mView.showPagerLoading("正在获取附近的加油站....");
        UserDataHelp.getGasstation(Utils.getLatLng(mView.manageActivity.getMyLocation()), new UserDataHelp.OnDoneListener() {
            @Override
            public void onDone(int code) {
                mView.hidePagerLoading();
                showShortGasStation();
            }
        });
    }

    @Override
    public void showAllGasStation() {
        List<BaseItem2LineData> topData = getTopData(true);
        List<BaseItem2LineData> detailDatas = getDetailDatas();
        detailDatas.add(0, topData.get(0));
        addToPager(detailDatas);

        showGasMarket();
    }

    @Override
    public void showShortGasStation() {
        hideGasMarket();
        List<BaseItem2LineData> topData = getTopData(false);
        addToPager(topData);
    }

    @Override
    public void orderReturnClick() {
        routeOverlay.removeFromMap();
        mView.hideOrderTop();
        mView.hideOrderFab();
        mView.viewPagerAnimationVisible(true, new Runnable() {
            @Override
            public void run() {
                mDspPresenter.mapPresent.requestMapControlPosition();
                mDspPresenter.mapPresent.setMyLocVisible(true);
                showGasMarket();
                Point mapCenterPoint = mDspPresenter.mapPresent.getMapCenterPoint();
                MapStatus status = new MapStatus.Builder()
                        .targetScreen(mapCenterPoint).rotate(0).overlook(0).build();
                mDspPresenter.mapPresent.baiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(status));
                mDspPresenter.mapPresent.zoomToFit();
            }
        });
    }

    @Override
    public void orderBuyClick() {

    }

    @Override
    public void orderGoClick() {

    }

    @Override
    protected void onStart() {
        mDspPresenter = mView.mPresenter;
        routePlanSearchHelp = new RoutePlanSearchHelp(mView.manageActivity, new RoutePlanSearchHelp.OnDrivingRouteListener() {
            @Override
            public void onSucceed(final DrivingRouteResult route) {
                mView.hidePagerLoading();
                if (!RouteHelp.isLegalRoute(route)) {
                    Toast.makeText(Utils.getContext(), "服务器错误: 获取结果无效", Toast.LENGTH_SHORT).show();
                    return;
                }

                mView.viewPagerAnimationVisible(false, new Runnable() {
                    @Override
                    public void run() {
                        mDspPresenter.mapPresent.setMyLocVisible(false);
                        mDspPresenter.mapPresent.requestMapControlPosition();
                        hideGasMarket();
                        mView.showOrderFab();
                        mView.showOrderTop();
                        resetRouteOverlay();
                        routeOverlay.setShowMarket(true);
                        routeOverlay.setData(route.getRouteLines().subList(0, 1));
                        routeOverlay.addToMap();

                        Point mapCenterPoint = mDspPresenter.mapPresent.getMapCenterPoint();
                        int different = mView.detailTop.getHeight() - mView.searchView.getHeight();
                        mapCenterPoint.y += (different - Utils.dp2Px(72)) / 2;

                        MapStatus status = new MapStatus.Builder()
                                .targetScreen(mapCenterPoint).rotate(0).overlook(0).build();
                        mDspPresenter.mapPresent.baiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(status));
                        mDspPresenter.mapPresent.zoomToFit();
                    }
                });
            }

            @Override
            public void onErrorRoute(int code) {
                mView.hidePagerLoading();
                Utils.toast("请检查网络设置...");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        routePlanSearchHelp.onDestroy();
    }

    private List<BaseItem2LineData> getTopData(boolean show) {
        GasStationData gasstationData = UserDataHelp.gasstationData;
        List<BaseItem2LineData> dataList = new ArrayList<>();
        if (gasstationData == null
                || !gasstationData.getResultcode().equals("200")
                || gasstationData.getResult() == null
                || gasstationData.getResult().getData() == null
                || gasstationData.getResult().getData().size() == 0) {

            BaseItem2LineData data = new BaseItem2LineData();
            data.icoLeft = R.drawable.ic_local_gas;
            data.title = "读取加油站数据失败";
            data.tipRight = "重试";
            data.tag = "error";
            dataList.add(data);
        } else {
            BaseItem2LineData data = new BaseItem2LineData();
            data.title = "附近有" + gasstationData.getResult().getData().size() + "个加油站";
            UserCarInfoData defaultCarInfoData = UserDataHelp.getDefaultCarInfoData();
            if (defaultCarInfoData != null) {
                int petrol_gage = defaultCarInfoData.getPetrol_gage();
                data.tip = "剩余汽油量 " + petrol_gage + "%";
            }
            if (show) {
                data.icoLeft = R.drawable.ic_keyboard_arrow_left;
                data.tipRight = "收起";
                data.tag = "close";
                data.icoRight = R.drawable.ic_visibility;
                data.icoRightColor = Utils.getColorFromRes(R.color.colorAccent);
                data.hasDivider = true;
                dataList.add(data);
            } else {
                data.icoLeft = R.drawable.ic_local_gas;
                data.tipRight = "显示";
                data.icoRight = R.drawable.ic_visibility;
                data.tag = "open";
                data.hasDivider = true;
                dataList.add(data);
            }
        }
        return dataList;
    }

    private void itemClick(int i) {
        GasStationData.ResultBean.DataBean gasData = UserDataHelp.gasstationData.getResult().getData().get(i);
        mView.showPagerLoading("正在加载详细数据 请稍后...");
        PlanNode planNode = PlanNode.withLocation(
                new LatLng(Double.valueOf(gasData.getLat()), Double.valueOf(gasData.getLon())));
        routePlanSearchHelp.requestRoutePlanSearch(planNode);
    }

    private List<BaseItem2LineData> getDetailDatas() {
        GasStationData gasstationData = UserDataHelp.gasstationData;
        List<BaseItem2LineData> dataList = new ArrayList<>();
        pointList = new ArrayList<>();
        int index = 0;
        for (GasStationData.ResultBean.DataBean bean : gasstationData.getResult().getData()) {
            pointList.add(new LatLng(Double.parseDouble(bean.getLat()), Double.parseDouble(bean.getLon())));
            index++;
            BaseItem2LineData data = new BaseItem2LineData();
            data.icoLeft = R.drawable.ic_location;
            data.title = String.valueOf(index) + "." + bean.getName();
            data.tip = bean.getAddress();
            data.icoRight = R.drawable.ic_keyboard_arrow_right;
            dataList.add(data);
        }
        return dataList;
    }

    private void resetRouteOverlay() {
        BaiduMap baiduMap = mDspPresenter.mapPresent.baiduMap;
        if (routeOverlay == null) {
            routeOverlay = new DrivingRouteOverlay(baiduMap);
            routeOverlay.setZoomWH(mView.mRootContain.getWidth() - Utils.dp2Px(32)
                    , mView.viewPagerFramelayout.getTop() + (int)mView.viewPagerFramelayout.getTranslationY()
                            - mView.detailTop.getBottom() - Utils.dp2Px(128));
        } else {
            routeOverlay.removeFromMap();
        }
    }

}
