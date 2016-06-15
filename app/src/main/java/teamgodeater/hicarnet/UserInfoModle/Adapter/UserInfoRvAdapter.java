package teamgodeater.hicarnet.UserInfoModle.Adapter;

import java.util.List;

import teamgodeater.hicarnet.Adapter.BaseItem2LineAdapter;
import teamgodeater.hicarnet.Data.BaseItem2LineData;

/**
 * Created by G on 2016/6/14 0014.
 */

public class UserInfoRvAdapter extends BaseItem2LineAdapter {
    public UserInfoRvAdapter(List<? extends BaseItem2LineData> l) {
        super(l);
    }

    @Override
    public void onBindViewHolder(BaseItem2LineHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (position == 0) {
            holder.icoLeft.setScaleX(1.5f);
            holder.icoLeft.setScaleY(1.5f);
        }
    }
}
