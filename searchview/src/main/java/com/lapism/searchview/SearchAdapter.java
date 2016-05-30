package com.lapism.searchview;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

// TODO ARROW / HAMBURGER / BEHAVIOR / SingleTask / icon, CLOSE KEY AND VIEW BOTH SAME TIME
// TODO file:///E:/Android/SearchView/sample/build/outputs/lint-results-debug.html
// TODO file:///E:/Android/SearchView/searchview/build/outputs/lint-results-debug.html
// TODO voice click result
// TODO E/RecyclerView: No adapter attached; skipping layout
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ResultViewHolder> implements Filterable {

    private final SearchHistoryTable mHistoryDatabase;
    private String key = "";
    private List<SearchItem> mResultList = new ArrayList<>();
    private List<SearchItem> mSuggestionsList = new ArrayList<>();

    private OnItemClickListener mItemClickListener;

    public SearchAdapter(Context context) {
        mSuggestionsList = new ArrayList<>();
        mHistoryDatabase = new SearchHistoryTable(context);
    }

    public void setSuggestionsList(List<SearchItem> list) {
        mSuggestionsList = list;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (!TextUtils.isEmpty(constraint)) {
                    key = constraint.toString();
                    List<SearchItem> results = new ArrayList<>();
                    List<SearchItem> history = new ArrayList<>();
                    history.addAll(mHistoryDatabase.getAllItems());
                    history.addAll(mSuggestionsList);
                    for (SearchItem str : history) {
                        if (str.key.contains(key)) {
                            results.add(str);
                        }
                    }

                    filterResults.values = results;
                    filterResults.count = results.size();

                } else {
                    key = "";
                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.values != null) {
                    mResultList.clear();
                    List<SearchItem> result = (List<SearchItem>) results.values;
                    for (SearchItem item : result) {
                        mResultList.add(item);
                    }
                } else {
                    mResultList.clear();
                    mResultList = mHistoryDatabase.getAllItems();
                }
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public ResultViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.search_item, parent, false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ResultViewHolder viewHolder, int position) {
        SearchItem item = mResultList.get(position);
        viewHolder.bindView(item);
    }

    @Override
    public int getItemCount() {
        return mResultList.size();
    }

    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(SearchItem item);
    }

    public class ResultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView ico;
        public TextView title;
        public TextView tip;
        public SearchItem item;


        public ResultViewHolder(View view) {
            super(view);
            ico = (ImageView) view.findViewById(R.id.icoLeft);
            title = (TextView) view.findViewById(R.id.titleKey);
            tip = (TextView) view.findViewById(R.id.tipCityDistrict);
            view.setOnClickListener(this);
        }

        public void bindView(SearchItem i) {
            item = i;
            ico.setImageResource(item.ico);
            ico.setColorFilter(SearchView.getIconColor(), PorterDuff.Mode.SRC_IN);
            String string = item.key;
            if (string.contains(key)) {
                SpannableString s = new SpannableString(string);
                s.setSpan(new ForegroundColorSpan(SearchView.getTextHighlightColor()), string.indexOf(key), string.indexOf(key) + key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                title.setText(s, TextView.BufferType.SPANNABLE);
            } else {
                title.setText(string);
            }

            if (item.longitude == 0) {
                tip.setVisibility(View.GONE);
            } else {
                tip.setVisibility(View.VISIBLE);
                tip.setText(item.city + "-" + item.district);
            }
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(item);
            }
        }
    }

}