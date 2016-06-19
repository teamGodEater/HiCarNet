package teamgodeater.hicarnet.MVP.Ui.Login;

import android.graphics.Bitmap;

import teamgodeater.hicarnet.MVP.Base.BasePresenter;

/**
 * Created by G on 2016/6/18 0018.
 */

public interface LoginContractor {
    interface View{
        void setHeadImage(Bitmap bitmap);

        void loginError(int code);

        void loginSucceed();

        void loadUserDataDone();
    }

    interface Model{
    }

    abstract class Present extends BasePresenter<LoginFragment, Model> {
        abstract public void login(String username,String paasword);

        abstract public void getHeadImage(String username);
    }
}
