package teamgodeater.hicarnet.MVP.Ui.Main;

import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.lapism.searchview.SearchHistoryTable;
import com.lapism.searchview.SearchItem;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import teamgodeater.hicarnet.Activity.ManageActivity;
import teamgodeater.hicarnet.Adapter.BaseItem2LineAdapter;
import teamgodeater.hicarnet.C;
import teamgodeater.hicarnet.Data.BaseItem2LineData;
import teamgodeater.hicarnet.MapHelp.RouteHelp;
import teamgodeater.hicarnet.MapHelp.RoutePlanSearchHelp;
import teamgodeater.hicarnet.MapHelp.SuggestSearchHelp;
import teamgodeater.hicarnet.MapOverlay.DrivingRouteOverlay;
import teamgodeater.hicarnet.R;
import teamgodeater.hicarnet.Utils.Utils;

/**
 * Created by G on 2016/6/20 0020.
 */

public class MapSearchPresent extends MainContractor.MapSearchPresent {
    private DrivingRouteOverlay routeOverlay;
    private SuggestSearchHelp suggestSearchHelp;
    private RoutePlanSearchHelp routePlanSearchHelp;
    private MainPresent mDspPresenter;
    private boolean hasCancel;

    @Override
    public boolean interceptZoomToFit() {
        return !hasCancel && routeOverlay != null && routeOverlay.zoomToSpan();
    }

    @Override
    public void searchLocation(String keyWord) {
        hasCancel = false;
        suggestSearchHelp.requestSuggestSearch(keyWord);
    }


    @Override
    public void showRoute(SearchItem item) {
        if (item.longitude == 0) {
            mView.searchView.setText(item.key);
            return;
        }
        if (item.ico == R.drawable.ic_location) {
            SearchHistoryTable table = new SearchHistoryTable(Utils.getContext());
            table.addItem(item);
        }
        onStartSearchRoute(item.key);
        PlanNode planNode = PlanNode.withLocation(new LatLng(item.latitude, item.longitude));
        routePlanSearchHelp.requestRoutePlanSearch(planNode);
    }

    @Override
    public void removeResult() {
        hasCancel = true;
        if (routeOverlay != null)
            routeOverlay.removeFromMap();
    }

    @Override
    protected void onStart() {
        mDspPresenter = mView.mPresenter;

        suggestSearchHelp = new SuggestSearchHelp(ManageActivity.manageActivity, new SuggestSearchHelp.onResultListener() {
            @Override
            public void onSucceed(List<SuggestionResult.SuggestionInfo> allSuggestions, String searchStr) {
                if (hasCancel)
                    return;
                List<SearchItem> list = new ArrayList<>();
                for (SuggestionResult.SuggestionInfo suggestionInfo : allSuggestions) {
                    SearchItem item = new SearchItem();
                    item.key = suggestionInfo.key;
                    if (suggestionInfo.pt == null) {
                        item.ico = R.drawable.ic_search;
                    } else {
                        item.ico = R.drawable.ic_location;
                        item.city = suggestionInfo.city;
                        item.district = suggestionInfo.district;
                        item.latitude = suggestionInfo.pt.latitude;
                        item.longitude = suggestionInfo.pt.longitude;
                    }
                    list.add(item);
                }
                mView.searchAdapter.setSuggestionsList(list);
                mView.searchView.startTextFilter();
            }

            @Override
            public void onErrorSuggest(int code) {
                if (hasCancel)
                    return;
                String tip = "";
                switch (code) {
                    case C.Map_Error_Cant_Request:
                        tip = "请求错误 请重试";
                        break;
                    case C.Map_Error_City_Empty:
                    case C.Map_Error_No_Loc:
                        tip = "定位失败 请检查你的网络和Gps设置";
                        break;
                    case C.Map_Error_No_Net:
                        tip = "无法连接服务器 请检查你的网络";
                        break;
                    case C.Map_Error_Search_String_Empty:
                        tip = "搜索的内容不能为空";
                        break;
                    case C.Map_Error_No_Result:
                        tip = "没有找到相关的内容";
                        break;
                }
                if (!tip.isEmpty()) {
                    Toast.makeText(Utils.getContext(), tip, Toast.LENGTH_SHORT).show();
                }
            }
        });

        routePlanSearchHelp = new RoutePlanSearchHelp(ManageActivity.manageActivity, new RoutePlanSearchHelp.OnDrivingRouteListener() {
            @Override
            public void onSucceed(DrivingRouteResult route) {
                if (hasCancel)
                    return;
                mView.hidePagerLoading();
                if (!RouteHelp.isLegalRoute(route)) {
                    Toast.makeText(Utils.getContext(), "服务器错误: 获取结果无效", Toast.LENGTH_SHORT).show();
                    return;
                }
                mDspPresenter.gasStationPresent.hideGasMarket();
                mDspPresenter.mapPresent.setMyLocVisible(false);

                List<BaseItem2LineData> list = new ArrayList<>();
                for (DrivingRouteLine line : route.getRouteLines()) {
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

                final BaseItem2LineAdapter adapter = new BaseItem2LineAdapter(list);
                adapter.setOnClickListener(
                        new BaseItem2LineAdapter.OnItemClickListener() {
                            @Override
                            public void onClick(BaseItem2LineData data, int position) {
                                if (routeOverlay != null && !hasCancel) {
                                    routeOverlay.changeFocusPolyLine(position);
                                    adapter.setFocusItem(position);
                                    routeOverlay.zoomToSpan();
                                }
                            }
                        }
                );
                mView.setSecondViewPager(adapter);
                mView.changeViewPagerIndex(1);
                resetRouteOverlay();
                routeOverlay.setShowMarket(true);
                routeOverlay.setData(route.getRouteLines());
                routeOverlay.setOnPolyLineChangeListener(new DrivingRouteOverlay.OnPolyLineChangeListener() {
                    @Override
                    public void onChangePolyLine(int index) {
                        if (hasCancel)
                            return;
                        adapter.setFocusItem(index);
                        routeOverlay.zoomToSpan();
                    }
                });

                routeOverlay.addToMap();
                mDspPresenter.mapPresent.zoomToFit();
            }

            @Override
            public void onErrorRoute(int code) {
                if (hasCancel)
                    return;
                String tip = "";
                switch (code) {
                    case C.Map_Error_Cant_Request:
                        tip = "请求错误 请重试";
                        break;
                    case C.Map_Error_No_Loc:
                        tip = "定位失败 请检查你的网络和Gps设置";
                        break;
                    case C.Map_Error_No_Net:
                        tip = "无法连接服务器 请检查你的网络";
                        break;
                    case C.Map_Error_No_Result:
                        tip = "没有找到相关的内容";
                        break;
                }
                Toast.makeText(Utils.getContext(), tip, Toast.LENGTH_SHORT).show();
                mView.hidePagerLoading();
            }
        }

        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        suggestSearchHelp.onDestroy();
        routePlanSearchHelp.onDestroy();
    }

    private void onStartSearchRoute(String key) {
        hasCancel = false;
        mView.searchView.itemClickClose(key);
        mView.showPagerLoading("正在努力寻找去\n\"" + key + "\"\n的路线 请稍后...");
    }

    private void resetRouteOverlay() {
        BaiduMap baiduMap = mDspPresenter.mapPresent.baiduMap;
        if (routeOverlay == null) {
            routeOverlay = new DrivingRouteOverlay(baiduMap);
            routeOverlay.setZoomWH(mView.mRootContain.getWidth() - Utils.dp2Px(32)
                    , mView.viewPagerFramelayout.getTop() - mView.searchView.getBottom() - Utils.dp2Px(32));
            baiduMap.setOnPolylineClickListener(routeOverlay);
        } else {
            routeOverlay.removeFromMap();
            baiduMap.setOnPolylineClickListener(routeOverlay);
        }
    }
}
