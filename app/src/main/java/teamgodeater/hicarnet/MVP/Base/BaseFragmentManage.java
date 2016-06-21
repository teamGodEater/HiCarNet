package teamgodeater.hicarnet.MVP.Base;

import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;

import teamgodeater.hicarnet.MVP.Ui.Main.MainFragment;
import teamgodeater.hicarnet.R;


/**
 * Created by G on 2016/6/14 0014.
 */

public class BaseFragmentManage {
    public static FragmentManager mFragmentManage;
    public static ArrayList<BaseFragment> mFragments = new ArrayList<>();
    private static OnFragmentChangeListener mListener;

    public static void switchFragment(BaseFragment to) {
        if (to instanceof MainFragment) {
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

        if (mListener != null)
            mListener.onFragmentChange(to);
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
        FragmentTransaction beginTransaction = mFragmentManage.beginTransaction();
        BaseFragment fragment = mFragments.get(mFragments.size() - 2);
        beginTransaction.show(fragment).commitAllowingStateLoss();
        destroyTop(delay);
    }

    private static void bringToTop(BaseFragment to) {
        int index = mFragments.indexOf(to);
        int size = mFragments.size();

        FragmentTransaction transaction = mFragmentManage.beginTransaction();
        transaction.show(to).commitAllowingStateLoss();
        for (int i = index + 1;i < size;i++) {

        }

        boolean isOk = false;
        for (int i = 0; i < size; i++) {
            BaseFragment nowFragment = mFragments.get(i);
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

    private static void removeFragment(BaseFragment baseFragment) {
        if (!(baseFragment instanceof MainFragment)) {
            Logger.d(baseFragment.getClass().getName());
            mFragments.remove(baseFragment);
            FragmentTransaction beginTransaction = mFragmentManage.beginTransaction();
            beginTransaction.remove(baseFragment).commitAllowingStateLoss();
        }
    }

    public static BaseFragment getTopFragment() {
        return mFragments.get(mFragments.size() - 1);
    }

    public static void setOnFragmentChangeListener(OnFragmentChangeListener listener) {
        mListener = listener;
    }

    public interface OnFragmentChangeListener{
       void onFragmentChange(BaseFragment to);
    }

}
