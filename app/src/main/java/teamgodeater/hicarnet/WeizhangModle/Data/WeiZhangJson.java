package teamgodeater.hicarnet.WeizhangModle.Data;

import java.util.List;

/**
 * Created by G on 2016/6/12 0012.
 */

public class WeiZhangJson {
    /**
     * status : 2001
     * total_score : 0
     * total_money : 100
     * count : 2
     * historys : [{"id":2155626,"car_id":1497817,"status":"N","fen":0,"officer":"石柱土家族自治县交巡警大队南宾勤务中队","occur_date":"2014-06-16 15:30:00","occur_area":"牛石嵌至交巡警大队路段","city_id":145,"province_id":10,"code":"1047","info":"机动车未按规定临时停车","money":0,"city_name":"恩施"},{"id":3058277,"car_id":1497817,"status":"N","fen":0,"officer":"重庆市公安局交通巡逻警察总队丰都大队","occur_date":"2014-09-09 11:18:00","occur_area":"省道105243公里900米","city_id":722,"province_id":31,"code":"60112","info":"在高速公路或城市快速路以外的道路上行驶时，驾驶人未按规定使用安全带的","money":100,"city_name":"钦州"}]
     */

    private int status;
    private int total_score;
    private int total_money;
    private int count;
    /**
     * id : 2155626
     * car_id : 1497817
     * status : N
     * fen : 0
     * officer : 石柱土家族自治县交巡警大队南宾勤务中队
     * occur_date : 2014-06-16 15:30:00
     * occur_area : 牛石嵌至交巡警大队路段
     * city_id : 145
     * province_id : 10
     * code : 1047
     * info : 机动车未按规定临时停车
     * money : 0
     * city_name : 恩施
     */

    private List<HistorysBean> historys;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTotal_score() {
        return total_score;
    }

    public void setTotal_score(int total_score) {
        this.total_score = total_score;
    }

    public int getTotal_money() {
        return total_money;
    }

    public void setTotal_money(int total_money) {
        this.total_money = total_money;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<HistorysBean> getHistorys() {
        return historys;
    }

    public void setHistorys(List<HistorysBean> historys) {
        this.historys = historys;
    }


}
