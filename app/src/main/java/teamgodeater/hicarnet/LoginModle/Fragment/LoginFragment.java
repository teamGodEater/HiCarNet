package teamgodeater.hicarnet.LoginModle.Fragment;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.Rect;
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
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.victor.loading.rotate.RotateLoading;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import teamgodeater.hicarnet.Fragment.BaseFragment;
import teamgodeater.hicarnet.Help.ConditionTask;
import teamgodeater.hicarnet.Help.DurationTask;
import teamgodeater.hicarnet.Help.RestClientHelp;
import teamgodeater.hicarnet.Help.UserDataHelp;
import teamgodeater.hicarnet.Help.Utils;
import teamgodeater.hicarnet.R;
import teamgodeater.hicarnet.RegistModle.Fragment.RegistFragment;
import teamgodeater.hicarnet.RestClient.RestClient;
import teamgodeater.hicarnet.Widget.RippleBackGroundView;
import teamgodeater.hicarnet.Widget.RoundedImageView;


/**
 * Created by G on 2016/6/8 0008.
 */

public class LoginFragment extends BaseFragment {
    @Bind(R.id.brandLogo)
    RoundedImageView headImage;
    @Bind(R.id.username)
    EditText username;
    @Bind(R.id.password)
    EditText password;
    @Bind(R.id.nextStep)
    RippleBackGroundView login;
    @Bind(R.id.cancel)
    RippleBackGroundView regist;
    @Bind(R.id.cleanUsername)
    ImageView cleanUsername;
    @Bind(R.id.cleanPassword)
    ImageView cleanPassword;
    @Bind(R.id.visiblePassword)
    ImageView visiblePassword;
    @Bind(R.id.rotateLoading)
    RotateLoading rotateLoading;
    @Bind(R.id.rotateBackGround)
    View rotateBackGround;

    boolean isPasswordVisible = false;
    private DurationTask durationGetHeadImageTask;
    private ConditionTask conditionTask;

    @NonNull
    @Override
    protected SupportWindowsParams onCreateSupportViewParams() {
        SupportWindowsParams params = new SupportWindowsParams();
        params.rootLayoutId = R.layout.frgm_login;
        params.primaryColor = Utils.getColorFromRes(R.color.colorPrimary);
        return params;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootContain);
        tSetDefaultView(true, "登陆");
        conditionTask = new ConditionTask(3, new Runnable() {
            @Override
            public void run() {
                setRotateLoading(false);
                finish();
            }
        });
        setColorFilter();
        setListener();
        username.setText(RestClientHelp.username);
        password.setText(RestClientHelp.password);
        return rootView;
    }

    @Override
    public boolean onInterceptBack() {
        finish();
        return true;
    }

    @Override
    public String getType() {
        return "login";
    }

    @Override
    protected void onToolBarClick(View v) {
        String tag = (String) v.getTag();
        if (tag.equals("back")) {
            finish();
        }
    }

    private void finish() {
        destroySelfShowBefore(280L);
        parentView.animate().translationY(parentView.getHeight()).setInterpolator(new AccelerateInterpolator()).start();
    }

    @Override
    protected void onOnceGlobalLayoutListen() {
        parentView.setTranslationY(parentView.getHeight());
        parentView.animate().translationY(0f).setInterpolator(new AccelerateInterpolator()).start();
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
                    durationGetHeadImageTask.excute();
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

        durationGetHeadImageTask = new DurationTask(1000, new Runnable() {
            @Override
            public void run() {
                getHeadImage();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });

        regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSelf(500);
                manageActivity.switchFragment(new RegistFragment());
            }
        });

        rootContain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() != R.id.password && v.getId() != R.id.password) {
                    hideSoftInput(v);
                }
            }
        });

        rootContain.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (password == null)
                    return;
                int usableHeightNow = computeUsableHeight();
                int focusBottom = password.getBottom() - 36;

                if (focusBottom > usableHeightNow && rootContain.getTranslationY() == 0) {
                    rootContain.animate().translationY(usableHeightNow - focusBottom).setDuration(150).start();
                } else if (rootContain.getTranslationY() != 0) {
                    rootContain.animate().translationY(0).start();
                }
            }
        });
    }

    private void getHeadImage() {
        new RestClientHelp().getFile("/carico/" + username.getText().toString() + ".png", new RestClient.OnServiceResultListener() {
            @Override
            public void resultListener(byte[] result, int code, Map<String, List<String>> header) {
                if (code == 200) {
                    headImage.setImageBitmap(BitmapFactory.decodeByteArray(result,0,result.length));
                }else {
                    headImage.setImageResource(R.drawable.logo);
                }
            }
        });
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
        headImage.requestFocus();
    }

    private void doLogin() {
        hideSoftInput(null);
        String user = username.getText().toString();
        String passwd = password.getText().toString();

        setRotateLoading(true);
        RestClientHelp.username = user;
        RestClientHelp.password = passwd;
        final RestClientHelp restClientHelp = new RestClientHelp();
        restClientHelp.login(new RestClient.OnResultListener<String>() {
            @Override
            public void succeed(String bean) {
                UserDataHelp.getUserInfoData(new UserDataHelp.OnDoneListener() {
                    @Override
                    public void onDone(int code) {
                        conditionTask.excute();
                    }
                });
                UserDataHelp.getUserCarInfoDatas(new UserDataHelp.OnDoneListener() {
                    @Override
                    public void onDone(int code) {
                        conditionTask.excute();
                    }
                });
                UserDataHelp.getUserPointData(new UserDataHelp.OnDoneListener() {
                    @Override
                    public void onDone(int code) {
                        conditionTask.excute();
                    }
                });

            }

            @Override
            public void error(int code) {
                setRotateLoading(false);
                if (code == -1)
                    Toast.makeText(getActivity(), "好像没有网络哦!请检查你的网络", Toast.LENGTH_SHORT).show();
               else if (code == RestClientHelp.HTTP_NOT_FOUND)
                    Toast.makeText(getActivity(), "没有找到该用户", Toast.LENGTH_SHORT).show();
                else if (code == RestClientHelp.UNPROCESSABLE_ENTITY)
                    Toast.makeText(getActivity(), "密码错误", Toast.LENGTH_SHORT).show();
                else if (code == RestClientHelp.HTTP_NOT_ACCEPTABLE)
                    Toast.makeText(getActivity(), "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
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

    private int computeUsableHeight() {
        Rect r = new Rect();
        rootContain.getRootView().getWindowVisibleDisplayFrame(r);
        return (r.bottom - r.top);
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

    private void setColorFilter() {
        username.getCompoundDrawables()[0].setColorFilter(getResources().getColor(R.color.colorBlack54), PorterDuff.Mode.SRC_IN);
        password.getCompoundDrawables()[0].setColorFilter(getResources().getColor(R.color.colorBlack54), PorterDuff.Mode.SRC_IN);
        cleanUsername.getDrawable().setColorFilter(getResources().getColor(R.color.colorBlack54), PorterDuff.Mode.SRC_IN);
        cleanPassword.getDrawable().setColorFilter(getResources().getColor(R.color.colorBlack54), PorterDuff.Mode.SRC_IN);
        visiblePassword.getDrawable().setColorFilter(getResources().getColor(R.color.colorBlack54), PorterDuff.Mode.SRC_IN);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (rotateLoading.isStart())
            rotateLoading.stop();
        ButterKnife.unbind(this);
    }

}
