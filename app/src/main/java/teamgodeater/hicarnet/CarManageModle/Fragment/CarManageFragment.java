package teamgodeater.hicarnet.CarManageModle.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rey.material.widget.TabPageIndicator;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import teamgodeater.hicarnet.CarManageModle.Adapter.CarPagerAdapter;
import teamgodeater.hicarnet.Data.UserCarInfoData;
import teamgodeater.hicarnet.Fragment.BaseFragment;
import teamgodeater.hicarnet.Help.UserDataHelp;
import teamgodeater.hicarnet.R;

/**
 * Created by G on 2016/6/9 0009.
 */

public class CarManageFragment extends BaseFragment {


    @Bind(R.id.tabPagerIndicator)
    TabPageIndicator tabPagerIndicator;
    @Bind(R.id.viewPager)
    ViewPager viewPager;

    @NonNull
    @Override
    protected SupportWindowsParams onCreateSupportViewParams() {
        SupportWindowsParams params = new SupportWindowsParams();
        params.rootLayoutId = R.layout.frgm_car_manager;
        return params;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        tSetDefaultView(true,"My Car");
        ButterKnife.bind(this, rootContain);


        UserCarInfoData userCarInfoData = new UserCarInfoData();
        userCarInfoData.setEngine_performance(1);
        userCarInfoData.setTransmission_performance(2);
        userCarInfoData.setLight_performance(1);
        UserDataHelp.userCarInfoDatas = new ArrayList<>();
        UserDataHelp.userCarInfoDatas.add(userCarInfoData);

        setViewPager();
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    @Override
    public boolean onInterceptBack() {
        destroySelfShowBefore();
        return true;
    }

    public void setViewPager() {
        viewPager.setAdapter(new CarPagerAdapter(getChildFragmentManager()));
        tabPagerIndicator.setViewPager(viewPager);
    }


    public static final int FROM_REGIST = 23;

    private int startType;

    public void setStartType(int type) {
        startType = type;
    }
}
