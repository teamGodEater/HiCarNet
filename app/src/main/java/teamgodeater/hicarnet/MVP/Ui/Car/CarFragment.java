package teamgodeater.hicarnet.MVP.Ui.Car;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.rey.material.widget.FloatingActionButton;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.hugo.android.scanner.CaptureActivity;
import teamgodeater.hicarnet.C;
import teamgodeater.hicarnet.Data.UserCarInfoData;
import teamgodeater.hicarnet.Layout.ErrorViewHelp;
import teamgodeater.hicarnet.Layout.LoadingView.BookLoadingViewHelp;
import teamgodeater.hicarnet.MVP.Base.BaseFragment;
import teamgodeater.hicarnet.MVP.Base.BaseFragmentManage;
import teamgodeater.hicarnet.MVP.Base.WindowsParams;
import teamgodeater.hicarnet.MVP.Ui.Login.LoginFragment;
import teamgodeater.hicarnet.R;
import teamgodeater.hicarnet.Utils.Utils;
import teamgodeater.hicarnet.Widget.MyScrollView;

import static cn.hugo.android.scanner.CaptureActivity.DECODE_OK;

/**
 * Created by G on 2016/6/19 0019.
 */

public class CarFragment extends BaseFragment<CarPresent> implements CarContractor.View {

    @Bind(R.id.linearLayout)
    LinearLayout linearLayout;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.myScrollView)
    public MyScrollView myScrollView;

    private ErrorViewHelp errorTip;
    private BookLoadingViewHelp bookLoading;

    @NonNull
    @Override
    protected WindowsParams onCreateSupportViewParams(WindowsParams windowsParams) {
        windowsParams.rootLayoutId = R.layout.frgm_car_manager;
        return windowsParams;
    }

    @Override
    public String getType() {
        return C.VT_CAR;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, mRootContain);
        tSetDefaultView(true, "车辆管理");
        setColorFilter();
        bookLoading = new BookLoadingViewHelp(mTopView);
        bookLoading.setLoadingBg(true, Color.TRANSPARENT);
        errorTip = new ErrorViewHelp(mTopView);
        setListen();
        mPresenter.legalCarData();
        return rootView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bookLoading.destroyView();
        errorTip.destroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            mPresenter.legalCarData();
            fab.setScaleX(0.001f);
            fab.setScaleY(0.001f);
            fab.animate().scaleX(1f).scaleY(1f).setInterpolator(new AccelerateInterpolator()).setStartDelay(700L).start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        hideLoadingErrorTip();
    }

    //--------------------------------------数据回调接口---------------------------------------------
    @Override
    public void onStartGetData() {
        setLoading("非常努力的读取数据中 请稍后...");
    }

    @Override
    public void errorNoNet() {
        Utils.toast("无法连接到网络 请检查网络设置!");
        linearLayout.removeAllViews();
        setErrorTip(R.drawable.face_sad, "重试", "无法连接到网络!", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getCarDatas();
            }
        });
    }

    @Override
    public void errorNoData() {
        linearLayout.removeAllViews();
        setErrorTip(R.drawable.face_sad, "刷新", "获取数据失败!", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getCarDatas();
            }
        });
    }

    @Override
    public void errorNoLogin() {
        linearLayout.removeAllViews();
        setErrorTip(R.drawable.face_sad, "去登陆", "你还没有登陆!", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSelf(400L);
                BaseFragmentManage.switchFragment(new LoginFragment());
            }
        });
    }

    @Override
    public void errorNoCarInfo() {
        linearLayout.removeAllViews();
        setErrorTip(R.drawable.face_wunai, "刷新", "你还没有添加车辆信息!", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getCarDatas();
            }
        });
    }

    @Override
    public void errorUnKnow() {
        linearLayout.removeAllViews();
        setErrorTip(R.drawable.face_wunai, "重试", "服务器返回了一个未知错误 请稍后重试!", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getCarDatas();
            }
        });
    }

    @Override
    public void setView(View[] carViews) {
        hideLoadingErrorTip();
        linearLayout.removeAllViews();
        boolean noData = true;
        for (View v : carViews) {
            if (v == null)
                continue;
            noData = false;
            linearLayout.addView(v);
        }
        if (noData)
            errorUnKnow();
    }


    //---------------------------------------获取二维码--------------------------------------------
    public static final int FORGETCARINFO = 24245;

    private void addCar() {
        Intent intent = new Intent(manageActivity, CaptureActivity.class);
        startActivityForResult(intent, FORGETCARINFO);
        setLoading("正在准备相机 请稍后...");
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

            if (userCarInfoData != null && userCarInfoData.getLicense_num() != null
                    && !userCarInfoData.getLicense_num().equals("")) {
                mPresenter.addCarData(userCarInfoData);
            } else {
                Toast.makeText(getActivity(), "没有扫描到有效的的车辆数据二维码,请检查后重试", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //---------------------------------------视图辅助相关-------------------------------------------

    private void setColorFilter() {
        fab.getIcon().setColorFilter(Utils.getColorFromRes(R.color.colorWhite), PorterDuff.Mode.SRC_IN);
    }

    private void setListen() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCar();
            }
        });
    }

    public void setLoading(String str) {
        errorTip.hideError();
        bookLoading.startAnimator(str);
    }

    public void hideLoading() {
        bookLoading.stopAnimator();
    }

    public void setErrorTip(int id, String button, String tip, View.OnClickListener listener) {
        bookLoading.stopAnimator();
        errorTip.showError(id, tip, button, listener);
    }

    public void hideLoadingErrorTip() {
        bookLoading.stopAnimator();
        errorTip.hideError();
    }

    //----------------------------------------动画转场相关-------------------------------------------

    @Override
    public boolean onInterceptBack() {
        finish();
        return true;
    }


    @Override
    public void onToolBarClick(View v, String tag) {
        if (tag.equals("back"))
            finish();
    }

    private void finish() {
        destroySelfShowBefore(280L);
        mTopView.animate().translationX(mTopView.getWidth()).setInterpolator(new AccelerateInterpolator()).start();
    }

    @Override
    public void onOnceGlobalLayoutListen() {
        fab.setScaleX(0.01f);
        fab.setScaleY(0.01f);
        mTopView.setTranslationX(mTopView.getWidth());
        mTopView.animate().translationX(0).setInterpolator(new AccelerateInterpolator()).start();
        fab.animate().scaleX(1f).scaleY(1f).setStartDelay(300).setInterpolator(new AccelerateInterpolator()).start();
    }
}
