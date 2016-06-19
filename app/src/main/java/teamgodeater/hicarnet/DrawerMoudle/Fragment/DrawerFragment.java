package teamgodeater.hicarnet.DrawerMoudle.Fragment;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import teamgodeater.hicarnet.Activity.ManageActivity;
import teamgodeater.hicarnet.Adapter.BaseItem2LineAdapter;
import teamgodeater.hicarnet.Data.BaseItem2LineData;
import teamgodeater.hicarnet.Data.UserCarInfoData;
import teamgodeater.hicarnet.Help.RestClientHelp;
import teamgodeater.hicarnet.Help.UserDataHelp;
import teamgodeater.hicarnet.MVP.Base.BaseFragment;
import teamgodeater.hicarnet.MVP.Base.BaseFragmentManage;
import teamgodeater.hicarnet.R;
import teamgodeater.hicarnet.Widget.RoundedImageView;

import static teamgodeater.hicarnet.C.VT_CAR;
import static teamgodeater.hicarnet.C.VT_MAIN;
import static teamgodeater.hicarnet.C.VT_ORDER;
import static teamgodeater.hicarnet.C.VT_WEIZHANG;

/**
 * Created by G on 2016/5/20 0020.
 */
public class DrawerFragment extends Fragment {


    @Bind(R.id.headImage)
    RoundedImageView headImage;
    @Bind(R.id.Greetings)
    TextView greetings;
    @Bind(R.id.LocationTip)
    TextView locationTip;
    @Bind(R.id.HeadContain)
    FrameLayout headContain;
    @Bind(R.id.RecyclerView)
    RecyclerView recyclerView;
    private DrawerLayout drawerLayout;

    public void setDrawerLayout(DrawerLayout drawerLayout) {
        this.drawerLayout = drawerLayout;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frgm_drawer, container, false);
        ButterKnife.bind(this, v);
        setColorFilter();
        setRv();
        setHeadView();
        setListener();
        return v;
    }

    private void setListener() {
        BaseFragmentManage.setOnFragmentChangeListener(new BaseFragmentManage.OnFragmentChangeListener() {
            @Override
            public void onFragmentChange(BaseFragment to) {
                if (recyclerView ==null || recyclerView.getAdapter() == null)
                    return;
                BaseItem2LineAdapter adapter = (BaseItem2LineAdapter) recyclerView.getAdapter();
                if (to.getType().equals(VT_MAIN))
                    adapter.setFocusItem(0);
                else if (to.getType().equals(VT_CAR))
                    adapter.setFocusItem(1);
                else if (to.getType().equals(VT_WEIZHANG))
                    adapter.setFocusItem(2);
                else if (to.getType().equals(VT_ORDER))
                    adapter.setFocusItem(3);
            }
        });

        headImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseFragmentManage.getTopFragment().hideSelf(400L);
                if (UserDataHelp.userInfoData == null && RestClientHelp.Session.equals("") && RestClientHelp.username.equals("")) {
                    // TODO: 2016/6/14 0014 denglu
//                    BaseFragmentManage.switchFragment();
                } else {
                    // TODO: 2016/6/14 0014 个人信息
                    drawerLayout.closeDrawer(Gravity.LEFT);
//                    BaseFragmentManage.switchFragment();
                }
            }
        });

        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                setHeadView();
                BaseItem2LineAdapter adapter = (BaseItem2LineAdapter) recyclerView.getAdapter();
                //车辆管理
                BaseItem2LineData d2 = adapter.list.get(1);
                UserCarInfoData defaultCarInfoData = UserDataHelp.getDefaultCarInfoData();
                if (defaultCarInfoData == null || defaultCarInfoData.getLicense_num().equals("")) {
                    d2.tipRight = "没有数据";
                } else {
                    d2.tipRight = "当前车辆 " + defaultCarInfoData.getLicense_num();
                }
                adapter.notifyItemChanged(1);
                BaseItem2LineData d4 = adapter.list.get(3);
                // TODO: 2016/6/18 0018 获取订单数量
//                int count = OrderFragmentOld.getNousedOrderCount();
//                d4.tipRight = count > 0 ? count + " 未消费" : "";
//                adapter.notifyItemChanged(3);
            }
        });
    }

    private void setHeadView() {
        BDLocation myLocation = ManageActivity.manageActivity.getMyLocation();
        if (myLocation == null || myLocation.getCity() == null)
            locationTip.setText("定位失败 Qvq");
        else {
            locationTip.setText(myLocation.getCity() + " " + myLocation.getDistrict());
        }
        if (UserDataHelp.headImage == null)
            headImage.setImageResource(R.drawable.logo);
        else {
            headImage.setImageBitmap(UserDataHelp.headImage);
        }
        if (RestClientHelp.Session.equals("")) {
            greetings.setText("你还没有登陆!");
        } else {
            if (UserDataHelp.userInfoData == null)
                greetings.setText("你好! ");
            else
                greetings.setText("你好! " + UserDataHelp.userInfoData.getName());
        }
    }

    private void setRv() {
        List<BaseItem2LineData> datas = new ArrayList<>();
        BaseItem2LineData d1 = new BaseItem2LineData();
        d1.icoLeft = R.drawable.ic_home;
        d1.title = "主页面";
        datas.add(d1);

        BaseItem2LineData d2 = new BaseItem2LineData();
        d2.icoLeft = R.drawable.ic_directions_car;
        d2.title = "车辆信息";
        UserCarInfoData defaultCarInfoData = UserDataHelp.getDefaultCarInfoData();
        if (defaultCarInfoData == null || defaultCarInfoData.getLicense_num().equals("")) {
            d2.tipRight = "没有数据";
        } else {
            d2.tipRight = "当前车辆 " + defaultCarInfoData.getLicense_num();
        }
        datas.add(d2);

        BaseItem2LineData d3 = new BaseItem2LineData();
        d3.icoLeft = R.drawable.ic_weizhang;
        d3.title = "违章查询";
        datas.add(d3);

        BaseItem2LineData d4 = new BaseItem2LineData();
        d4.icoLeft = R.drawable.ic_description;
        d4.title = "我的订单";
        datas.add(d4);

        BaseItem2LineAdapter baseItem2LineAdapter = new BaseItem2LineAdapter(datas);
        baseItem2LineAdapter.setFocusItem(0);
        baseItem2LineAdapter.setOnClickListener(new BaseItem2LineAdapter.OnItemClickListener() {
            @Override
            public void onClick(BaseItem2LineData data, int position) {
                BaseFragmentManage.getTopFragment().hideSelf(400L);
                if (position == 0) {
//                    BaseFragmentManage.switchFragment(MainFragmentOld.getInstans());
                } else if (position == 1) {
//                    BaseFragmentManage.switchFragment(CarManageFragmentOld.getInstans());
                } else if (position == 2) {
//                    BaseFragmentManage.switchFragment(WeizhangFragmentOld.getInstans());
                } else if (position == 3) {
//                    BaseFragmentManage.switchFragment(OrderFragmentOld.getInstans());
                }
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(baseItem2LineAdapter);
    }

    private void setColorFilter() {
        locationTip.getCompoundDrawables()[0].setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
