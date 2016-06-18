package teamgodeater.hicarnet.MVP.Base;

import teamgodeater.hicarnet.MVP.Utils.Tutil;

/**
 * Created by G on 2016/6/18 0018.
 */

public abstract class BasePresenter<V ,M extends BaseModel > {
    public M mModel;
    public V mView;

    public BasePresenter() {
        mModel = Tutil.getT(this, 1);
    }

    public void setView(V v) {
        this.mView = v;
        this.onStart();
    }

    public abstract void onStart();

    public void onDestroy() {
        mView = null;
    }
}
