package teamgodeater.hicarnet.MVP.Layout.LoadingView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.victor.loading.rotate.RotateLoading;

import teamgodeater.hicarnet.R;

/**
 * Created by G on 2016/6/18 0018.
 */

public class RotateLoadingViewHelp implements ILoadingAnimator {

    ViewGroup mViewGroup;
    RotateLoading mRotaleLoading;
    TextView mLoadingTip;
    private final View mInflate;

    public RotateLoadingViewHelp(ViewGroup vp) {
        mInflate = LayoutInflater.from(vp.getContext())
                .inflate(R.layout.view_rotation_loadding, vp, true);
        mRotaleLoading= (RotateLoading) mInflate.findViewById(R.id.rotateLoading);
        mLoadingTip = (TextView) mInflate.findViewById(R.id.loadingTip);
        mInflate.setVisibility(View.GONE);
    }

    @Override
    public void startAnimator(String tip) {
        mLoadingTip.setText(tip);
        mInflate.setVisibility(View.VISIBLE);
        mRotaleLoading.start();
    }

    @Override
    public void stopAnimator() {
        if (mViewGroup == null)
            return;
        mRotaleLoading.stop();
        mInflate.setVisibility(View.GONE);
    }

    @Override
    public void destroyView() {
        mViewGroup = null;
    }

    @Override
    public void setTextColor(int color) {
        mLoadingTip.setTextColor(color);
    }

    @Override
    public void setTextSize(int size) {
        mLoadingTip.setTextSize(size);
    }


}
