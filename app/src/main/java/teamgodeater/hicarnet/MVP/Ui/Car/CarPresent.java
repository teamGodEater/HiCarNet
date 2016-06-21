package teamgodeater.hicarnet.MVP.Ui.Car;

import android.graphics.Bitmap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import teamgodeater.hicarnet.Adapter.BaseItem2LineAdapter;
import teamgodeater.hicarnet.C;
import teamgodeater.hicarnet.Data.BaseItem2LineData;
import teamgodeater.hicarnet.Data.UserCarInfoData;
import teamgodeater.hicarnet.Help.RestClientHelp;
import teamgodeater.hicarnet.Help.UserDataHelp;
import teamgodeater.hicarnet.MVP.Base.BaseFragmentManage;
import teamgodeater.hicarnet.MVP.Ui.Car.Adapter.SmallRightRvAdapter;
import teamgodeater.hicarnet.R;
import teamgodeater.hicarnet.RestClient.RestClient;
import teamgodeater.hicarnet.Utils.Utils;

/**
 * Created by G on 2016/6/19 0019.
 */

public class CarPresent extends CarContractor.Present {
    @Override
    void getCarDatas() {
        if (Utils.getNetworkType() == C.NETTYPE_NONET) {
            mView.errorNoNet();
            return;
        }
        mView.onStartGetData();
        UserDataHelp.getUserCarInfoDatas(new UserDataHelp.OnDoneListener() {
            @Override
            public void onDone(int code) {
                if (code == -1) {
                    mView.errorUnKnow();
                    return;
                }
                if (code == 200 || code == C.HTTP_NOT_FOUND)
                    legalCarData();
                else if (code == C.HTTP_UNAUTHORIZED)
                    mView.errorNoLogin();
                else
                    mView.errorUnKnow();

            }
        });
    }

    ArrayList<UserCarInfoData> cars;

    @Override
    void addCarData(final UserCarInfoData carInfoData) {
        mView.setLoading("正在处理扫描结果...");
        RestClientHelp restClientHelp = new RestClientHelp();
        restClientHelp.setUserCarInfo(carInfoData, new RestClient.OnResultListener<String>() {
            @Override
            public void succeed(String bean) {
                getCarDatas();
            }

            @Override
            public void error(int code) {
                mView.hideLoading();
                RestClientHelp.generalErrorToast(code);
            }
        });
    }

    boolean isFristEntry = true;

    @Override
    void legalCarData() {
        List<UserCarInfoData> carDatas = UserDataHelp.userCarInfoDatas;
        if (carDatas == null) {
            if (isFristEntry) {
                isFristEntry = false;
                getCarDatas();
            } else
                mView.errorNoData();
            return;
        } else if (carDatas.size() == 0) {
            mView.errorNoCarInfo();
            return;
        } else if (carDatas.get(0).getLicense_num().equals("")) {
            mView.errorUnKnow();
            return;
        } else if (carDatas.size() == mView.linearLayout.getChildCount()) {
            mView.hideLoadingErrorTip();
        }

        int size = carDatas.size();
        View[] views = new View[size];

        for (int i = 0; i < size; i++) {
            UserCarInfoData data = carDatas.get(i);
            if (data == null || data.getLicense_num().equals(""))
                continue;
            View overView = LayoutInflater.from(mView.linearLayout.getContext())
                    .inflate(R.layout.item_car_overview, mView.linearLayout, false);
            RecyclerView rv = (RecyclerView) overView.findViewById(R.id.recyclerView);
            TextView gas = (TextView) overView.findViewById(R.id.gas);
            TextView mileage = (TextView) overView.findViewById(R.id.mileage);

            gas.setText(data.getPetrol_gage() + "");
            mileage.setText(data.getMileage() + "");

            List<BaseItem2LineData> items = new ArrayList<>();

            BaseItem2LineData d1 = new BaseItem2LineData();
            Bitmap signBitmap = data.getSignBitmap();
            if (signBitmap != null)
                d1.icoLeftBitmap = signBitmap;
            else
                d1.icoLeft = R.drawable.ic_directions_car;
            d1.title = data.getBrand();
            d1.tip = data.getLicense_num();
            d1.tipRight = "查看详情";
            d1.icoRight = R.drawable.ic_keyboard_arrow_right;
            d1.hasDivider = true;
            d1.tag = i;
            items.add(d1);
            BaseItem2LineData d2 = new BaseItem2LineData();
            d2.title = "引擎";
            d2.isClickAble = false;
            int engine_performance = data.getEngine_performance();
            if (engine_performance == 1) {
                d2.tipRight = "良好";
                d2.icoRight = R.drawable.ic_beenhere;
                d2.icoRightColor = Utils.getColorFromRes(R.color.color5);
            } else {
                d2.tipRight = "损坏";
                d2.icoRight = R.drawable.ic_beenhere;
                d2.icoRightColor = Utils.getColorFromRes(R.color.colorBlack20);
            }
            items.add(d2);
            BaseItem2LineData d3 = new BaseItem2LineData();
            d3.title = "变速器";
            d3.isClickAble = false;
            int transmission_performance = data.getTransmission_performance();
            if (transmission_performance == 1) {
                d3.tipRight = "良好";
                d3.icoRight = R.drawable.ic_beenhere;
                d3.icoRightColor = Utils.getColorFromRes(R.color.color5);
            } else {
                d3.tipRight = "损坏";
                d3.icoRight = R.drawable.ic_beenhere;
                d3.icoRightColor = Utils.getColorFromRes(R.color.colorBlack20);
            }
            items.add(d3);
            BaseItem2LineData d4 = new BaseItem2LineData();
            d4.title = "车灯";
            d4.isClickAble = false;
            int light_performance = data.getLight_performance();
            if (light_performance == 1) {
                d4.tipRight = "良好";
                d4.icoRight = R.drawable.ic_beenhere;
                d4.icoRightColor = Utils.getColorFromRes(R.color.color5);
            } else {
                d4.tipRight = "损坏";
                d4.icoRight = R.drawable.ic_beenhere;
                d4.icoRightColor = Utils.getColorFromRes(R.color.colorBlack20);
            }
            items.add(d4);

            rv.setLayoutManager(new LinearLayoutManager(mView.getContext()));
            rv.setHasFixedSize(true);
            SmallRightRvAdapter adapter = new SmallRightRvAdapter(items);
            final int finalI = i;
            adapter.setOnClickListener(new BaseItem2LineAdapter.OnItemClickListener() {
                @Override
                public void onClick(BaseItem2LineData data, int position) {
                    // TODO: 2016/6/19 0019 切换到详情
                    mView.hideSelf(0L);
                    int index = (int) data.tag;
                    View childAt = mView.linearLayout.getChildAt(index);
                    float y = childAt.getY() - mView.myScrollView.getScrollY();
                    BaseFragmentManage.switchFragment(new CarDetailFragment(index, y));
                }
            });
            rv.setAdapter(adapter);

            int margin16 = Utils.dp2Px(16);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (i == 0 && size == 1)
                layoutParams.setMargins(margin16, margin16, margin16, margin16);
            else if (i == 0)
                layoutParams.setMargins(margin16, margin16, margin16, margin16 / 2);
            else if (i == size - 1)
                layoutParams.setMargins(margin16, margin16 / 2, margin16, margin16);
            else
                layoutParams.setMargins(margin16, margin16 / 2, margin16, margin16 / 2);

            overView.setLayoutParams(layoutParams);
            views[i] = overView;
        }
        mView.setView(views);
    }

    @Override
    protected void onStart() {

    }
}
