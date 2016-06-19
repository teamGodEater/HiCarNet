package teamgodeater.hicarnet.Data;

import android.graphics.Bitmap;

import teamgodeater.hicarnet.R;
import teamgodeater.hicarnet.Utils.Utils;

/**
 * Created by G on 2016/5/29 0029.
 */

public class BaseItem2LineData {
    public int icoLeft = 0;
    public int icoLeftColor = Utils.getColorFromRes(R.color.colorBlack54);
    public int icoRight = 0;
    public int icoRightColor = Utils.getColorFromRes(R.color.colorBlack54);
    public String title;
    public String tip;
    public String tipRight;
    public Object tag;
    public Bitmap icoLeftBitmap = null ;
    public boolean hasDivider =  false;
    public boolean isClickAble = true;
}