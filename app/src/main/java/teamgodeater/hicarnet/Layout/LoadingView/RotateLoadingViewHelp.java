package teamgodeater.hicarnet.Layout.LoadingView;

import android.graphics.PorterDuff;
import android.os.Handler;
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

    RotateLoading mRotaleLoading;
    TextView mLoadingTip;
    View mLoadingBackGround;
    boolean hasBackGround = false;
    private ViewGroup mInflate;
    private long mDuration = 300L;

    public RotateLoadingViewHelp(ViewGroup vp) {
        mInflate = (ViewGroup) LayoutInflater.from(vp.getContext())
                .inflate(R.layout.view_rotation_loadding, vp, false);
        mRotaleLoading = (RotateLoading) mInflate.findViewById(R.id.rotateLoading);
        mLoadingTip = (TextView) mInflate.findViewById(R.id.loadingTip);
        mLoadingBackGround = mInflate.findViewById(R.id.loadingBackGround);
        mInflate.setVisibility(View.GONE);
        vp.addView(mInflate);
    }

    @Override
    public void startAnimator(String tip) {
        mLoadingTip.setText(tip);
        if (mRotaleLoading.isStart())
            return;

        mInflate.setVisibility(View.VISIBLE);

        if (hasBackGround)
            mLoadingBackGround.setVisibility(View.VISIBLE);
        else
            mLoadingBackGround.setVisibility(View.GONE);
        mRotaleLoading.start();
    }

    @Override
    public void stopAnimator() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mInflate == null)
                    return;
                mRotaleLoading.stop();
                mInflate.setVisibility(View.GONE);
            }
        }, mDuration);
    }

    @Override
    public void destroyView() {
        if (mInflate != null) {
            if (mRotaleLoading.isStart())
                mRotaleLoading.stop();
            mInflate.removeAllViews();
        }
        mInflate = null;
    }

    @Override
    public void setTextBackGroundColor(int color) {
        mLoadingTip.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

    @Override
    public void setTextColor(int color) {
        mLoadingTip.setTextColor(color);
    }

    @Override
    public void setTextSize(int size) {
        mLoadingTip.setTextSize(size);
    }

    public void setRotaleColor(int color) {
        mRotaleLoading.setLoadingColor(color);
    }

    @Override
    public void setLoadingBg(boolean Visible, int color, View.OnClickListener listener) {
        hasBackGround = Visible;
        if (color != -1)
        mLoadingBackGround.setBackgroundColor(color);
        if (listener != null)
            mLoadingBackGround.setOnClickListener(listener);
    }

    @Override
    public void setMinShowTime(long duration) {
        mDuration = duration;
    }


}
