package teamgodeater.hicarnet.Data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by G on 2016/6/16 0016.
 */

public class GasStationData {

    /**
     * resultcode : 200
     * reason : Successed!
     * result : {"data":[{"id":"30818","name":"中石化长风加油站","area":"571133","areaname":"海南省 海口市 美兰区","address":"海南省海口市美兰区和平东路与长提路交叉口,西侧,路北","brandname":"中石化","type":"其他","discount":"非打折加油站","exhaust":"国Ⅳ","position":"110.349366,20.045485","lon":"110.35592462262","lat":"20.051193980571","price":{"E90":"6.55","E93":"7.08","E97":"7.5","E0":"5.63"},"gastprice":{"E90":"6.98","E93":"6.98","E97":"7.31","E0":"5.53"},"fwlsmc":"加油卡,便利店,发卡充值网点,卫生间,银联卡充值,加油卡充值业务","distance":2388}],"pageinfo":{"pnums":20,"current":1,"allpage":1}}
     * error_code : 0
     */

    private String resultcode;
    private String reason;
    /**
     * data : [{"id":"30818","name":"中石化长风加油站","area":"571133","areaname":"海南省 海口市 美兰区","address":"海南省海口市美兰区和平东路与长提路交叉口,西侧,路北","brandname":"中石化","type":"其他","discount":"非打折加油站","exhaust":"国Ⅳ","position":"110.349366,20.045485","lon":"110.35592462262","lat":"20.051193980571","price":{"E90":"6.55","E93":"7.08","E97":"7.5","E0":"5.63"},"gastprice":{"E90":"6.98","E93":"6.98","E97":"7.31","E0":"5.53"},"fwlsmc":"加油卡,便利店,发卡充值网点,卫生间,银联卡充值,加油卡充值业务","distance":2388}]
     * pageinfo : {"pnums":20,"current":1,"allpage":1}
     */

    private ResultBean result;
    private int error_code;

    public String getResultcode() {
        return resultcode;
    }

    public void setResultcode(String resultcode) {
        this.resultcode = resultcode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public static class ResultBean {
        /**
         * pnums : 20
         * current : 1
         * allpage : 1
         */

        private PageinfoBean pageinfo;
        /**
         * id : 30818
         * name : 中石化长风加油站
         * area : 571133
         * areaname : 海南省 海口市 美兰区
         * address : 海南省海口市美兰区和平东路与长提路交叉口,西侧,路北
         * brandname : 中石化
         * type : 其他
         * discount : 非打折加油站
         * exhaust : 国Ⅳ
         * position : 110.349366,20.045485
         * lon : 110.35592462262
         * lat : 20.051193980571
         * price : {"E90":"6.55","E93":"7.08","E97":"7.5","E0":"5.63"}
         * gastprice : {"E90":"6.98","E93":"6.98","E97":"7.31","E0":"5.53"}
         * fwlsmc : 加油卡,便利店,发卡充值网点,卫生间,银联卡充值,加油卡充值业务
         * distance : 2388
         */

        private List<DataBean> data;

        public PageinfoBean getPageinfo() {
            return pageinfo;
        }

        public void setPageinfo(PageinfoBean pageinfo) {
            this.pageinfo = pageinfo;
        }

        public List<DataBean> getData() {
            return data;
        }

        public void setData(List<DataBean> data) {
            this.data = data;
        }

        public static class PageinfoBean {
            private int pnums;
            private int current;
            private int allpage;

            public int getPnums() {
                return pnums;
            }

            public void setPnums(int pnums) {
                this.pnums = pnums;
            }

            public int getCurrent() {
                return current;
            }

            public void setCurrent(int current) {
                this.current = current;
            }

            public int getAllpage() {
                return allpage;
            }

            public void setAllpage(int allpage) {
                this.allpage = allpage;
            }
        }

        public static class DataBean {
            private String id;
            private String name;
            private String area;
            private String areaname;
            private String address;
            private String brandname;
            private String type;
            private String discount;
            private String exhaust;
            private String position;
            private String lon;
            private String lat;
            /**
             * E90 : 6.55
             * E93 : 7.08
             * E97 : 7.5
             * E0 : 5.63
             */

            private PriceBean price;
            /**
             * E90 : 6.98
             * E93 : 6.98
             * E97 : 7.31
             * E0 : 5.53
             */

            private GastpriceBean gastprice;
            private String fwlsmc;
            private int distance;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getArea() {
                return area;
            }

            public void setArea(String area) {
                this.area = area;
            }

            public String getAreaname() {
                return areaname;
            }

            public void setAreaname(String areaname) {
                this.areaname = areaname;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public String getBrandname() {
                return brandname;
            }

            public void setBrandname(String brandname) {
                this.brandname = brandname;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getDiscount() {
                return discount;
            }

            public void setDiscount(String discount) {
                this.discount = discount;
            }

            public String getExhaust() {
                return exhaust;
            }

            public void setExhaust(String exhaust) {
                this.exhaust = exhaust;
            }

            public String getPosition() {
                return position;
            }

            public void setPosition(String position) {
                this.position = position;
            }

            public String getLon() {
                return lon;
            }

            public void setLon(String lon) {
                this.lon = lon;
            }

            public String getLat() {
                return lat;
            }

            public void setLat(String lat) {
                this.lat = lat;
            }

            public PriceBean getPrice() {
                return price;
            }

            public void setPrice(PriceBean price) {
                this.price = price;
            }

            public GastpriceBean getGastprice() {
                return gastprice;
            }

            public void setGastprice(GastpriceBean gastprice) {
                this.gastprice = gastprice;
            }

            public String getFwlsmc() {
                return fwlsmc;
            }

            public void setFwlsmc(String fwlsmc) {
                this.fwlsmc = fwlsmc;
            }

            public int getDistance() {
                return distance;
            }

            public void setDistance(int distance) {
                this.distance = distance;
            }

            public static class PriceBean {
                private String E90;
                private String E93;
                private String E97;
                private String E0;

                public String getE90() {
                    return E90;
                }

                public void setE90(String E90) {
                    this.E90 = E90;
                }

                public String getE93() {
                    return E93;
                }

                public void setE93(String E93) {
                    this.E93 = E93;
                }

                public String getE97() {
                    return E97;
                }

                public void setE97(String E97) {
                    this.E97 = E97;
                }

                public String getE0() {
                    return E0;
                }

                public void setE0(String E0) {
                    this.E0 = E0;
                }
            }

            public static class GastpriceBean {
                @SerializedName("90#")
                private String E90;
                @SerializedName("93#")
                private String E93;
                @SerializedName("97#")
                private String E97;
                @SerializedName("0#车柴")
                private String E0;

                public String getE90() {
                    return E90;
                }

                public void setE90(String E90) {
                    this.E90 = E90;
                }

                public String getE93() {
                    return E93;
                }

                public void setE93(String E93) {
                    this.E93 = E93;
                }

                public String getE97() {
                    return E97;
                }

                public void setE97(String E97) {
                    this.E97 = E97;
                }

                public String getE0() {
                    return E0;
                }

                public void setE0(String E0) {
                    this.E0 = E0;
                }
            }
        }
    }
}
