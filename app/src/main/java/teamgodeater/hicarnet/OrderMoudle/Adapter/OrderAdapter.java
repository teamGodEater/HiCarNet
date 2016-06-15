package teamgodeater.hicarnet.OrderMoudle.Adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by G on 2016/6/15 0015.
 */

public class OrderAdapter extends PagerAdapter {

    private ArrayList<RecyclerView> pagerList;

    public void setList(ArrayList<RecyclerView> list) {
        pagerList = list;
    }

    public ArrayList<RecyclerView> getList() {
        return pagerList;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public int getCount() {
        return pagerList.size();
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

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "未消费";
        }else {
            return "全部订单";
        }
    }
}
