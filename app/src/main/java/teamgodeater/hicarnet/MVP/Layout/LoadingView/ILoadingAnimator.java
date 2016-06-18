package teamgodeater.hicarnet.MVP.Layout.LoadingView;

/**
 * Created by G on 2016/6/18 0018.
 */

public interface ILoadingAnimator {
    void startAnimator(String tip);

    void stopAnimator();

    void destroyView();

    void setTextColor(int color);

    void setTextSize(int size);
}
