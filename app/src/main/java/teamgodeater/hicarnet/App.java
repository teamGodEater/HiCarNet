package teamgodeater.hicarnet;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MapView;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.orhanobut.logger.Logger;

import java.io.File;

import teamgodeater.hicarnet.Help.LocationHelp;
import teamgodeater.hicarnet.Help.Utils;

/**
 * Created by G on 2016/5/18 0018.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Logger.init("GGGGG");
        Utils.setApplication(this);
        SDKInitializer.initialize(this);
        LocationHelp.InitLLocationClien(this);
        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=571b0efa");
        setCustomMapStyle();
    }

    private void setCustomMapStyle() {
        File skin = Utils.assest2Phone("custom_config(primary).txt");
        MapView.setCustomMapStylePath(skin.getAbsolutePath());
    }
}
