package teamgodeater.hicarnet.MVP.Ui.Main.Adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import teamgodeater.hicarnet.R;
import teamgodeater.hicarnet.Utils.Utils;

/**
 * Created by G on 2016/6/20 0020.
 */

public class BottomPagerAdapter extends PagerAdapter {

    List<View> pagerList = new ArrayList<>();
    public RecyclerView rv1;
    public RecyclerView rv2;


    public BottomPagerAdapter(Context context) {
        View pager1 = LayoutInflater.from(context).inflate(R.layout.item_main_bottom_pager1, null, false);
        rv1 = (RecyclerView) pager1.findViewById(R.id.recyclerView);
        rv2 = new RecyclerView(Utils.getContext());

        rv1.setLayoutManager(new LinearLayoutManager(context));
        rv2.setLayoutManager(new LinearLayoutManager(context));

        rv1.setHasFixedSize(true);
        rv2.setHasFixedSize(true);

        pagerList.add(pager1);
        pagerList.add(rv2);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(pagerList.get(position));
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(pagerList.get(position), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);//添加页卡
        return pagerList.get(position);
    }

}
