package teamgodeater.hicarnet.MainModle.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.zagum.speechrecognitionview.RecognitionProgressView;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.orhanobut.logger.Logger;

import butterknife.Bind;
import butterknife.ButterKnife;
import teamgodeater.hicarnet.R;

/**
 * Created by G on 2016/5/24 0024.
 */

public class SpeachFragment extends DialogFragment {

    @Bind(R.id.recognition)
    RecognitionProgressView recognition;
    @Bind(R.id.status_tip)
    TextView statusTip;
    @Bind(R.id.button)
    ImageButton button;

    SpeechRecognizer iat;
    StringBuilder sayText ;


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
                Logger.d("speach start listen speach");
                iat.startListening(mRecoListener);
            }
        });
    }

    RecognizerListener mRecoListener = new RecognizerListener() {
        @Override
        public void onVolumeChanged(int i, byte[] bytes) {
            Logger.d("speach onVolumeChanged " + i);
            recognition.onRmsChanged(i);
        }

        @Override
        public void onBeginOfSpeech() {
            Logger.d("speach onBeginOfSpeech");
            recognition.play();
            recognition.onBeginningOfSpeech();
            sayText = new StringBuilder();
        }

        @Override
        public void onEndOfSpeech() {
            Logger.d("speach onEndOfSpeech");
            recognition.onEndOfSpeech();
        }

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            Logger.d("speach onRouteResult  islast" + b);
            if (b) {
                recognition.stop();

            }
            sayText.append(recognizerResult.getResultString());
            statusTip.setText(sayText.toString());
        }

        @Override
        public void onError(SpeechError speechError) {
            Logger.d("speach speechError  islast" + speechError.getErrorDescription());
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
