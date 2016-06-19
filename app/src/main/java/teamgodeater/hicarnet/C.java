package teamgodeater.hicarnet;

/**
 * Created by G on 2016/6/18 0018.
 */

public class C {
    /**
     * 视图类型
     */
    public static final String VT_MAIN = "VT_MAIN" , VT_LOGIN = "VT_LOGIN"
            , VT_CAR = "VT_CAR" ,VT_SIGN = "VT_SIGN" , VT_WEIZHANG = "VT_WEIZHANG"
            ,VT_ORDER = "VT_ORDER";

    /**
     * 获取当前网络类型
     * @return 0：没有网络   1：WIFI网络   2：WAP网络    3：NET网络
     */

    public static final int NETTYPE_WIFI = 0x01;
    public static final int NETTYPE_CMWAP = 0x02;
    public static final int NETTYPE_CMNET = 0x03;
    public static final int NETTYPE_NONET = 0x00;

    public static final String SERVICE_URL = "http://42.96.174.160:8080";
    public static final String SERVICE_REST_URL = SERVICE_URL + "/resty/api/v1.0";
    public static final String SESSIONS = SERVICE_REST_URL + "/sessions";
    public static final String USER_INFO = SERVICE_REST_URL + "/users/user_info";
    public static final String USER_CAR_INFO = SERVICE_REST_URL + "/users/user_car_info";
    public static final String USER_POINT = SERVICE_REST_URL + "/users/user_point";
    public static final String USER_ORDER = SERVICE_REST_URL + "/users/order";
    public static final String USER_HEADIMAGE = SERVICE_REST_URL + "/head_image";

    public static final int HTTP_NOT_FOUND = 404;
    public static final int HTTP_UNAUTHORIZED = 401;
    public static final int HTTP_NOT_ACCEPTABLE = 406;
    public static final int HTTP_FOUND = 302;
    public static final int HTTP_UNPROCESSABLE_ENTITY = 422;
    public static final int HTTP_SERVICE_ERROR = 500;

    public static final String GASSTATION = "http://apis.juhe.cn/oil/local?key=9c060f190062368578e0851b3883dfd9";

    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String DELETE = "DELETE";
    public static final String PUT = "PUT";

    public final static int Map_Error_No_Net = 20001, Map_Error_No_Loc = 20002,
            Map_Error_No_Result = 20003, Map_Error_Search_String_Empty = 20004, Map_Error_City_Empty = 20005, Map_Error_Cant_Request = 20006;
}
