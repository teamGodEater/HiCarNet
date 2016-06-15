package teamgodeater.hicarnet.Help;


import android.content.SharedPreferences;

import com.baidu.mapapi.model.LatLng;

import java.util.List;
import java.util.Map;

import teamgodeater.hicarnet.Data.GasstationData;
import teamgodeater.hicarnet.Data.UserCarInfoData;
import teamgodeater.hicarnet.Data.UserInfoData;
import teamgodeater.hicarnet.Data.UserOrderData;
import teamgodeater.hicarnet.Data.UserPointData;
import teamgodeater.hicarnet.RestClient.RestClient;

import static android.content.Context.MODE_PRIVATE;
import static teamgodeater.hicarnet.RestClient.RestClient.GET;


/**
 * Created by G on 2016/5/29 0029.
 */

public class RestClientHelp {
    //-------------------------------------Params---------------------------------------------------
    private static final String SERVICE_URL = "http://192.168.137.1:8080";
    private static final String SERVICE_REST_URL = SERVICE_URL + "/api/v1.0";
    private static final String SESSIONS = SERVICE_REST_URL + "/sessions";
    private static final String USER_INFO = SERVICE_REST_URL + "/users/user_info";
    private static final String USER_CAR_INFO = SERVICE_REST_URL + "/users/user_car_info";
    private static final String USER_POINT = SERVICE_REST_URL + "/users/user_point";
    private static final String USER_ORDER = SERVICE_REST_URL + "/users/order";
    public static final String USER_HEADIMAGE = SERVICE_REST_URL + "/head_image";
    public static String Session = "";

    //---------------------------------------------------------------------------------------------
    private static final String GASSTATION = "http://apis.juhe.cn/oil/local?key=9c060f190062368578e0851b3883dfd9";
    //---------------------------------------------------------------------------------------------

    public static final int HTTP_NOT_FOUND = 404;
    public static final int HTTP_UNAUTHORIZED = 401;
    public static final int HTTP_NOT_ACCEPTABLE = 406;
    public static final int HTTP_FOUND = 302;
    public static final int UNPROCESSABLE_ENTITY = 422;
    public static final int HTTP_JSON_ERROR = -2;

    //---------------------------------------------------------------------------------------------

    public static String username = "";
    public static String password = "";

    //----------------------------------------------------------------------------------------------
    public static void generalErrorToast(int code) {
        if (code == -1)
            Utils.toast("连接服务器失败 请检查网络设置");
        else if (code == RestClientHelp.HTTP_UNAUTHORIZED)
            Utils.toast("还没有登录啊!请先登录吧");
        else if (code == RestClientHelp.HTTP_NOT_ACCEPTABLE)
            Utils.toast("参数错误 服务器拒绝本次请求");
        else if (code == RestClientHelp.HTTP_NOT_FOUND)
            Utils.toast("没有找到");
        else
            Utils.toast("未知错误");
    }

    //----------------------------------------------POST--------------------------------------------
    public void login(final RestClient.OnResultListener<String> listener) {
        RestClient restClient = new RestClient();
        restClient
                .addHeaderParams("username", username)
                .addHeaderParams("password", password);
        restClient.post(SESSIONS, new RestClient.OnServiceResultListener() {
            @Override
            public void resultListener(byte[] result, int code, Map<String, List<String>> header) {
                if (code == 200) {
                    List<String> session = header.get("SESSION");
                    if (session != null && session.size() > 0) {
                        Session = session.get(0);
                        SharedPreferences sharedPreferences = Utils.getContext().getSharedPreferences(SharedPreferencesHelp.CLIENT_INFO, MODE_PRIVATE);
                        sharedPreferences.edit().putString(SharedPreferencesHelp.CLIENT_INFO_SESSION, Session).apply();
                        if (listener == null) {
                            return;
                        }
                        listener.succeed("Ok");
                    } else {

                        if (listener == null) {
                            return;
                        }
                        listener.error(-2);
                    }
                } else {
                    Session = "";
                    if (listener == null) {
                        return;
                    }
                    listener.error(code);
                }
            }

        });
    }

    public void setUserInfo(UserInfoData data, RestClient.OnResultListener<String> listener) {
        RestClient restClient = new RestClient();
        restClient
                .addUrlParams("name", data.getName())
                .addUrlParams("email", data.getEmail())
                .addUrlParams("phone", data.getPhone())
                .addUrlParams("gender", data.getGender());
        RestClient.loginOperation(RestClient.POST, USER_INFO, restClient, listener);
    }

    public void setUserCarInfo(UserCarInfoData data, RestClient.OnResultListener<String> listener) {
        RestClient restClient = new RestClient();
        restClient
                .addUrlParams("brand", data.getBrand())
                .addUrlParams("model", data.getModel())
                .addUrlParams("license_num", data.getLicense_num())
                .addUrlParams("engine_num", data.getEngine_num())
                .addUrlParams("level", data.getLevel())
                .addUrlParams("mileage", String.valueOf(data.getMileage()))
                .addUrlParams("petrol_gage", String.valueOf(data.getPetrol_gage()))
                .addUrlParams("engine_performance", String.valueOf(data.getEngine_performance()))
                .addUrlParams("transmission_performance", String.valueOf(data.getTransmission_performance()))
                .addUrlParams("light_performance", String.valueOf(data.getLight_performance()));
        RestClient.loginOperation(RestClient.POST, USER_CAR_INFO, restClient, listener);
    }

    public void setUserPoint(UserPointData data, RestClient.OnResultListener<String> listener) {
        RestClient restClient = new RestClient();
        restClient
                .addUrlParams("home_latitude", String.valueOf(data.getHome_latitude()))
                .addUrlParams("home_longitude", String.valueOf(data.getHome_longitude()))
                .addUrlParams("company_latitude", String.valueOf(data.getCompany_latitude()))
                .addUrlParams("company_longitude", String.valueOf(data.getCompany_longitude()));
        RestClient.loginOperation(RestClient.POST, USER_POINT, restClient, listener);
    }

    //---------------------------------------------PUT----------------------------------------------
    public void regist(String username, String password, RestClient.OnResultListener<String> listener) {
        RestClient restClient = new RestClient();
        restClient
                .addHeaderParams("username", username)
                .addHeaderParams("password", password);

        RestClient.loginOperation(RestClient.PUT, SESSIONS, restClient, listener);
    }

    //---------------------------------------GET----------------------------------------------------
    public void getFile(String fileName, RestClient.OnServiceResultListener listener) {
        RestClient restClient = new RestClient();
        restClient.get(SERVICE_URL + fileName, listener);
    }

    public void getUserInfo(RestClient.OnResultListener<UserInfoData> listener) {
        RestClient.loginOperation(GET, USER_INFO, new RestClient(), listener);
    }

    public void getUserPoint(RestClient.OnResultListener<UserPointData> listener) {
        RestClient restClient = new RestClient();
        RestClient.loginOperation(GET, USER_POINT, restClient, listener);
    }

    public void getUserCarInfo(String License, RestClient.OnResultListener<List<UserCarInfoData>> listener) {
        RestClient restClient = new RestClient();
        if (listener != null && License != null && !License.equals("")) {
            restClient.addUrlParams("license", License);
        }
        RestClient.loginOperation(GET, USER_CAR_INFO, restClient, listener);
    }

    public void getUserOrder(RestClient.OnResultListener<List<UserOrderData>> listener) {
        RestClient restClient = new RestClient();
        RestClient.loginOperation(GET,USER_ORDER,restClient,listener);
    }

    public void getGasStation(LatLng latLng, RestClient.OnResultListener<GasstationData> listener) {
        RestClient restClient = new RestClient();
        restClient.addUrlParams("lon", String.valueOf(latLng.longitude));
        restClient.addUrlParams("lat", String.valueOf(latLng.latitude));
        RestClient.loginOperation(GET,GASSTATION,restClient,listener);
    }
    //--------------------------------------DELETE--------------------------------------------------

    public void delUserPoint(RestClient.OnResultListener<String> listener) {
        RestClient restClient = new RestClient();
        RestClient.loginOperation(RestClient.DELETE, USER_POINT, restClient, listener);
    }

    public void delUserCarInfo(String License, RestClient.OnResultListener<String> listener) {
        RestClient restClient = new RestClient();
        restClient.addUrlParams("license", License);
        RestClient.loginOperation(RestClient.DELETE, USER_CAR_INFO, restClient, listener);
    }


}
