package teamgodeater.hicarnet.MainModle.Help;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;

import teamgodeater.hicarnet.Activity.ManageActivity;
import teamgodeater.hicarnet.Help.Utils;

/**
 * Created by G on 2016/5/26 0026.
 */

public class RoutePlanSearchHelp implements OnGetRoutePlanResultListener {

    private final ManageActivity manageActivity;
    private final RoutePlanSearch routePlanSearch;
    private final OnDrivingRouteListener resultListener;

    public final static int Error_No_Net = 20001, Error_No_Loc = 20002,
            Error_No_Result = 20003, Error_Cant_Request = 20006;

    public interface OnDrivingRouteListener {
        void onSucceed(DrivingRouteResult route);

        void onErrorRoute(int code);
    }

    public RoutePlanSearchHelp(ManageActivity activity, OnDrivingRouteListener listener) {
        manageActivity = activity;
        resultListener = listener;
        routePlanSearch = RoutePlanSearch.newInstance();
        routePlanSearch.setOnGetRoutePlanResultListener(this);
    }

    public void requestRoutePlanSearch(PlanNode to) {

        BDLocation myLocation = manageActivity.getMyLocation();
        if (Utils.getNetworkType() == Utils.NETTYPE_NONET) {
            resultListener.onErrorRoute(Error_No_Net);
            return;
        }
        if (myLocation == null) {
            resultListener.onErrorRoute(Error_No_Loc);
            return;
        }

        PlanNode from = PlanNode.withLocation(Utils.getLatLng(myLocation));

        DrivingRoutePlanOption option = new DrivingRoutePlanOption();
        option.trafficPolicy(DrivingRoutePlanOption.DrivingTrafficPolicy.ROUTE_PATH_AND_TRAFFIC);
        option.from(from).to(to);

        boolean b = routePlanSearch.drivingSearch(option);

        if (!b) {
            resultListener.onErrorRoute(Error_Cant_Request);
        }

    }

    public void onDestroy() {
        routePlanSearch.destroy();
    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
        if (drivingRouteResult == null || drivingRouteResult.getRouteLines() == null
                || drivingRouteResult.error != DrivingRouteResult.ERRORNO.NO_ERROR) {
            resultListener.onErrorRoute(Error_No_Result);
        } else {
            resultListener.onSucceed(drivingRouteResult);
        }
    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

    }
}
