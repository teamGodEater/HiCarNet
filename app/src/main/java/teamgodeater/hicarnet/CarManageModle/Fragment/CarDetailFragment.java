package teamgodeater.hicarnet.CarManageModle.Fragment;

import android.animation.ValueAnimator;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.rey.material.widget.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import teamgodeater.hicarnet.Adapter.BaseItem2LineAdapter;
import teamgodeater.hicarnet.CarManageModle.Adapter.SmallRightRvAdapter;
import teamgodeater.hicarnet.Data.BaseItem2LineData;
import teamgodeater.hicarnet.Data.UserCarInfoData;
import teamgodeater.hicarnet.Fragment.BaseFragment;
import teamgodeater.hicarnet.Help.RestClientHelp;
import teamgodeater.hicarnet.Help.UserDataHelp;
import teamgodeater.hicarnet.Help.Utils;
import teamgodeater.hicarnet.R;
import teamgodeater.hicarnet.RestClient.RestClient;


/**
 * Created by G on 2016/6/13 0013.
 */

public class CarDetailFragment extends BaseFragment {

    UserCarInfoData carInfoData;

    @Bind(R.id.gas)
    TextView gas;
    @Bind(R.id.mileage)
    TextView mileage;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.gasTip)
    TextView gasTip;
    @Bind(R.id.mileageTip)
    TextView mileageTip;

    @Bind(R.id.headContain)
    View headContain;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    private float oldY;

    @NonNull
    @Override
    protected SupportWindowsParams onCreateSupportViewParams() {
        SupportWindowsParams supportWindowsParams = new SupportWindowsParams();
        supportWindowsParams.rootLayoutId = R.layout.frgm_car_manager_detail;
        return supportWindowsParams;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootContain);
        tSetDefaultView(true, carInfoData.getBrand());
        setView();
        return rootView;
    }


    long lastFabClickTime = 0;

    private void setFab() {
        fab.getIcon().setColorFilter(Utils.getColorFromRes(R.color.colorWhite), PorterDuff.Mode.SRC_IN);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long currentTimeMillis = System.currentTimeMillis();
                if (currentTimeMillis - lastFabClickTime < 700) {
                    fab.setClickable(false);
                    RestClientHelp restClientHelp = new RestClientHelp();
                    restClientHelp.delUserCarInfo(carInfoData.getLicense_num(), new RestClient.OnResultListener<String>() {
                        @Override
                        public void succeed(String bean) {
                            destroySelfShowBefore(280L);
                            UserDataHelp.userCarInfoDatas.remove(carInfoData);
                            parentView.animate().scaleX(0.01f).scaleY(0.01f)
                                    .setInterpolator(new AccelerateInterpolator()).start();
                        }

                        @Override
                        public void error(int code) {
                            fab.setClickable(true);
                            RestClientHelp.generalErrorToast(code);
                        }
                    });
                } else {
                    lastFabClickTime = currentTimeMillis;
                    Toast.makeText(getActivity(), "快速双击 就能删除了\nTip : 此操作不可逆!请谨慎操作", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void setView() {
        gas.setText(carInfoData.getPetrol_gage() + "");
        mileage.setText(carInfoData.getMileage() + "");
        setRv();
        setFab();
    }

    private void setRv() {

        List<BaseItem2LineData> items = new ArrayList<>();

        BaseItem2LineData d1 = new BaseItem2LineData();
        d1.isClickAble = false;
        d1.icoLeftBitmap = carInfoData.getSignBitmap();
        d1.title = carInfoData.getBrand();
        d1.tip = carInfoData.getLicense_num();
        d1.hasDivider = true;
        items.add(d1);
        BaseItem2LineData d2 = new BaseItem2LineData();
        d2.title = "引擎";
        d2.isClickAble = false;
        int engine_performance = carInfoData.getEngine_performance();
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
        int transmission_performance = carInfoData.getTransmission_performance();
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
        d4.hasDivider = true;
        d4.title = "车灯";
        d4.isClickAble = false;
        int light_performance = carInfoData.getLight_performance();
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

        if (carInfoData.getLight_performance() != 1
                || carInfoData.getTransmission_performance() != 1
                || carInfoData.getEngine_performance() != 1
                || carInfoData.getMileage() > 1500) {

            BaseItem2LineData t1 = new BaseItem2LineData();
            t1.hasDivider = true;
            t1.icoLeft = R.drawable.ic_build;
            t1.title = "出现损坏";
            t1.tip = "安全隐患";
            t1.tipRight = "附近的4s店";
            t1.icoRight = R.drawable.ic_keyboard_arrow_right;
            t1.tag = "fix";
            items.add(t1);
        }

        if (carInfoData.getPetrol_gage() < 30) {
            BaseItem2LineData t2 = new BaseItem2LineData();
            t2.hasDivider = true;
            t2.icoLeft = R.drawable.ic_local_gas;
            t2.title = "油量低";
            t2.tip = "主人!我跑不了多远了";
            t2.tipRight = "附近的加油站";
            t2.icoRight = R.drawable.ic_keyboard_arrow_right;
            t2.tag = "low Gas";
            items.add(t2);
        }

        BaseItem2LineData d5 = new BaseItem2LineData();
        d5.title = "车身级别";
        d5.tipRight = carInfoData.getLevel();
        d5.isClickAble = false;
        items.add(d5);

        BaseItem2LineData d6 = new BaseItem2LineData();
        d6.title = "型号";
        d6.tipRight = carInfoData.getModel();
        d6.isClickAble = false;
        items.add(d6);

        BaseItem2LineData d7 = new BaseItem2LineData();
        d7.title = "引擎号";
        d7.tipRight = carInfoData.getEngine_num();
        d7.isClickAble = false;
        items.add(d7);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        SmallRightRvAdapter adapter = new SmallRightRvAdapter(items);
        adapter.setOnClickListener(new BaseItem2LineAdapter.OnItemClickListener() {
            @Override
            public void onClick(BaseItem2LineData data, int position) {
                if (data.tag instanceof String) {
                    String tag = (String) data.tag;
                    if (tag.equals("fix")) {
                        onFixClick();
                    } else if (tag.equals("low Gas")) {
                        onLowGasClick();
                    }
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void onLowGasClick() {

    }

    private void onFixClick() {

    }

    public void setCarIndex(int index) {
        carInfoData = UserDataHelp.userCarInfoDatas.get(index);
    }

    public void setCarPosititon(float y) {
        oldY = y;
    }

    @Override
    protected void onOnceGlobalLayoutListen() {
        float nowY = recyclerView.getY();
        final float differentY = nowY - oldY;
        int nowH = recyclerView.getHeight();
        final int dp216 = Utils.dp2Px(216);
        final int differentH = nowH - dp216 - Utils.dp2Px(16);

        rootContain.setAlpha(0);
        rootContain.animate().alpha(1f).start();

        recyclerView.setTranslationY(-differentY);
        recyclerView.getLayoutParams().height = dp216;
        recyclerView.requestLayout();
        recyclerView.animate().translationY(0f).setDuration(300L).setInterpolator(new AccelerateInterpolator()).start();
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 1f);
        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                recyclerView.getLayoutParams().height = (int) (dp216 + differentH * value);
                recyclerView.requestLayout();
            }
        });

        valueAnimator.setStartDelay(250L);
        valueAnimator.setDuration(200);
        valueAnimator.start();

        gas.setAlpha(0f);
        gasTip.setAlpha(0f);
        mileage.setAlpha(0f);
        mileageTip.setAlpha(0f);

        headContain.setTranslationY(-headContain.getHeight());
        headContain.animate().translationY(0f).setInterpolator(new AccelerateInterpolator()).setStartDelay(400).setDuration(200).start();

        gas.animate().alpha(1f).setDuration(200L).setStartDelay(500L).start();
        gasTip.animate().alpha(1f).setDuration(200L).setStartDelay(500L).start();
        mileage.animate().alpha(1f).setDuration(200L).setStartDelay(500L).start();
        mileageTip.animate().alpha(1f).setDuration(200L).setStartDelay(500L).start();

        fab.setScaleX(0.001f);
        fab.setScaleY(0.001f);
        fab.animate().scaleX(1f).scaleY(1f).setInterpolator(new AccelerateInterpolator()).setStartDelay(700L).start();
    }

    private void finish() {
        destroySelfShowBefore(380L);

        toolbarContain.setAlpha(0f);
        gas.setAlpha(0f);
        gasTip.setAlpha(0f);
        mileage.setAlpha(0f);
        mileageTip.setAlpha(0f);
        fab.setAlpha(0f);
        headContain.setAlpha(0f);

        parentView.animate().alpha(0f).setStartDelay(220L).setInterpolator(new AccelerateInterpolator()).start();

        int dp216 = Utils.dp2Px(216);

        final int nowH = recyclerView.getHeight();
        float nowY = recyclerView.getY();

        final float differentY = nowY - oldY;
        final int differentH = nowH - dp216;


        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 1f);
        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                recyclerView.getLayoutParams().height = (int) (nowH - differentH * value);
                recyclerView.requestLayout();
            }
        });
        valueAnimator.start();

        recyclerView.animate().translationY(-differentY).setDuration(400L).setInterpolator(new AccelerateInterpolator()).start();
    }

    @Override
    public boolean onInterceptBack() {
        finish();
        return true;
    }

    @Override
    public String getType() {
        return "car";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
