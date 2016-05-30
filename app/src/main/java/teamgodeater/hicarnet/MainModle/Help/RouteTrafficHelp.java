package teamgodeater.hicarnet.MainModle.Help;

import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRouteResult;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by G on 2016/5/31 0031.
 */

public class RouteTrafficHelp {
    public static String getTrafficSuggest(DrivingRouteLine line) {
        List<DrivingRouteLine.DrivingStep> allStep = line.getAllStep();

        int trafficSlowly = 0;
        for (DrivingRouteLine.DrivingStep step : allStep) {
            if (step.getTrafficList() != null && step.getTrafficList()[0] >= 2) {
                trafficSlowly += step.getDistance();
            }
        }

        if (trafficSlowly <= 5) {
            return "畅通路线";
        } else {
            String dis = "";
            if (trafficSlowly < 1000) {
                dis = trafficSlowly + " m";
            } else {
                DecimalFormat format = new DecimalFormat("#.0");
                dis = format.format((trafficSlowly / 1000f)) + " Km";
            }
            return "拥堵 " + dis;
        }

    }

    public static boolean isLegalRoute(DrivingRouteResult routeLine) {
        if (routeLine == null
                || routeLine.getRouteLines() == null
                || routeLine.getRouteLines().get(0) == null
                || routeLine.getRouteLines().get(0).getAllStep() == null
                || routeLine.getRouteLines().get(0).getAllStep().size() <= 0
                || routeLine.getRouteLines().get(0).getAllStep().get(0).getWayPoints() == null
                || routeLine.getRouteLines().get(0).getAllStep().get(0).getWayPoints().size() <= 0 ){
            return false;
        }
        return true;
    }

}
