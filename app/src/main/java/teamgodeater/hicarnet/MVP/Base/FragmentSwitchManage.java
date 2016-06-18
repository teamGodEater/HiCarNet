package teamgodeater.hicarnet.MVP.Base;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import teamgodeater.hicarnet.MVP.Ui.Main.Main;
import teamgodeater.hicarnet.R;

;

/**
 * Created by G on 2016/6/14 0014.
 */

public class FragmentSwitchManage {
    public static FragmentManager mFragmentManage;
    public static ArrayList<teamgodeater.hicarnet.MVP.Base.BaseFragment> mFragments = new ArrayList<>();

    public static void switchFragment(teamgodeater.hicarnet.MVP.Base.BaseFragment to) {
        if (to instanceof Main) {
            int size = mFragments.size();
            for (int i = size - 1; i >= 0; i--) {
                removeFragment(mFragments.get(i));
            }
        }

        if (to.isAdded()) {
            bringToTop(to);
        } else {
            FragmentTransaction  beginTransaction = mFragmentManage.beginTransaction();
            beginTransaction.add(R.id.MainContain, to).commitAllowingStateLoss();
            mFragments.add(to);
        }
    }

    public static void destroyTop(long delay) {
        final teamgodeater.hicarnet.MVP.Base.BaseFragment baseFragment = getTopFragment();

        List<Fragment> fragments = mFragmentManage.getFragments();
        if (fragments.indexOf(baseFragment) == fragments.size() - 1) {
            baseFragment.fadeHideSelf(delay - 50);
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                removeFragment(baseFragment);
            }
        }, delay);
    }

    public static void destroyTopShowBefore(long delay) {
        FragmentTransaction beginTransaction = mFragmentManage.beginTransaction();
        teamgodeater.hicarnet.MVP.Base.BaseFragment fragment = mFragments.get(mFragments.size() - 2);
        beginTransaction.show(fragment).commitAllowingStateLoss();
        destroyTop(delay);
    }

    private static void bringToTop(teamgodeater.hicarnet.MVP.Base.BaseFragment to) {
        int index = mFragments.indexOf(to);
        int size = mFragments.size();

        FragmentTransaction transaction = mFragmentManage.beginTransaction();
        transaction.show(to).commitAllowingStateLoss();
        for (int i = index + 1;i < size;i++) {

        }

        boolean isOk = false;
        for (int i = 0; i < size; i++) {
            teamgodeater.hicarnet.MVP.Base.BaseFragment nowFragment = mFragments.get(i);
            if (!isOk && nowFragment.equals(to)) {
                isOk = true;
            }else  {
                if (i < size - 1) {
                    nowFragment.hideSelf(0L);
                    mFragments.set(i - 1, nowFragment);
                }
                else {
                    nowFragment.fadeHideSelf(280);
                    mFragments.set(i, to);
                }
            }

        }
    }

    private static void removeFragment(teamgodeater.hicarnet.MVP.Base.BaseFragment baseFragment) {
        if (!(baseFragment instanceof Main)) {
            Logger.d(baseFragment.getClass().getName());
            mFragments.remove(baseFragment);
            FragmentTransaction beginTransaction = mFragmentManage.beginTransaction();
            beginTransaction.remove(baseFragment).commitAllowingStateLoss();
        }
    }

    public static BaseFragment getTopFragment() {
        return mFragments.get(mFragments.size() - 1);
    }

}
