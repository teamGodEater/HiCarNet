package teamgodeater.hicarnet.MainModle.Help;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import teamgodeater.hicarnet.Activity.ManageActivity;
import teamgodeater.hicarnet.Adapter.BaseItem2LineAdapter;
import teamgodeater.hicarnet.Help.Utils;

/**
 * Created by G on 2016/5/29 0029.
 */

public class BottomHelp implements ViewPager.OnPageChangeListener {

    private final ManageActivity manageActivity;
    private final ViewPager viewPager;
    private final View select1;
    private final View select2;
    private final mPagerAdapter adapter;
    private List<View> pagerList;

    public BottomHelp(ManageActivity m, ViewPager b, View s1, View s2) {
        manageActivity = m;
        viewPager = b;
        select1 = s1;
        select2 = s2;

        initView();

        adapter = new mPagerAdapter();
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
        changePagerSelect(0);
    }


    //------------------------------pagerStatusChangeListener---------------------------------------

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        changePagerSelect(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    //-------------------------------------------pagerAdapterClass----------------------------------
    class mPagerAdapter extends PagerAdapter {

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

    }

    //------------------------------------method----------------------------------------------------
    //------------------public
    public void setCurrentItem(int position) {
        if (position == 0) {
            viewPager.setCurrentItem(0, true);
        } else {
            viewPager.setCurrentItem(1, true);
        }
    }

    public void setRvAdapter(int position, BaseItem2LineAdapter adapter) {
        RecyclerView view = (RecyclerView) pagerList.get(position);
        view.setAdapter(adapter);
    }

    public RecyclerView.Adapter getRvAdapter(int position) {
        RecyclerView view = (RecyclerView) pagerList.get(position);
        return view.getAdapter();
    }

    //------------------private
    private void changePagerSelect(int position) {
        if (position == 0) {
            select1.setAlpha(0.54f);
            select2.setAlpha(0.34f);
        } else {
            select1.setAlpha(0.34f);
            select2.setAlpha(0.53f);
        }
    }

    private void initView() {
        pagerList = new ArrayList<>();

        RecyclerView view1 = new RecyclerView(Utils.getContext());
        RecyclerView view2 = new RecyclerView(Utils.getContext());

        view1.setHasFixedSize(true);
        view2.setHasFixedSize(true);

        view1.setLayoutManager(new LinearLayoutManager(Utils.getContext()));
        view2.setLayoutManager(new LinearLayoutManager(Utils.getContext()));

        pagerList.add(view1);
        pagerList.add(view2);
    }

}
