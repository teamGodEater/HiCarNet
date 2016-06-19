package teamgodeater.hicarnet.MapHelp;

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
import teamgodeater.hicarnet.Utils.Utils;

import static teamgodeater.hicarnet.C.Map_Error_Cant_Request;
import static teamgodeater.hicarnet.C.Map_Error_No_Loc;
import static teamgodeater.hicarnet.C.Map_Error_No_Net;
import static teamgodeater.hicarnet.C.Map_Error_No_Result;
import static teamgodeater.hicarnet.C.NETTYPE_NONET;

/**
 * Created by G on 2016/5/26 0026.
 */

public class RoutePlanSearchHelp implements OnGetRoutePlanResultListener {

    private final ManageActivity manageActivity;
    private final RoutePlanSearch routePlanSearch;
    private final OnDrivingRouteListener resultListener;

    private PlanNode mFrom;
    private boolean isOnlyExecuteOnce = false;

    public void setOnlyExecuteOnce(boolean onlyExecuteOnce) {
        isOnlyExecuteOnce = onlyExecuteOnce;
    }

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

    public void setFrom(PlanNode mFrom) {
        this.mFrom = mFrom;
    }

    public void requestRoutePlanSearch(PlanNode to) {

        BDLocation myLocation = manageActivity.getMyLocation();
        if (Utils.getNetworkType() == NETTYPE_NONET) {
            resultListener.onErrorRoute(Map_Error_No_Net);
            return;
        }

        if (myLocation == null && mFrom == null) {
            resultListener.onErrorRoute(Map_Error_No_Loc);
            return;
        }

        PlanNode from;
        if (mFrom == null)
            from = PlanNode.withLocation(Utils.getLatLng(myLocation));
        else
            from = mFrom;

        DrivingRoutePlanOption option = new DrivingRoutePlanOption();
        option.trafficPolicy(DrivingRoutePlanOption.DrivingTrafficPolicy.ROUTE_PATH_AND_TRAFFIC);

        option.from(from).to(to);

        boolean b = routePlanSearch.drivingSearch(option);

        if (!b) {
            resultListener.onErrorRoute(Map_Error_Cant_Request);
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
            resultListener.onErrorRoute(Map_Error_No_Result);
        } else {
            resultListener.onSucceed(drivingRouteResult);
        }
        if (isOnlyExecuteOnce) {
            onDestroy();
        }
    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

    }
}
