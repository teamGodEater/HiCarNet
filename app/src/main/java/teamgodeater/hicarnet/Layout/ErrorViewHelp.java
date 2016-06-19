package teamgodeater.hicarnet.Layout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import teamgodeater.hicarnet.R;
import teamgodeater.hicarnet.Widget.RippleBackGroundView;

/**
 * Created by G on 2016/6/18 0018.
 */

public class ErrorViewHelp {
    ImageView mImageView;
    TextView mTip;
    RippleBackGroundView mButton;
    private ViewGroup mInflate;

    public ErrorViewHelp(ViewGroup vp) {
        mInflate = (ViewGroup) LayoutInflater.from(vp.getContext())
                .inflate(R.layout.view_error_tip, vp, false);
        mImageView = (ImageView) mInflate.findViewById(R.id.imageView);
        mTip = (TextView) mInflate.findViewById(R.id.textView);
        mButton = (RippleBackGroundView) mInflate.findViewById(R.id.rippleView);
        hideError();
        vp.addView(mInflate);
    }

    public void destroyView() {
        if (mInflate != null) {
            mInflate.removeAllViews();
            mInflate = null;
        }
    }

    public void hideError() {
        if (mImageView != null)
            mInflate.setVisibility(View.GONE);
    }

    public void showError(int id, String tip, String button, View.OnClickListener listener) {
        mImageView.setImageResource(id);
        mTip.setText(tip);
        mButton.setText(button);
        mButton.setOnClickListener(listener);
        mInflate.setVisibility(View.VISIBLE);
    }

}
