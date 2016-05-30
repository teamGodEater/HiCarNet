package teamgodeater.hicarnet.MainModle.Help;

import android.graphics.Point;

import com.baidu.location.BDLocation;
import com.baidu.location.Poi;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import teamgodeater.hicarnet.Activity.ManageActivity;
import teamgodeater.hicarnet.Help.Utils;
import teamgodeater.hicarnet.R;

/**
 * Created by G on 2016/5/23 0023.
 */

public class MapHelp {

    private final ManageActivity manageActivity;
    public MapView mainMapView;
    public final BaiduMap map;
    BDLocation myLoc;
    private Marker myLocMarker;

    public MapHelp(ManageActivity manageActivity) {
        this.manageActivity = manageActivity;
        mainMapView = manageActivity.getMainMapView();
        myLoc = manageActivity.getMyLocation();
        map = mainMapView.getMap();
    }

    public boolean zoomToMyLoc(Point centerPoint) {
        if (myLoc == null) {
            //定位失败
            return false;
        }

        MapStatus status = new MapStatus.Builder().target(Utils.getLatLng(myLoc)).targetScreen(centerPoint).rotate(0).overlook(0).zoom(14f).build();
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(status);
        map.animateMapStatus(mapStatusUpdate, 800);
        return true;
    }

    public void onReceiverLoc(BDLocation location) {

        StringBuffer sb = new StringBuffer(256);
        sb.append("time : ");
        sb.append(location.getTime());
        sb.append("\nerror code : ");
        sb.append(location.getLocType());
        sb.append("\nlatitude : ");
        sb.append(location.getLatitude());
        sb.append("\nlontitude : ");
        sb.append(location.getLongitude());
        sb.append("\nradius : ");
        sb.append(location.getRadius());
        if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
            sb.append("\nspeed : ");
            sb.append(location.getSpeed());// 单位：公里每小时
            sb.append("\nsatellite : ");
            sb.append(location.getSatelliteNumber());
            sb.append("\nheight : ");
            sb.append(location.getAltitude());// 单位：米
            sb.append("\ndirection : ");
            sb.append(location.getDirection());// 单位度
            sb.append("\naddr : ");
            sb.append(location.getAddrStr());
            sb.append("\ndescribe : ");
            sb.append("gps定位成功");

        } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
            sb.append("\naddr : ");
            sb.append(location.getAddrStr());
            //运营商信息
            sb.append("\noperationers : ");
            sb.append(location.getOperators());
            sb.append("\ndescribe : ");
            sb.append("网络定位成功");
        } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
            sb.append("\ndescribe : ");
            sb.append("离线定位成功，离线定位结果也是有效的");
        } else if (location.getLocType() == BDLocation.TypeServerError) {
            sb.append("\ndescribe : ");
            sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
        } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
            sb.append("\ndescribe : ");
            sb.append("网络不同导致定位失败，请检查网络是否通畅");
        } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
            sb.append("\ndescribe : ");
            sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
        }
        sb.append("\nlocationdescribe : ");
        sb.append(location.getLocationDescribe());// 位置语义化信息
        List<Poi> list = location.getPoiList();// POI数据
        if (list != null) {
            sb.append("\npoilist size = : ");
            sb.append(list.size());
            for (Poi p : list) {
                sb.append("\npoi= : ");
                sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
            }
        }


        myLoc = manageActivity.getMyLocation();
        if (myLoc != null) {
            requestLocMarket();
        } else {
            Logger.d(sb.toString());
        }

    }

    public void requestLocMarket() {
        if (myLocMarker == null) {
            OverlayOptions ooD = new MarkerOptions().position(Utils.getLatLng(myLoc))
                    .icons(getGlfLst()).zIndex(0).period(10);
            myLocMarker = (Marker) (map.addOverlay(ooD));
        }else{
            myLocMarker.setPosition(Utils.getLatLng(myLoc));
        }
    }

    public void setLocVisiable(boolean visiable) {
        if (myLocMarker != null) {
            myLocMarker.setVisible(visiable);
        }
    }
    private ArrayList<BitmapDescriptor> getGlfLst() {
        ArrayList<BitmapDescriptor> gifList = new ArrayList<>();
        gifList.add(BitmapDescriptorFactory.fromResource(R.drawable.loc_0));
        gifList.add(BitmapDescriptorFactory.fromResource(R.drawable.loc_1));
        gifList.add(BitmapDescriptorFactory.fromResource(R.drawable.loc_2));
        gifList.add(BitmapDescriptorFactory.fromResource(R.drawable.loc_3));
        gifList.add(BitmapDescriptorFactory.fromResource(R.drawable.loc_4));
        gifList.add(BitmapDescriptorFactory.fromResource(R.drawable.loc_5));
        gifList.add(BitmapDescriptorFactory.fromResource(R.drawable.loc_5));
        gifList.add(BitmapDescriptorFactory.fromResource(R.drawable.loc_4));
        gifList.add(BitmapDescriptorFactory.fromResource(R.drawable.loc_3));
        gifList.add(BitmapDescriptorFactory.fromResource(R.drawable.loc_2));
        gifList.add(BitmapDescriptorFactory.fromResource(R.drawable.loc_1));
        gifList.add(BitmapDescriptorFactory.fromResource(R.drawable.loc_0));
        return gifList;
    }
}
