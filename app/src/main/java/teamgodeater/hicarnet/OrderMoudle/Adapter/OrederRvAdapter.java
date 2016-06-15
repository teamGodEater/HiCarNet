package teamgodeater.hicarnet.OrderMoudle.Adapter;

import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.hugo.android.scanner.StringToErWeiMa;
import teamgodeater.hicarnet.Activity.ManageActivity;
import teamgodeater.hicarnet.Data.UserOrderData;
import teamgodeater.hicarnet.Help.Utils;
import teamgodeater.hicarnet.R;

import static teamgodeater.hicarnet.Data.UserOrderData.C93;
import static teamgodeater.hicarnet.Data.UserOrderData.C95;
import static teamgodeater.hicarnet.Data.UserOrderData.C97;
import static teamgodeater.hicarnet.Data.UserOrderData.Q93;
import static teamgodeater.hicarnet.Data.UserOrderData.Q95;
import static teamgodeater.hicarnet.Data.UserOrderData.Q97;

/**
 * Created by G on 2016/6/15 0015.
 */

public class OrederRvAdapter extends RecyclerView.Adapter<OrederRvAdapter.OrderHolder> {

    ArrayList<UserOrderData> datas;
    private boolean hasErWeiMa = true;

    public OrederRvAdapter(ArrayList<UserOrderData> datas) {
        this.datas = datas;
    }

    public void setHasErWeiMa(boolean hasErWeiMa) {
        this.hasErWeiMa = hasErWeiMa;
    }

    @Override
    public OrderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card_order, parent, false);
        return new OrderHolder(inflate);
    }

    @Override
    public void onBindViewHolder(final OrderHolder holder, int position) {
        final UserOrderData userOrderData = datas.get(position);
        holder.tvLocation.setText(userOrderData.getGas_address());
        holder.tvDate.setText(userOrderData.getDate());
        holder.tvJiayouliang.setText(userOrderData.getRise());
        holder.tvMoney.setText(userOrderData.getMoney());
        String type = "";
        switch (userOrderData.getType()) {
            case Q97:
                type = "汽油 #97";
                break;
            case Q95:
                type = "汽油 #97";
                break;
            case Q93:
                type = "汽油 #97";
                break;
            case C97:
                type = "柴油 #97";
                break;
            case C95:
                type = "柴油 #97";
                break;
            case C93:
                type = "柴油 #97";
                break;
        }
        holder.tvType.setText(type);

        holder.ivStreet.setImageBitmap(userOrderData.streeMap);
        holder.ivGas.setColorFilter(Utils.getColorFromRes(R.color.colorBlack54), PorterDuff.Mode.SRC_IN);
        holder.ivRight.setColorFilter(Utils.getColorFromRes(R.color.colorBlack54), PorterDuff.Mode.SRC_IN);

        if (userOrderData.is_used()) {
            holder.status.setText("已消费");
            holder.status.setTextColor(Utils.getColorFromRes(R.color.color2));
        } else {
            holder.status.setText("未使用");
            holder.status.setTextColor(Utils.getColorFromRes(R.color.colorPrimary));
        }

        if (hasErWeiMa) {
            holder.bgErWeiMa.setVisibility(View.VISIBLE);
            holder.ivErWeiMa.setVisibility(View.VISIBLE);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Bitmap image = StringToErWeiMa.createImage(userOrderData.getOrder_code());
                    ManageActivity.manageActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            holder.ivErWeiMa.setImageBitmap(image);
                        }
                    });
                }
            });
            holder.ivErWeiMa.setImageResource(R.color.colorBlack20);
            holder.orderCode.setText(userOrderData.getOrder_code());
        }else {
            holder.bgErWeiMa.setVisibility(View.GONE);
            holder.ivErWeiMa.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (datas == null)
            return 0;
        return datas.size();
    }

    public class OrderHolder extends RecyclerView.ViewHolder {
        public OrderHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Bind(R.id.iv_gas)
        public ImageView ivGas;
        @Bind(R.id.tvLocation)
        public TextView tvLocation;
        @Bind(R.id.status)
        public TextView status;
        @Bind(R.id.ivStreet)
        public ImageView ivStreet;
        @Bind(R.id.tvType)
        public TextView tvType;
        @Bind(R.id.tv_jiayouliang)
        public TextView tvJiayouliang;
        @Bind(R.id.tvDate)
        public TextView tvDate;
        @Bind(R.id.orderCode)
        public TextView orderCode;
        @Bind(R.id.tvMoney)
        public TextView tvMoney;
        @Bind(R.id.ivRight)
        public ImageView ivRight;
        @Bind(R.id.bgErWeiMa)
        public View bgErWeiMa;
        @Bind(R.id.ivErWeiMa)
        public ImageView ivErWeiMa;

    }
}
