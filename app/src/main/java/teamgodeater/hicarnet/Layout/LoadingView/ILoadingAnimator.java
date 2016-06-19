package teamgodeater.hicarnet.Layout.LoadingView;

import android.view.View;

/**
 * Created by G on 2016/6/18 0018.
 */

public interface ILoadingAnimator {
    void startAnimator(String tip);

    void stopAnimator();

    void destroyView();

    void setTextColor(int color);

    void setTextBackGroundColor(int color);

    void setTextSize(int size);

    void setLoadingBg(boolean Visible, int color, View.OnClickListener listener);

    void setMinShowTime(long duration);
}
