package teamgodeater.hicarnet.WeizhangModle.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cheshouye.api.client.json.WeizhangResponseHistoryJson;
import com.cheshouye.api.client.json.WeizhangResponseJson;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import teamgodeater.hicarnet.Adapter.BaseItem2LineAdapter;
import teamgodeater.hicarnet.Data.BaseItem2LineData;
import teamgodeater.hicarnet.Fragment.BaseFragment;
import teamgodeater.hicarnet.Help.Utils;
import teamgodeater.hicarnet.R;

/**
 * Created by G on 2016/6/12 0012.
 */

public class WeiZhangDetialFragment extends BaseFragment {
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.linearLayout)
    LinearLayout linearLayout;

    WeizhangResponseJson weiZhangJson;


    public void setWeizhangJson(WeizhangResponseJson weiZhangJson) {
        this.weiZhangJson = weiZhangJson;
    }

    @NonNull
    @Override
    protected SupportWindowsParams onCreateSupportViewParams() {
        SupportWindowsParams params = new SupportWindowsParams();
        params.rootLayoutId = R.layout.frgm_weizhang_detail;
        params.primaryColor = Utils.getColorFromRes(R.color.color1);
        return params;
    }

    @Override
    protected void onOnceGlobalLayoutListen() {
        parentView.setTranslationX(parentView.getWidth());
        parentView.animate().translationX(0).setInterpolator(new AccelerateInterpolator()).start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootContain);
        tSetDefaultView(true, "违章详情");
        setRv();
        return rootView;
    }

    private void setRv() {
        if (weiZhangJson == null) {
            Toast.makeText(getActivity(),"没有获取到数据",Toast.LENGTH_SHORT).show();
            return;
        }

        List<BaseItem2LineData> datas = new ArrayList<>();
        BaseItem2LineData data1 = new BaseItem2LineData();
        data1.hasDivider = true;
        data1.isClickAble = false;
        data1.icoLeft = R.drawable.ic_receipt;
        data1.title = "扣分总计";
        data1.tipRight = weiZhangJson.getTotal_score() + " 分";
        datas.add(data1);
        BaseItem2LineData data2 = new BaseItem2LineData();
        data2.hasDivider = true;
        data2.isClickAble = false;
        data2.icoLeft = R.drawable.ic_monetization_on;
        data2.title = "罚款总计";
        data2.tipRight = weiZhangJson.getTotal_money() + " 元";
        datas.add(data2);
        BaseItem2LineData data3 = new BaseItem2LineData();
        data3.hasDivider = true;
        data3.isClickAble = false;
        data3.icoLeft = R.drawable.ic_format_list_numbered;
        data3.title = "违章记录条数";
        data3.tipRight = weiZhangJson.getCount() + " 条";
        datas.add(data3);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new BaseItem2LineAdapter(datas));

         List<WeizhangResponseHistoryJson> historys = weiZhangJson.getHistorys();

        if (historys == null || historys.size() == 0)
            return;

        int count = 0;
        for (WeizhangResponseHistoryJson bean : historys) {
            count++;
            View view = LayoutInflater.from(rootContain.getContext()).inflate(R.layout.item_weizhang, rootContain, false);
            TextView textView3 = (TextView) view.findViewById(R.id.textView3);
            TextView info = (TextView) view.findViewById(R.id.info);
            TextView status = (TextView) view.findViewById(R.id.status);
            TextView occurDate = (TextView) view.findViewById(R.id.occur_date);
            TextView occurArea = (TextView) view.findViewById(R.id.occur_area);
            TextView cityName = (TextView) view.findViewById(R.id.city_name);
            TextView officer = (TextView) view.findViewById(R.id.officer);
            TextView fen = (TextView) view.findViewById(R.id.fen);
            TextView money = (TextView) view.findViewById(R.id.money);

            textView3.setText("违章内容 " + count);

            info.setText(bean.getInfo());
            if (bean.getStatus().equals("Y")) {
                status.setTextColor(Utils.getColorFromRes(R.color.color5));
                status.setText("已处理");
            }else {
                status.setTextColor(Utils.getColorFromRes(R.color.color2));
                status.setText("没处理");
            }
            occurDate.setText(bean.getOccur_date());
            occurArea.setText(bean.getOccur_area());
            cityName.setText(bean.getCity_name());
            officer.setText(bean.getOfficer());
            fen.setText(bean.getFen() + " 分");
            money.setText(bean.getMoney() + " 元");

            LinearLayout.LayoutParams layoutParams =
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int margin16 = Utils.dp2Px(16);
            layoutParams.setMargins(margin16,0,margin16,margin16);
            view.setLayoutParams(layoutParams);
            linearLayout.addView(view);
        }


    }


    @Override
    protected void onToolBarClick(View v) {
        String tag = (String) v.getTag();
        if (tag.equals("back")) {
            finish();
        }
    }

    @Override
    public boolean onInterceptBack() {
        finish();
        return true;
    }

    @Override
    public String getType() {
        return "weizhang";

    }

    private void finish() {
        parentView.animate().translationX(parentView.getWidth())
                .setInterpolator(new AccelerateInterpolator()).start();
        destroySelfShowBefore(280);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
