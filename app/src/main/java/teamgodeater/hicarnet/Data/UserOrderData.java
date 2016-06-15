package teamgodeater.hicarnet.Data;

import android.graphics.Bitmap;

/**
 * Created by G on 2016/6/15 0015.
 */

public class UserOrderData {
    public static transient final int Q93 = 23, Q95 = 24, Q97 = 22, C93 = 26, C95 = 27, C97 = 28;

    //加油类型
    private int type;
    //加油量
    private int rise;
    private int money;
    private String date;
    //订单状态
    private boolean is_used;
    private String gas_address;
    private double latitude;
    //订单编号
    private String order_code;
    //订单图片
    public transient Bitmap streeMap;

    public String getOrder_code() {
        return order_code;
    }

    public void setOrder_code(String order_code) {
        this.order_code = order_code;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    private double longitude;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getRise() {
        return rise;
    }

    public void setRise(int rise) {
        this.rise = rise;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean is_used() {
        return is_used;
    }

    public void setIs_used(boolean is_used) {
        this.is_used = is_used;
    }

    public String getGas_address() {
        return gas_address;
    }

    public void setGas_address(String gas_address) {
        this.gas_address = gas_address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

}
