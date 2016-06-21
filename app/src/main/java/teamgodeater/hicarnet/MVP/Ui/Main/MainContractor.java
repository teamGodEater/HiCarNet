package teamgodeater.hicarnet.MVP.Ui.Main;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;

import com.lapism.searchview.SearchItem;

import teamgodeater.hicarnet.MVP.Base.BasePresenter;

/**
 * 契约类
 * Created by G on 2016/6/18 0018.
 */

public interface MainContractor {
    interface View {
        //---------------------------------search------------------------------------------
        /**
         * 打开搜索框
         */
        void openSearch();

        void closeSearch();

        /**
         * 设置搜索框内容
         */
        void setSearchText(String text);

        //------------------------------------music-----------------------------------------

        /**
         * 设置显示的歌曲
         *
         * @param fenmian    封面图片
         * @param musicName  歌名
         * @param artistName 歌手
         * @param allTime    时长
         */
        void setMusic(Bitmap fenmian, String musicName, String artistName, String allTime);

        /**
         * 设置歌曲进度显示
         *
         * @param time    当前播放时长
         * @param percent 进度百分比
         */
        void setMusicCurrentTime(String time, int percent);

        /**
         * 显示暂停还是播放按钮
         *
         * @param play 真为播放 假暂停
         */
        void setMusicPlay(boolean play);

        //----------------------------------BottomViewPage------------------------------------
        void changeViewPagerIndex(int index);

        //会先清空之前的
        void setFirstViewPager(RecyclerView.Adapter v);

        void setSecondViewPager(RecyclerView.Adapter v);

        //-------------------------------------等待动画----------------------------------------

        void showLoading(String tip);

        void showPagerLoading(String tip);

        void hideLoading();

        void hidePagerLoading();
    }

    //--------------------------------------Model------------------------------------------------

    interface Model {

    }


    abstract class DispatchPresent extends BasePresenter<MainFragment, Model> {
    }

    /**
     * 地图管理
     */
    abstract class MapPresent extends BasePresenter<MainFragment, Model> {
        abstract public void zoomToFit();
    }

    /**
     * 用户信息概览
     */
    abstract class OveViewPresent extends BasePresenter<View, Model> {
        abstract public boolean interceptZoomToFit();

        abstract public void showGasMarket();

        abstract public void hideGasMarket();

        /**
         * 添加到Viewpager第一页 通过调用setFirstViewPager(View v);
         */
        abstract public void addToPager();

        /**
         * 获取数据
         */
        abstract public void getData();
    }

    /**
     * GasStation
     */
    abstract class GasStationPresent extends BasePresenter<MainFragment, Model> {
        /**
         * 添加到Viewpager第一页 通过调用setFirstViewPager(View v);
         */
        abstract public void addToPager();

        abstract public boolean interceptZoomToFit();


        abstract public void showGasMarket();

        abstract public void hideGasMarket();

        /**
         * 获取数据
         */
        abstract public void getData();

        abstract public void showAllGasStation();

        abstract public void showShortGasStation();
    }

    /**
     * 地点搜索
     */
    abstract class MapSearchPresent extends BasePresenter<MainFragment, Model> {
        abstract public boolean interceptZoomToFit();

        abstract public void searchLocation(String keyWord);

        abstract public void showRoute(SearchItem item);

        abstract public void removeResult();
    }

    /**
     * 语音
     */
    abstract class SpeechPresent extends BasePresenter<MainFragment, Model> {
        abstract public void openDetail();
    }

    /**
     * 音乐
     */
    abstract class MusicPresent extends BasePresenter<MainFragment, Model> {
        abstract public void swichNext();

        abstract public void swichBefore();

        abstract public void setProgress();

        /**
         * 音乐界面
         */
        abstract public void openDetial();

        /**
         * 开始播放
         *
         * @param start 真播放 假暂停
         */
        abstract public void setStartPlay(boolean start);
    }
}

