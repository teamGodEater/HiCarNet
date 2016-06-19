package teamgodeater.hicarnet.MVP.Ui.Sign;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import teamgodeater.hicarnet.Adapter.TextWatchAdapter;
import teamgodeater.hicarnet.C;
import teamgodeater.hicarnet.MVP.Base.BaseFragment;
import teamgodeater.hicarnet.MVP.Base.WindowsParams;
import teamgodeater.hicarnet.Layout.LoadingView.RotateLoadingViewHelp;
import teamgodeater.hicarnet.R;
import teamgodeater.hicarnet.Utils.Utils;
import teamgodeater.hicarnet.Widget.RippleBackGroundView;
import teamgodeater.hicarnet.Widget.RoundedImageView;

import static android.app.Activity.RESULT_OK;
import static teamgodeater.hicarnet.C.HTTP_FOUND;
import static teamgodeater.hicarnet.C.HTTP_NOT_ACCEPTABLE;
import static teamgodeater.hicarnet.C.HTTP_SERVICE_ERROR;

/**
 * Created by G on 2016/6/19 0019.
 */

public class SignFragment extends BaseFragment<SignPresent> implements SignConstractor.View {
    @Bind(R.id.headImage)
    RoundedImageView headImage;
    @Bind(R.id.username)
    EditText username;
    @Bind(R.id.password)
    EditText password;
    @Bind(R.id.passwordConfirm)
    EditText passwordConfirm;
    @Bind(R.id.sign)
    RippleBackGroundView sign;
    @Bind(R.id.backLogin)
    RippleBackGroundView backLogin;
    @Bind(R.id.cleanUsername)
    ImageView cleanUsername;
    @Bind(R.id.cleanPassword)
    ImageView cleanPassword;
    @Bind(R.id.visiblePassword)
    ImageView visiblePassword;
    @Bind(R.id.cleanPasswordConfirm)
    ImageView cleanPasswordConfirm;

    private RotateLoadingViewHelp rotateLoading;
    private boolean isPasswordVisible = false;
    private Bitmap headBitMap;

    @NonNull
    @Override
    protected WindowsParams onCreateSupportViewParams(WindowsParams windowsParams) {
        windowsParams.rootLayoutId = R.layout.frgm_regist;
        windowsParams.primaryColor = Utils.getColorFromRes(R.color.colorAccent);
        return windowsParams;
    }

    @Override
    public String getType() {
        return C.VT_SIGN;
    }

    @Override
    public void onErrorSign(int code) {
        rotateLoading.stopAnimator();
        if (code == -1)
            Toast.makeText(getContext(), "你好像没有连接到网络哦?请检查打开网络", Toast.LENGTH_SHORT).show();
        else if (code == HTTP_NOT_ACCEPTABLE)
            Toast.makeText(getContext(), "用户名 和 密码长度 要求在6~15位之间 哦!", Toast.LENGTH_SHORT).show();
        else if (code == HTTP_FOUND)
            Toast.makeText(getContext(), "来晚了一步 这个用户名已经被别人占用了", Toast.LENGTH_SHORT).show();
        else if (code == HTTP_SERVICE_ERROR)
            Toast.makeText(getContext(), "未知服务器错误", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onErrorLogin(int code) {
        rotateLoading.stopAnimator();
        if (code == -1)
            Utils.toast( "未知错误 访问服务器失败 请尝试手动登陆");
    }

    @Override
    public void onErrorSetImage(int code) {
        rotateLoading.stopAnimator();
    }

   @Override
    public void onSucceedLogin() {
        rotateLoading.startAnimator("正在初始化账户 这通常不会超过15S 请稍后...");
    }

    @Override
    public void onDone() {
        rotateLoading.stopAnimator();
        Utils.toast( "注册成功!");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, mRootContain);
        tSetDefaultView(true, "注册");
        setColorFilter();
        rotateLoading = new RotateLoadingViewHelp(mTopView);
        rotateLoading.setLoadingBg(true,-1,null);
        setListener();
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rotateLoading.destroyView();
        ButterKnife.unbind(this);
    }

    private void setListener() {
        username.addTextChangedListener(new TextWatchAdapter(){
            @Override
            public void onTextChanged(String s) {
                if (s.equals("")) {
                    cleanUsername.setVisibility(View.GONE);
                } else {
                    cleanUsername.setVisibility(View.VISIBLE);
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

        passwordConfirm.addTextChangedListener(new TextWatchAdapter() {
            @Override
            public void onTextChanged(String s) {
                if (s.equals("")) {
                    cleanPasswordConfirm.setVisibility(View.GONE);
                } else {
                    cleanPasswordConfirm.setVisibility(View.VISIBLE);
                }
            }
        });

        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    doSign();
                    return true;
                }
                return false;
            }
        });

        passwordConfirm.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    doSign();
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

        cleanPasswordConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordConfirm.setText("");
            }
        });

        visiblePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reversePasswordVisible();
            }
        });

        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSign();
            }
        });

        backLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int height = mTopView.getHeight();
                int width = mTopView.getWidth();
                backBefore(width / 2, height - 48);
            }
        });

        mTopView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(v instanceof EditText))
                    hideSoftInput();
            }
        });

        headImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT); // "android.intent.action.GET_CONTENT"
                innerIntent.setType("image/*");
                Intent wrapperIntent = Intent.createChooser(innerIntent,
                        "选择头像图片");
                startActivityForResult(wrapperIntent, REQUEST_CODE_PHOTO_SELECT_REQUEST_CODE);
            }
        });
    }

    //------------------------从相册选择图片 And 截取一部分---------------------------------
    private static final int REQUEST_CODE_PHOTO_CUT_REQUEST_CODE = 224;
    private static final int REQUEST_CODE_PHOTO_SELECT_REQUEST_CODE = 225;

    //打开截图界面
    private void startPhotoZoom(Uri uri, int size) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", true);

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", true);
        try {
            startActivityForResult(intent, REQUEST_CODE_PHOTO_CUT_REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if  (resultCode != RESULT_OK)
            return;

        if (requestCode == REQUEST_CODE_PHOTO_SELECT_REQUEST_CODE) {
            startPhotoZoom(data.getData(),160);
        } else if (requestCode == REQUEST_CODE_PHOTO_CUT_REQUEST_CODE) {
            headBitMap = data.getExtras().getParcelable("data");
            headImage.setPadding(0,0,0,0);
            headImage.setImageBitmap(headBitMap);
        }
    }
    //----------------------------------------注册------------------------------------------------

    private void doSign() {
        hideSoftInput();
        String user = username.getText().toString();
        String passwd = password.getText().toString();
        String passwdConfirm = passwordConfirm.getText().toString();
        if (isPasswordVisible || passwd.equals(passwdConfirm)) {
            if (headImage == null) {
                Utils.toast("请选择一个头像");
            }else {
                rotateLoading.startAnimator("正在提交数据 请稍等...");
                mPresenter.Sign(user, passwd, headBitMap);
            }
        } else {
            Utils.toast("你两次输入的密码不一样哦!点击小眼睛使密码可见");
        }
    }

    //----------------------------------动画 切换相关------------------------------------------------
    private void reversePasswordVisible() {
        isPasswordVisible = !isPasswordVisible;
        if (isPasswordVisible) {
            visiblePassword.getDrawable().setColorFilter(getResources().getColor(R.color.color1), PorterDuff.Mode.SRC_IN);
            password.setImeOptions(EditorInfo.IME_ACTION_DONE);
            password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            passwordConfirm.setVisibility(View.GONE);

        } else {
            visiblePassword.getDrawable().setColorFilter(getResources().getColor(R.color.colorBlack54), PorterDuff.Mode.SRC_IN);
            password.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordConfirm.setVisibility(View.VISIBLE);
        }

        Editable etable = password.getText();
        Selection.setSelection(etable, etable.length());
    }

    @Override
    public void onToolBarClick(View v, String tag) {
        if (tag.equals("back"))
            backBefore(48, 80);
    }

    @Override
    public boolean onInterceptBack() {
        int height = mTopView.getHeight();
        int width = mTopView.getWidth();
        backBefore(width / 2, height - 48);
        return true;
    }

    @Override
    public void onOnceGlobalLayoutListen() {
        int height = mTopView.getHeight();
        int width = mTopView.getWidth();
        float maxRadius = (float) Math.sqrt(height * height + width * width);
        SupportAnimator circularReveal = ViewAnimationUtils.createCircularReveal(
                mTopView, width / 2, height - 48, 0, maxRadius);
        circularReveal.setInterpolator(new AccelerateInterpolator());
        circularReveal.setDuration(350L);
        circularReveal.start();
    }

    private void backBefore(int x, int y) {
        int height = mTopView.getHeight();
        int width = mTopView.getWidth();
        float maxRadius = (float) Math.sqrt(height * height + width * width);
        SupportAnimator circularReveal = ViewAnimationUtils.createCircularReveal(
                mTopView, x, y, maxRadius, 0);
        circularReveal.setInterpolator(new AccelerateInterpolator());
        circularReveal.setDuration(350L);
        circularReveal.start();
        destroySelfShowBefore(330L);
    }

    private void setColorFilter() {
        username.getCompoundDrawables()[0].setColorFilter(getResources().getColor(R.color.colorBlack54), PorterDuff.Mode.SRC_IN);
        password.getCompoundDrawables()[0].setColorFilter(getResources().getColor(R.color.colorBlack54), PorterDuff.Mode.SRC_IN);
        passwordConfirm.getCompoundDrawables()[0].setColorFilter(getResources().getColor(R.color.colorBlack54), PorterDuff.Mode.SRC_IN);
        cleanUsername.getDrawable().setColorFilter(getResources().getColor(R.color.colorBlack54), PorterDuff.Mode.SRC_IN);
        cleanPassword.getDrawable().setColorFilter(getResources().getColor(R.color.colorBlack54), PorterDuff.Mode.SRC_IN);
        visiblePassword.getDrawable().setColorFilter(getResources().getColor(R.color.colorBlack54), PorterDuff.Mode.SRC_IN);
        cleanPasswordConfirm.getDrawable().setColorFilter(getResources().getColor(R.color.colorBlack54), PorterDuff.Mode.SRC_IN);
        headImage.getDrawable().setColorFilter(getResources().getColor(R.color.colorWhite87), PorterDuff.Mode.SRC_IN);
    }
}
