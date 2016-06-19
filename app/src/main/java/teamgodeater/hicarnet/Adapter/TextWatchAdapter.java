package teamgodeater.hicarnet.Adapter;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by G on 2016/6/18 0018.
 */

public class TextWatchAdapter implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        onTextChanged(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public void onTextChanged(String s) {

    }
}
