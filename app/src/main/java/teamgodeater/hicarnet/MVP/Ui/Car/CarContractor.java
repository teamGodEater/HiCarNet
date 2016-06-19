package teamgodeater.hicarnet.MVP.Ui.Car;

import teamgodeater.hicarnet.Data.UserCarInfoData;
import teamgodeater.hicarnet.MVP.Base.BasePresenter;

/**
 * Created by G on 2016/6/18 0018.
 */

public interface CarContractor {

    interface View{
        void onStartGetData();

        void errorNoNet();

        void errorNoData();

        void errorNoLogin();

        void errorNoCarInfo();

        void errorUnKnow();

        void setView(android.view.View[] carViews);
    }

    interface Model {

    }

    abstract class Present extends BasePresenter<CarFragment, Model> {
        abstract void getCarDatas();

        abstract void addCarData(UserCarInfoData carInfoData);

        abstract void legalCarData();
    }
}
