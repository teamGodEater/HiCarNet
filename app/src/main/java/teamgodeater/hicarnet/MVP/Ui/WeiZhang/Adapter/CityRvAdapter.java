package teamgodeater.hicarnet.MVP.Ui.WeiZhang.Adapter;

import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import teamgodeater.hicarnet.R;
import teamgodeater.hicarnet.Utils.Utils;
import teamgodeater.hicarnet.Widget.RippleBackGroundView;

/**
 * Created by G on 2016/6/11 0011.
 */

public class CityRvAdapter extends RecyclerView.Adapter<CityRvAdapter.CityHolder> {

    private List<String> dataList;
    private OnClickListener listener;

    public interface OnClickListener {
        void onClick(int position);
    }

    public CityRvAdapter(List<String> dataList, OnClickListener listener) {
        this.dataList = dataList;
        this.listener = listener;
    }

    @Override
    public CityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_city, parent, false);
        return new CityHolder(v);
    }

    @Override
    public void onBindViewHolder(CityHolder holder, final int position) {
        String string = dataList.get(position);
        SpannableString s = new SpannableString(string);
        s.setSpan(new ForegroundColorSpan(Utils.getColorFromRes(R.color.color1)), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.title.setText(s);
        if (listener != null)
            holder.clickBg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(position);
                }
            });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class CityHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public RippleBackGroundView clickBg;

        public CityHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            clickBg = (RippleBackGroundView) itemView.findViewById(R.id.clickBg);
        }
    }

}
