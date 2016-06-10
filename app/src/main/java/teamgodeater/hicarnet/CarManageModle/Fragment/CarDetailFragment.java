package teamgodeater.hicarnet.CarManageModle.Fragment;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import teamgodeater.hicarnet.Adapter.BaseItem2LineAdapter;
import teamgodeater.hicarnet.Data.BaseItem2LineData;
import teamgodeater.hicarnet.Data.UserCarInfoData;
import teamgodeater.hicarnet.Help.UserDataHelp;
import teamgodeater.hicarnet.Help.Utils;
import teamgodeater.hicarnet.R;
import teamgodeater.hicarnet.Widget.RippleBackGroundView;
import teamgodeater.hicarnet.Widget.RoundedImageView;

/**
 * Created by G on 2016/6/10 0010.
 */

public class CarDetailFragment extends Fragment {


    @Bind(R.id.brandLogo)
    RoundedImageView brandLogo;
    @Bind(R.id.brand)
    TextView brand;
    @Bind(R.id.license)
    TextView license;
    @Bind(R.id.toGas)
    RippleBackGroundView toGas;
    @Bind(R.id.toGasImage)
    ImageView toGasImage;
    @Bind(R.id.to4sShop)
    RippleBackGroundView to4sShop;
    @Bind(R.id.to4sShopImage)
    ImageView to4sShopImage;
    @Bind(R.id.toWeizhang)
    RippleBackGroundView toWeizhang;
    @Bind(R.id.toWeizhangImage)
    ImageView toWeizhangImage;
    @Bind(R.id.cardView1)
    CardView cardView1;
    @Bind(R.id.gas)
    TextView gas;
    @Bind(R.id.mileage)
    TextView mileage;
    @Bind(R.id.performanceRecycleView)
    RecyclerView performanceRecycleView;
    @Bind(R.id.cardView2)
    CardView cardView2;
    @Bind(R.id.otherRecyclerView)
    RecyclerView otherRecyclerView;
    @Bind(R.id.cardView3)
    CardView cardView3;
    private int index = -1;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frgm_car_manager_detail, container, false);
        ButterKnife.bind(this, rootView);
        setColorFilter();
        setRecycleViewAdapter();
        return rootView;
    }

    private void setRecycleViewAdapter() {
        setPerformanceRv();
        setOtherRv();
    }

    private void setOtherRv() {

    }

    private void setPerformanceRv() {
        UserCarInfoData CarData = UserDataHelp.userCarInfoDatas.get(index);
        List<BaseItem2LineData> datas = new ArrayList<>();
        BaseItem2LineData eData = new BaseItem2LineData();
        eData.title = "引擎状态";
        eData.tip = CarData.getEngine_performance() == 1 ? "良好" : "故障";
        datas.add(eData);
        BaseItem2LineData tData = new BaseItem2LineData();
        tData.title = "变速器状态";
        tData.tip = CarData.getTransmission_performance() == 1 ? "良好" : "故障";
        datas.add(tData);
        BaseItem2LineData lData = new BaseItem2LineData();
        lData.title = "车灯状态";
        lData.tip = CarData.getLight_performance() == 1 ? "良好" : "故障";
        datas.add(lData);
        performanceRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        performanceRecycleView.setHasFixedSize(true);
        performanceRecycleView.setAdapter(new BaseItem2LineAdapter(datas));
    }

    private void setColorFilter() {
        toGasImage.getDrawable().setColorFilter(Utils.getColorFromRes(R.color.colorWhite87), PorterDuff.Mode.SRC_IN);
        to4sShopImage.getDrawable().setColorFilter(Utils.getColorFromRes(R.color.colorWhite87), PorterDuff.Mode.SRC_IN);
        toWeizhangImage.getDrawable().setColorFilter(Utils.getColorFromRes(R.color.colorWhite87), PorterDuff.Mode.SRC_IN);
    }

    public void setIndex(int i) {
        index = i;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
