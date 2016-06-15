package teamgodeater.hicarnet.OrderMoudle.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;

import com.rey.material.widget.TabPageIndicator;
import com.victor.loading.book.BookLoading;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import teamgodeater.hicarnet.Activity.ManageActivity;
import teamgodeater.hicarnet.Data.UserOrderData;
import teamgodeater.hicarnet.Fragment.BaseFragment;
import teamgodeater.hicarnet.Help.RestClientHelp;
import teamgodeater.hicarnet.Help.UserDataHelp;
import teamgodeater.hicarnet.Help.Utils;
import teamgodeater.hicarnet.LoginModle.Fragment.LoginFragment;
import teamgodeater.hicarnet.OrderMoudle.Adapter.OrderAdapter;
import teamgodeater.hicarnet.OrderMoudle.Adapter.OrederRvAdapter;
import teamgodeater.hicarnet.R;
import teamgodeater.hicarnet.Widget.RippleBackGroundView;

import static teamgodeater.hicarnet.Help.UserDataHelp.userOrderDatas;

/**
 * Created by G on 2016/6/15 0015.
 */

public class OrderFragment extends BaseFragment {
    @Bind(R.id.tpi)
    TabPageIndicator tpi;
    @Bind(R.id.viewPager)
    ViewPager viewPager;
    @Bind(R.id.errorTip)
    TextView errorTip;
    @Bind(R.id.errorButton)
    RippleBackGroundView errorButton;
    @Bind(R.id.bookLoading)
    BookLoading bookLoading;
    @Bind(R.id.loadingTip)
    TextView loadingTip;
    private RecyclerView rv1;
    private RecyclerView rv2;

    private ArrayList<UserOrderData> allOrder;
    private ArrayList<UserOrderData> noUsedOrder;

    boolean hasDestroy = false;

    @NonNull
    @Override
    protected SupportWindowsParams onCreateSupportViewParams() {
        SupportWindowsParams params = new SupportWindowsParams();
        params.rootLayoutId = R.layout.frgm_order;
        return params;
    }

    public static int noUserOrderCount = 0;

    public static int getNousedOrderCount() {
        return noUserOrderCount;
    }

    @Override
    public String getType() {
        return "order";
    }

    public static OrderFragment getInstans() {
        List<Fragment> fragments = ManageActivity.manageActivity.getSupportFragmentManager().getFragments();
        for (Fragment f : fragments) {
            if (f instanceof OrderFragment) {
                return (OrderFragment) f;
            }
        }
        return new OrderFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootContain);
        initView();
        setView();
        return rootView;
    }

    private void initView() {
        rv1 = new RecyclerView(getActivity());
        rv2 = new RecyclerView(getActivity());

        rv1.setHasFixedSize(true);
        rv2.setHasFixedSize(true);

        rv1.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv2.setLayoutManager(new LinearLayoutManager(getActivity()));

        ArrayList<RecyclerView> datas = new ArrayList<>();
        datas.add(rv1);
        datas.add(rv2);

        OrderAdapter orderAdapter = new OrderAdapter();
        orderAdapter.setList(datas);
        viewPager.setAdapter(orderAdapter);
        tpi.setViewPager(viewPager);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden)
            setView();
    }

    private void setView() {
        goneErrorTip(true);
        setLoading(false, null);

        if (hasDestroy || noChange() || noNet() || noLogin() || illegalData())
            return;

        allOrder = new ArrayList<>();
        allOrder.addAll(userOrderDatas);
        getNoUsedOrder();

        rv1.setAdapter(new OrederRvAdapter(noUsedOrder));
        rv2.setAdapter(new OrederRvAdapter(allOrder));
    }

    private boolean noNet() {
        if (Utils.getNetworkType() == Utils.NETTYPE_NONET) {
            errorNoNet();
            return true;
        }
        return false;
    }

    private boolean illegalData() {
        if (UserDataHelp.userOrderDatas == null) {
            errorNoData();
            return true;
        } else if (UserDataHelp.userOrderDatas.size() == 0) {
            errorNoOrder();
            return true;
        }
        return false;
    }


    private void getNoUsedOrder() {
        noUsedOrder = new ArrayList<>();
        for (UserOrderData data : allOrder) {
            if (!data.is_used()) {
                noUsedOrder.add(data);
            }
        }
        noUserOrderCount = noUsedOrder.size();
    }

    private void errorNoNet() {
        Utils.toast("请检查网络设置");
        setErrorTip("没有网络!", "重试", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setView();
            }
        });
    }

    private void errorNoData() {
        setErrorTip("读取数据失败", "刷新", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLoading(true, "正在加载 数据...");
                UserDataHelp.getUserOrderDatas(new UserDataHelp.OnDoneListener() {
                    @Override
                    public void onDone(int code) {
                        setLoading(false, null);
                        if (code == 200) {
                            setView();
                            return;
                        }
                        RestClientHelp.generalErrorToast(code);
                        if (code == RestClientHelp.HTTP_UNAUTHORIZED) {
                            errorNoLogin();
                        } else if (code == RestClientHelp.HTTP_NOT_FOUND) {
                            errorNoOrder();
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

    private void errorNoOrder() {
        //没有订单
        setErrorTip("你还没有订单哦!", "刷新", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLoading(true, "正在加载 数据...");
                UserDataHelp.getUserOrderDatas(new UserDataHelp.OnDoneListener() {
                    @Override
                    public void onDone(int code) {
                        setLoading(false, null);
                        if (code == 200) {
                            setView();
                            return;
                        }
                        if (code == RestClientHelp.HTTP_NOT_FOUND) {
                            errorNoOrder();
                        }
                        RestClientHelp.generalErrorToast(code);
                    }
                });
            }
        });
    }

    private boolean noLogin() {
        if (RestClientHelp.Session.equals(""))
            return true;
        else
            return false;
    }

    private boolean noChange() {
        if (allOrder == null || !allOrder.equals(userOrderDatas))
            return false;
        return true;
    }

    public void setLoading(boolean start, String tip) {
        if (start) {
            goneErrorTip(true);
            bookLoading.setVisibility(View.VISIBLE);
            bookLoading.start();
            loadingTip.setText(tip);
            loadingTip.setVisibility(View.VISIBLE);
        } else {
            bookLoading.stop();
            bookLoading.setVisibility(View.GONE);
            loadingTip.setVisibility(View.GONE);
        }
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hasDestroy = true;
        ButterKnife.unbind(this);
    }

    @Override
    public boolean onInterceptBack() {
        finish();
        return true;
    }

    @Override
    protected void onToolBarClick(View v) {
        String tag = (String) v.getTag();
        if (tag.equals("back"))
            finish();
    }

    @Override
    protected void onOnceGlobalLayoutListen() {
        parentView.setTranslationX(parentView.getWidth());
        parentView.animate().translationX(0f).setInterpolator(new AccelerateInterpolator()).start();
    }

    private void finish() {
        destroySelfShowBefore(280L);
        parentView.animate().translationX(parentView.getWidth()).setInterpolator(new AccelerateInterpolator()).start();
    }
}
