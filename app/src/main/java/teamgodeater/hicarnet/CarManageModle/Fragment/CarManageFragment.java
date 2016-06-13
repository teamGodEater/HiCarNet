package teamgodeater.hicarnet.CarManageModle.Fragment;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.rey.material.widget.FloatingActionButton;
import com.victor.loading.rotate.RotateLoading;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.hugo.android.scanner.CaptureActivity;
import teamgodeater.hicarnet.Adapter.BaseItem2LineAdapter;
import teamgodeater.hicarnet.CarManageModle.Adapter.SmallRightRvAdapter;
import teamgodeater.hicarnet.Data.BaseItem2LineData;
import teamgodeater.hicarnet.Data.UserCarInfoData;
import teamgodeater.hicarnet.Fragment.BaseFragment;
import teamgodeater.hicarnet.Help.RestClientHelp;
import teamgodeater.hicarnet.Help.UserDataHelp;
import teamgodeater.hicarnet.Help.Utils;
import teamgodeater.hicarnet.LoginModle.Fragment.LoginFragment;
import teamgodeater.hicarnet.R;
import teamgodeater.hicarnet.RestClient.RestClient;
import teamgodeater.hicarnet.Widget.RippleBackGroundView;

import static cn.hugo.android.scanner.CaptureActivity.DECODE_OK;

/**
 * Created by G on 2016/6/9 0009.
 */

public class CarManageFragment extends BaseFragment {

    @Bind(R.id.linearLayout)
    LinearLayout linearLayout;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.otherTip)
    TextView otherTip;
    @Bind(R.id.otherButton)
    RippleBackGroundView otherButton;
    @Bind(R.id.bg)
    View rotateBackGround;
    @Bind(R.id.rotateLoading)
    RotateLoading rotateLoading;

    @NonNull
    @Override
    protected SupportWindowsParams onCreateSupportViewParams() {
        SupportWindowsParams params = new SupportWindowsParams();
        params.rootLayoutId = R.layout.frgm_car_manager;
        return params;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        tSetDefaultView(true, "车辆管理");
        ButterKnife.bind(this, rootContain);
        setColorFilter();
        setListener();
        refresh();
        return rootView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden == false) {
            refresh();
        }
    }

    private void setListener() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCar();
            }
        });
    }

    public static final int FORGETCARINFO = 24245;

    private void addCar() {
        if (RestClientHelp.Session.equals("")) {
            Utils.toast("此操作需要登录才能完成");
            return;
        }
        Intent intent = new Intent(manageActivity, CaptureActivity.class);
        startActivityForResult(intent, FORGETCARINFO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == DECODE_OK && requestCode == FORGETCARINFO) {
            String result = data.getExtras().getString("result");
            Gson gson = new Gson();
            UserCarInfoData userCarInfoData = null;
            try {
                userCarInfoData = gson.fromJson(result, UserCarInfoData.class);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
            if (userCarInfoData != null && !userCarInfoData.getLicense_num().equals("")) {
                final RestClientHelp restClientHelp = new RestClientHelp();
                restClientHelp.setUserCarInfo(userCarInfoData, new RestClient.OnResultListener<String>() {
                    @Override
                    public void succeed(String bean) {
                        setRotateLoading(true);
                        UserDataHelp.getUserCarInfoDatas(new UserDataHelp.OnDoneListener() {
                            @Override
                            public void onDone(int code) {
                                setRotateLoading(false);
                                setView();
                            }
                        });
                    }

                    @Override
                    public void error(int code) {
                        restClientHelp.generalErrorToast(code);
                    }
                });
            } else {
                Toast.makeText(getActivity(), "没有扫描到有效的的车辆数据二维码,请检查后重试", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setColorFilter() {
        fab.getIcon().setColorFilter(Utils.getColorFromRes(R.color.colorWhite), PorterDuff.Mode.SRC_IN);
    }

    private void setView() {
        otherButton.setVisibility(View.GONE);
        otherTip.setVisibility(View.GONE);
        rotateBackGround.setVisibility(View.GONE);



        if (Utils.getNetworkType() == Utils.NETTYPE_NONET) {
            linearLayout.removeAllViews();
            otherTip.setText("无法连接到网络");
            otherTip.setVisibility(View.VISIBLE);
            otherButton.setText("重试");
            otherButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refresh();
                }
            });
            otherButton.setVisibility(View.VISIBLE);
            return;
        }

        List<UserCarInfoData> datas = UserDataHelp.userCarInfoDatas;

        if (RestClientHelp.Session.equals("") ) {
            linearLayout.removeAllViews();
            otherTip.setText("你还没有登录哦 请先登录");
            otherTip.setVisibility(View.VISIBLE);
            otherButton.setText("登录");
            otherButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    manageActivity.switchFragment(new LoginFragment());
                    hideSelfDelay(500L);
                }
            });
            otherButton.setVisibility(View.VISIBLE);
            return;
        }

        if (datas == null || datas.size() == 0) {
            linearLayout.removeAllViews();
            otherTip.setText("你还没有添加车辆数据\n添加后才能看到哦");
            otherTip.setVisibility(View.VISIBLE);
            otherButton.setText("刷新");
            otherButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refresh();
                }
            });
            otherButton.setVisibility(View.VISIBLE);
            return;
        }

        int size = datas.size();
        int childCount = linearLayout.getChildCount();

        //如果已经添加了view 而且和当前的carInfoDatas一样就不刷新
        if (childCount != 0 && childCount == size)
            return;

        linearLayout.removeAllViews();

        for (int i = 0; i < size; i++) {
            UserCarInfoData data = datas.get(i);
            if (data == null || data.getLicense_num().equals(""))
                continue;
            View overView = LayoutInflater.from(rootContain.getContext())
                    .inflate(R.layout.item_car_overview, rootContain, false);
            RecyclerView rv = (RecyclerView) overView.findViewById(R.id.recyclerView);
            TextView gas = (TextView) overView.findViewById(R.id.gas);
            TextView mileage = (TextView) overView.findViewById(R.id.mileage);

            gas.setText(data.getPetrol_gage() + "");
            mileage.setText(data.getMileage() + "");

            List<BaseItem2LineData> items = new ArrayList<>();

            BaseItem2LineData d1 = new BaseItem2LineData();
            d1.icoLeftBitmap = data.getSignBitmap();
            d1.title = data.getBrand();
            d1.tip = data.getLicense_num();
            d1.tipRight = "查看详情";
            d1.icoRight = R.drawable.ic_keyboard_arrow_right;
            d1.isDivider = true;
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

            rv.setLayoutManager(new LinearLayoutManager(getActivity()));
            rv.setHasFixedSize(true);
            SmallRightRvAdapter adapter = new SmallRightRvAdapter(items);
            final int finalI = i;
            adapter.setOnClickListener(new BaseItem2LineAdapter.OnItemClickListener() {
                @Override
                public void onClick(BaseItem2LineData data, int position) {
                    onRVItemClick(finalI);
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

            linearLayout.addView(overView, layoutParams);

            overView.setClickable(true);
        }

    }

    private void refresh() {
        //没有网络或者 carInfoData没问题直接返回
        if (UserDataHelp.getUserCarInfoStatus() == UserDataHelp.OK || Utils.getNetworkType() == Utils.NETTYPE_NONET) {
            setView();
            return;
        }

        setRotateLoading(true);
        UserDataHelp.getUserCarInfoDatas(new UserDataHelp.OnDoneListener() {
            @Override
            public void onDone(int code) {
                setView();
                setRotateLoading(false);
            }
        });
    }

    public void onRVItemClick(int position) {
        CarDetailFragment to = new CarDetailFragment();
        to.setCarIndex(position);
        View c = linearLayout.getChildAt(position);
        to.setCarPosititon(c.getY());
        manageActivity.switchFragment(to);
        hideSelf();
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

    private void setRotateLoading(boolean start) {
        if (start) {
            rotateLoading.start();
            rotateBackGround.setVisibility(View.VISIBLE);
        } else {
            rotateLoading.stop();
            rotateBackGround.setVisibility(View.GONE);
        }
    }

}
