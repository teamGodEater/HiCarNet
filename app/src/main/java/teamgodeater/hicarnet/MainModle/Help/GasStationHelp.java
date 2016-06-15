package teamgodeater.hicarnet.MainModle.Help;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import teamgodeater.hicarnet.Adapter.BaseItem2LineAdapter;
import teamgodeater.hicarnet.Data.BaseItem2LineData;
import teamgodeater.hicarnet.Data.GasstationData;
import teamgodeater.hicarnet.Help.UserDataHelp;
import teamgodeater.hicarnet.R;

/**
 * Created by G on 2016/6/15 0015.
 */

public class GasStationHelp {

    private List<BaseItem2LineData> sbPgasstationData;

    RecyclerView recyclerView;

    public GasStationHelp(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public boolean setBottomRv(BaseItem2LineAdapter.OnItemClickListener listener) {
        if (sbPgasstationData == null)
            sbPgasstationData = getSBPgasstationData();
        if (sbPgasstationData == null)
            return false;
        BaseItem2LineAdapter adapter = new BaseItem2LineAdapter(sbPgasstationData);
        adapter.setOnClickListener(listener);
        recyclerView.setAdapter(adapter);
        return true;
    }

    private List<BaseItem2LineData> getSBPgasstationData() {
        GasstationData gasstationData = UserDataHelp.gasstationData;
        if (gasstationData == null || !gasstationData.getResult().equals("200"))
            return null;
        List<BaseItem2LineData> dataList = new ArrayList<>();
        for (GasstationData.ResultBean.DataBean bean : gasstationData.getResult().getData()) {
            BaseItem2LineData data = new BaseItem2LineData();
            data.icoLeft = R.drawable.ic_location;
            data.title = bean.getName();
            data.tip = bean.getAddress();
            data.tipRight = bean.getBrandname();
            data.icoRight = R.drawable.ic_keyboard_arrow_right;
            data.hasDivider = true;
        }

        if (dataList.size() == 0)
            return null;
        return dataList;
    }
}
