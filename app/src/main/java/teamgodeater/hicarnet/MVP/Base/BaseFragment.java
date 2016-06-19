package teamgodeater.hicarnet.MVP.Base;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import teamgodeater.hicarnet.Activity.ManageActivity;
import teamgodeater.hicarnet.R;
import teamgodeater.hicarnet.Utils.Tutil;
import teamgodeater.hicarnet.Utils.Utils;
import teamgodeater.hicarnet.Widget.RippleBackGroundView;

/**
 * Created by G on 2016/6/18 0018.
 */

public abstract class BaseFragment<P extends BasePresenter> extends Fragment {

    public static final String TOOL_TAG_BACK = "back", TOOL_TAG_DRAWER = "drawer", TOOL_TAG_TITLE = "title";

    public P mPresenter;

    final int TOOLBAR_HEIGHT = 50;
    public boolean mHasDestroy = false;
    public ViewGroup mTopView;
    public ViewGroup mRootContain;
    public ViewGroup mToolbarContain;

    public LinearLayout tLeftContain;
    public LinearLayout tMidContain;
    public LinearLayout tRightContain;

    public ManageActivity manageActivity;
    public WindowsParams mWindowsParams = new WindowsParams();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWindowsParams = onCreateSupportViewParams(mWindowsParams);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int statusBarHeight = manageActivity.getStatusBarHeight();

        mTopView = new FrameLayout(container.getContext());
        mTopView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        if (mWindowsParams.rootLayoutId != -1) {
            createRootView(inflater, mTopView, statusBarHeight);
        }

        if (mWindowsParams.isHasToolBar) {
            createToolBar(inflater, mTopView, statusBarHeight);
        }

        manageActivity.setStatusBarAlpha(mWindowsParams.statusAlpha);

        mPresenter = Tutil.getT(this, 0);
        mPresenter.setView(this);
        mPresenter.onStart();

        return mTopView;
    }

    private void createRootView(LayoutInflater inflater, @Nullable ViewGroup container, int statusBarHeight) {
        //rootView 加载
        mRootContain = (ViewGroup) inflater.inflate(mWindowsParams.rootLayoutId, container, false);
        //rootView 是否在toolbar下面
        if (mWindowsParams.isNoFullScreen) {
            if (mWindowsParams.isHasToolBar)
                mRootContain.setPadding(0, statusBarHeight + Utils.dp2Px(TOOLBAR_HEIGHT), 0, 0);
            else
            mRootContain.setPadding(0, statusBarHeight, 0, 0);

        }

        final ViewTreeObserver.OnGlobalLayoutListener global = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                onOnceGlobalLayoutListen();
                mRootContain.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        };

        mRootContain.getViewTreeObserver().addOnGlobalLayoutListener(global);
        container.addView(mRootContain);
    }


    private void createToolBar(LayoutInflater inflater, @Nullable ViewGroup container, int statusBarHeight) {
        //toolbar 创建
        mToolbarContain = (ViewGroup) inflater.inflate(R.layout.default_toolbar_view, null);
        tLeftContain = (LinearLayout) mToolbarContain.findViewById(R.id.LeftContain);
        tMidContain = (LinearLayout) mToolbarContain.findViewById(R.id.MidContain);
        tRightContain = (LinearLayout) mToolbarContain.findViewById(R.id.RightContain);
        //toolbar 大小位置
        mToolbarContain.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                Utils.dp2Px(TOOLBAR_HEIGHT) + statusBarHeight));

        mToolbarContain.setPadding(0, statusBarHeight, 0, 0);
        mToolbarContain.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        //将toolbar添加到视图
        container.addView(mToolbarContain);
        setPrimaryColor(mWindowsParams.primaryColor);
    }

    /**
     * 视图加载OK
     * 开始CircleReveal动画
     */
    public void onOnceGlobalLayoutListen() {
    }

    /**
     * 设置主色调
     *
     * @param color
     */
    public void setPrimaryColor(int color) {
        if (mToolbarContain != null) {
            mToolbarContain.setBackgroundColor(color);
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

    @Override
    public void onDestroyView() {
        mHasDestroy = true;
        mPresenter.onDestroy();
        super.onDestroyView();
    }

    /**
     * 创建布局onActivityCreated
     *
     * @return 布局的参数
     */
    @NonNull
    protected abstract WindowsParams onCreateSupportViewParams(WindowsParams windowsParams);

    /**
     * 获取一个常用的可点击的ClickCircleBackGroundView
     *
     * @param src   View的图片
     * @param alpha View的头目睹
     * @return
     */
    public RippleBackGroundView tGetSimpleView(Drawable src, float alpha) {
        return tGetSimpleView(null, src, alpha);
    }

    /**
     * 获取一个常用的可点击的ClickCircleBackGroundView
     *
     * @param str   View的text
     * @param alpha View的头目睹
     * @return
     */
    public RippleBackGroundView tGetSimpleView(String str, float alpha) {
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
    public RippleBackGroundView tGetSimpleView(String str, Drawable src, float alpha) {
        RippleBackGroundView v = new RippleBackGroundView(getActivity());
        v.setRounded(true);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(Utils.dp2Px(TOOLBAR_HEIGHT), Utils.dp2Px(TOOLBAR_HEIGHT));
        v.setLayoutParams(params);
        v.setAlpha(alpha);

        if (src != null) {
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
        }

        v.setText(str);
        return v;
    }


    public void tAddLeftContainView(View v, String tag) {
        v.setTag(tag);
        tLeftContain.addView(v);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) v.getLayoutParams();
        layoutParams.setMarginEnd(Utils.dp2Px(8));

        v.setOnClickListener(mViewClickListener);
    }

    public void tAddRightContainView(View v, String tag) {
        v.setTag(tag);
        tRightContain.addView(v);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) v.getLayoutParams();
        layoutParams.setMarginStart(Utils.dp2Px(8));

        v.setOnClickListener(mViewClickListener);
    }

    public void tAddMidContainView(View v, String tag) {
        v.setTag(tag);
        tMidContain.addView(v);

        v.setOnClickListener(mViewClickListener);
    }

    public View.OnClickListener mViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String tag = "";
            Object objTag = v.getTag();
            if (objTag != null && objTag instanceof String) {
                tag = (String) objTag;
            }
            onToolBarClick(v, tag);
        }
    };

    public void onToolBarClick(View v, String tag) {

    }

    /**
     * 快速的设置toolbar 常用界面
     *
     * @param leftBackOrDrawer 左边 显示 返回 (true) 还是 抽屉menu (false)
     * @param titlestr         中间显示标题 的内容
     */
    public void tSetDefaultView(boolean leftBackOrDrawer, String titlestr) {
        tMidContain.removeAllViews();
        tLeftContain.removeAllViews();

        TextView title = new TextView(getActivity());
        title.setText(titlestr);
        title.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utils.dp2Px(18));
        title.setTextColor(Color.WHITE);
        title.setAlpha(0.87f);
        tAddMidContainView(title, TOOL_TAG_TITLE);

        if (leftBackOrDrawer) {
            View view = tGetSimpleView(Utils.getDrawableWithColorFromRes(R.drawable.ic_keyboard_arrow_left, Color.WHITE), 0.87f);
            tAddLeftContainView(view, TOOL_TAG_BACK);
        } else {
            View view = tGetSimpleView(Utils.getDrawableWithColorFromRes(R.drawable.ic_menu, Color.WHITE), 0.87f);
            tAddLeftContainView(view, TOOL_TAG_DRAWER);
        }
    }

    public void destroySelfShowBefore(long delay) {
        BaseFragmentManage.destroyTopShowBefore(delay);
    }

    public void destroySelf(long delay) {
        BaseFragmentManage.destroyTop(delay);
    }

    public void hideSelf(long delay) {
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                getFragmentManager().beginTransaction().hide(BaseFragment.this).commitAllowingStateLoss();
            }
        }, delay);
    }

    public void fadeHideSelf(long duration) {
        mTopView.animate().alpha(0f).setDuration(duration).setInterpolator(new AccelerateInterpolator()).start();
        hideSelf(duration - 50L);
    }

    public void hideSoftInput() {
        View focusedChild = mRootContain.getFocusedChild();
        if (focusedChild == null)
            return;
        IBinder windowToken = focusedChild.getWindowToken();
        if (windowToken == null)
            return;
        InputMethodManager imm = (InputMethodManager)
                manageActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(windowToken, 0);
        mRootContain.getRootView().requestFocus();
    }

    public boolean onInterceptBack() {
        return false;
    }

    public abstract String getType();

}
