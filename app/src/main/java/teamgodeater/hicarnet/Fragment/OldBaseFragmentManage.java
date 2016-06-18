package teamgodeater.hicarnet.Fragment;

import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;

import teamgodeater.hicarnet.MainModle.Fragment.MainFragmentOld;
import teamgodeater.hicarnet.R;

/**
 * Created by G on 2016/6/14 0014.
 */

public class OldBaseFragmentManage {
    public static FragmentManager fragmentManager;
    public static ArrayList<OldBaseFragment> fragments = new ArrayList<>();

    public static void switchFragment(OldBaseFragment to) {
        FragmentTransaction beginTransaction;
        if (to instanceof MainFragmentOld) {
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
        final OldBaseFragment oldBaseFragment = getTopFragment();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
                beginTransaction.hide(oldBaseFragment).commitAllowingStateLoss();
            }
        }, delay);
    }

    public static void destroyTop(long delay) {
        final OldBaseFragment oldBaseFragment = getTopFragment();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                removeFragment(oldBaseFragment);
            }
        }, delay);
    }

    public static void destroyTopShowBefore(long delay) {
        FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
        OldBaseFragment fragment = fragments.get(fragments.size() - 2);
        beginTransaction.show(fragment).commitAllowingStateLoss();
        destroyTop(delay);
    }

    private static void bringToTop(OldBaseFragment to) {
        int size = fragments.size();
        if (size <= 1)
            return;
        boolean isOk = false;
        for (int i = 0; i < size; i++) {
            OldBaseFragment nowFragment = fragments.get(i);
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

    private static void removeFragment(OldBaseFragment oldBaseFragment) {
        if (!(oldBaseFragment instanceof MainFragmentOld)) {
            Logger.d(oldBaseFragment.getClass().getName());
            fragments.remove(oldBaseFragment);
            FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
            beginTransaction.remove(oldBaseFragment).commitAllowingStateLoss();
        }
    }

    public static OldBaseFragment getTopFragment() {
        return fragments.get(fragments.size() - 1);
    }

}
