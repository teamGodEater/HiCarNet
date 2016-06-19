package teamgodeater.hicarnet.MapOverlay;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by G on 2016/6/16 0016.
 */

public class MarketOverlay extends OverlayManager {

    List<LatLng> pointList = null;
    private OnGasMarketClickListener listen;

    public void setPointList(List<LatLng> pointList) {
        this.pointList = pointList;
    }

    /**
     * 通过一个BaiduMap 对象构造
     *
     * @param baiduMap
     */

    public MarketOverlay(BaiduMap baiduMap) {
        super(baiduMap);
    }

    @Override
    public List<OverlayOptions> getOverlayOptions() {
        if (pointList == null)
            return null;

        List<OverlayOptions> overlayOptions = new ArrayList<>();
        int index = 0;
        for (LatLng point : pointList) {
            index++;
            overlayOptions.add(
                    new MarkerOptions().icon(getBitmapDescriptorByIndex(index))
                            .position(point).animateType(MarkerOptions.MarkerAnimateType.grow).zIndex(10).title("测试标题")
            );
        }
        return overlayOptions;
    }

    private BitmapDescriptor getBitmapDescriptorByIndex(int index) {
        BitmapDescriptor bitmapDescriptor;
        switch (index) {
            case 1:
                bitmapDescriptor = BitmapDescriptorFactory.fromAssetWithDpi("loc_market_1.png");
                break;
            case 2:
                bitmapDescriptor = BitmapDescriptorFactory.fromAssetWithDpi("loc_market_2.png");
                break;
            case 3:
                bitmapDescriptor = BitmapDescriptorFactory.fromAssetWithDpi("loc_market_3.png");
                break;
            case 4:
                bitmapDescriptor = BitmapDescriptorFactory.fromAssetWithDpi("loc_market_4.png");
                break;
            case 5:
                bitmapDescriptor = BitmapDescriptorFactory.fromAssetWithDpi("loc_market_5.png");
                break;
            case 6:
                bitmapDescriptor = BitmapDescriptorFactory.fromAssetWithDpi("loc_market_6.png");
                break;
            case 7:
                bitmapDescriptor = BitmapDescriptorFactory.fromAssetWithDpi("loc_market_7.png");
                break;
            case 8:
                bitmapDescriptor = BitmapDescriptorFactory.fromAssetWithDpi("loc_market_8.png");
                break;
            case 9:
                bitmapDescriptor = BitmapDescriptorFactory.fromAssetWithDpi("loc_market_9.png");
                break;
            default:
                bitmapDescriptor = BitmapDescriptorFactory.fromAssetWithDpi("loc_market_more.png");
        }
        return bitmapDescriptor;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (listen == null) {
            return false;
        }
        int i = mOverlayList.indexOf(marker);
        if (i != -1) {
            listen.onGasMarketClick(i);
            return true;
        }
        return false;
    }

    @Override
    public boolean zoomToSpan() {
        if (mOverlayList.size() <= 0) {
            return false;
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        boolean hasBound = false;
        for (Overlay overlay : mOverlayList) {
            if (overlay instanceof Marker) {
                hasBound = true;
                Marker m = (Marker) overlay;
                builder.include(m.getPosition());
            }
        }
        if (!hasBound)
            return false;

        LatLngBounds build = builder.build();
        if (width == 0 || height == 0) {
            throw new RuntimeException("调用次方法前必须调用setZoomWh 设置显示区域宽高");
        }
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory
                .newLatLngBounds(build, width, height), 500);
        return true;
    }


    @Override
    public boolean onPolylineClick(Polyline polyline) {
        return false;
    }

    public interface OnGasMarketClickListener {
        void onGasMarketClick(int index);
    }

    public void setMarketClickListen(OnGasMarketClickListener listen) {
        this.listen = listen;
        mBaiduMap.setOnMarkerClickListener(this);
    }
}
