package teamgodeater.hicarnet.MVP.Base;

import teamgodeater.hicarnet.Utils.Tutil;

/**
 * Created by G on 2016/6/18 0018.
 */

public abstract class BasePresenter<V ,M> {
    public M mModel;
    public V mView;

    public BasePresenter() {
        mModel = Tutil.getT(this, 1);
    }

    public void setView(V v) {
        this.mView = v;
        this.onStart();
    }

    protected abstract void onStart();

    protected void onDestroy() {
        mView = null;
    }
}
