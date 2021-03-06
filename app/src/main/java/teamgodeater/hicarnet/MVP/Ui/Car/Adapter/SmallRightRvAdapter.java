package teamgodeater.hicarnet.MVP.Ui.Car.Adapter;

import java.util.List;

import teamgodeater.hicarnet.Adapter.BaseItem2LineAdapter;
import teamgodeater.hicarnet.Data.BaseItem2LineData;
import teamgodeater.hicarnet.R;
import teamgodeater.hicarnet.Utils.Utils;

/**
 * Created by G on 2016/6/19 0019.
 */

public class SmallRightRvAdapter extends BaseItem2LineAdapter {
    public SmallRightRvAdapter(List<? extends BaseItem2LineData> l) {
        super(l);
    }

    @Override
    public void onBindViewHolder(BaseItem2LineHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (list.get(position).icoRight == R.drawable.ic_beenhere) {
            int i = Utils.dp2Px(4);
            holder.icoRight.setPadding(i, i, i, i);
        } else if (position == 0) {
            holder.icoLeft.setScaleX(1.5f);
            holder.icoLeft.setScaleY(1.5f);
        }
    }
}
