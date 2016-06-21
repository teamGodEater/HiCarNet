package teamgodeater.hicarnet.Layout.LoadingView;

import android.graphics.PorterDuff;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.victor.loading.book.BookLoading;

import teamgodeater.hicarnet.R;

/**
 * Created by G on 2016/6/18 0018.
 */

public class BookLoadingViewHelp implements ILoadingAnimator {

    BookLoading mBookLoading;
    TextView mLoadingTip;
    View mLoadingBackGround;
    boolean hasBackGround = false;
    private ViewGroup mInflate;
    private long mDuration = 400L;

    public BookLoadingViewHelp(ViewGroup vp) {
        mInflate = (ViewGroup) LayoutInflater.from(vp.getContext())
                .inflate(R.layout.view_book_loading, vp, false);
        mBookLoading = (BookLoading) mInflate.findViewById(R.id.bookLoading);
        mLoadingTip = (TextView) mInflate.findViewById(R.id.loadingTip);
        mLoadingBackGround = mInflate.findViewById(R.id.loadingBackGround);
        mInflate.setVisibility(View.GONE);
        vp.addView(mInflate);
    }

    @Override
    public void startAnimator(String tip) {
        mLoadingTip.setText(tip);
        if (mBookLoading.isStart())
            return;

        mInflate.setVisibility(View.VISIBLE);
        if (hasBackGround)
            mLoadingBackGround.setVisibility(View.VISIBLE);
        else
            mLoadingBackGround.setVisibility(View.GONE);
        mBookLoading.start();
    }

    @Override
    public void stopAnimator() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mInflate == null)
                    return;
                mBookLoading.stop();
                mInflate.setVisibility(View.GONE);
            }
        }, mDuration);
    }

    @Override
    public void destroyView() {
        if (mInflate != null) {
            if (mBookLoading.isStart())
                mBookLoading.stop();
            mInflate.removeAllViews();
            mInflate = null;
        }
    }

    @Override
    public void setTextColor(int color) {
        mLoadingTip.setTextColor(color);
    }

    @Override
    public void setTextBackGroundColor(int color) {
        mLoadingTip.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

    @Override
    public void setTextSize(int size) {
        mLoadingTip.setTextSize(size);
    }

    @Override
    public void setLoadingBg(boolean Visible, int color, View.OnClickListener listener) {
        setLoadingBg(Visible, false, color, listener);
    }

    @Override
    public void setLoadingBg(boolean Visible, int color) {
        setLoadingBg(Visible, false, color, null);

    }

    @Override
    public void setLoadingBg(boolean Visible) {
        setLoadingBg(Visible, true, -1, null);
    }

    private void setLoadingBg(boolean Visible, boolean defaultColor, int color, View.OnClickListener listener) {
        hasBackGround = Visible;
        if (!defaultColor)
            mLoadingBackGround.setBackgroundColor(color);
        if (listener != null)
            mLoadingBackGround.setOnClickListener(listener);
    }

    @Override
    public void setMinShowTime(long duration) {
        mDuration = duration;
    }
}
