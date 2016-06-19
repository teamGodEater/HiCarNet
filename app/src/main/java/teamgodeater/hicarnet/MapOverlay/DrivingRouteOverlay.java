package teamgodeater.hicarnet.MapOverlay;

import android.os.Bundle;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRouteLine.DrivingStep;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于显示一条驾车路线的overlay，自3.4.0版本起可实例化多个添加在地图中显示，当数据中包含路况数据时，则默认使用路况纹理分段绘制
 */
public class DrivingRouteOverlay extends OverlayManager {

    private DrivingRouteResult mRouteResult = null;
    private OnPolyLineChangeListener lineChangeListener;
    boolean isShowMarket = true;
    /**
     * 构造函数
     *
     * @param baiduMap 该DrivingRouteOvelray引用的 BaiduMap
     */
    public DrivingRouteOverlay(BaiduMap baiduMap) {
        super(baiduMap);
    }

    public void setShowMarket(boolean showMarket) {
        isShowMarket = showMarket;
    }

    @Override
    public final List<OverlayOptions> getOverlayOptions() {
        List<DrivingRouteLine> routeLinesList = mRouteResult.getRouteLines();
        List<OverlayOptions> overlayOptions = new ArrayList<>();

        if (isShowMarket) {
            //起点
            if (routeLinesList.get(0).getStarting() != null) {
                overlayOptions
                        .add((new MarkerOptions())
                                .position(mRouteResult.getRouteLines().get(0).getStarting().getLocation())
                                .icon(getStartMarker() != null ? getStartMarker() :
                                        BitmapDescriptorFactory
                                                .fromAssetWithDpi("Icon_start.png"))
                                .zIndex(10));
            }

            //终点
            if (mRouteResult.getRouteLines().get(0).getTerminal() != null) {
                overlayOptions
                        .add((new MarkerOptions())
                                .position(mRouteResult.getRouteLines().get(0).getTerminal().getLocation())
                                .icon(getTerminalMarker() != null ? getTerminalMarker() :
                                        BitmapDescriptorFactory
                                                .fromAssetWithDpi("Icon_end.png"))
                                .zIndex(10));
            }
        }

        for (int i = 0; i < routeLinesList.size(); i++) {
            Bundle b = new Bundle();
            b.putInt("index", i);
            overlayOptions.add(getPolyline(routeLinesList.get(i), b));
        }

        return overlayOptions;
    }

    public PolylineOptions getPolyline(DrivingRouteLine line, Bundle extra) {

        List<DrivingStep> allSteps = line.getAllStep();
        int allStepSize = allSteps.size();

        List<LatLng> points = new ArrayList<>();
        ArrayList<Integer> traffics = new ArrayList<>();

        for (int i = 0; i < allStepSize; i++) {
            DrivingStep step = allSteps.get(i);

            int test = step.getWayPoints().size();
            Logger.d(test + "  point");
            int trafficLength = step.getWayPoints().size() - 1;
            for (int j = 0; j < trafficLength; j++) {
                points.add(step.getWayPoints().get(j));
                if (step.getTrafficList() != null) {
                    traffics.add(step.getTrafficList()[j]);
                } else {
                    traffics.add(0);
                }
            }
            if (i == allStepSize - 1) {
                points.add(step.getWayPoints().get(step.getWayPoints().size() - 1));
            }

        }

        return new PolylineOptions().points(points).focus(false)
                .textureIndex(traffics).dottedLine(true).width(7).extraInfo(extra)
                .customTextureList(getCustomTextureList()).zIndex(0);

    }

    /**
     * 设置路线数据
     *
     * @param routeLine 路线数据
     */
    public void setData(DrivingRouteResult routeLine) {
        this.mRouteResult = routeLine;
    }


    public List<BitmapDescriptor> getCustomTextureList() {
        ArrayList<BitmapDescriptor> list = new ArrayList<>();
        list.add(BitmapDescriptorFactory.fromAsset("icon_road_blue_arrow.png"));
        list.add(BitmapDescriptorFactory.fromAsset("icon_road_green_arrow.png"));
        list.add(BitmapDescriptorFactory.fromAsset("icon_road_yellow_arrow.png"));
        list.add(BitmapDescriptorFactory.fromAsset("icon_road_red_arrow.png"));
        list.add(BitmapDescriptorFactory.fromAsset("icon_road_nofocus.png"));
        return list;
    }

    @Override
    protected void doSomething() {
        changeFocusPolyLine(0);
        if (lineChangeListener != null) {
            lineChangeListener.onChangePolyLine(0);
        }
    }

    /**
     * 覆写此方法以改变默认终点图标
     *
     * @return 终点图标
     */
    public BitmapDescriptor getTerminalMarker() {
        return null;
    }

    /**
     * 覆写此方法以改变默认起点图标
     *
     * @return 起点图标
     */
    public BitmapDescriptor getStartMarker() {
        return null;
    }


    @Override
    public final boolean onMarkerClick(Marker marker) {

        return true;
    }

    @Override
    public boolean onPolylineClick(Polyline polyline) {
        int index = polyline.getExtraInfo().getInt("index");
        changeFocusPolyLine(index);
        if (lineChangeListener != null) {
            lineChangeListener.onChangePolyLine(index);
        }
        return true;
    }

    public void changeFocusPolyLine(int i) {
        for (Overlay mPolyline : mOverlayList) {
            if (mPolyline instanceof Polyline) {
                int index = mPolyline.getExtraInfo().getInt("index");
                if (index == i) {
                    ((Polyline) mPolyline).setFocus(true);
                    mPolyline.setZIndex(1);
                } else {
                    ((Polyline) mPolyline).setFocus(false);
                    mPolyline.setZIndex(0);
                }
            }
        }
    }

    @Override
    public boolean zoomToSpan() {
        if ( mOverlayList.size() <= 0) {
            return false;
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        boolean hasBound = false;
        for (Overlay overlay : mOverlayList) {
            if (overlay instanceof Polyline) {
                Polyline polyline = (Polyline) overlay;
                if (polyline.isFocus()) {
                    List<LatLng> points = polyline.getPoints();
                    if (points != null && points.size() > 0) {
                        hasBound = true;
                        int bounder = points.size() - 1;
                        for (int i = 0; i < 10; i++) {
                            builder.include(points.get((int) (bounder * i / 9f)));
                        }
                    }
                }
            }
        }
        if (!hasBound)
            return false;

        LatLngBounds build = builder.build();
        if (width == 0 || height == 0) {
            throw new RuntimeException("调用次方法前必须调用setZoomWh 设置显示区域宽高");
        }
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory
                .newLatLngBounds(build, width,height),500);
        return true;
    }

    //---------------------------------------interface----------------------------------------------
    public interface OnPolyLineChangeListener {
        void onChangePolyLine(int index);
    }

    public void setOnPolyLineChangeListener(OnPolyLineChangeListener listener) {
        lineChangeListener = listener;
    }
}
