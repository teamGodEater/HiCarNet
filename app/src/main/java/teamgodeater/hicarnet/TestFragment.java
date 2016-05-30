package teamgodeater.hicarnet;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import teamgodeater.hicarnet.Activity.ManageActivity;

/**
 * Created by G on 2016/5/23 0023.
 */

public class TestFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ViewGroup inflate = (ViewGroup) inflater.inflate(R.layout.frgm_main_main, container, false);
        Handler g = new Handler();

        g.postDelayed(new Runnable() {
            @Override
            public void run() {
                getManageActivity().createMapView();
                inflate.addView(getManageActivity().getMainMapView(),0,
                        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                getManageActivity().onMapResume();
            }
        }, 3000);
        return inflate;
    }

    private ManageActivity getManageActivity() {
        return (ManageActivity)getActivity();
    }

}
