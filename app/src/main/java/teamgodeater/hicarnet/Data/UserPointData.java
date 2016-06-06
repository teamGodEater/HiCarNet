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
    private double company_latitude;
    private double company_longitude;
    private double home_latitude;
    private double home_longitude;

    public double getCompany_latitude() {
        return company_latitude;
    }

    public void setCompany_latitude(double company_latitude) {
        this.company_latitude = company_latitude;
    }

    public double getCompany_longitude() {
        return company_longitude;
    }

    public void setCompany_longitude(double company_longitude) {
        this.company_longitude = company_longitude;
    }

    public double getHome_latitude() {
        return home_latitude;
    }

    public void setHome_latitude(double home_latitude) {
        this.home_latitude = home_latitude;
    }

    public double getHome_longitude() {
        return home_longitude;
    }

    public void setHome_longitude(double home_longitude) {
        this.home_longitude = home_longitude;
    }

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


    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
