package teamgodeater.hicarnet.MVP.Ui.Sign;

import android.graphics.Bitmap;

import teamgodeater.hicarnet.Help.RestClientHelp;
import teamgodeater.hicarnet.RestClient.RestClient;

/**
 * Created by G on 2016/6/19 0019.
 */

public class SignPresent extends SignConstractor.Present {


    @Override
    protected void onStart() {

    }

    @Override
    public void Sign(final String username, final String password, Bitmap headImage) {

        new RestClientHelp().regist(username, password, new RestClient.OnResultListener<String>() {
            @Override
            public void succeed(String bean) {
                mView.onSucceedLogin();
                loginTry = 3;
                login(username, password);
            }

            @Override
            public void error(int code) {
                mView.onErrorSign(code);
            }
        });
    }

    private int loginTry ;

    private void login(final String username, final String password) {
        new RestClientHelp().login(new RestClient.OnResultListener<String>() {
            @Override
            public void succeed(String bean) {
                mView.onDone();
            }

            @Override
            public void error(int code) {
                loginTry--;
                if (loginTry >= 0) {
                    login(username, password);
                } else {
                    mView.onErrorLogin(code);
                }
            }
        });
    }

    // TODO: 2016/6/19 0019 上传头像
    int setHeadImageTry = 3;

    private void setHeadImage(Bitmap headImage) {
    }
}
