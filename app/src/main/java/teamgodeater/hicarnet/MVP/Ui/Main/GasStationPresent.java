package teamgodeater.hicarnet.MVP.Ui.Main;

import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import teamgodeater.hicarnet.Data.BaseItem2LineData;
import teamgodeater.hicarnet.Data.GasStationData;
import teamgodeater.hicarnet.Data.UserCarInfoData;
import teamgodeater.hicarnet.Help.UserDataHelp;
import teamgodeater.hicarnet.MapOverlay.MarketOverlay;
import teamgodeater.hicarnet.R;
import teamgodeater.hicarnet.Utils.Utils;

/**
 * Created by G on 2016/6/21 0021.
 */

public class GasStationPresent extends MainContractor.GasStationPresent {
    MarketOverlay gasStationOverlay;
    MainPresent mDspPresenter;
    List<LatLng> pointList;

    @Override
    public void addToPager() {

    }

    @Override
    public boolean interceptZoomToFit() {
        return gasStationOverlay != null && gasStationOverlay.zoomToSpan();
    }

    @Override
    public void showGasMarket() {
        gasStationOverlay.setVisible(true);
    }

    @Override
    public void hideGasMarket() {
        gasStationOverlay.setVisible(false);
    }

    @Override
    public void getData() {

    }

    @Override
    public void showAllGasStation() {

    }

    @Override
    public void showShortGasStation() {

    }

    @Override
    protected void onStart() {
        mDspPresenter = mView.mPresenter;
    }


    private List<BaseItem2LineData> getTopData(boolean show) {
        GasStationData gasstationData = UserDataHelp.gasstationData;
        List<BaseItem2LineData> dataList = new ArrayList<>();
        if (gasstationData == null
                || !gasstationData.getResultcode().equals("200")
                || gasstationData.getResult() == null
                || gasstationData.getResult().getData() == null
                || gasstationData.getResult().getData().size() == 0) {

            BaseItem2LineData data = new BaseItem2LineData();
            data.icoLeft = R.drawable.ic_local_gas;
            data.title = "读取加油站数据失败";
            data.tipRight = "重试";
            data.tag = "error";
            dataList.add(data);
        } else {
            BaseItem2LineData data = new BaseItem2LineData();
            data.title = "附近有" + gasstationData.getResult().getData().size() + "个加油站";
            UserCarInfoData defaultCarInfoData = UserDataHelp.getDefaultCarInfoData();
            if (defaultCarInfoData != null) {
                int petrol_gage = defaultCarInfoData.getPetrol_gage();
                data.tip = "剩余汽油量 " + petrol_gage + "%";
            }
            if (show) {
                data.icoLeft = R.drawable.ic_keyboard_arrow_left;
                data.tipRight = "收起";
                data.tag = "close";
                data.icoRight = R.drawable.ic_visibility;
                data.icoRightColor = Utils.getColorFromRes(R.color.colorAccent);
                data.hasDivider = true;
                dataList.add(data);
            } else {
                data.icoLeft = R.drawable.ic_local_gas;
                data.tipRight = "显示";
                data.icoRight = R.drawable.ic_visibility;
                data.tag = "open";
                data.hasDivider = true;
                dataList.add(data);
            }
        }
        return dataList;
    }

    private List<BaseItem2LineData> getDetailDatas() {
        GasStationData gasstationData = UserDataHelp.gasstationData;
        List<BaseItem2LineData> dataList = new ArrayList<>();
        pointList = new ArrayList<>();
        int index = 0;
        for (GasStationData.ResultBean.DataBean bean : gasstationData.getResult().getData()) {
            pointList.add(new LatLng(Double.parseDouble(bean.getLat()), Double.parseDouble(bean.getLon())));
            index++;
            BaseItem2LineData data = new BaseItem2LineData();
            data.icoLeft = R.drawable.ic_location;
            data.title = String.valueOf(index) + "." + bean.getName();
            data.tip = bean.getAddress();
            data.icoRight = R.drawable.ic_keyboard_arrow_right;
            dataList.add(data);
        }
        return dataList;
    }
}
