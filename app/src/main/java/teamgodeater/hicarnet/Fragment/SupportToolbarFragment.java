package teamgodeater.hicarnet.Fragment;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import teamgodeater.hicarnet.Activity.ManageActivity;
import teamgodeater.hicarnet.Help.Utils;
import teamgodeater.hicarnet.R;
import teamgodeater.hicarnet.Widget.RippleBackGroundView;

/**
 * Created by G on 2016/5/19 0019.
 */
public abstract class SupportToolbarFragment extends Fragment {

    //ToolBar高度
    final int TOOLBAR_HEIGHT = 50;
    //rootView
    protected ViewGroup rootContain;
    //toolbarView
    protected ViewGroup toolbarContain;

    protected LinearLayout tLeftContain;
    protected LinearLayout tMidContain;
    protected LinearLayout tRightContain;
    protected ManageActivity manageActivity;


    private SupportWindowsParams windowsParams;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        windowsParams = onCreateSupportViewParams();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int statusBarHeight = manageActivity.getStatuBarHeight();
        if (windowsParams.isHasToolBar) {
            createToolBar(inflater, container, statusBarHeight);
        }
        if (windowsParams.rootLayoutId != -1) {
            createRootView(inflater, container, statusBarHeight);
        }

        manageActivity.setStatusBarAlpha(windowsParams.statusAlpha);
        setPrimaryColor(windowsParams.primaryColor);
        return rootContain;
    }

    protected void createRootView(LayoutInflater inflater, @Nullable ViewGroup container, int statusBarHeight) {
        //rootView 加载
        rootContain = (ViewGroup) inflater.inflate(windowsParams.rootLayoutId, container, false);
        //rootView 是否在toolbar下面
        if (windowsParams.isNoFullScreen) {
            rootContain.setPadding(0, statusBarHeight + Utils.dp2Px(TOOLBAR_HEIGHT), 0, 0);
        }

       final ViewTreeObserver.OnGlobalLayoutListener global = new ViewTreeObserver.OnGlobalLayoutListener() {
           @Override
           public void onGlobalLayout() {
               onOnceGlobalLayoutListen();
               rootContain.getViewTreeObserver().removeOnGlobalLayoutListener(this);
           }
       };

        rootContain.getViewTreeObserver().addOnGlobalLayoutListener(global);

    }

    protected void createToolBar(LayoutInflater inflater, @Nullable ViewGroup container, int statusBarHeight) {
        //toolbar 创建
        toolbarContain = (ViewGroup) inflater.inflate(R.layout.default_toolbar_view, null);
        tLeftContain = (LinearLayout) toolbarContain.findViewById(R.id.LeftContain);
        tMidContain = (LinearLayout) toolbarContain.findViewById(R.id.MidContain);
        tRightContain = (LinearLayout) toolbarContain.findViewById(R.id.RightContain);
        //toolbar 大小位置
        toolbarContain.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                Utils.dp2Px(TOOLBAR_HEIGHT) + statusBarHeight));

        toolbarContain.setPadding(0, statusBarHeight, 0, 0);
        toolbarContain.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        //将toolbar添加到视图
        container.addView(toolbarContain);
    }

    /**
     * 视图加载OK
     * 开始CircleReveal动画
     */
    protected void onOnceGlobalLayoutListen() {
        Logger.d("once Global");
    }

    /**
     * 设置主色调
     *
     * @param color
     */
    protected void setPrimaryColor(int color) {
        if (toolbarContain != null) {
            toolbarContain.setBackgroundColor(color);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //将toolbar放在顶层
        if (toolbarContain != null) {
            toolbarContain.getParent().bringChildToFront(toolbarContain);
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        manageActivity = (ManageActivity) activity;

    }

    @Override
    public void onDetach() {
        super.onDetach();
        manageActivity = null;
    }

    /**
     * 创建布局onActivityCreated
     *
     * @return 布局的参数
     */
    @NonNull
    protected abstract SupportWindowsParams onCreateSupportViewParams();

    /**
     * 获取一个常用的可点击的ClickCircleBackGroundView
     *
     * @param src   View的图片
     * @param alpha View的头目睹
     * @return
     */
    protected RippleBackGroundView tGetSimpleView(Drawable src, float alpha) {
        return tGetSimpleView(null, src, alpha);
    }

    /**
     * 获取一个常用的可点击的ClickCircleBackGroundView
     *
     * @param str   View的text
     * @param alpha View的头目睹
     * @return
     */
    protected RippleBackGroundView tGetSimpleView(String str, float alpha) {
        return tGetSimpleView(str, null, alpha);
    }

    /**
     * 获取一个常用的可点击的ClickCircleBackGroundView
     *
     * @param str   View的text
     * @param src   View的图片
     * @param alpha View的头目睹
     * @return
     */
    protected RippleBackGroundView tGetSimpleView(String str, Drawable src, float alpha) {
        RippleBackGroundView v = new RippleBackGroundView(getActivity());
        v.setRounded(true);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(Utils.dp2Px(TOOLBAR_HEIGHT), Utils.dp2Px(TOOLBAR_HEIGHT));
        v.setLayoutParams(params);
        v.setAlpha(alpha);

        Matrix matrix = new Matrix();
        // 然后再以某个点为中心进行缩放
        float targetSize = Utils.dp2Px(30);
        float centerX = Utils.dp2Px(25);
        float centerY = Utils.dp2Px(25);
        matrix.postTranslate(centerX - src.getIntrinsicWidth() / 2f, centerY - src.getIntrinsicHeight() / 2f);
        matrix.postScale(targetSize / src.getIntrinsicWidth(), targetSize / src.getIntrinsicHeight(), centerX, centerY);
        v.setScaleType(ImageView.ScaleType.MATRIX);
        v.setImageDrawable(src);
        v.setImageMatrix(matrix);

        v.setText(str);
        return v;
    }


    protected void tAddLeftContainView(View v, String tag) {
        v.setTag(tag);
        tLeftContain.addView(v);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) v.getLayoutParams();
        layoutParams.setMarginEnd(Utils.dp2Px(8));

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onToolBarClick(v);
            }
        });
    }

    protected void tAddRightContainView(View v, String tag) {
        v.setTag(tag);
        tLeftContain.addView(v);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) v.getLayoutParams();
        layoutParams.setMarginStart(Utils.dp2Px(8));

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onToolBarClick(v);
            }
        });
    }

    protected void tAddMidContainView(View v, String tag) {
        v.setTag(tag);
        tMidContain.addView(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onToolBarClick(v);
            }
        });
    }

    protected void onToolBarClick(View v) {

    }

    /**
     * 快速的设置toolbar 常用界面
     *
     * @param leftBackOrDrawer 左边 显示 返回 (true) 还是 抽屉menu (false)
     * @param titlestr         中间显示标题 的内容
     */
    protected void tSetDefaultView(boolean leftBackOrDrawer, String titlestr) {
        tMidContain.removeAllViews();
        tLeftContain.removeAllViews();

        TextView title = new TextView(getActivity());
        title.setText(titlestr);
        title.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utils.dp2Px(18));
        title.setTextColor(Color.WHITE);
        title.setAlpha(0.87f);
        tAddMidContainView(title, "title");

        if (leftBackOrDrawer) {
            View view = tGetSimpleView(Utils.getDrawableWithColorFromRes(R.drawable.ic_keyboard_arrow_left, Color.WHITE), 0.87f);
            tAddLeftContainView(view, "back");
        } else {
            View view = tGetSimpleView(Utils.getDrawableWithColorFromRes(R.drawable.ic_menu, Color.WHITE), 0.87f);
            tAddLeftContainView(view, "drawer");

        }

    }

    /**
     * 界面设置的参数
     */
    public class SupportWindowsParams {
        public int rootLayoutId = -1;
        public boolean isNoFullScreen = true;
        public boolean isHasToolBar = true;
        public float statusAlpha = 0.2f;
        public int primaryColor = Utils.getColorFromRes(R.color.colorPrimary);
    }

    public void destroySelf() {
        getFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
    }

    public boolean isInterceptBack() {
        return false;
    }
}
