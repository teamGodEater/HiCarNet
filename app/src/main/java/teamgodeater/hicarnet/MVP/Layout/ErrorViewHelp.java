package teamgodeater.hicarnet.MVP.Layout;

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
    ViewGroup mViewGroup;
    ImageView mImageView;
    TextView mTip;
    RippleBackGroundView mButton;
    private final View mInflate;

    public ErrorViewHelp(ViewGroup vp) {
        mInflate = LayoutInflater.from(vp.getContext())
                .inflate(R.layout.view_error_tip, vp, true);
        mImageView = (ImageView) mInflate.findViewById(R.id.imageView);
        mTip = (TextView) mInflate.findViewById(R.id.textView);
        mButton = (RippleBackGroundView) mInflate.findViewById(R.id.rippleView);
    }

    public void destroy() {
        mViewGroup = null;
    }

    public void hideError() {
        if (mViewGroup == null)
            return;
        mInflate.setVisibility(View.GONE);
    }

    public void showError(int id, String tip, String button, View.OnClickListener listener) {
        mImageView.setImageResource(id);
        mTip.setText(tip);
        mButton.setText(button);
        mButton.setOnClickListener(listener);
    }

}
