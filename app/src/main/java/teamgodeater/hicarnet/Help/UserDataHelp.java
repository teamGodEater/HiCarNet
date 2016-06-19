package teamgodeater.hicarnet.Help;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.PlanNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import teamgodeater.hicarnet.Data.GasStationData;
import teamgodeater.hicarnet.Data.UserCarInfoData;
import teamgodeater.hicarnet.Data.UserInfoData;
import teamgodeater.hicarnet.Data.UserOrderData;
import teamgodeater.hicarnet.Data.UserPointData;
import teamgodeater.hicarnet.MapHelp.RoutePlanSearchHelp;
import teamgodeater.hicarnet.RestClient.RestClient;
import teamgodeater.hicarnet.Utils.Utils;

import static android.content.Context.MODE_PRIVATE;
import static teamgodeater.hicarnet.C.HTTP_NOT_FOUND;
import static teamgodeater.hicarnet.C.USER_HEADIMAGE;
import static teamgodeater.hicarnet.Help.RestClientHelp.password;
import static teamgodeater.hicarnet.Help.RestClientHelp.username;

/**
 * Created by G on 2016/6/7 0007.
 */

public class UserDataHelp {
    public static UserInfoData userInfoData;
    public static Bitmap headImage;
    public static UserPointData userPointData;
    public static List<UserCarInfoData> userCarInfoDatas;
    public static List<UserOrderData> userOrderDatas;
    public static DrivingRouteResult userTrafficLine;
    public static GasStationData gasstationData;

    public static void loginOut() {
        SharedPreferences sharedPreferences = Utils.getContext().getSharedPreferences(SharedPreferencesHelp.CLIENT_INFO, MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.remove(SharedPreferencesHelp.CLIENT_INFO_USERNAME);
        edit.remove(SharedPreferencesHelp.CLIENT_INFO_PASSWORD);
        edit.remove(SharedPreferencesHelp.CLIENT_INFO_SESSION);
        edit.apply();

        username = "";
        password = "";
        RestClientHelp.Session = "";

        userInfoData = null;
        headImage = null;
        userPointData = null;
        userCarInfoDatas = null;
        userTrafficLine = null;
    }

    public static void getGasstation(LatLng latLng, final OnDoneListener listener) {
        RestClientHelp restClientHelp = new RestClientHelp();
        restClientHelp.getGasStation(latLng, new RestClient.OnResultListener<GasStationData>() {
            @Override
            public void succeed(GasStationData bean) {
                gasstationData = bean;
                if (listener != null)
                    listener.onDone(200);
            }

            @Override
            public void error(int code) {
                if (listener != null)
                    listener.onDone(code);
            }
        });
    }

    public static void getHeadBitmap(String username, final OnDoneListener listener) {
        headImage = null;
        RestClientHelp restClientHelp = new RestClientHelp();
        restClientHelp.getFile(USER_HEADIMAGE + "/" + username, new RestClient.OnServiceResultListener() {
            @Override
            public void resultListener(byte[] result, int code, Map<String, List<String>> header) {
                if (code == 200) {
                    headImage = BitmapFactory.decodeByteArray(result, 0, result.length);
                }
                if (listener != null) {
                    listener.onDone(code);
                }
            }
        });
    }

    public static UserCarInfoData getDefaultCarInfoData() {
        if (userCarInfoDatas == null) {
            return null;
        }
        SharedPreferences sharedPreferences = Utils.getContext().getSharedPreferences(SharedPreferencesHelp.USER_SET, MODE_PRIVATE);
        int defaultCar = sharedPreferences.getInt(SharedPreferencesHelp.USER_SET_DEFAULT_CAT, 0);
        defaultCar = defaultCar >= userCarInfoDatas.size() ? 0 : defaultCar;

        if (userCarInfoDatas.size() == 0 || defaultCar > userCarInfoDatas.size() - 1)
            return null;

        return userCarInfoDatas.get(defaultCar);
    }

    public static void getUserTrafficRoute(final OnDoneListener listener) {
        if (userPointData == null || userPointData.getUser_id() == 0) {
            getUserPointData(listener);
            return;
        }

        RoutePlanSearchHelp routePlanSearchHelp = new RoutePlanSearchHelp(null, new RoutePlanSearchHelp.OnDrivingRouteListener() {
            @Override
            public void onSucceed(DrivingRouteResult route) {
                userTrafficLine = route;
                if (listener != null)
                    listener.onDone(200);
            }

            @Override
            public void onErrorRoute(int code) {
                userTrafficLine = null;
                if (listener != null)
                    listener.onDone(code);
            }
        });

        PlanNode from = PlanNode.withLocation(new LatLng(userPointData.getHome_latitude(), userPointData.getHome_longitude()));
        PlanNode to = PlanNode.withLocation(new LatLng(userPointData.getCompany_latitude(), userPointData.getCompany_longitude()));
        routePlanSearchHelp.setFrom(from);
        routePlanSearchHelp.setOnlyExecuteOnce(true);
        routePlanSearchHelp.requestRoutePlanSearch(to);
    }

    public static void getUserPointData(final OnDoneListener listener) {
        userPointData = null;
        userTrafficLine = null;
        RestClientHelp restClientHelp = new RestClientHelp();
        restClientHelp.getUserPoint(new RestClient.OnResultListener<UserPointData>() {
            @Override
            public void succeed(UserPointData bean) {
                userPointData = bean;
                getUserTrafficRoute(listener);
            }

            @Override
            public void error(int code) {
                if (code == HTTP_NOT_FOUND)
                    userPointData = new UserPointData();
                if (listener != null)
                    listener.onDone(code);
            }
        });
    }

    public static void getUserInfoData(final OnDoneListener listener) {
        userInfoData = null;
        RestClientHelp restClientHelp = new RestClientHelp();
        restClientHelp.getUserInfo(new RestClient.OnResultListener<UserInfoData>() {
            @Override
            public void succeed(UserInfoData bean) {
                userInfoData = bean;
                if (listener != null)
                    listener.onDone(200);
            }

            @Override
            public void error(int code) {
                if (listener != null)
                    listener.onDone(code);
            }
        });
    }

    public static void getUserCarInfoDatas(final OnDoneListener listener) {
        userCarInfoDatas = null;
        RestClientHelp restClientHelp = new RestClientHelp();
        restClientHelp.getUserCarInfo(null, new RestClient.OnResultListener<List<UserCarInfoData>>() {
            @Override
            public void succeed(List<UserCarInfoData> bean) {
                userCarInfoDatas = bean;
                getUserCarSignIco(userCarInfoDatas, listener);
            }

            @Override
            public void error(int code) {
                if (code == HTTP_NOT_FOUND)
                    userCarInfoDatas = new ArrayList<>();
                if (listener != null)
                    listener.onDone(code);
            }
        });
    }

    public static void getUserCarSignIco(final List<UserCarInfoData> carInfoDatas, final OnDoneListener listener) {
        if (carInfoDatas == null || carInfoDatas.size() == 0) {
            listener.onDone(-1);
            return;
        }
        RestClientHelp restClientHelp = new RestClientHelp();
        final int size = carInfoDatas.size();
        final int[] doneCount = {0};
        for (int i = 0; i < size; i++) {
            if (carInfoDatas.get(i).getLicense_num().equals("")) {
                doneCount[0]++;
                if (listener != null && doneCount[0] == size)
                    listener.onDone(-1);
                continue;
            }
            final int finalI = i;
            restClientHelp.getFile(carInfoDatas.get(i).getSign_ico(), new RestClient.OnServiceResultListener() {
                @Override
                public void resultListener(byte[] result, int code, Map<String, List<String>> header) {
                    if (code == 200)
                        carInfoDatas.get(finalI).setSignBitmap(BitmapFactory.decodeByteArray(result, 0, result.length));
                    doneCount[0]++;
                    if (listener != null && doneCount[0] == size)
                        listener.onDone(code);
                }
            });
        }
    }

    public static void getUserOrderDatas(final OnDoneListener listener) {
        userOrderDatas = null;
        RestClientHelp restClientHelp = new RestClientHelp();
        restClientHelp.getUserOrder(new RestClient.OnResultListener<List<UserOrderData>>() {
            @Override
            public void succeed(List<UserOrderData> bean) {
                userOrderDatas = bean;
            }

            @Override
            public void error(int code) {
                if (code == HTTP_NOT_FOUND)
                    userOrderDatas = new ArrayList<>();
                if (listener != null)
                    listener.onDone(code);
            }
        });
    }


    public interface OnDoneListener {
        void onDone(int code);
    }
}
