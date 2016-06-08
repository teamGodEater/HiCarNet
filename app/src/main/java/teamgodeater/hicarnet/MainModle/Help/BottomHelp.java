package teamgodeater.hicarnet.MainModle.Help;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.mapapi.search.route.DrivingRouteLine;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import teamgodeater.hicarnet.Activity.ManageActivity;
import teamgodeater.hicarnet.Adapter.BaseItem2LineAdapter;
import teamgodeater.hicarnet.Data.BaseItem2LineData;
import teamgodeater.hicarnet.Data.UserCarInfoData;
import teamgodeater.hicarnet.Help.UserDataHelp;
import teamgodeater.hicarnet.Help.Utils;
import teamgodeater.hicarnet.R;

/**
 * Created by G on 2016/5/29 0029.
 */

public class BottomHelp implements ViewPager.OnPageChangeListener {

    private final ManageActivity manageActivity;
    private final ViewPager viewPager;
    private final View select1;
    private final View select2;
    private final mPagerAdapter adapter;
    private List<View> pagerList;

    public BottomHelp(ManageActivity m, ViewPager b, View s1, View s2) {
        manageActivity = m;
        viewPager = b;
        select1 = s1;
        select2 = s2;

        initView();

        adapter = new mPagerAdapter();
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
        changePagerSelect(0);
    }


    //------------------------------pagerStatusChangeListener---------------------------------------

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        changePagerSelect(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    //-------------------------------------------pagerAdapterClass----------------------------------
    class mPagerAdapter extends PagerAdapter {

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return pagerList.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(pagerList.get(position));
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(pagerList.get(position), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);//添加页卡
            return pagerList.get(position);
        }

    }

    //------------------------------------method----------------------------------------------------
    //------------------public
    public void setCurrentItem(int position) {
        if (position == 0) {
            viewPager.setCurrentItem(0, true);
        } else {
            viewPager.setCurrentItem(1, true);
        }
    }

    public void setRvAdapter(int position, BaseItem2LineAdapter adapter) {
        RecyclerView view = (RecyclerView) pagerList.get(position);
        view.setAdapter(adapter);
    }

    public RecyclerView.Adapter getRvAdapter(int position) {
        RecyclerView view = (RecyclerView) pagerList.get(position);
        return view.getAdapter();
    }

    //------------------private
    private void changePagerSelect(int position) {
        if (position == 0) {
            select1.setAlpha(0.54f);
            select2.setAlpha(0.34f);
        } else {
            select1.setAlpha(0.34f);
            select2.setAlpha(0.53f);
        }
    }

    private void initView() {
        pagerList = new ArrayList<>();

        RecyclerView view1 = new RecyclerView(Utils.getContext());
        RecyclerView view2 = new RecyclerView(Utils.getContext());

        view1.setHasFixedSize(true);
        view2.setHasFixedSize(true);

        view1.setLayoutManager(new LinearLayoutManager(Utils.getContext()));
        view2.setLayoutManager(new LinearLayoutManager(Utils.getContext()));

        pagerList.add(view1);
        pagerList.add(view2);
    }

    //-----------------firstPagerData
    public List<BaseItem2LineData> getFBPCarInfoData() {
        List<BaseItem2LineData> dataList = new ArrayList<>();
        UserCarInfoData userCarInfoData = UserDataHelp.getDefaultCarInfoData();
        if (userCarInfoData == null) {
            // 获取数据失败
            BaseItem2LineData data = new BaseItem2LineData();
            data.icoLeft = R.drawable.ic_directions_car;
            data.title = "获取车辆数据失败";
            data.tip = "轻松查看车况 和 违章";
            data.tipRight = "刷新";
            data.icoRight = R.drawable.ic_keyboard_arrow_right;
            data.isDivider = true;
            data.tag = "获取车辆数据失败";
            dataList.add(data);
        } else if (userCarInfoData.getLicense_num().equals("")) {
            //没有设置车辆信息
            BaseItem2LineData data = new BaseItem2LineData();
            data.icoLeft = R.drawable.ic_directions_car;
            data.title = "没有添加车辆";
            data.tip = "轻松查看车况 和 违章";
            data.tipRight = "去添加";
            data.icoRight = R.drawable.ic_keyboard_arrow_right;
            data.isDivider = true;
            data.tag = "没有设置车辆";
            dataList.add(data);
        } else {
            //成功获取
            BaseItem2LineData data = new BaseItem2LineData();
            data.icoLeftBitmap = userCarInfoData.getSignBitmap();
            data.title = userCarInfoData.getBrand();
            data.tip = userCarInfoData.getLicense_num();
            data.icoRight = R.drawable.ic_keyboard_arrow_right;
            data.tipRight = "管理车辆";
            data.icoLeft = R.drawable.ic_directions_car;
            data.tag = "管理车辆";
            dataList.add(data);
            //其他违章什么的XXX
            BaseItem2LineData weizhang = new BaseItem2LineData();
            weizhang.title = "无违章记录";
            weizhang.tip = "继续保持下去哦~";
            weizhang.icoRight = R.drawable.ic_keyboard_arrow_right;
            weizhang.tipRight = "违章查询";
            weizhang.tag = "违章查询";
            dataList.add(weizhang);
            //性能
            BaseItem2LineData performance = new BaseItem2LineData();
            performance.isDivider = true;
            if (userCarInfoData.getEngine_performance() == 2 || userCarInfoData.getTransmission_performance() == 2
                    || userCarInfoData.getLight_performance() == 2) {
                performance.title = "车辆出现故障";
                String guzhang = "";
                if (userCarInfoData.getEngine_performance() == 2) {
                    guzhang += "发动机 ";
                }
                if (userCarInfoData.getLight_performance() == 2) {
                    guzhang += "车灯 ";
                }
                if (userCarInfoData.getTransmission_performance() == 2) {
                    guzhang += "变速器 ";
                }
                performance.tip = guzhang + "出现异常";
                performance.tipRight = "附近的4S店";
                performance.icoRight = R.drawable.ic_keyboard_arrow_right;
                performance.tag = "附近的4S店";
            } else {
                performance.title = "没有异常";
                performance.tip = "发动机 车灯 变速器 正常";
                performance.isClickAble = false;
            }
            dataList.add(performance);
        }

        return dataList;
    }

    public BaseItem2LineData getFBPTrafficData() {
        BaseItem2LineData data = null;

        if (UserDataHelp.userTrafficLine == null && UserDataHelp.userPointData == null) {
            //获取信息失败
            data = new BaseItem2LineData();
            data.icoLeft = R.drawable.ic_traffic;
            data.title = "获取路况数据失败";
            data.tip = "帮助你摆脱拥堵路线";
            data.tipRight = "刷新";
            data.icoRight = R.drawable.ic_keyboard_arrow_right;
            data.isDivider = true;
            data.tag = "获取路况数据失败";

        } else if (UserDataHelp.userPointData.getUser_id() == 0) {
            //没有设置
            data = new BaseItem2LineData();
            data.icoLeft = R.drawable.ic_traffic;
            data.title = "让我更懂你";
            data.tip = "设置常用路线 避免拥堵";
            data.tipRight = "去设置";
            data.icoRight = R.drawable.ic_keyboard_arrow_right;
            data.isDivider = true;
            data.tag = "没有设置路况";
        } else {
            //成功获取
            int maxTraffic = 0;
            for (DrivingRouteLine line : UserDataHelp.userTrafficLine.getRouteLines()) {
                int trafficTime = RouteHelp.getTrafficDis(line);
                if (trafficTime > maxTraffic) {
                    maxTraffic = trafficTime;
                }
            }

            //如果没有拥堵 不显示
            if (maxTraffic > 10) {
                data = new BaseItem2LineData();
                data.icoLeft = R.drawable.ic_traffic;
                data.title = "家 - 公司 出现拥堵";
                String dis = "";
                if (maxTraffic < 1000) {
                    dis = maxTraffic + " m";
                } else {
                    DecimalFormat format = new DecimalFormat("#.0");
                    dis = format.format((maxTraffic / 1000f)) + " Km";
                }
                data.tip = "请尽量避免,拥堵 " + dis;
                data.icoRight = R.drawable.ic_keyboard_arrow_right;
                data.tipRight = "规划新路线";
                data.isDivider = true;
                data.tag = "规划新路线";
            }
        }

        return data;
    }

}
