package teamgodeater.hicarnet.MVP.Ui.Login;

import teamgodeater.hicarnet.Help.ConditionTask;
import teamgodeater.hicarnet.Help.DurationOnceTask;
import teamgodeater.hicarnet.Help.RestClientHelp;
import teamgodeater.hicarnet.Help.UserDataHelp;
import teamgodeater.hicarnet.RestClient.RestClient;


/**
 * Created by G on 2016/6/18 0018.
 */

public class LoginPresent extends LoginContractor.Present {

    DurationOnceTask headImageTask = new DurationOnceTask(700, new Runnable() {
        @Override
        public void run() {
            String username = mView.username.getText().toString();
            UserDataHelp.getHeadBitmap(username, new UserDataHelp.OnDoneListener() {
                @Override
                public void onDone(int code) {
                    mView.setHeadImage(UserDataHelp.headImage);
                }
            });
        }
    });

    ConditionTask userDataConditionTask = new ConditionTask(4, new Runnable() {
        @Override
        public void run() {
            mView.loadUserDataDone();
        }
    });

    @Override
    protected void onStart() {

    }

    @Override
    public void login(String un, String pw) {
        RestClientHelp.username = un;
        RestClientHelp.password = pw;

        new RestClientHelp().login(new RestClient.OnResultListener<String>() {
            @Override
            public void succeed(String bean) {
                mView.loginSucceed();
                getUserData();
            }

            @Override
            public void error(int code) {
                mView.loginError(code);
            }
        });
    }

    @Override
    public void getHeadImage(String username) {
        headImageTask.excute();
    }

    private void getUserData() {
        UserDataHelp.getUserInfoData(new UserDataHelp.OnDoneListener() {
            @Override
            public void onDone(int code) {
                userDataConditionTask.excute();
            }
        });
        UserDataHelp.getUserCarInfoDatas(new UserDataHelp.OnDoneListener() {
            @Override
            public void onDone(int code) {
                userDataConditionTask.excute();
            }
        });
        UserDataHelp.getUserOrderDatas(new UserDataHelp.OnDoneListener() {
            @Override
            public void onDone(int code) {
                userDataConditionTask.excute();
            }
        });
        UserDataHelp.getUserPointData(new UserDataHelp.OnDoneListener() {
            @Override
            public void onDone(int code) {
                userDataConditionTask.excute();
            }
        });
    }
}
