package teamgodeater.hicarnet.MVP.Ui.WeiZhang.Help;

import com.cheshouye.api.client.WeizhangClient;
import com.cheshouye.api.client.json.CarInfo;
import com.cheshouye.api.client.json.CityInfoJson;
import com.cheshouye.api.client.json.InputConfigJson;
import com.cheshouye.api.client.json.ProvinceInfoJson;

import java.util.List;

import teamgodeater.hicarnet.Data.UserCarInfoData;

/**
 * Created by G on 2016/6/12 0012.
 */

public class UserCarInfoWeizhangHelp {

    public static CarInfo toCarInfo(UserCarInfoData data) {
        int cityId = getCityIdFromShotName(data.getLicense_num());
        if (cityId == -1)
            return null;

        InputConfigJson inputConfig = WeizhangClient.getInputConfig(cityId);
        String engine_num = data.getEngine_num();
        int length = engine_num.length();
        int engineno = inputConfig.getEngineno();

        CarInfo carInfo = new CarInfo();
        carInfo.setCity_id(cityId);
        carInfo.setChepai_no(data.getLicense_num());
        if (engineno != 0) {
            carInfo.setEngine_no(engineno == -1 ? engine_num : engine_num.substring(length - engineno, length));
        }

        return carInfo;
    }

    public static int getCityIdFromShotName(String str) {
        List<ProvinceInfoJson> allProvince = WeizhangClient.getAllProvince();
        String shotName = str.substring(0, 1);
        String shotCityName = str.substring(0, 2);

        for (ProvinceInfoJson p : allProvince) {
            if (p.getProvinceShortName().contains(shotName)) {
                List<CityInfoJson> citys = WeizhangClient.getCitys(p.getProvinceId());
                int quanshengId = 0;
                for (CityInfoJson c : citys) {
                    if (c.getCar_head().contains(shotCityName)) {
                        return c.getCity_id();
                    }
                    if (c.getCity_name().contains("全省")) {
                       quanshengId = c.getCity_id();
                    }
                }
                return quanshengId;
            }
        }
        return -1;
    }
}
