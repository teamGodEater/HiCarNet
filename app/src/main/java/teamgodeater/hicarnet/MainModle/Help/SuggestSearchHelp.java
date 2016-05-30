package teamgodeater.hicarnet.MainModle.Help;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;

import java.util.List;

import teamgodeater.hicarnet.Activity.ManageActivity;
import teamgodeater.hicarnet.Help.Utils;

/**
 * Created by G on 2016/5/25 0025.
 */

public class SuggestSearchHelp {

    private final SuggestionSearch mSuggestionSearch;
    private final ManageActivity manageActivity;
    private onResultListener resultListener;
    private String searchStr;

    public final static int Error_No_Net = 20001, Error_No_Loc = 20002,
            Error_No_Result = 20003, Error_Search_String_Empty = 20004, Error_City_Empty = 20005, Error_Cant_Request = 20006;

    public interface onResultListener {
        void onSucceed(List<SuggestionResult.SuggestionInfo> allSuggestions, String searchStr);
        void onErrorSuggest(int code);
    }

    public SuggestSearchHelp(ManageActivity activity, onResultListener l) {
        manageActivity = activity;
        resultListener = l;
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(listener);
    }

    public void requestSuggestSearch(String searchText) {
        searchText = searchText.trim();

        if (searchText.isEmpty()) {
            resultListener.onErrorSuggest(Error_Search_String_Empty);
            return;
        }
        BDLocation myLocation = manageActivity.getMyLocation();
        if (Utils.getNetworkType() == Utils.NETTYPE_NONET) {
            resultListener.onErrorSuggest(Error_No_Net);
            return;
        }
        if (myLocation == null) {
            resultListener.onErrorSuggest(Error_No_Loc);
            return;
        }
        if (myLocation.getCity() == null) {
            resultListener.onErrorSuggest(Error_City_Empty);
            return;
        }
        boolean b = mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                .location(Utils.getLatLng(myLocation))
                .keyword(searchText)
                .city(myLocation.getCity()));

        if (!b) {
            resultListener.onErrorSuggest(Error_Cant_Request);
        }else {
            searchStr = searchText;
        }
    }

    public void onDestroy() {
        mSuggestionSearch.destroy();
    }

    OnGetSuggestionResultListener listener = new OnGetSuggestionResultListener() {
        public void onGetSuggestionResult(SuggestionResult res) {
            List<SuggestionResult.SuggestionInfo> allSuggestions = res.getAllSuggestions();
            if (res == null || allSuggestions == null) {
                resultListener.onErrorSuggest(Error_No_Result);
                return;
            }
            resultListener.onSucceed(allSuggestions, searchStr);
        }
    };

}
