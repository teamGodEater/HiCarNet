package teamgodeater.hicarnet.WeizhangModle.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.baidu.location.BDLocation;
import com.cheshouye.api.client.WeizhangClient;
import com.cheshouye.api.client.json.CityInfoJson;
import com.cheshouye.api.client.json.ProvinceInfoJson;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import teamgodeater.hicarnet.Fragment.BaseFragment;
import teamgodeater.hicarnet.R;

/**
 * Created by G on 2016/6/11 0011.
 */

public class WeizhangFragment extends BaseFragment {
    @Bind(R.id.button)
    Button button;

    @NonNull
    @Override
    protected SupportWindowsParams onCreateSupportViewParams() {
        SupportWindowsParams params = new SupportWindowsParams();
        params.rootLayoutId = R.layout.frgm_weizhang;
        return params;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootContain);
        tSetDefaultView(true, "违章查询");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int loctionCityId = getLocationCityId();
                manageActivity.switchFragment(new CitySelectFragment());
                hideSelf();
            }
        });
        return rootView;
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
            if (locProvince.indexOf(province.getProvinceName()) != -1) {
                locProvinceId = province.getProvinceId();
                break;
            }
        }
        if (locProvinceId == -1)
            return -1;
        String locCity = myLocation.getCity();
        List<CityInfoJson> citys = WeizhangClient.getCitys(locProvinceId);
        for (CityInfoJson city : citys) {
            if (locCity.indexOf(city.getCity_name()) != -1) {
                return city.getCity_id();
            }
        }
        return -1;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
