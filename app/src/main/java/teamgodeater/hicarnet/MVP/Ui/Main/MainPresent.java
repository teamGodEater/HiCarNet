package teamgodeater.hicarnet.MVP.Ui.Main;

/**
 * Created by G on 2016/6/20 0020.
 */

public class MainPresent extends MainContractor.DispatchPresent {
    public MapPresent mapPresent;
    public MapSearchPresent mapSearchPresent;
    public SpeechPresent speechPresent;
    public GasStationPresent gasStationPresent;


    @Override
    protected void onStart() {
        mapPresent = new MapPresent();
        mapSearchPresent = new MapSearchPresent();
        speechPresent = new SpeechPresent();
        gasStationPresent = new GasStationPresent();

        mapSearchPresent.setView(mView);
        mapPresent.setView(mView);
        speechPresent.setView(mView);
        gasStationPresent.setView(mView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapPresent.onDestroy();
        mapSearchPresent.onDestroy();
        speechPresent.onDestroy();
    }
}
