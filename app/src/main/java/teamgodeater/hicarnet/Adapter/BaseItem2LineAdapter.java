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
import teamgodeater.hicarnet.Data.BaseItem2LineData;
import teamgodeater.hicarnet.R;
import teamgodeater.hicarnet.Widget.RippleBackGroundView;
import teamgodeater.hicarnet.Widget.RoundedImageView;

/**
 * Created by G on 2016/5/29 0029.
 */

public class BaseItem2LineAdapter extends RecyclerView.Adapter<BaseItem2LineAdapter.BaseItem2LineHolder> {

    public List<? extends BaseItem2LineData> list;
    private OnItemClickListener itemClickListener;

    public BaseItem2LineAdapter(List<? extends BaseItem2LineData> l) {
        list = l;
    }

    private int focusItemIndex = -1;

    @Override
    public BaseItem2LineHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ico_2line, parent, false);
        return new BaseItem2LineHolder(v);
    }


    public interface OnItemClickListener {
        void onClick(BaseItem2LineData data, int position);
    }

    public void setOnClickListener(OnItemClickListener listener) {
        itemClickListener = listener;
    }

    @Override
    public void onBindViewHolder(BaseItem2LineHolder holder, final int position) {
        BaseItem2LineData itemData = list.get(position);
        if (itemData.icoLeftBitmap == null) {
            holder.icoLeft.setImageResource(itemData.icoLeft);
            holder.icoLeft.setColorFilter(itemData.icoLeftColor, PorterDuff.Mode.SRC_IN);
        } else {
            holder.icoLeft.setImageBitmap(itemData.icoLeftBitmap);
        }

        holder.title.setText(itemData.title);
        if (itemData.tip == null || itemData.tip.equals("")) {
            holder.tip.setVisibility(View.GONE);
        }else {
            holder.tip.setVisibility(View.VISIBLE);
            holder.tip.setText(itemData.tip);
        }

        holder.tipRight.setText(itemData.tipRight);

        if (itemData.icoRight == 0) {
            holder.icoRight.setVisibility(View.GONE);
        }else {
            holder.icoRight.setVisibility(View.VISIBLE);
            holder.icoRight.setImageResource(itemData.icoRight);
            holder.icoRight.setColorFilter(itemData.icoRightColor, PorterDuff.Mode.SRC_IN);
        }

        if (itemData.hasDivider) {
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

    public class BaseItem2LineHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.icoLeft)
        public RoundedImageView icoLeft;
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

        public BaseItem2LineHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            ButterKnife.bind(this, itemView);
        }
    }

    public void setFocusItem(int position) {
        if (focusItemIndex == position)
            return;
        focusItemIndex = position;
        notifyDataSetChanged();
    }

    public void cleanFocusItem() {
        focusItemIndex = -1;
        notifyDataSetChanged();
    }
}
