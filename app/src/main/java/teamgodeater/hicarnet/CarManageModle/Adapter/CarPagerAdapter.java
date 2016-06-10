package teamgodeater.hicarnet.CarManageModle.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import teamgodeater.hicarnet.CarManageModle.Fragment.CarDetailFragment;
import teamgodeater.hicarnet.Help.UserDataHelp;

/**
 * Created by G on 2016/6/10 0010.
 */

public class CarPagerAdapter extends FragmentPagerAdapter {

    public CarPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        CarDetailFragment carDetailFragment = new CarDetailFragment();
        carDetailFragment.setIndex(position);
        return carDetailFragment;
    }

    @Override
    public int getCount() {
        int count = 0;
        if (UserDataHelp.userCarInfoDatas != null && UserDataHelp.userCarInfoDatas.size() > 0) {
            count = UserDataHelp.userCarInfoDatas.size();
        } else {
            count = 1;
        }
        return count;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (UserDataHelp.userCarInfoDatas != null && UserDataHelp.userCarInfoDatas.size() > 0)
            return UserDataHelp.userCarInfoDatas.get(position).getBrand();
        return "添加一个";
    }
}
