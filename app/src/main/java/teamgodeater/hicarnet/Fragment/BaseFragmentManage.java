package teamgodeater.hicarnet.Fragment;

import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;

import teamgodeater.hicarnet.MainModle.Fragment.MainFragment;
import teamgodeater.hicarnet.R;

/**
 * Created by G on 2016/6/14 0014.
 */

public class BaseFragmentManage {
    public static FragmentManager fragmentManager;
    public static ArrayList<BaseFragment> fragments = new ArrayList<>();

    public static void switchFragment(BaseFragment to) {
        FragmentTransaction beginTransaction;
        if (to instanceof MainFragment) {
            int size = fragments.size();
            for (int i = size - 1; i >= 0; i--) {
                removeFragment(fragments.get(i));
            }
        }
        beginTransaction = fragmentManager.beginTransaction();
        if (to.isAdded()) {
            beginTransaction.show(to).commitAllowingStateLoss();
            bringToTop(to);
        } else {
            beginTransaction.add(R.id.MainContain, to).commitAllowingStateLoss();
            fragments.add(to);
        }
    }

    public static void hideTopDelay(long delay) {
        final BaseFragment baseFragment = getTopFragment();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
                beginTransaction.hide(baseFragment).commitAllowingStateLoss();
            }
        }, delay);
    }

    public static void destroyTop(long delay) {
        final BaseFragment baseFragment = getTopFragment();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                removeFragment(baseFragment);
            }
        }, delay);
    }

    public static void destroyTopShowBefore(long delay) {
        FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
        BaseFragment fragment = fragments.get(fragments.size() - 2);
        beginTransaction.show(fragment).commitAllowingStateLoss();
        destroyTop(delay);
    }

    private static void bringToTop(BaseFragment to) {
        int size = fragments.size();
        if (size <= 1)
            return;
        boolean isOk = false;
        for (int i = 0; i < size; i++) {
            BaseFragment nowFragment = fragments.get(i);
            if (!isOk && nowFragment.equals(to))
                isOk = true;
            if (isOk) {
                if (i < size - 1)
                    fragments.set(i, fragments.get(i + 1));
                else
                    fragments.set(i, to);
            }

        }
    }

    private static void removeFragment(BaseFragment baseFragment) {
        if (!(baseFragment instanceof MainFragment)) {
            Logger.d(baseFragment.getClass().getName());
            fragments.remove(baseFragment);
            FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
            beginTransaction.remove(baseFragment).commitAllowingStateLoss();
        }
    }

    public static BaseFragment getTopFragment() {
        return fragments.get(fragments.size() - 1);
    }

}
