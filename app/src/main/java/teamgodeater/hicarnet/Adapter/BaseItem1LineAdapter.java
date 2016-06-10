package teamgodeater.hicarnet.Adapter;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import teamgodeater.hicarnet.Data.BaseItem1LineData;
import teamgodeater.hicarnet.Help.Utils;
import teamgodeater.hicarnet.R;
import teamgodeater.hicarnet.Widget.RippleBackGroundView;

/**
 * Created by G on 2016/5/29 0029.
 */

public class BaseItem1LineAdapter extends RecyclerView.Adapter<BaseItem1LineAdapter.BaseItem1LineHolder> {

    public List<? extends BaseItem1LineData> list;
    private OnItemClickListener itemClickListener;

    public BaseItem1LineAdapter(List<? extends BaseItem1LineData> l) {
        list = l;
    }

    private int focusItemIndex = -1;

    @Override
    public BaseItem1LineHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ico_1line, parent, false);
        return new BaseItem1LineHolder(v);
    }


    public interface OnItemClickListener {
        void onClick(BaseItem1LineData data, int position);
    }

    public void setOnClickListener(OnItemClickListener listener) {
        itemClickListener = listener;
    }

    @Override
    public void onBindViewHolder(BaseItem1LineHolder holder, final int position) {
        BaseItem1LineData itemData = list.get(position);
        if (itemData.icoLeftBitmap == null) {
            holder.icoLeft.setImageResource(itemData.icoLeft);
            holder.icoLeft.setColorFilter(Utils.getColorFromRes(R.color.colorBlack54), PorterDuff.Mode.SRC_IN);
        } else {
            holder.icoLeft.setImageBitmap(itemData.icoLeftBitmap);
        }
        holder.title.setText(itemData.title);

        holder.tipRight.setText(itemData.tipRight);
        holder.icoRight.setImageResource(itemData.icoRight);
        holder.icoRight.setColorFilter(Utils.getColorFromRes(R.color.colorBlack54), PorterDuff.Mode.SRC_IN);

        if (itemData.isDivider) {
            holder.divider.setVisibility(View.VISIBLE);
        } else {
            holder.divider.setVisibility(View.GONE);
        }

        if (position == focusItemIndex) {
            holder.backGround.setClickable(false);
            holder.backGround.setBackgroundColor(Color.argb(10, 0, 0, 0));
        } else {
            holder.backGround.setBackground(null);
            if (itemData.isClickAble) {
                holder.backGround.setClickable(true);
                holder.backGround.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (itemClickListener != null) {
                            itemClickListener.onClick(list.get(position), position);
                        }
                    }
                });
            } else {
                holder.backGround.setClickable(false);
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class BaseItem1LineHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.icoLeft)
        public ImageView icoLeft;
        @Bind(R.id.title)
        public TextView title;
        @Bind(R.id.tip)
        public TextView tip;
        @Bind(R.id.divider)
        public View divider;
        @Bind(R.id.tipRight)
        public TextView tipRight;
        @Bind(R.id.icoRight)
        public ImageView icoRight;
        @Bind(R.id.rightLayout)
        public LinearLayout rightLayout;
        @Bind(R.id.backGround)
        public RippleBackGroundView backGround;

        public View rootView;

        public BaseItem1LineHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            ButterKnife.bind(this, itemView);
        }
    }

    public void setFocusItem(int position) {
        focusItemIndex = position;
        notifyDataSetChanged();
    }

    public void cleanFocusItem() {
        focusItemIndex = -1;
        notifyDataSetChanged();
    }
}
