package teamgodeater.hicarnet.Fragment;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import teamgodeater.hicarnet.R;
import teamgodeater.hicarnet.Widget.RoundedImageView;

/**
 * Created by G on 2016/5/20 0020.
 */
public class DrawerFragment extends Fragment {


    @Bind(R.id.headImage)
    RoundedImageView headImage;
    @Bind(R.id.Greetings)
    TextView greetings;
    @Bind(R.id.LocationTip)
    TextView locationTip;
    @Bind(R.id.HeadContain)
    FrameLayout headContain;
    @Bind(R.id.RecyclerView)
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frgm_drawer, container, false);
        ButterKnife.bind(this, v);
        Drawable[] compoundDrawables = locationTip.getCompoundDrawables();
        compoundDrawables[0].setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
