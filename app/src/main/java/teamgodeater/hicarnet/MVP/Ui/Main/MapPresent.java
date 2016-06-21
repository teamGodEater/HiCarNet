package teamgodeater.hicarnet.MVP.Ui.Main;

import android.graphics.Point;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.location.BDLocation;
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

import java.util.ArrayList;

import teamgodeater.hicarnet.Activity.ManageActivity;
import teamgodeater.hicarnet.Help.ConditionTask;
import teamgodeater.hicarnet.R;
import teamgodeater.hicarnet.Utils.Utils;

import static teamgodeater.hicarnet.Activity.ManageActivity.manageActivity;

/**
 * Created by G on 2016/6/20 0020.
 */

public class MapPresent extends MainContractor.MapPresent implements BaiduMap.OnMapLoadedCallback, BaiduMap.OnMapTouchListener, ManageActivity.OnLocReceiverObserve {
    public MapView mapView;
    public BaiduMap baiduMap;

    private ConditionTask firstLocTask;
    private Handler handler;
    private Marker myLocMarker;
    private ArrayList<BitmapDescriptor> gifList;
    private MainPresent mDspPresenter;

    @Override
    public void zoomToFit() {
        if (mDspPresenter.gasStationPresent.interceptZoomToFit() ||
                mDspPresenter.mapSearchPresent.interceptZoomToFit())
            return;
        zoomToMyLoc();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDspPresenter = null;
    }

    @Override
    protected void onStart() {
        mDspPresenter = mView.mPresenter;
        handler = new Handler();
        createFirstLocTask();
        getGlfLst();
        //添加百度地图
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mapView = manageActivity.getMainMapView();
        mView.mRootContain
                .addView(mapView, 0, params);
        //设置百度地图
        baiduMap = mapView.getMap();
        baiduMap.showMapPoi(false);
        //设置百度监听
        baiduMap.setOnMapLoadedCallback(this);
        baiduMap.setOnMapTouchListener(this);
        //设置定位监听观察者
        manageActivity.setLocReceiverObserve(this);
    }

    private void createFirstLocTask() {
        firstLocTask = new ConditionTask(2, new Runnable() {
            @Override
            public void run() {
                if (mDspPresenter != null)
                    zoomToMyLoc();
            }
        });
    }

    @Override
    public void onMapLoaded() {
        requestMapControlPosition();
        firstLocTask.excute();
    }


    /**
     * 隐藏地图控制器Runnable
     */
    Runnable hideMapControlRunnable = new Runnable() {
        @Override
        public void run() {
            hideMapControlButton();
        }
    };

    private void hideMapControlButton() {
        mView.zoomLocButton.setVisibility(View.INVISIBLE);
        mapView.showZoomControls(false);
    }

    private void showMapControlButton() {
        mView.zoomLocButton.setVisibility(View.VISIBLE);
        mapView.showZoomControls(true);
    }


    @Override
    public void onTouch(MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            handler.removeCallbacks(hideMapControlRunnable);
            showMapControlButton();
        }
        if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
            handler.removeCallbacks(hideMapControlRunnable);
            handler.postDelayed(hideMapControlRunnable, 3000);
        }
    }


    @Override
    public void onReceiveLoc(BDLocation loc) {
        if (manageActivity.getMyLocation() != null) {
            requestLocMarket();
        }
        if (manageActivity.getMyLocation() != null) {
            firstLocTask.excuteOnce();
        }
    }

    private void requestLocMarket() {
        if (locVisible)
            if (myLocMarker == null) {
                if (gifList == null)
                    getGlfLst();
                OverlayOptions ooD = new MarkerOptions().position(Utils.getLatLng(manageActivity.getMyLocation()))
                        .icons(gifList).zIndex(0).period(10);
                myLocMarker = (Marker) (baiduMap.addOverlay(ooD));
            } else {
                myLocMarker.setPosition(Utils.getLatLng(manageActivity.getMyLocation()));
            }
    }


    public Point getMapCenterPoint() {
        return new Point(mView.mRootContain.getWidth() / 2
                , mView.searchView.getBottom() + (mView.viewPagerFramelayout.getTop()
                + (int) mView.viewPagerFramelayout.getTranslationY()
                - mView.searchView.getBottom()) / 2 + Utils.dp2Px(8));
    }


    private void zoomToMyLoc() {
        if (manageActivity.getMyLocation() == null) {
            Utils.toast("定位失败 请检查你的网络设置");
            return;
        }
        MapStatus status = new MapStatus.Builder().target(Utils.getLatLng(manageActivity.getMyLocation()))
                .targetScreen(getMapCenterPoint()).rotate(0).overlook(0).zoom(14f).build();
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(status);
        baiduMap.animateMapStatus(mapStatusUpdate, 800);
    }

    /**
     * 改变地图控制器位置 适应当前屏幕
     */
    public void requestMapControlPosition() {
        int left = mView.viewPagerFramelayout.getLeft();
        int top = mView.searchView.getBottom();
        int bottom = mView.viewPagerFramelayout.getTop() + (int) mView.viewPagerFramelayout.getTranslationY();
        int right = mView.viewPagerFramelayout.getRight();
        Point scaleControlPoint = new Point(left, bottom - mapView.getScaleControlViewHeight() - Utils.dp2Px(6));
        Point ZoomControlsPoint = new Point(right - Utils.dp2Px(40), top - Utils.dp2Px(60) + (bottom - top) / 2);
        mapView.setZoomControlsPosition(ZoomControlsPoint);
        mapView.setScaleControlPosition(scaleControlPoint);
        handler.post(hideMapControlRunnable);
    }

    boolean locVisible = true;

    public void setMyLocVisible(boolean visible) {
        if (visible) {
            locVisible = true;
            requestLocMarket();
        } else {
            locVisible = false;
            if (myLocMarker != null) {
                myLocMarker.remove();
                myLocMarker = null;
            }
        }
    }

    private void getGlfLst() {
        gifList = new ArrayList<>();
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
    }
}
