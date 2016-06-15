package teamgodeater.hicarnet.MainModle.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.zagum.speechrecognitionview.RecognitionProgressView;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;

import butterknife.Bind;
import butterknife.ButterKnife;
import teamgodeater.hicarnet.R;
import teamgodeater.hicarnet.Widget.RippleBackGroundView;

/**
 * Created by G on 2016/5/24 0024.
 */

public class SpeachFragment extends DialogFragment {


    SpeechRecognizer iat;
    StringBuilder sayText;
    @Bind(R.id.recognition)
    RecognitionProgressView recognition;
    @Bind(R.id.status_tip)
    TextView statusTip;
    @Bind(R.id.button)
    RippleBackGroundView button;
    @Bind(R.id.tip)
    TextView tip;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.frgm_main_speach, container, false);
        ButterKnife.bind(this, inflate);
        init();
        return inflate;
    }

    private void init() {

        Context activity = getActivity();
        int[] colors = {
                ContextCompat.getColor(activity, R.color.color1),
                ContextCompat.getColor(activity, R.color.color2),
                ContextCompat.getColor(activity, R.color.color3),
                ContextCompat.getColor(activity, R.color.color4),
                ContextCompat.getColor(activity, R.color.color5)
        };
        int[] heights = {60, 76, 58, 80, 55};

        recognition.setColors(colors);
        recognition.setBarMaxHeightsInDp(heights);

        iat = SpeechRecognizer.createRecognizer(activity, null);
        iat.setParameter(SpeechConstant.DOMAIN, "poi");
        iat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        iat.setParameter(SpeechConstant.NET_TIMEOUT, "8000");
        iat.setParameter(SpeechConstant.ACCENT, "mandarin");
        iat.setParameter(SpeechConstant.ASR_PTT, "0");
        iat.setParameter(SpeechConstant.RESULT_TYPE, "plain");
        iat.setParameter(SpeechConstant.VAD_BOS, "6000");
        iat.setParameter(SpeechConstant.VAD_EOS, "2500");

        iat.startListening(mRecoListener);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = ((RippleBackGroundView) v).getText();
                if (tag.equals("停止说话")){
                    iat.stopListening();
                } else if (tag.equals("确定")) {

                } else if (tag.equals("停止")) {
                    iat.stopListening();
                } else if (tag.equals("开始说话")) {
                    iat.startListening(mRecoListener);
                }
            }
        });
    }

    RecognizerListener mRecoListener = new RecognizerListener() {
        @Override
        public void onVolumeChanged(int i, byte[] bytes) {
            recognition.onRmsChanged(i);
            if (i > 4) {
                tip.setText("正在接收...");
                button.setText("停止说话");
            }
        }

        @Override
        public void onBeginOfSpeech() {
            tip.setText("开始识别");
            button.setText("请说话");
            recognition.play();
            recognition.onBeginningOfSpeech();
            sayText = new StringBuilder();
        }

        @Override
        public void onEndOfSpeech() {
            tip.setText("识别中...");
            button.setText("停止");
            recognition.onEndOfSpeech();
        }

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            if (b) {
                tip.setText("识别成功");
                button.setText("确定");
                recognition.stop();
            }
            sayText.append(recognizerResult.getResultString());
            statusTip.setText(sayText.toString());
        }

        @Override
        public void onError(SpeechError speechError) {
            tip.setText("错误: " + speechError.getErrorDescription());
            button.setText("开始说话");
            recognition.stop();
            statusTip.setText(speechError.getErrorDescription());
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        iat.destroy();
        ButterKnife.unbind(this);
    }
}
