package teamgodeater.hicarnet.WeizhangModle.Fragment;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.cheshouye.api.client.WeizhangClient;
import com.cheshouye.api.client.json.CityInfoJson;
import com.cheshouye.api.client.json.ProvinceInfoJson;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import teamgodeater.hicarnet.Fragment.BaseFragment;
import teamgodeater.hicarnet.Help.Utils;
import teamgodeater.hicarnet.R;
import teamgodeater.hicarnet.WeizhangModle.Adapter.CityRvAdapter;
import teamgodeater.hicarnet.Widget.RippleBackGroundView;

/**
 * Created by G on 2016/6/11 0011.
 */

public class CitySelectFragment extends BaseFragment implements CityRvAdapter.OnClickListener {

    @Bind(R.id.search)
    EditText search;
    @Bind(R.id.back)
    RippleBackGroundView back;
    @Bind(R.id.location)
    TextView location;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

    int searchLevel = 0;
    private List<ProvinceInfoJson> allProvince;
    private List<CityInfoJson> citys;

    private List<ProvinceInfoJson> currentProvince = new ArrayList<>();
    private List<CityInfoJson> currentCitys = new ArrayList<>();
    private int position;
    private OnGetCityIdListener listener;


    @NonNull
    @Override
    protected SupportWindowsParams onCreateSupportViewParams() {
        SupportWindowsParams params = new SupportWindowsParams();
        params.rootLayoutId = R.layout.frgm_weizhang_city_select;
        params.isHasToolBar = false;
        params.statusAlpha = 0.8f;
        return params;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootContain);
        allProvince = WeizhangClient.getAllProvince();
        if (allProvince == null || allProvince.size() == 0){
            Toast.makeText(getActivity(), "无法连接到服务器,请检查你网络后重试", Toast.LENGTH_SHORT);
            finish();
        }else {
            setColorFilter();
            setView();
            setRv();
            setListener();
        }
        return rootView;
    }

    public interface OnGetCityIdListener {
        void onGetCityId(int id);
    }

    @Override
    protected void onOnceGlobalLayoutListen() {
        parentView.setTranslationY(parentView.getHeight());
        parentView.animate().translationY(0f).setInterpolator(new AccelerateInterpolator()).start();
    }

    public void setOnGetCityIdListener(OnGetCityIdListener listener) {
        this.listener = listener;
    }

    private void setView() {
        CityInfoJson locationCityInfo = getLocationCityInfo();
        if (locationCityInfo != null) {
            location.setText(locationCityInfo.getCar_head() + " - " + locationCityInfo.getCity_name());
            location.setTag(locationCityInfo.getCity_id());
        }
    }

    private void setListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftInput();
                if (searchLevel == 0){
                    finish();
                    return;
                }
                searchLevel--;
                search.setText("");
            }
        });

        rootContain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftInput();
            }
        });

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    hideSoftInput();
                }
                return false;
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftInput();
                if (listener != null)
                    listener.onGetCityId((Integer) v.getTag());
                finish();
            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setRv();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void finish() {
        destroySelfShowBefore(280L);
        parentView.animate().translationY(parentView.getHeight())
                .setInterpolator(new AccelerateInterpolator()).start();
    }

    private void hideSoftInput() {
        View v;
        View focusedChild = rootContain.getFocusedChild();
        if (focusedChild instanceof EditText) {
            v = focusedChild;
        } else {
            return;
        }
        InputMethodManager imm = (InputMethodManager)
                manageActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        back.requestFocus();
    }

    private void setRv() {
        List<String> dataList = new ArrayList<>();
        String str = search.getText().toString();
        if (searchLevel == 0) {
            currentProvince.clear();
            if (str.equals("")) {
                currentProvince.addAll(allProvince);
                for (ProvinceInfoJson p : currentProvince) {
                    String s = p.getProvinceShortName() + "   -   " + p.getProvinceName();
                    dataList.add(s);
                }
            } else {
                for (ProvinceInfoJson p : allProvince) {
                    String s = p.getProvinceShortName() + "   -   " + p.getProvinceName();
                    if (s.lastIndexOf(str) != -1) {
                        currentProvince.add(p);
                        dataList.add(s);
                    }
                }
            }
        } else {
            ProvinceInfoJson provinceInfoJson = currentProvince.get(position);
            citys = WeizhangClient.getCitys(provinceInfoJson.getProvinceId());
            currentCitys.clear();
            if (str.equals("")) {
                currentCitys.addAll(citys);
                for (CityInfoJson city : citys) {
                    String s = city.getCar_head() + "   -   " + city.getCity_name();
                    dataList.add(s);
                }
            } else {
                for (CityInfoJson city : citys) {
                    String s = city.getCar_head() + "   -   " + city.getCity_name();
                    if (s.lastIndexOf(str) != -1) {
                        dataList.add(s);
                        currentCitys.add(city);
                    }
                }
            }

        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new CityRvAdapter(dataList, this));
    }

    private void setColorFilter() {
        location.getCompoundDrawables()[0].setColorFilter(Utils.getColorFromRes(R.color.colorBlack54), PorterDuff.Mode.SRC_IN);
        back.setColorFilter(Utils.getColorFromRes(R.color.colorBlack54), PorterDuff.Mode.SRC_IN);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(int position) {
        if (searchLevel == 0) {
            this.position = position;
            searchLevel++;
            search.setText("");
        } else if (searchLevel == 1) {
            if (listener != null)
                listener.onGetCityId(currentCitys.get(position).getCity_id());
            finish();
        }
    }

    public CityInfoJson getLocationCityInfo() {
        BDLocation myLocation = manageActivity.getMyLocation();
        if (myLocation == null || myLocation.getCity() == null || myLocation.getCity().equals("")
                || myLocation.getProvince() == null || myLocation.getProvince().equals(""))
            return null;
        List<ProvinceInfoJson> allProvince = WeizhangClient.getAllProvince();
        String locProvince = myLocation.getProvince();
        int locProvinceId = -1;
        for (ProvinceInfoJson province : allProvince) {
            if (locProvince.indexOf(province.getProvinceName()) != -1) {
                locProvinceId = province.getProvinceId();
                break;
            }
        }
        if (locProvinceId == -1)
            return null;
        String locCity = myLocation.getCity();
        List<CityInfoJson> citys = WeizhangClient.getCitys(locProvinceId);
        for (CityInfoJson city : citys) {
            if (locCity.indexOf(city.getCity_name()) != -1) {
                return city;
            }
        }
        return null;
    }

    @Override
    public boolean onInterceptBack() {
        if (searchLevel == 0) {
            finish();
        } else {
            searchLevel--;
            search.setText("");
        }
        return true;
    }
}
