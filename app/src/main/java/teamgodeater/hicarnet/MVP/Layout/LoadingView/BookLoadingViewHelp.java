package teamgodeater.hicarnet.MVP.Layout.LoadingView;

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
    ViewGroup mViewGroup;
    BookLoading mBookLoading;
    TextView mLoadingTip;
    private final View mInflate;

    public BookLoadingViewHelp(ViewGroup vp) {
        mInflate = LayoutInflater.from(vp.getContext())
                .inflate(R.layout.view_book_loading, vp, true);
        mBookLoading = (BookLoading) mInflate.findViewById(R.id.bookLoading);
        mLoadingTip = (TextView) mInflate.findViewById(R.id.loadingTip);
        mInflate.setVisibility(View.GONE);
    }

    @Override
    public void startAnimator(String tip) {
        mLoadingTip.setText(tip);
        mInflate.setVisibility(View.VISIBLE);
        mBookLoading.start();
    }

    @Override
    public void stopAnimator() {
        if (mViewGroup == null)
            return;
        mBookLoading.stop();
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
