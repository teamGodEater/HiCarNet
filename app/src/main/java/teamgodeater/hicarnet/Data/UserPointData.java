package teamgodeater.hicarnet.Data;

/**
 * Created by G on 2016/6/5 0005.
 */

public class UserPointData {

    /**
     * create_data : 2016-06-04 04:04:21.000
     * id : 5
     * latitude : 21
     * longitude : 1242
     * point_describe : 12s
     * use_time_end : 10:00:26
     * use_time_start : 10:00:23
     * user_id : 1
     */

    private String create_data;
    private int id;
    private int user_id;
    private double latitude;
    private double longitude;
    private String point_describe;
    private String use_time_end;
    private String use_time_start;

    public String getCreate_data() {
        return create_data;
    }

    public void setCreate_data(String create_data) {
        this.create_data = create_data;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPoint_describe() {
        return point_describe;
    }

    public void setPoint_describe(String point_describe) {
        this.point_describe = point_describe;
    }

    public String getUse_time_end() {
        return use_time_end;
    }

    public void setUse_time_end(String use_time_end) {
        this.use_time_end = use_time_end;
    }

    public String getUse_time_start() {
        return use_time_start;
    }

    public void setUse_time_start(String use_time_start) {
        this.use_time_start = use_time_start;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
