package teamgodeater.hicarnet.Help;


import java.util.List;
import java.util.Map;

import teamgodeater.hicarnet.Data.UserCarInfoData;
import teamgodeater.hicarnet.Data.UserInfoData;
import teamgodeater.hicarnet.Data.UserPointData;
import teamgodeater.hicarnet.RestClient.RestClient;


/**
 * Created by G on 2016/5/29 0029.
 */

public class UserHelp {
    //-------------------------------------Params---------------------------------------------------
    private static final String SERVICE_URL = "http://192.168.137.1:8080/api/v1.0";
    private static final String SESSIONS = SERVICE_URL + "/sessions";
    private static final String USER_INFO = SERVICE_URL + "/users/user_info";
    private static final String USER_CAR_INFO = SERVICE_URL + "/users/user_car_info";
    private static final String USER_POINT = SERVICE_URL + "/users/user_point";
    public static String Session = "";

    //---------------------------------------------------------------------------------------------

    private static final int HTTP_NOT_FOUND = 404;
    private static final int HTTP_UNAUTHORIZED = 401;
    private static final int HTTP_NOT_ACCEPTABLE = 406;
    private static final int HTTP_JSON_ERROR = -2;

    //---------------------------------------------------------------------------------------------

    public static String username = "";
    public static String password = "";

    //----------------------------------------------POST--------------------------------------------
    public void login(final RestClient.OnResultListener<String> listener) {
        RestClient restClient = new RestClient();
        restClient
                .addHeaderParams("username", username)
                .addHeaderParams("password", password);
        restClient.post(SESSIONS, new RestClient.OnServiceResultListener() {
            @Override
            public void resultListener(String result, int code, Map<String, List<String>> header) {
                if (code == 200) {
                    List<String> session = header.get("SESSION");

                    if (session != null && session.size() > 0) {
                        Session = session.get(0);

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
                .addHeaderParams("name", data.getName())
                .addHeaderParams("email", data.getEmail())
                .addHeaderParams("phone", data.getPhone())
                .addHeaderParams("gender", data.getGender());
        RestClient.loginOperation(RestClient.POST, Session, USER_INFO, restClient, listener);
    }

    public void setUserCarInfo(UserCarInfoData data, RestClient.OnResultListener<String> listener) {
        RestClient restClient = new RestClient();
        restClient
                .addHeaderParams("brand", data.getBrand())
                .addHeaderParams("model", data.getModel())
                .addHeaderParams("license_num", data.getLicense_num())
                .addHeaderParams("engine_num", data.getEngine_num())
                .addHeaderParams("level", data.getLevel())
                .addHeaderParams("mileage", String.valueOf(data.getMileage()))
                .addHeaderParams("petrol_gage", String.valueOf(data.getPetrol_gage()))
                .addHeaderParams("engine_performance", String.valueOf(data.getEngine_performance()))
                .addHeaderParams("transmission_performance", String.valueOf(data.getTransmission_performance()))
                .addHeaderParams("light_performance", String.valueOf(data.getLight_performance()));
        RestClient.loginOperation(RestClient.POST, Session, USER_CAR_INFO, restClient, listener);
    }

    public void setUserPoint(UserPointData data, RestClient.OnResultListener<String> listener) {
        RestClient restClient = new RestClient();
        restClient
                .addHeaderParams("home_latitude", String.valueOf(data.getHome_latitude()))
                .addHeaderParams("home_longitude", String.valueOf(data.getHome_longitude()))
                .addHeaderParams("company_latitude", String.valueOf(data.getCompany_latitude()))
                .addHeaderParams("company_longitude", String.valueOf(data.getCompany_longitude()));
        RestClient.loginOperation(RestClient.POST, Session, USER_POINT, restClient, listener);
    }

    //---------------------------------------------PUT----------------------------------------------
    public void regist(String username, String password, RestClient.OnResultListener<String> listener) {
        RestClient restClient = new RestClient();
        restClient
                .addHeaderParams("username", username)
                .addHeaderParams("password", password);
        RestClient.loginOperation(RestClient.PUT, Session, SESSIONS, restClient, listener);
    }

    //---------------------------------------GET----------------------------------------------------
    public void getUserInfo(RestClient.OnResultListener<UserInfoData> listener) {
        RestClient.loginOperation(RestClient.GET, Session, USER_INFO, new RestClient(), listener);
    }

    public void getUserPoint(RestClient.OnResultListener<UserPointData> listener) {
        RestClient restClient = new RestClient();
        RestClient.loginOperation(RestClient.GET, Session, USER_POINT, restClient, listener);
    }

    public void getUserCarInfo(String License, RestClient.OnResultListener<List<UserCarInfoData>> listener) {
        RestClient restClient = new RestClient();
        if (listener != null && License != null && !License.equals("")) {
            restClient.addUrlParams("license", License);
        }
        RestClient.loginOperation(RestClient.GET, Session, USER_CAR_INFO, restClient, listener);
    }

    //--------------------------------------DELETE--------------------------------------------------

    public void delUserPoint(RestClient.OnResultListener<String> listener) {
        RestClient restClient = new RestClient();
        RestClient.loginOperation(RestClient.DELETE, Session, USER_POINT, restClient, listener);
    }

    public void delUserCarInfo(String License, RestClient.OnResultListener<String> listener) {
        RestClient restClient = new RestClient();
        restClient.addUrlParams("license", License);
        RestClient.loginOperation(RestClient.DELETE, Session, USER_CAR_INFO, restClient, listener);
    }


}
