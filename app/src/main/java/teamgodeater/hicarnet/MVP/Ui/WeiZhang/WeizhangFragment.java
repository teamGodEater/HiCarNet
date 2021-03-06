package teamgodeater.hicarnet.MVP.Ui.WeiZhang;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.cheshouye.api.client.WeizhangClient;
import com.cheshouye.api.client.json.CarInfo;
import com.cheshouye.api.client.json.CityInfoJson;
import com.cheshouye.api.client.json.InputConfigJson;
import com.cheshouye.api.client.json.ProvinceInfoJson;
import com.cheshouye.api.client.json.WeizhangResponseJson;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import teamgodeater.hicarnet.Activity.ManageActivity;
import teamgodeater.hicarnet.Adapter.BaseItem2LineAdapter;
import teamgodeater.hicarnet.C;
import teamgodeater.hicarnet.Data.BaseItem2LineData;
import teamgodeater.hicarnet.Data.UserCarInfoData;
import teamgodeater.hicarnet.Help.SharedPreferencesHelp;
import teamgodeater.hicarnet.Help.UserDataHelp;
import teamgodeater.hicarnet.MVP.Base.BaseFragment;
import teamgodeater.hicarnet.MVP.Base.BaseFragmentManage;
import teamgodeater.hicarnet.MVP.Base.WindowsParams;
import teamgodeater.hicarnet.MVP.Ui.WeiZhang.Help.UserCarInfoWeizhangHelp;
import teamgodeater.hicarnet.R;
import teamgodeater.hicarnet.Utils.Utils;
import teamgodeater.hicarnet.Widget.RippleBackGroundView;

/**
 * Created by G on 2016/6/11 0011.
 */

public class WeizhangFragment extends BaseFragment {


    @Bind(R.id.cityChose)
    TextView cityChose;
    @Bind(R.id.license)
    EditText license;
    @Bind(R.id.engineTip)
    TextView engineTip;
    @Bind(R.id.engine)
    EditText engine;
    @Bind(R.id.chejiaTip)
    TextView chejiaTip;
    @Bind(R.id.chejia)
    EditText chejia;
    @Bind(R.id.certificateTip)
    TextView certificateTip;
    @Bind(R.id.licenseTip)
    TextView licenseTip;
    @Bind(R.id.certificate)
    EditText certificate;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.testQuery)
    RippleBackGroundView testQuery;

    public static WeizhangFragment getInstans() {
        List<Fragment> fragments = ManageActivity.manageActivity.getSupportFragmentManager().getFragments();
        for (Fragment f : fragments) {
            if (f instanceof WeizhangFragment) {
                return (WeizhangFragment) f;
            }
        }
        return new WeizhangFragment();
    }

    @NonNull
    @Override
    protected WindowsParams onCreateSupportViewParams(WindowsParams windowsParams) {
        windowsParams.rootLayoutId = R.layout.frgm_weizhang;
        windowsParams.primaryColor = Utils.getColorFromRes(R.color.color1);
        return windowsParams;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, mRootContain);
        cityChose.getCompoundDrawables()[2].setColorFilter(Utils.getColorFromRes(R.color.colorWhite87), PorterDuff.Mode.SRC_IN);
        tSetDefaultView(true, "违章查询");
        tAddRightContainView(tGetSimpleView("查询", 1f), "query");
        setViewByCityId(getLocationCityId());
        setListener();
        setRv();
        return rootView;
    }

    @Override
    public void onToolBarClick(View v,String tag) {
        if (tag.equals("query")) {
            query();
        } else if (tag.equals("back")) {
            finish();
        }
    }

    private void query() {
        CarInfo carInfo = new CarInfo();
        carInfo.setChepai_no(licenseTip.getText().toString() + license.getText().toString());
        if (engine.getVisibility() == View.VISIBLE) {
            String engine_no = engine.getText().toString();
            if (engine_no.equals("")) {
                Toast.makeText(getActivity(), "输入的信息不够完整", Toast.LENGTH_SHORT).show();
                return;
            }
            carInfo.setEngine_no(engine_no);
        }
        if (chejia.getVisibility() == View.VISIBLE) {
            String chejia_no = chejia.getText().toString();
            if (chejia_no.equals("")) {
                Toast.makeText(getActivity(), "输入的信息不够完整", Toast.LENGTH_SHORT).show();
                return;
            }
            carInfo.setChejia_no(chejia_no);
        }
        if (certificate.getVisibility() == View.VISIBLE) {
            String register_no = certificate.getText().toString();
            if (register_no.equals("")) {
                Toast.makeText(getActivity(), "输入的信息不够完整", Toast.LENGTH_SHORT).show();
                return;
            }
            carInfo.setRegister_no(register_no);
        }

        WeizhangResponseJson weizhang = WeizhangClient.getWeizhang(carInfo);

        if (weizhang.getStatus() == 2000 || weizhang.getStatus() == 2001) {
            WeiZhangDetialFragment to = new WeiZhangDetialFragment();
            to.setWeizhangJson(weizhang);
            BaseFragmentManage.switchFragment(to);
            hideSelf(500L);
            return;
        } else if (weizhang.getStatus() >= 5000) {
            Toast.makeText(getActivity(), "系统繁忙 请稍后重试", Toast.LENGTH_SHORT).show();
        } else if (weizhang.getStatus() < 2000) {
            Toast.makeText(getActivity(), "没有找到相关的信息 请检查 输入的数据是否准确", Toast.LENGTH_SHORT).show();
        }
    }

    private void setRv() {
        List<BaseItem2LineData> datas = new ArrayList<>();
        List<BaseItem2LineData> carInfoDatas = loadCarInfo();
        List<BaseItem2LineData> historyDatas = loadHistory();
        if (carInfoDatas != null)
            datas.addAll(carInfoDatas);
        if (historyDatas != null)
            datas.addAll(historyDatas);

        if (datas.size() == 0) {
            BaseItem2LineData data = new BaseItem2LineData();
            data.isClickAble = false;
            data.title = "没有历史记录";
            datas.add(data);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        BaseItem2LineAdapter adapter = new BaseItem2LineAdapter(datas);


        adapter.setOnClickListener(new BaseItem2LineAdapter.OnItemClickListener() {
            @Override
            public void onClick(BaseItem2LineData data, int position) {
                CarInfo tag = (CarInfo) data.tag;
                setViewByCityId(tag.getCity_id());

                String ltip = licenseTip.getText().toString();
                String chepai = tag.getChepai_no();
                license.setText(chepai
                        .substring(ltip.length(), chepai.length()));
                if (engine.getVisibility() == View.VISIBLE) {
                    engine.setText(tag.getEngine_no());
                }
                if (chejia.getVisibility() == View.VISIBLE) {
                    chejia.setText(tag.getChejia_no());
                }
                if (certificate.getVisibility() == View.VISIBLE) {
                    certificate.setText(tag.getRegister_no());
                }
                query();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private List<BaseItem2LineData> loadHistory() {
        SharedPreferences sharedPreferences = manageActivity.getSharedPreferences(SharedPreferencesHelp.WEIZHANG, Context.MODE_PRIVATE);
        String string = sharedPreferences.getString(SharedPreferencesHelp.WEIZHANG_JSON, "");
        if (string.equals(""))
            return null;
        Gson gson = new Gson();
        List<CarInfo> t;
        t = gson.fromJson(string, new TypeToken<List<CarInfo>>() {
        }.getType());

        if (t == null || t.size() == 0)
            return null;

        List<BaseItem2LineData> datas = new ArrayList<>();

        for (CarInfo c : t) {
            BaseItem2LineData item2LineData = new BaseItem2LineData();
            item2LineData.hasDivider = true;
            item2LineData.tag = c;
            item2LineData.title = c.getChepai_no();
            item2LineData.icoLeft = R.drawable.search_ic_history_black_24dp;
            datas.add(item2LineData);
        }

        if (datas.size() == 0)
            return null;
        return datas;
    }

    private List<BaseItem2LineData> loadCarInfo() {
        List<UserCarInfoData> userCarInfoDatas = UserDataHelp.userCarInfoDatas;
        if (userCarInfoDatas == null || userCarInfoDatas.size() == 0)
            return null;

        List<BaseItem2LineData> datas = new ArrayList<>();

        for (UserCarInfoData cid : userCarInfoDatas) {
            CarInfo carInfo = UserCarInfoWeizhangHelp.toCarInfo(cid);
            if (carInfo == null)
                continue;
            BaseItem2LineData data = new BaseItem2LineData();
            data.hasDivider = true;
            data.icoLeftBitmap = cid.getSignBitmap();
            data.title = cid.getBrand();
            data.tip = cid.getLicense_num();
            data.tag = carInfo;
            datas.add(data);
        }

        if (datas.size() == 0)
            return null;
        return datas;
    }

    private void setListener() {
        // TODO: 2016/6/16 0016 测试查询数据
        testQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WeiZhangDetialFragment to = new WeiZhangDetialFragment();
                String s = "{\n" +
                        "\t\"status\": 2001,\n" +
                        "\t\"total_score\": 0,\n" +
                        "\t\"total_money\": 100,\n" +
                        "\t\"count\": 2,\n" +
                        "\t\"historys\": [\n" +
                        "\t\t{\n" +
                        "\t\t\t\"id\": 2155626,\n" +
                        "\t\t\t\"car_id\": 1497817,\n" +
                        "\t\t\t\"status\": \"N\",\n" +
                        "\t\t\t\"fen\": 0,\n" +
                        "\t\t\t\"officer\": \"石柱土家族自治县交巡警大队南宾勤务中队\",\n" +
                        "\t\t\t\"occur_date\": \"2014-06-16 15:30:00\",\n" +
                        "\t\t\t\"occur_area\": \"牛石嵌至交巡警大队路段\",\n" +
                        "\t\t\t\"city_id\": 145,\n" +
                        "\t\t\t\"province_id\": 10,\n" +
                        "\t\t\t\"code\": \"1047\",\n" +
                        "\t\t\t\"info\": \"机动车未按规定临时停车\",\n" +
                        "\t\t\t\"money\": 0,\n" +
                        "\t\t\t\"city_name\": \"恩施\"\n" +
                        "\t\t},\n" +
                        "\t\t{\n" +
                        "\t\t\t\"id\": 3058277,\n" +
                        "\t\t\t\"car_id\": 1497817,\n" +
                        "\t\t\t\"status\": \"N\",\n" +
                        "\t\t\t\"fen\": 0,\n" +
                        "\t\t\t\"officer\": \"重庆市公安局交通巡逻警察总队丰都大队\",\n" +
                        "\t\t\t\"occur_date\": \"2014-09-09 11:18:00\",\n" +
                        "\t\t\t\"occur_area\": \"省道105243公里900米\",\n" +
                        "\t\t\t\"city_id\": 722,\n" +
                        "\t\t\t\"province_id\": 31,\n" +
                        "\t\t\t\"code\": \"60112\",\n" +
                        "\t\t\t\"info\": \"在高速公路或城市快速路以外的道路上行驶时，驾驶人未按规定使用安全带的\",\n" +
                        "\t\t\t\"money\": 100,\n" +
                        "\t\t\t\"city_name\": \"钦州\"\n" +
                        "\t\t}\n" +
                        "\t]\n" +
                        "}";
                Gson gson = new Gson();
                WeizhangResponseJson weizhang = gson.fromJson(s, WeizhangResponseJson.class);
                to.setWeizhangJson(weizhang);
                BaseFragmentManage.switchFragment(to);
                hideSelf(500L);
            }
        });

        cityChose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CitySelectFragment citySelectFragment = new CitySelectFragment();
                citySelectFragment.setOnGetCityIdListener(new CitySelectFragment.OnGetCityIdListener() {
                    @Override
                    public void onGetCityId(int id) {
                        setViewByCityId(id);
                    }
                });
                BaseFragmentManage.switchFragment(citySelectFragment);
                hideSelf(500L);
            }
        });

    }

    public int getLocationCityId() {
        BDLocation myLocation = manageActivity.getMyLocation();
        if (myLocation == null || myLocation.getCity() == null || myLocation.getCity().equals("")
                || myLocation.getProvince() == null || myLocation.getProvince().equals(""))
            return -1;
        List<ProvinceInfoJson> allProvince = WeizhangClient.getAllProvince();
        String locProvince = myLocation.getProvince();
        int locProvinceId = -1;
        for (ProvinceInfoJson province : allProvince) {
            if (locProvince.contains(province.getProvinceName())) {
                locProvinceId = province.getProvinceId();
                break;
            }
        }
        if (locProvinceId == -1)
            return -1;
        String locCity = myLocation.getCity();
        List<CityInfoJson> citys = WeizhangClient.getCitys(locProvinceId);
        for (CityInfoJson city : citys) {
            if (locCity.contains(city.getCity_name())) {
                return city.getCity_id();
            }
        }
        return -1;
    }

    public void setViewByCityId(int cityId) {
        if (cityId == 0) {
            Utils.toast("Sorry!发生了一些错误 请手动设置 车牌的省份(城市)");
            return;
        }
        cityId = cityId == -1 ? 189 : cityId;
        InputConfigJson inputConfig = WeizhangClient.getInputConfig(cityId);
        CityInfoJson cityInfo = WeizhangClient.getCity(cityId);

        /**
         engineno	int	需要输入发动机号位数（"-1"：全部输入，"0"：不需要输入，其他的显示几位输入发动机号后面几位）
         classno	int	需要输入车架号位数（"-1"：全部输入，"0"：不需要输入，其他的显示几位输入车架号后面几位）
         registno	int	需要输入证书编号位数（"-1"：全部输入，"0"：不需要输入，其他的显示几位输入证书编号后面几位）
         */

        if (inputConfig == null || cityInfo == null) {
            Utils.toast("发生了一个错误 放弃本次请求");
            return;
        }

        int engineno = inputConfig.getEngineno();
        int classno = inputConfig.getClassno();
        int registno = inputConfig.getRegistno();

        cityChose.setText(cityInfo.getCity_name());
        licenseTip.setText(cityInfo.getCar_head());
        license.setHint("车牌号");

        if (engineno == 0) {
            engineTip.setVisibility(View.GONE);
            engine.setVisibility(View.GONE);
        } else {
            engineTip.setVisibility(View.VISIBLE);
            engine.setVisibility(View.VISIBLE);
            engine.setText("");
            String hint = "请输入" + (engineno == -1 ? "完整" : "后 " + engineno + " 位") + "发动机号";
            engine.setHint(hint);
        }

        if (classno == 0) {
            chejiaTip.setVisibility(View.GONE);
            chejia.setVisibility(View.GONE);
        } else {
            chejiaTip.setVisibility(View.VISIBLE);
            chejia.setVisibility(View.VISIBLE);
            chejia.setText("");
            String hint = "请输入" + (classno == -1 ? "完整" : "后 " + classno + " 位") + "车架号";
            chejia.setHint(hint);
        }

        if (registno == 0) {
            certificateTip.setVisibility(View.GONE);
            certificate.setVisibility(View.GONE);
        } else {
            certificateTip.setVisibility(View.VISIBLE);
            certificate.setVisibility(View.VISIBLE);
            certificate.setText("");
            String hint = "请输入" + (registno == -1 ? "完整" : "后 " + registno + " 位") + "证书号";
            engine.setHint(hint);
        }
    }

    @Override
    public void onOnceGlobalLayoutListen() {
        mTopView.setTranslationX(mTopView.getWidth());
        mTopView.animate().translationX(0).setInterpolator(new AccelerateInterpolator()).start();
    }

    @Override
    public boolean onInterceptBack() {
        finish();
        return true;
    }

    @Override
    public String getType() {
        return C.VT_WEIZHANG;
    }

    boolean hasFinish = false;
    private void finish() {
        if (hasFinish)
            return;
        hasFinish = true;
        destroySelfShowBefore(280L);
        mTopView.animate().translationX(mTopView.getWidth()).setInterpolator(new AccelerateInterpolator()).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
