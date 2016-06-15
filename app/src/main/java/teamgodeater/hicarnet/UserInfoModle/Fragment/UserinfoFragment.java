package teamgodeater.hicarnet.UserInfoModle.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;

import com.victor.loading.rotate.RotateLoading;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import teamgodeater.hicarnet.Adapter.BaseItem2LineAdapter;
import teamgodeater.hicarnet.Data.BaseItem2LineData;
import teamgodeater.hicarnet.Data.UserInfoData;
import teamgodeater.hicarnet.Fragment.BaseFragment;
import teamgodeater.hicarnet.Help.RestClientHelp;
import teamgodeater.hicarnet.Help.UserDataHelp;
import teamgodeater.hicarnet.Help.Utils;
import teamgodeater.hicarnet.LoginModle.Fragment.LoginFragment;
import teamgodeater.hicarnet.R;
import teamgodeater.hicarnet.UserInfoModle.Adapter.UserInfoRvAdapter;
import teamgodeater.hicarnet.Widget.RippleBackGroundView;

import static teamgodeater.hicarnet.Help.UserDataHelp.userInfoData;

/**
 * Created by G on 2016/6/14 0014.
 */

public class UserinfoFragment extends BaseFragment implements BaseItem2LineAdapter.OnItemClickListener {


    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.errorButton)
    RippleBackGroundView errorButton;
    @Bind(R.id.errorTip)
    TextView errorTip;
    @Bind(R.id.rotateLoading)
    RotateLoading rotateLoading;
    @Bind(R.id.loadingTip)
    TextView loadingTip;
    private UserInfoData infoData;

    @NonNull
    @Override
    protected SupportWindowsParams onCreateSupportViewParams() {
        SupportWindowsParams params = new SupportWindowsParams();
        params.rootLayoutId = R.layout.frgm_userinfo;
        params.primaryColor = Utils.getColorFromRes(R.color.colorAccent);
        return params;
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootContain);
        tSetDefaultView(true, "账户详情");
        setView();
        return rootView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden == false) {
            setView();
        }
    }


    private void setView() {
        setRotateLoading(false, null);
        goneErrorTip(true);

        if (hasDestroy || noChange() || noNet() || noLogin() || illegalData())
            return;

        setRv();
    }

    private void setRv() {
        List<BaseItem2LineData> datas = new ArrayList<>();
        infoData = UserDataHelp.userInfoData;

        BaseItem2LineData d1 = new BaseItem2LineData();
        d1.icoLeftBitmap = UserDataHelp.headImage;
        d1.title = infoData.getName();
        d1.icoRight = R.drawable.ic_keyboard_arrow_right;
        d1.hasDivider = true;
        datas.add(d1);

        BaseItem2LineData d2 = new BaseItem2LineData();
        d2.title = "电话号码";
        d2.tipRight = infoData.getPhone();
        d2.icoRight = R.drawable.ic_keyboard_arrow_right;
        d2.hasDivider = true;
        datas.add(d2);

        BaseItem2LineData d3 = new BaseItem2LineData();
        d3.title = "邮箱号码";
        d3.tipRight = infoData.getEmail();
        d3.icoRight = R.drawable.ic_keyboard_arrow_right;
        d3.hasDivider = true;
        datas.add(d3);

        BaseItem2LineData d4 = new BaseItem2LineData();
        d4.title = "性别";
        d4.tipRight = infoData.getGender();
        d4.icoRight = R.drawable.ic_keyboard_arrow_right;
        d4.hasDivider = true;
        datas.add(d4);

        BaseItem2LineData d5 = new BaseItem2LineData();
        d5.title = "退出登陆";
        d5.hasDivider = true;
        datas.add(d5);

        UserInfoRvAdapter userInfoRvAdapter = new UserInfoRvAdapter(datas);
        userInfoRvAdapter.setOnClickListener(this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(userInfoRvAdapter);
    }


    private boolean illegalData() {
        if (UserDataHelp.userInfoData == null) {
            errorNoData();
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
        if (infoData == null || !infoData.equals(userInfoData))
            return false;
        return true;
    }

    private void errorNoData() {
        setErrorTip("读取数据失败", "刷新", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRotateLoading(true, "正在加载 数据...");
                UserDataHelp.getUserInfoData(new UserDataHelp.OnDoneListener() {
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
                        } else {
                            errorNoData();
                        }
                    }
                });
            }
        });
    }


    private void errorNoLogin() {
        setErrorTip("需要登陆后才能查看 请先登录!", "登陆", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageActivity.switchFragment(new LoginFragment());
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
            rotateLoading.stop();
            loadingTip.setVisibility(View.GONE);
        }
    }

    boolean hasDestroy = false;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hasDestroy = true;
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(BaseItem2LineData data, int position) {
        if (position == 4) {
            UserDataHelp.loginOut();
            manageActivity.switchFragment(new LoginFragment());
            hideSelf(400L);
        }
    }

    @Override
    protected void onToolBarClick(View v) {
        String tag = (String) v.getTag();
        if (tag.equals("back")) {
            finish();
        }
    }

    @Override
    public boolean onInterceptBack() {
        finish();
        return true;
    }

    public void finish() {
        destroySelfShowBefore(280L);
        parentView.animate().translationY(parentView.getHeight()).setInterpolator(new AccelerateInterpolator()).start();
    }

    @Override
    protected void onOnceGlobalLayoutListen() {
        parentView.setTranslationY(parentView.getHeight());
        parentView.animate().translationY(0f).setInterpolator(new AccelerateInterpolator()).start();
    }
}
