package teamgodeater.hicarnet.RegistModle.Fragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.victor.loading.rotate.RotateLoading;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import teamgodeater.hicarnet.CarManageModle.Fragment.CarManageFragmentOld;
import teamgodeater.hicarnet.Fragment.OldBaseFragment;
import teamgodeater.hicarnet.Help.RestClientHelp;
import teamgodeater.hicarnet.Help.Utils;
import teamgodeater.hicarnet.R;
import teamgodeater.hicarnet.RestClient.RestClient;
import teamgodeater.hicarnet.Widget.RippleBackGroundView;
import teamgodeater.hicarnet.Widget.RoundedImageView;

import static android.app.Activity.RESULT_OK;

/**
 * Created by G on 2016/6/8 0008.
 */

public class RegistFragmentOld extends OldBaseFragment {
    @Bind(R.id.username)
    EditText username;
    @Bind(R.id.password)
    EditText password;
    @Bind(R.id.passwordConfirm)
    EditText passwordConfirm;
    @Bind(R.id.nextStep)
    RippleBackGroundView nextStep;
    @Bind(R.id.cancel)
    RippleBackGroundView cancel;
    @Bind(R.id.cleanUsername)
    ImageView cleanUsername;
    @Bind(R.id.cleanPassword)
    ImageView cleanPassword;
    @Bind(R.id.visiblePassword)
    ImageView visiblePassword;
    @Bind(R.id.cleanPasswordConfirm)
    ImageView cleanPasswordConfirm;
    @Bind(R.id.rotateBackGround)
    View rotateBackGround;
    @Bind(R.id.rotateLoading)
    RotateLoading rotateLoading;
    @Bind(R.id.focus)
    View focus;
    @Bind(R.id.baseLine)
    RoundedImageView headImage;
    private boolean isPasswordVisible = false;
    private Bitmap headBitMap;

    @NonNull
    @Override
    protected SupportWindowsParams onCreateSupportViewParams() {
        SupportWindowsParams params = new SupportWindowsParams();
        params.primaryColor = Utils.getColorFromRes(R.color.color1);
        params.rootLayoutId = R.layout.frgm_regist;
        return params;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootContain);
        tSetDefaultView(true, "注册");
        setColorFilter();
        setListener();
        username.setText("");
        password.setText("");
        passwordConfirm.setText("");
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    protected void onOnceGlobalLayoutListen() {
        int height = parentView.getHeight();
        int width = parentView.getWidth();
        float maxRadius = (float) Math.sqrt(height * height + width * width);
        SupportAnimator circularReveal = ViewAnimationUtils.createCircularReveal(
                parentView, width / 2, height - 48, 0, maxRadius);
        circularReveal.setInterpolator(new AccelerateInterpolator());
        circularReveal.start();
    }

    private void setListener() {
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String string = s.toString();
                if (string.equals("")) {
                    cleanUsername.setVisibility(View.GONE);
                } else {
                    cleanUsername.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    cleanPassword.setVisibility(View.GONE);
                } else {
                    cleanPassword.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        passwordConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    cleanPasswordConfirm.setVisibility(View.GONE);
                } else {
                    cleanPasswordConfirm.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    doRegist();
                    return true;
                }
                return false;
            }
        });

        passwordConfirm.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    doRegist();
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

        nextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doRegist();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int height = parentView.getHeight();
                int width = parentView.getWidth();
                backBefore(width / 2, height - 48);
            }
        });

        rootContain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(v instanceof EditText))
                    hideSoftInput(v);
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

    private void doRegist() {
        hideSoftInput(null);
        String user = username.getText().toString();
        String passwd = password.getText().toString();
        String passwdConfirm = passwordConfirm.getText().toString();
        if (isPasswordVisible || passwd.equals(passwdConfirm)) {
            if (headImage == null) {
                Utils.toast("请选择一个头像");
                return;
            }
            RestClientHelp restClientHelp = new RestClientHelp();
            setRotateLoading(true);
            restClientHelp.regist(user, passwd, new RestClient.OnResultListener<String>() {
                @Override
                public void succeed(String bean) {
                    Toast.makeText(getContext(), "注册成功", Toast.LENGTH_SHORT).show();
                    succeedRegist();
                }

                @Override
                public void error(int code) {
                    setRotateLoading(false);
                    if (code == -1)
                        Toast.makeText(getContext(), "你好像没有连接到网络哦?请检查打开网络", Toast.LENGTH_SHORT).show();
                    else if (code == RestClientHelp.HTTP_NOT_ACCEPTABLE)
                        Toast.makeText(getContext(), "用户名 和 密码长度 要求在6~15位之间 哦!", Toast.LENGTH_SHORT).show();
                    else if (code == RestClientHelp.HTTP_FOUND)
                        Toast.makeText(getContext(), "来晚了一步 这个用户名已经被别人占用了", Toast.LENGTH_SHORT).show();
                    else if (code == 500)
                        Toast.makeText(getContext(), "未知服务器错误", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "你两次输入的密码不一样啊!点击小眼睛使密码可见?", Toast.LENGTH_SHORT).show();
        }
    }

    private void succeedRegist() {
        final int[] count = {0};
        RestClientHelp.username = username.getText().toString();
        RestClientHelp.password = password.getText().toString();
        final RestClientHelp restClientHelp = new RestClientHelp();
        restClientHelp.login(new RestClient.OnResultListener<String>() {
            @Override
            public void succeed(String bean) {
                setRotateLoading(false);
                CarManageFragmentOld carManageFragment = new CarManageFragmentOld();
                manageActivity.switchFragment(carManageFragment);
                // TODO: 2016/6/14 0014 记得把头像完善下
                destroySelf(350);
            }

            @Override
            public void error(int code) {
                count[0]++;
                if (count[0] <= 3) {
                    restClientHelp.login(this);
                } else {
                    setRotateLoading(false);
                    Toast.makeText(getContext(), "服务器发生了一个错误,请一会再尝试登陆", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setRotateLoading(boolean start) {
        if (start) {
            rotateLoading.start();
            rotateBackGround.setVisibility(View.VISIBLE);
        } else {
            rotateLoading.stop();
            rotateBackGround.setVisibility(View.GONE);
        }
    }

    private void backBefore(int x, int y) {
        int height = parentView.getHeight();
        int width = parentView.getWidth();
        float maxRadius = (float) Math.sqrt(height * height + width * width);
        SupportAnimator circularReveal = ViewAnimationUtils.createCircularReveal(
                parentView, x, y, maxRadius, 0);
        circularReveal.setInterpolator(new AccelerateInterpolator());
        circularReveal.setDuration(350);
        circularReveal.start();
        destroySelfShowBefore(300);
    }

    @Override
    public boolean onInterceptBack() {
        int height = parentView.getHeight();
        int width = parentView.getWidth();
        backBefore(width / 2, height - 48);
        return true;
    }

    @Override
    public String getType() {
        return "regist";
    }

    @Override
    protected void onToolBarClick(View v) {
        String tag = (String) v.getTag();
        if (tag.equals("back"))
            backBefore(48, 80);
    }

    private void hideSoftInput(View v) {
        if (v == null) {
            View focusedChild = rootContain.getFocusedChild();
            if (focusedChild instanceof EditText) {
                v = focusedChild;
            } else {
                return;
            }
        }
        InputMethodManager imm = (InputMethodManager)
                manageActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        focus.requestFocus();
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
