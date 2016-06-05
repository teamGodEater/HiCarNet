package teamgodeater.hicarnet.Help;

import android.app.Application;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by G on 2016/5/19 0019.
 */
public class Utils {

    private static Application context;

    public static void setApplication(Application app) {
        context = app;
    }

    public static int getColorFromRes(int id) {
        return context.getResources().getColor(id);
    }

    public static Drawable getDrawableWithColorFromRes(int id, int color) {
        Drawable drawable = context.getResources().getDrawable(id);
        Drawable mutate = drawable.mutate();
        mutate.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        return mutate;
    }
    public static Application getContext() {
        return context;
    }

    public static int dp2Px(int d) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (d * scale + 0.5f);
    }


    /**
     * 执行拷贝任务
     *
     * @param assest 需要拷贝的assets文件路径
     * @return 拷贝成功后的目标文件句柄
     * @throws IOException
     */
    public static File assest2Phone(String assest) {
        File file = new File(context.getFilesDir().getPath(), assest);
        Logger.d("path " + file.getAbsolutePath());
        if (file.exists()) {
            return file;
        }

        FileOutputStream fos = null;
        InputStream is = null;

        try {
            is = context.getAssets().open(assest);
            fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int byteCount = 0;
            while ((byteCount = is.read(buffer)) != -1) {//循环从输入流读取 buffer字节
                fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
            }
            fos.flush();//刷新缓冲区
            Logger.d(assest + " 复制到内存成功");
        } catch (IOException e) {

            Logger.d( assest + " 写入文件失败");
            e.printStackTrace();

        } finally {

            try {
                if (is != null) {
                    is.close();
                }
                if (fos != null) {
                    fos.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return file;
    }


    public static LatLng getLatLng(BDLocation location) {
        if (location != null) {
            return new LatLng(location.getLatitude(), location.getLongitude());
        }
        return null;
    }

    /**
     * 获取当前网络类型
     * @return 0：没有网络   1：WIFI网络   2：WAP网络    3：NET网络
    */

    public static final int NETTYPE_WIFI = 0x01;
    public static final int NETTYPE_CMWAP = 0x02;
    public static final int NETTYPE_CMNET = 0x03;
    public static final int NETTYPE_NONET = 0x00;

    public static int getNetworkType() {
        int netType = NETTYPE_NONET;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            String extraInfo = networkInfo.getExtraInfo();
            if (!extraInfo.isEmpty()) {
                if (extraInfo.toLowerCase().equals("cmnet")) {
                    netType = NETTYPE_CMNET;
                } else {
                    netType = NETTYPE_CMWAP;
                }
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = NETTYPE_WIFI;
        }
        return netType;
    }
}