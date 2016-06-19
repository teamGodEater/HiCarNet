package teamgodeater.hicarnet.MVP.Ui.Main;

import android.support.annotation.NonNull;

import teamgodeater.hicarnet.MVP.Base.BaseFragment;
import teamgodeater.hicarnet.MVP.Base.WindowsParams;

import static teamgodeater.hicarnet.C.VT_MAIN;

/**
 * Created by G on 2016/6/18 0018.
 */

public class Main extends BaseFragment {
    @NonNull
    @Override
    protected WindowsParams onCreateSupportViewParams(WindowsParams windowsParams) {
        return windowsParams;
    }

    @Override
    public String getType() {
        return VT_MAIN;
    }
}
