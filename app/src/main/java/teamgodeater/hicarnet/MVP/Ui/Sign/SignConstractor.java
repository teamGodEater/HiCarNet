package teamgodeater.hicarnet.MVP.Ui.Sign;

import android.graphics.Bitmap;

import teamgodeater.hicarnet.MVP.Base.BasePresenter;

/**
 * Created by G on 2016/6/18 0018.
 */

public interface SignConstractor {
    interface View {
        void onErrorSign(int code);

        void onErrorLogin(int code);

        void onErrorSetImage(int code);

        void onSucceedLogin();

        void onDone();
    }

    interface Model {

    }

    abstract class Present extends BasePresenter<View, Model> {
        abstract public void Sign(String username, String password, Bitmap headImage);
    }
}
