package teamgodeater.hicarnet.Help;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.PlanNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import teamgodeater.hicarnet.Data.UserCarInfoData;
import teamgodeater.hicarnet.Data.UserInfoData;
import teamgodeater.hicarnet.Data.UserPointData;
import teamgodeater.hicarnet.MainModle.Help.RoutePlanSearchHelp;
import teamgodeater.hicarnet.RestClient.RestClient;

/**
 * Created by G on 2016/6/7 0007.
 */

public class UserDataHelp {
    public static int OK = 233, NOFOUND = 42, NOGET = 425, INVAIN = 552;
    public static UserInfoData userInfoData;
    public static Bitmap headImage;
    public static UserPointData userPointData;
    public static List<UserCarInfoData> userCarInfoDatas;
    public static DrivingRouteResult userTrafficLine;


    public static int getUserCarInfoStatus() {
        if (userCarInfoDatas == null)
            return NOGET;
        if (userCarInfoDatas.size() == 0)
            return NOFOUND;
        if (userCarInfoDatas.get(0).getLicense_num().equals(""))
            return INVAIN;
        return OK;
    }

    public static void getHeadBitmap(final OnDoneListener listener) {
        headImage = null;
        RestClientHelp restClientHelp = new RestClientHelp();
        restClientHelp.getFile(RestClientHelp.USER_HEADIMAGE, new RestClient.OnServiceResultListener() {
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
        if (userCarInfoDatas.size() == 0) {
            return new UserCarInfoData();
        }
        SharedPreferences sharedPreferences = Utils.getContext().getSharedPreferences(SharedPreferencesHelp.USER_SET, Context.MODE_PRIVATE);
        int defaultCar = sharedPreferences.getInt(SharedPreferencesHelp.USER_SET_DEFAULT_CAT, 0);
        defaultCar = defaultCar >= userCarInfoDatas.size() ? 0 : defaultCar;
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
                if (code == RestClientHelp.HTTP_NOT_FOUND)
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
                if (code == RestClientHelp.HTTP_NOT_FOUND)
                    userInfoData = new UserInfoData();
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
                getUserCarSignIco(listener);
            }

            @Override
            public void error(int code) {
                if (code == RestClientHelp.HTTP_NOT_FOUND)
                    userCarInfoDatas = new ArrayList<>();
                if (listener != null)
                    listener.onDone(code);
            }
        });
    }

    public static void getUserCarSignIco(final OnDoneListener listener) {
        if (userCarInfoDatas == null || userCarInfoDatas.size() == 0)
            return;
        RestClientHelp restClientHelp = new RestClientHelp();
        final int size = userCarInfoDatas.size();
        final int[] doneCount = {0};
        for (int i = 0; i < size; i++) {
            final int finalI = i;
            restClientHelp.getFile(userCarInfoDatas.get(i).getSign_ico(), new RestClient.OnServiceResultListener() {
                @Override
                public void resultListener(byte[] result, int code, Map<String, List<String>> header) {
                    if (code == 200)
                        userCarInfoDatas.get(finalI).setSignBitmap(BitmapFactory.decodeByteArray(result, 0, result.length));
                    doneCount[0]++;
                    if (listener != null && doneCount[0] == size)
                        listener.onDone(code);
                }
            });
        }
    }


    public interface OnDoneListener {
        void onDone(int code);
    }
}
