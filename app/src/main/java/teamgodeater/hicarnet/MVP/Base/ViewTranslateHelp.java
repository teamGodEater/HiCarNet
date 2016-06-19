package teamgodeater.hicarnet.MVP.Base;

import android.view.View;
import android.view.animation.AccelerateInterpolator;

/**
 * Created by G on 2016/6/18 0018.
 */

public class ViewTranslateHelp {
    public static void bottom2Up(View view) {
        view.setTranslationY(view.getHeight());
        view.animate().translationY(0f).setInterpolator(new AccelerateInterpolator()).start();
    }

    public static void up2Bottom(View view) {
        view.setTranslationY(0f);
        view.animate().translationY(view.getHeight()).setInterpolator(new AccelerateInterpolator()).start();
    }

    public static void right2Left(View view) {
        view.setTranslationX(view.getWidth());
        view.animate().translationX(0f).setInterpolator(new AccelerateInterpolator()).start();
    }

    public static void ledt2Right(View view) {
        view.setTranslationX(0f);
        view.animate().translationX(view.getHeight()).setInterpolator(new AccelerateInterpolator()).start();
    }
}
