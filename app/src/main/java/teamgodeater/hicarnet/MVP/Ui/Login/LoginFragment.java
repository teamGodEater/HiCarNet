package teamgodeater.hicarnet.MVP.Ui.Login;

import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import teamgodeater.hicarnet.Adapter.TextWatchAdapter;
import teamgodeater.hicarnet.Help.RestClientHelp;
import teamgodeater.hicarnet.Layout.LoadingView.RotateLoadingViewHelp;
import teamgodeater.hicarnet.MVP.Base.BaseFragment;
import teamgodeater.hicarnet.MVP.Base.BaseFragmentManage;
import teamgodeater.hicarnet.MVP.Base.ViewTranslateHelp;
import teamgodeater.hicarnet.MVP.Base.WindowsParams;
import teamgodeater.hicarnet.MVP.Ui.Sign.SignFragment;
import teamgodeater.hicarnet.R;
import teamgodeater.hicarnet.Utils.Utils;
import teamgodeater.hicarnet.Widget.RippleBackGroundView;
import teamgodeater.hicarnet.Widget.RoundedImageView;

import static teamgodeater.hicarnet.C.HTTP_NOT_ACCEPTABLE;
import static teamgodeater.hicarnet.C.HTTP_NOT_FOUND;
import static teamgodeater.hicarnet.C.HTTP_UNPROCESSABLE_ENTITY;
import static teamgodeater.hicarnet.C.VT_LOGIN;

/**
 * Created by G on 2016/6/18 0018.
 */

public class LoginFragment extends BaseFragment<LoginPresent> implements LoginContractor.View {

    @Bind(R.id.brandLogo)
    RoundedImageView brandLogo;
    @Bind(R.id.username)
    EditText username;
    @Bind(R.id.password)
    EditText password;
    @Bind(R.id.login)
    RippleBackGroundView login;
    @Bind(R.id.forget)
    TextView forget;
    @Bind(R.id.sign)
    RippleBackGroundView sign;
    @Bind(R.id.cleanUsername)
    ImageView cleanUsername;
    @Bind(R.id.cleanPassword)
    ImageView cleanPassword;
    @Bind(R.id.visiblePassword)
    ImageView visiblePassword;

    boolean isPasswordVisible = false;
    private RotateLoadingViewHelp rotateLoading;

    @NonNull
    @Override
    protected WindowsParams onCreateSupportViewParams(WindowsParams windowsParams) {
        windowsParams.rootLayoutId = R.layout.frgm_login;
        return windowsParams;
    }

    @Override
    public String getType() {
        return VT_LOGIN;
    }

    //----------------------------------回调接口--------------------------------------------------
    @Override
    public void setHeadImage(Bitmap bitmap) {
        if (bitmap == null)
            brandLogo.setImageResource(R.drawable.logo);
        else
            brandLogo.setImageBitmap(bitmap);
    }

    @Override
    public void loginError(int code) {
        stopLoading();
        if (code == -1)
            Utils.toast("好像没有网络哦!请检查你的网络");
        else if (code == -2)
            Utils.toast("哦哦!服务器发生了一点错误 请稍后重试");
        else if (code == HTTP_NOT_FOUND)
            Utils.toast("没有找到该用户");
        else if (code == HTTP_UNPROCESSABLE_ENTITY)
            Utils.toast("密码错误");
        else if (code == HTTP_NOT_ACCEPTABLE)
            Utils.toast("用户名或密码不能为空");
    }

    @Override
    public void loginSucceed() {
        startLoading("登陆成功 获取用户信息 请稍后...");
    }

    @Override
    public void loadUserDataDone() {
        stopLoading();
        returnBack();
    }

    //---------------------------------------视图辅助---------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, mRootContain);
        tSetDefaultView(true, "登陆");
        setColorFilter();
        rotateLoading = new RotateLoadingViewHelp(mTopView);
        rotateLoading.setLoadingBg(true);
        setListener();
        username.setText(RestClientHelp.username);
        password.setText(RestClientHelp.password);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rotateLoading.destroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onOnceGlobalLayoutListen() {
        ViewTranslateHelp.bottom2Up(mTopView);
    }

    private void setListener() {
        username.addTextChangedListener(new TextWatchAdapter() {
            @Override
            public void onTextChanged(String s) {
                if (s.equals("")) {
                    cleanUsername.setVisibility(View.GONE);
                } else {
                    cleanUsername.setVisibility(View.VISIBLE);
                    mPresenter.getHeadImage(s);
                }
            }
        });

        password.addTextChangedListener(new TextWatchAdapter() {
            @Override
            public void onTextChanged(String s) {
                if (s.equals("")) {
                    cleanPassword.setVisibility(View.GONE);
                } else {
                    cleanPassword.setVisibility(View.VISIBLE);
                }
            }
        });

        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    doLogin();
                    return true;
                }
                return false;
            }
        });

        cleanUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username.setText("");
            }
        });

        cleanPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password.setText("");
            }
        });

        visiblePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reversePasswordVisible();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });

        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchForget();
            }
        });

        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchSign();
            }
        });

        mTopView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() != R.id.password && v.getId() != R.id.password) {
                    hideSoftInput();
                }
            }
        });

        mRootContain.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (password == null)
                    return;
                int usableHeightNow = computeUsableHeight();
                int focusBottom = password.getBottom() - 36;

                if (focusBottom > usableHeightNow && mRootContain.getTranslationY() == 0) {
                    mRootContain.animate().translationY(usableHeightNow - focusBottom).setDuration(150).start();
                } else if (mRootContain.getTranslationY() != 0) {
                    mRootContain.animate().translationY(0).start();
                }
            }
        });
    }

    private void startLoading(String tip) {
        rotateLoading.startAnimator(tip);
    }

    private void stopLoading() {
        rotateLoading.stopAnimator();
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        mRootContain.getRootView().getWindowVisibleDisplayFrame(r);
        return (r.bottom - r.top);
    }

    private void doLogin() {
        String user = username.getText().toString();
        String passwd = password.getText().toString();
        startLoading("验证身份 请稍后...");
        mPresenter.login(user, passwd);
    }

    private void setColorFilter() {
        username.getCompoundDrawables()[0].setColorFilter(getResources().getColor(R.color.colorBlack54), PorterDuff.Mode.SRC_IN);
        password.getCompoundDrawables()[0].setColorFilter(getResources().getColor(R.color.colorBlack54), PorterDuff.Mode.SRC_IN);
        cleanUsername.getDrawable().setColorFilter(getResources().getColor(R.color.colorBlack54), PorterDuff.Mode.SRC_IN);
        cleanPassword.getDrawable().setColorFilter(getResources().getColor(R.color.colorBlack54), PorterDuff.Mode.SRC_IN);
        visiblePassword.getDrawable().setColorFilter(getResources().getColor(R.color.colorBlack54), PorterDuff.Mode.SRC_IN);
    }

    private void reversePasswordVisible() {
        isPasswordVisible = !isPasswordVisible;
        if (isPasswordVisible) {
            visiblePassword.getDrawable().setColorFilter(getResources().getColor(R.color.color1), PorterDuff.Mode.SRC_IN);
            password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

        } else {
            visiblePassword.getDrawable().setColorFilter(getResources().getColor(R.color.colorBlack54), PorterDuff.Mode.SRC_IN);
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        }

        Editable etable = password.getText();
        Selection.setSelection(etable, etable.length());
    }

    private void switchSign() {
        hideSelf(400L);
        BaseFragmentManage.switchFragment(new SignFragment());
    }

    private void switchForget() {

    }

    //---------------------------------------返回之前的----------------------------------------------

    @Override
    public boolean onInterceptBack() {
        returnBack();
        return true;
    }

    @Override
    public void onToolBarClick(View v, String tag) {
        if (tag.equals("back"))
            returnBack();
    }

    private void returnBack() {
        destroySelfShowBefore(280L);
        ViewTranslateHelp.up2Bottom(mTopView);
    }
}
