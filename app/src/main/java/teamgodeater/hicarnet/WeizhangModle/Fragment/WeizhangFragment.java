package teamgodeater.hicarnet.WeizhangModle.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.Bind;
import butterknife.ButterKnife;
import teamgodeater.hicarnet.Fragment.BaseFragment;
import teamgodeater.hicarnet.R;

/**
 * Created by G on 2016/6/11 0011.
 */

public class WeizhangFragment extends BaseFragment {
    @Bind(R.id.button)
    Button button;

    @NonNull
    @Override
    protected SupportWindowsParams onCreateSupportViewParams() {
        SupportWindowsParams params = new SupportWindowsParams();
        params.rootLayoutId = R.layout.frgm_weizhang;
        return params;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootContain);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageActivity.switchFragment(new CitySelectFragment());
                hideSelf();
            }
        });
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
