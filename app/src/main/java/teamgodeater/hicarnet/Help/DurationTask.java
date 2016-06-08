package teamgodeater.hicarnet.Help;

import android.os.Handler;

/**
 * Created by G on 2016/6/8 0008.
 */

public class DurationTask {
    private final int duration;
    private final Runnable runnable;
    int excuteCount = 0;

    public DurationTask(int duration, Runnable runnable) {
        this.duration = duration;
        this.runnable = runnable;
    }

    public void excute() {
        Handler h = new Handler();
        excuteCount++;
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                excuteCount--;
                if (excuteCount == 0)
                    runnable.run();
            }
        }, duration);
    }
}
