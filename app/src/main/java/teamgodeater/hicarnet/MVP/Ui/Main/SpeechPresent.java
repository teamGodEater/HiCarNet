package teamgodeater.hicarnet.MVP.Ui.Main;

/**
 * Created by G on 2016/6/20 0020.
 */

public class SpeechPresent extends MainContractor.SpeechPresent {
    @Override
    public void openDetail() {
        SpeechFragment speechFragment = new SpeechFragment();
        speechFragment.setResultListener(new SpeechFragment.OnSpeechResultListener() {
            @Override
            public void onResult(String result) {
                if (! mView.searchView.isSearchOpen()) {
                    mView.searchView.open(false);
                }
                mView.searchView.setText(result);
            }
        });
        speechFragment.show(mView.getChildFragmentManager(), "speech");
    }
    @Override
    protected void onStart() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
