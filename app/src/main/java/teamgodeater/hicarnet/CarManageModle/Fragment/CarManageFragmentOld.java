package teamgodeater.hicarnet.CarManageModle.Fragment;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
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
import teamgodeater.hicarnet.Activity.ManageActivity;
import teamgodeater.hicarnet.Adapter.BaseItem2LineAdapter;
import teamgodeater.hicarnet.CarManageModle.Adapter.SmallRightRvAdapter;
import teamgodeater.hicarnet.Data.BaseItem2LineData;
import teamgodeater.hicarnet.Data.UserCarInfoData;
import teamgodeater.hicarnet.Fragment.OldBaseFragment;
import teamgodeater.hicarnet.Help.RestClientHelp;
import teamgodeater.hicarnet.Help.UserDataHelp;
import teamgodeater.hicarnet.Help.Utils;
import teamgodeater.hicarnet.LoginModle.Fragment.LoginFragmentOld;
import teamgodeater.hicarnet.R;
import teamgodeater.hicarnet.RestClient.RestClient;
import teamgodeater.hicarnet.Widget.RippleBackGroundView;

import static cn.hugo.android.scanner.CaptureActivity.DECODE_OK;
import static teamgodeater.hicarnet.Help.UserDataHelp.userCarInfoDatas;

/**
 * Created by G on 2016/6/9 0009.
 */

public class CarManageFragmentOld extends OldBaseFragment {


    @Bind(R.id.linearLayout)
    LinearLayout linearLayout;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.errorTip)
    TextView errorTip;
    @Bind(R.id.errorButton)
    RippleBackGroundView errorButton;
    @Bind(R.id.rotateLoading)
    RotateLoading rotateLoading;
    @Bind(R.id.loadingTip)
    TextView loadingTip;
    private List<UserCarInfoData> carDatas;

    public static CarManageFragmentOld getInstans() {
        List<Fragment> fragments = ManageActivity.manageActivity.getSupportFragmentManager().getFragments();
        for (Fragment f : fragments) {
            if (f instanceof CarManageFragmentOld) {
                return (CarManageFragmentOld) f;
            }
        }
        return new CarManageFragmentOld();
    }

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
        setView();
        return rootView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            setView();
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
                        setRotateLoading(true, "正在处理扫描结果");
                        UserDataHelp.getUserCarInfoDatas(new UserDataHelp.OnDoneListener() {
                            @Override
                            public void onDone(int code) {
                                setRotateLoading(false, null);
                                setView();
                            }
                        });
                    }

                    @Override
                    public void error(int code) {
                        setView();
                        restClientHelp.generalErrorToast(code);
                    }
                });
            } else {
                Toast.makeText(getActivity(), "没有扫描到有效的的车辆数据二维码,请检查后重试", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onOnceGlobalLayoutListen() {
        fab.setScaleX(0.01f);
        fab.setScaleY(0.01f);
        parentView.setTranslationX(parentView.getWidth());
        parentView.animate().translationX(0).setInterpolator(new AccelerateInterpolator()).start();
        fab.animate().scaleX(1f).scaleY(1f).setStartDelay(300).setInterpolator(new AccelerateInterpolator()).start();
    }

    private void setColorFilter() {
        fab.getIcon().setColorFilter(Utils.getColorFromRes(R.color.colorWhite), PorterDuff.Mode.SRC_IN);
    }

    boolean isDestroy = false;

    private void setView() {
        goneErrorTip(true);
        setRotateLoading(false, null);

        if (isDestroy
                || noChange()
                || noNet()
                || noLogin()
                || illegalData())
            return;

        carDatas = UserDataHelp.userCarInfoDatas;

        int size = carDatas.size();
        linearLayout.removeAllViews();

        for (int i = 0; i < size; i++) {
            UserCarInfoData data = carDatas.get(i);
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
            d1.hasDivider = true;
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

    private boolean illegalData() {
        if (UserDataHelp.userCarInfoDatas == null) {
            errorNoData();
            return true;
        } else if (UserDataHelp.userCarInfoDatas.size() == 0) {
            errorNoCarInfo();
            return true;
        }
        return false;
    }

    private boolean noLogin() {
        if (RestClientHelp.Session.equals(""))
            return true;
        return false;
    }

    private boolean noNet() {
        if (Utils.getNetworkType() == Utils.NETTYPE_NONET) {
            errorNoNet();
            return true;
        }
        return false;
    }

    private boolean noChange() {
        if (carDatas == null || !carDatas.equals(userCarInfoDatas))
            return false;
        return true;
    }

    private void errorNoData() {
        setErrorTip("读取数据失败", "刷新", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRotateLoading(true, "正在加载 数据...");
                UserDataHelp.getUserCarInfoDatas(new UserDataHelp.OnDoneListener() {
                    @Override
                    public void onDone(int code) {
                        setRotateLoading(false, null);
                        if (code == 200) {
                            setView();
                            return;
                        }
                        RestClientHelp.generalErrorToast(code);
                        if (code == RestClientHelp.HTTP_UNAUTHORIZED) {
                            errorNoLogin();
                        } else if (code == RestClientHelp.HTTP_NOT_FOUND) {
                            errorNoCarInfo();
                        } else {
                            errorNoData();
                        }
                    }
                });
            }
        });
    }

    private void errorNoCarInfo() {
        //没有订单
        setErrorTip("你还没有添加车辆!", "刷新", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRotateLoading(true, "正在加载 数据...");
                UserDataHelp.getUserCarInfoDatas(new UserDataHelp.OnDoneListener() {
                    @Override
                    public void onDone(int code) {
                        setRotateLoading(false, null);
                        if (code == 200) {
                            setView();
                            return;
                        }
                        if (code == RestClientHelp.HTTP_NOT_FOUND) {
                            errorNoCarInfo();
                        }
                        RestClientHelp.generalErrorToast(code);
                    }
                });
            }
        });
    }

    private void errorNoLogin() {
        setErrorTip("需要登陆后才能查看 请先登录!", "登陆", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageActivity.switchFragment(new LoginFragmentOld());
                hideSelf(280L);
            }
        });
    }

    private void errorNoNet() {
        Utils.toast("请检查网络设置");
        setErrorTip("无法连接到网络", "重试", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setView();
            }
        });

    }

    public void setErrorTip(String tip, String tipButton, View.OnClickListener listener) {
        goneErrorTip(false);
        errorTip.setText(tip);
        errorButton.setText(tipButton);
        errorButton.setOnClickListener(listener);
    }

    public void goneErrorTip(boolean gone) {
        if (gone) {
            errorButton.setVisibility(View.GONE);
            errorTip.setVisibility(View.GONE);
        } else {
            errorButton.setVisibility(View.VISIBLE);
            errorTip.setVisibility(View.VISIBLE);
        }
    }

    public void setRotateLoading(boolean start, String str) {
        if (start) {
            goneErrorTip(true);
            rotateLoading.start();
            loadingTip.setText(str);
            loadingTip.setVisibility(View.VISIBLE);
        } else {
            if (rotateLoading == null) {
                return;
            }
            rotateLoading.stop();
            loadingTip.setVisibility(View.GONE);
        }
    }

    public void onRVItemClick(int position) {
        CarDetailFragmentOld to = new CarDetailFragmentOld();
        to.setCarIndex(position);
        View c = linearLayout.getChildAt(position);
        to.setCarPosititon(c.getY());
        manageActivity.switchFragment(to);
        hideSelf(280L);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isDestroy = true;
        ButterKnife.unbind(this);
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
    protected void onToolBarClick(View v) {
        String tag = (String) v.getTag();
        if (tag.equals("back"))
            finish();
    }

    private void finish() {
        destroySelfShowBefore(280L);
        parentView.animate().translationX(parentView.getWidth()).setInterpolator(new AccelerateInterpolator()).start();
    }


}
