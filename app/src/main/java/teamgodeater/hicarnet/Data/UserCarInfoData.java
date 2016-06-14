package teamgodeater.hicarnet.Data;

import android.graphics.Bitmap;

/**
 * Created by G on 2016/6/5 0005.
 */

public class UserCarInfoData {

    /**
     * {
     * "brand" : "五菱",
     * "engine_num" : "55EC2323D",
     * "level" : "八门八座",
     * "license_num" : "琼A515152",
     * "mileage" : 1224,
     * "model" : "秋明山车神",
     * "petrol_gage" : 21,
     * "engine_performance" : 2,
     * "light_performance" : 1,
     * "transmission_performance" : 1
     * }
     */

    private String brand;
    private String create_data;
    private String engine_num;
    private int engine_performance;
    private int id;
    private String level;
    private String license_num = "";
    private int light_performance;
    private int mileage;
    private String model;
    private int petrol_gage;
    private String sign_ico;
    private int transmission_performance;
    private int user_id;
    private transient Bitmap signBitmap;

    public Bitmap getSignBitmap() {
        return signBitmap;
    }

    public void setSignBitmap(Bitmap signBitmap) {
        this.signBitmap = signBitmap;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCreate_data() {
        return create_data;
    }

    public void setCreate_data(String create_data) {
        this.create_data = create_data;
    }

    public String getEngine_num() {
        return engine_num;
    }

    public void setEngine_num(String engine_num) {
        this.engine_num = engine_num;
    }

    public int getEngine_performance() {
        return engine_performance;
    }

    public void setEngine_performance(int engine_performance) {
        this.engine_performance = engine_performance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getLicense_num() {
        return license_num;
    }

    public void setLicense_num(String license_num) {
        this.license_num = license_num;
    }

    public int getLight_performance() {
        return light_performance;
    }

    public void setLight_performance(int light_performance) {
        this.light_performance = light_performance;
    }

    public int getMileage() {
        return mileage;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getPetrol_gage() {
        return petrol_gage;
    }

    public void setPetrol_gage(int petrol_gage) {
        this.petrol_gage = petrol_gage;
    }

    public String getSign_ico() {
        return sign_ico;
    }

    public void setSign_ico(String sign_ico) {
        this.sign_ico = sign_ico;
    }

    public int getTransmission_performance() {
        return transmission_performance;
    }

    public void setTransmission_performance(int transmission_performance) {
        this.transmission_performance = transmission_performance;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
