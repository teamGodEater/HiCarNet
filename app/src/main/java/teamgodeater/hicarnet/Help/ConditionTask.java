package teamgodeater.hicarnet.Help;

/**
 * Created by G on 2016/5/23 0023.
 */

public class ConditionTask {
    int times;
    Runnable runnable;
    boolean isCancle = false;

    public ConditionTask(int t, Runnable r) {
        times = t;
        runnable = r;
    }

    public void excute() {
        times--;
        if (times == 0 && !isCancle) {
            runnable.run();
        }
    }

    boolean isFrist = true;
    public void excuteOnce() {
        if (isFrist) {
            times--;
            isFrist = false;
            if (times == 0 && !isCancle) {
                runnable.run();
            }
        }
    }

    public void cancle() {
        isCancle = true;
    }
}
