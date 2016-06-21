package teamgodeater.hicarnet.MVP.Ui.Main;

import android.graphics.Point;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import teamgodeater.hicarnet.Adapter.BaseItem2LineAdapter;
import teamgodeater.hicarnet.Data.BaseItem2LineData;
import teamgodeater.hicarnet.Data.GasStationData;
import teamgodeater.hicarnet.Data.UserCarInfoData;
import teamgodeater.hicarnet.Help.UserDataHelp;
import teamgodeater.hicarnet.Help.Utils;
import teamgodeater.hicarnet.MainModle.Fragment.MainFragmentOld;
import teamgodeater.hicarnet.MainModle.MapOverlay.MarketOverlay;
import teamgodeater.hicarnet.R;

/**
 * Created by G on 2016/6/15 0015.
 */

public class GasStationHelp {

    private MainFragmentOld mainFragment;
    private MarketOverlay marketOverlay;
    private List<LatLng> pointList;
    private List<BaseItem2LineData> allGasRvDatas;

    public GasStationHelp(MainFragmentOld mainFragment) {
        this.mainFragment = mainFragment;
        showShortGasStation();
    }

    public void showShortGasStation() {
        removeOnMap();
        mainFragment.bottomHelp.setRvAdapter(1, getAdapter(getTopData(false)));
    }

    private void showAllGasStation() {
        if (allGasRvDatas == null) {
            List<BaseItem2LineData> topData = getTopData(true);
            allGasRvDatas = getDetailDatas();
            allGasRvDatas.add(0, topData.get(0));
        }

        showOnMap();
        mainFragment.bottomHelp.setRvAdapter(1, getAdapter(allGasRvDatas));
    }

    private void dataError() {
        if (Utils.getNetworkType() == Utils.NETTYPE_NONET) {
            Utils.toast("你好像没有连接到网络哦");
            return;
        }
        BDLocation myLocation = mainFragment.manageActivity.getMyLocation();
        if (myLocation == null) {
            Utils.toast("定位失败,请检查 网络和Gps 设置后重试");
            return;
        }
        setLoadingAnimator(true);
        LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        UserDataHelp.getGasstation(latLng, new UserDataHelp.OnDoneListener() {
            @Override
            public void onDone(int code) {
                setLoadingAnimator(false);
                showShortGasStation();
            }
        });
    }

    private BaseItem2LineAdapter getAdapter(List<BaseItem2LineData> datas) {
        BaseItem2LineAdapter adapter = new BaseItem2LineAdapter(datas);
        adapter.setOnClickListener(new BaseItem2LineAdapter.OnItemClickListener() {
            @Override
            public void onClick(BaseItem2LineData data, int position) {
                if (position == 0) {
                    String tag = (String) data.tag;
                    if (tag.equals("error")) {
                        dataError();
                    } else if (tag.equals("open")) {
                        showAllGasStation();
                    } else if (tag.equals("close")) {
                        showShortGasStation();
                    }
                } else {
                    itemClick(position - 1);
                }
            }

        });
        return adapter;
    }

    private void showOnMap() {
        if (marketOverlay != null)
            removeOnMap();
        marketOverlay = new MarketOverlay(mainFragment.mapHelp.map);
        marketOverlay.setPointList(pointList);
        marketOverlay.setMarketClickListen(new MarketOverlay.OnGasMarketClickListener() {
            @Override
            public void onGasMarketClick(int index) {
                itemClick(index);
            }
        });
        marketOverlay.addToMap();
        marketOverlay.setZoomWH(mainFragment.rootContain.getWidth() - Utils.dp2Px(32)
                , mainFragment.bottomViewPager.getTop() - mainFragment.searchView.getBottom() - Utils.dp2Px(32));
        marketOverlay.zoomToSpan();
    }

    private void itemClick(int index) {
        Utils.toast("index " + index);
        GasStationData.ResultBean.DataBean gasData = UserDataHelp.gasstationData.getResult().getData().get(index);
        animationToDetail();
    }

    private void animationToDetail() {
        mainFragment.bottomHelp.animationVisible(false, new Runnable() {
            @Override
            public void run() {
                mainFragment.requestMapControlPosition();
            }
        });

        Point point = new Point(
                mainFragment.rootContain.getWidth() / 2,
                mainFragment.rootContain.getHeight() / 2 + mainFragment.searchView.getBottom() / 2);
        MapStatus status = new MapStatus.Builder().targetScreen(point).rotate(5f).overlook(5f).build();
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(status);
        mainFragment.mapHelp.map.animateMapStatus(mapStatusUpdate, 800);

    }

    public boolean zoomToMLocInterceptor() {
        return marketOverlay != null && marketOverlay.zoomToSpan();
    }

    private void removeOnMap() {
        if (marketOverlay != null)
            marketOverlay.removeFromMap();
        marketOverlay = null;
    }

    private void setLoadingAnimator(boolean start) {
        mainFragment.bottomHelp.setRotation(start, "加油站数据 正在加载中...");
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
}
