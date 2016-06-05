package teamgodeater.hicarnet.Data;

/**
 * Created by G on 2016/6/4 0004.
 */

public class UserInfoData {
    /**
     * create_data : 2016-06-03 14:04:51.000
     * email : 12.comweqe
     * gender : 0
     * id : 19
     * name : qweqwe
     * phone : 123789557qwe67
     * user_id : 1
     */

    private String email;
    private String gender;
    private String name;
    private String phone;
    private transient int user_id;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
