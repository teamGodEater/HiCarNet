package teamgodeater.hicarnet.MainModle.MapOverlay;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnPolylineClickListener;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;

import static com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;

/**
 * 该类提供一个能够显示和管理多个Overlay的基类
 * <p>
 * 复写{@link #getOverlayOptions()} 设置欲显示和管理的Overlay列表
 * </p>
 * <p>
 * 通过
 * {@link com.baidu.mapapi.map.BaiduMap#setOnMarkerClickListener(com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener)}
 * 将覆盖物点击事件传递给OverlayManager后，OverlayManager才能响应点击事件。
 * <p>
 * 复写{@link #onMarkerClick(com.baidu.mapapi.map.Marker)} 处理Marker点击事件
 * </p>
 */
public abstract class OverlayManager implements OnMarkerClickListener, OnPolylineClickListener {

    BaiduMap mBaiduMap = null;
    private List<OverlayOptions> mOverlayOptionList = null;

    List<Overlay> mOverlayList = null;
    private int width;
    private int height;

    /**
     * 通过一个BaiduMap 对象构造
     *
     * @param baiduMap
     */
    public OverlayManager(BaiduMap baiduMap) {
        mBaiduMap = baiduMap;
        // mBaiduMap.setOnMarkerClickListener(this);
        if (mOverlayOptionList == null) {
            mOverlayOptionList = new ArrayList<OverlayOptions>();
        }
        if (mOverlayList == null) {
            mOverlayList = new ArrayList<Overlay>();
        }
    }

    /**
     * 覆写此方法设置要管理的Overlay列表
     *
     * @return 管理的Overlay列表
     */
    public abstract List<OverlayOptions> getOverlayOptions();

    /**
     * 将所有Overlay 添加到地图上
     */
    public void addToMap() {
        if (mBaiduMap == null) {
            return;
        }

        removeFromMap();
        List<OverlayOptions> overlayOptions = getOverlayOptions();
        if (overlayOptions != null) {
            mOverlayOptionList.addAll(overlayOptions);
        }

        mOverlayList.addAll(mBaiduMap.addOverlays(mOverlayOptionList));

        doSomething();
    }


    /**
     * 在地图添加到地图上之后调用
     */
    protected void doSomething() {
    }


    /**
     * 将所有Overlay 从 地图上消除
     */
    public final void removeFromMap() {
        if (mBaiduMap == null || mOverlayList == null || mOverlayList.size() <= 0) {
            return;
        }

        for (Overlay marker : mOverlayList) {
            marker.remove();
        }

        mOverlayOptionList.clear();
        mOverlayList.clear();
    }

    /**
     * 缩放地图，使所有Overlay都在合适的视野内
     * <p>
     * 注： 该方法只对Marker类型的overlay有效
     * </p>
     */
    public boolean zoomToSpan() {
        if ( mOverlayList.size() <= 0) {
            return false;
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Overlay overlay : mOverlayList) {
            if (overlay instanceof Polyline) {
                Polyline polyline = (Polyline) overlay;
                if (polyline.isFocus()) {
                    List<LatLng> points = polyline.getPoints();
                    if (points != null && points.size() > 0) {
                        int bounder = points.size() - 1;
                        for (int i = 0; i < 10; i++) {
                            builder.include(points.get((int) (bounder * i / 9f)));
                        }
                    }
                }
            }
        }

        LatLngBounds build = builder.build();
        if (width == 0 || height == 0) {
            throw new RuntimeException("调用次方法前必须调用setZoomWh 设置显示区域宽高");
        }
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory
                .newLatLngBounds(build, width,height),500);
        return true;
    }

    public void setZoomWH(int w, int h) {
        width = w;
        height = h;
    }

}
