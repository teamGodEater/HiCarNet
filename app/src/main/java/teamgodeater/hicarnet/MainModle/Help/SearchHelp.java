package teamgodeater.hicarnet.MainModle.Help;


import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.lapism.searchview.SearchAdapter;
import com.lapism.searchview.SearchHistoryTable;
import com.lapism.searchview.SearchItem;
import com.lapism.searchview.SearchView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import teamgodeater.hicarnet.Activity.ManageActivity;
import teamgodeater.hicarnet.Help.Utils;
import teamgodeater.hicarnet.MainModle.Fragment.SpeachFragment;
import teamgodeater.hicarnet.R;

/**
 * Created by G on 2016/5/23 0023.
 */

public class SearchHelp implements SearchView.OnVoiceClickListener, SearchView.OnQueryTextListener, SearchView.OnMenuClickListener, SuggestSearchHelp.onResultListener, SearchAdapter.OnItemClickListener, RoutePlanSearchHelp.OnDrivingRouteListener {
    private final ManageActivity manageActivity;
    private final SearchView searchView;
    private final FragmentManager fragmentManager;
    private final SuggestSearchHelp suggestSearchHelp;
    private final SearchAdapter searchAdapter;
    private final RoutePlanSearchHelp routePlanSearchHelp;
    private OnSearchRouteListener searchRouteListener;


    /**
     * 构造
     *
     * @param m
     * @param s
     * @param fm
     */
    public SearchHelp(final ManageActivity m, SearchView s, final FragmentManager fm) {
        manageActivity = m;
        searchView = s;
        fragmentManager = fm;

        suggestSearchHelp = new SuggestSearchHelp(m, this);
        routePlanSearchHelp = new RoutePlanSearchHelp(m, this);

        //-----------------------------searchView视图和监听设置--------------------------------------
        int margin = Utils.dp2Px(16);
        s.setVersionMargins(manageActivity.getStatuBarHeight() + margin / 2, margin, margin / 2);
        searchAdapter = new SearchAdapter(Utils.getContext());
        searchAdapter.setOnItemClickListener(this);
        s.setAdapter(searchAdapter);
        s.setOnVoiceClickListener(this);
        s.setOnMenuClickListener(this);
        s.setOnQueryTextListener(this);
    }

    //
    public interface OnSearchRouteListener {
        void onRouteStart(String title);

        void onRouteResult(DrivingRouteResult result);
    }

    //---------------------------------------Implements---------------------------------------------

    //---------------------------SearchViewImplements-----------------------------------------
    @Override
    public void onVoiceClick() {
        new SpeachFragment().show(fragmentManager, "speach");
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Logger.d("newText " + newText);
        suggestSearchHelp.requestSuggestSearch(newText);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Logger.d("query " + query);
        suggestSearchHelp.requestSuggestSearch(query);
        return true;
    }

    @Override
    public void onMenuClick() {
        manageActivity.openDrawer();
    }

    //------------------------------SuggestImplements-----------------------------------------
    @Override
    public void onSucceed(List<SuggestionResult.SuggestionInfo> allSuggestions, String searchStr) {
        Logger.d("onSucceed ");
        List<SearchItem> list = new ArrayList<>();
        for (SuggestionResult.SuggestionInfo suggestionInfo : allSuggestions) {

            SearchItem item = new SearchItem();
            item.key = suggestionInfo.key;
            if (suggestionInfo.pt == null) {
                item.ico = R.drawable.ic_search;
            } else {
                item.ico = R.drawable.ic_location;
                item.city = suggestionInfo.city;
                item.district = suggestionInfo.district;
                item.latitude = suggestionInfo.pt.latitude;
                item.longitude = suggestionInfo.pt.longitude;
            }
            list.add(item);
        }
        searchAdapter.setSuggestionsList(list);
        searchView.startTextFilter();
    }


    @Override
    public void onErrorSuggest(int code) {
        String tip = "";
        switch (code) {
            case SuggestSearchHelp.Error_Cant_Request:
                tip = "请求错误 请重试";
                break;
            case SuggestSearchHelp.Error_City_Empty:
            case SuggestSearchHelp.Error_No_Loc:
                tip = "定位失败 请检查你的网络和Gps设置";
                break;
            case SuggestSearchHelp.Error_No_Net:
                tip = "无法连接服务器 请检查你的网络";
                break;
            case SuggestSearchHelp.Error_Search_String_Empty:
                tip = "搜索的内容不能为空";
                break;
            case SuggestSearchHelp.Error_No_Result:
                tip = "没有找到相关的内容";
                break;
        }
        if (!tip.isEmpty()) {
            Toast.makeText(Utils.getContext(), tip, Toast.LENGTH_SHORT).show();
        }
    }
    //-------------------------------RoutePlanSearchHelpImplement----------------------------

    @Override
    public void onSucceed(DrivingRouteResult route) {
        Logger.d("getRoute");
        if (searchRouteListener != null) {
            searchRouteListener.onRouteResult(route);
        }
    }

    @Override
    public void onErrorRoute(int code) {
        String tip = "";
        switch (code) {
            case RoutePlanSearchHelp.Error_Cant_Request:
                tip = "请求错误 请重试";
                break;
            case RoutePlanSearchHelp.Error_No_Loc:
                tip = "定位失败 请检查你的网络和Gps设置";
                break;
            case RoutePlanSearchHelp.Error_No_Net:
                tip = "无法连接服务器 请检查你的网络";
                break;
            case SuggestSearchHelp.Error_No_Result:
                tip = "没有找到相关的内容";
                break;
        }
        Toast.makeText(Utils.getContext(), tip, Toast.LENGTH_SHORT).show();
    }

    //-----------------------------------SearchItemClick--------------------------------------
    @Override
    public void onItemClick(SearchItem item) {
        if (item.longitude == 0) {
            searchView.setText(item.key);
            return;
        }
        if (item.ico == R.drawable.ic_location) {
            SearchHistoryTable table = new SearchHistoryTable(Utils.getContext());
            table.addItem(item);
        }
        PlanNode planNode = PlanNode.withLocation(new LatLng(item.latitude, item.longitude));
        routePlanSearchHelp.requestRoutePlanSearch(planNode);

        if (searchRouteListener != null) {
            searchRouteListener.onRouteStart(item.key);
        }
    }

    //--------------------------------------Method--------------------------------------------
    public boolean isInterceptBack() {
        if (searchView.isSearchOpen()) {
            searchView.close();
            return true;
        }
        return false;
    }

    /**
     * 销毁 释放资源
     */
    public void onDestroy() {
        suggestSearchHelp.onDestroy();
        routePlanSearchHelp.onDestroy();
    }

    public void setOnSearchRouteListener(OnSearchRouteListener listener) {
        searchRouteListener = listener;
    }

}
