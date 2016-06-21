package teamgodeater.hicarnet.MVP.Ui.Main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;

import com.lapism.searchview.SearchAdapter;
import com.lapism.searchview.SearchItem;
import com.lapism.searchview.SearchView;
import com.rey.material.widget.FloatingActionButton;

import butterknife.Bind;
import butterknife.ButterKnife;
import teamgodeater.hicarnet.Layout.LoadingView.BookLoadingViewHelp;
import teamgodeater.hicarnet.Layout.LoadingView.RotateLoadingViewHelp;
import teamgodeater.hicarnet.MVP.Base.BaseFragment;
import teamgodeater.hicarnet.MVP.Base.WindowsParams;
import teamgodeater.hicarnet.MVP.Ui.Main.Adapter.BottomPagerAdapter;
import teamgodeater.hicarnet.R;
import teamgodeater.hicarnet.Utils.Utils;
import teamgodeater.hicarnet.Widget.RippleBackGroundView;

import static teamgodeater.hicarnet.C.VT_MAIN;

/**
 * Created by G on 2016/6/18 0018.
 */

public class MainFragment extends BaseFragment<MainPresent> implements MainContractor.View {


    @Bind(R.id.zoomLocButton)
    public RippleBackGroundView zoomLocButton;
    @Bind(R.id.bottomViewPager)
    public ViewPager bottomViewPager;
    @Bind(R.id.searchView)
    public SearchView searchView;
    @Bind(R.id.viewPagerFramelayout)
    public FrameLayout viewPagerFramelayout;
    @Bind(R.id.pagerSelect1)
    View pagerSelect1;
    @Bind(R.id.pagerSelect2)
    View pagerSelect2;
    @Bind(R.id.detail_return)
    FloatingActionButton detailReturn;
    @Bind(R.id.detail_buy)
    FloatingActionButton detailBuy;
    @Bind(R.id.detail_go)
    FloatingActionButton detailGo;

    public SearchAdapter searchAdapter;
    @Bind(R.id.detail_top)
    CardView detailTop;

    private BottomPagerAdapter bottomPagerAdapter;
    private RotateLoadingViewHelp rotateLoading;
    private BookLoadingViewHelp bottomRotateLoading;

    @NonNull
    @Override
    protected WindowsParams onCreateSupportViewParams(WindowsParams params) {
        params.rootLayoutId = R.layout.frgm_main_main;
        params.isHasToolBar = false;
        params.isNoFullScreen = false;
        return params;
    }

    @Override
    public String getType() {
        return VT_MAIN;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, mRootContain);
        setColorFilter();
        setView();
        setListener();
        return rootView;
    }

    @Override
    public void onOnceGlobalLayoutListen() {
        mPresenter.gasStationPresent.showShortGasStation();
        int margin = Utils.dp2Px(16);
        int topMargin = manageActivity.getStatusBarHeight() + margin / 2;
        detailTop.setTranslationY(-detailTop.getHeight() - topMargin);
        hideOrderFab();
    }

    @Override
    public void onResume() {
        super.onResume();
        manageActivity.onMapResume();
    }

    private void setView() {
        rotateLoading = new RotateLoadingViewHelp(mTopView);
        bottomRotateLoading = new BookLoadingViewHelp(viewPagerFramelayout);

        rotateLoading.setLoadingBg(true);
        bottomRotateLoading.setLoadingBg(true, Color.WHITE);
        bottomRotateLoading.setTextBackGroundColor(Utils.getColorFromRes(R.color.colorPrimary));

        bottomPagerAdapter = new BottomPagerAdapter(getContext());
        bottomViewPager.setAdapter(bottomPagerAdapter);

        int margin = Utils.dp2Px(16);
        int topMargin = manageActivity.getStatusBarHeight() + margin / 2;
        searchView.setVersionMargins(topMargin, margin, margin / 2);
        searchAdapter = new SearchAdapter(Utils.getContext());
        searchView.setAdapter(searchAdapter);

    }


    private void setListener() {
        searchAdapter.setOnItemClickListener(new SearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SearchItem item) {
                mPresenter.mapSearchPresent.showRoute(item);
            }
        });
        searchView.setOnVoiceClickListener(new SearchView.OnVoiceClickListener() {
            @Override
            public void onVoiceClick() {
                mPresenter.speechPresent.openDetail();
            }
        });
        searchView.setOnMenuClickListener(new SearchView.OnMenuClickListener() {
            @Override
            public void onMenuClick() {
                manageActivity.openDrawer();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                mPresenter.mapSearchPresent.searchLocation(newText);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                mPresenter.mapSearchPresent.searchLocation(query);
                return true;
            }
        });

        searchView.setOnBackClickListener(new SearchView.OnBackClickListener() {
            @Override
            public void onBackClick() {
                backClick();
            }
        });

        bottomViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                changePagerSelect(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        zoomLocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.mapPresent.zoomToFit();
            }
        });

        detailGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.gasStationPresent.orderGoClick();
            }
        });

        detailReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.gasStationPresent.orderReturnClick();
            }
        });

        detailBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.gasStationPresent.orderBuyClick();
            }
        });
    }

    private void backClick() {
        hidePagerLoading();
        mPresenter.mapSearchPresent.removeResult();
        mPresenter.mapPresent.setMyLocVisible(true);
        mPresenter.gasStationPresent.showShortGasStation();
        mPresenter.mapPresent.zoomToFit();
    }

    private void setColorFilter() {
        changePagerSelect(0);
        zoomLocButton.getDrawable().mutate().setColorFilter(getResources().getColor(R.color.colorBlack54), PorterDuff.Mode.SRC_IN);
        detailBuy.getIcon().mutate().setColorFilter(getResources().getColor(R.color.colorBlack54), PorterDuff.Mode.SRC_IN);
        detailGo.getIcon().mutate().setColorFilter(getResources().getColor(R.color.colorBlack54), PorterDuff.Mode.SRC_IN);
        detailReturn.getIcon().mutate().setColorFilter(getResources().getColor(R.color.colorBlack54), PorterDuff.Mode.SRC_IN);
    }

    private void changePagerSelect(int position) {
        if (position == 0) {
            pagerSelect1.setAlpha(0.87f);
            pagerSelect2.setAlpha(0.34f);
        } else {
            pagerSelect1.setAlpha(0.34f);
            pagerSelect2.setAlpha(0.87f);
        }
    }

    public void viewPagerAnimationVisible(boolean visible, final Runnable runnable) {
        if (visible) {
            pagerSelect1.setVisibility(View.VISIBLE);
            pagerSelect2.setVisibility(View.VISIBLE);
        } else {
            pagerSelect1.setVisibility(View.GONE);
            pagerSelect2.setVisibility(View.GONE);
        }

        int height = viewPagerFramelayout.getHeight();
        viewPagerFramelayout.animate().translationY(visible ? 0f : height)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationCancel(Animator animation) {
                        super.onAnimationCancel(animation);
                        if (runnable != null)
                            runnable.run();
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (runnable != null)
                            runnable.run();
                    }
                }).setInterpolator(new AccelerateInterpolator()).setDuration((long) (height * 0.5)).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    @Override
    public void showOrderFab() {
        detailBuy.setClickable(true);
        detailReturn.setClickable(true);
        detailGo.setClickable(true);
        detailBuy.setVisibility(View.VISIBLE);
        detailReturn.setVisibility(View.VISIBLE);
        detailGo.setVisibility(View.VISIBLE);
        detailBuy.setScaleX(0.0001f);
        detailGo.setScaleX(0.0001f);
        detailReturn.setScaleX(0.0001f);
        detailBuy.setScaleY(0.0001f);
        detailGo.setScaleY(0.0001f);
        detailReturn.setScaleY(0.0001f);
        detailBuy.animate().scaleY(1f).scaleX(1f).setInterpolator(new AccelerateInterpolator()).start();
        detailReturn.animate().scaleY(1f).scaleX(1f).setInterpolator(new AccelerateInterpolator()).setStartDelay(300).start();
        detailGo.animate().scaleY(1f).scaleX(1f).setInterpolator(new AccelerateInterpolator()).setStartDelay(300).start();
    }

    @Override
    public void hideOrderFab() {
        detailBuy.setClickable(false);
        detailReturn.setClickable(false);
        detailGo.setClickable(false);
        detailBuy.setVisibility(View.INVISIBLE);
        detailReturn.setVisibility(View.INVISIBLE);
        detailGo.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showOrderTop() {
        searchView.setVisibility(View.GONE);
        int margin = Utils.dp2Px(16);
        int topMargin = manageActivity.getStatusBarHeight() + margin / 2;
        detailTop.setTranslationY(-detailTop.getHeight() - topMargin);
        detailTop.animate().translationY(topMargin).setStartDelay(300).setInterpolator(new AccelerateInterpolator()).start();
    }

    @Override
    public void hideOrderTop() {
        searchView.setVisibility(View.VISIBLE);
        searchView.setAlpha(0f);
        int margin = Utils.dp2Px(16);
        int topMargin = manageActivity.getStatusBarHeight() + margin / 2;
        searchView.animate().alpha(1f).setStartDelay(300).setInterpolator(new AccelerateInterpolator()).start();
        detailTop.animate().translationY(-detailTop.getHeight() - topMargin).setDuration(200).setInterpolator(new AccelerateInterpolator()).start();
    }

    @Override
    public void openSearch() {
        searchView.open(true);
    }

    @Override
    public void closeSearch() {
        searchView.close();
    }

    @Override
    public void setSearchText(String text) {
        searchView.setText(text);
    }

    @Override
    public void setMusic(Bitmap fenmian, String musicName, String artistName, String allTime) {

    }

    @Override
    public void setMusicCurrentTime(String time, int percent) {

    }

    @Override
    public void setMusicPlay(boolean play) {

    }

    @Override
    public void changeViewPagerIndex(int index) {
        bottomViewPager.setCurrentItem(index, true);
    }

    @Override
    public void setFirstViewPager(RecyclerView.Adapter v) {
        bottomPagerAdapter.rv1.setAdapter(v);
    }

    @Override
    public void setSecondViewPager(RecyclerView.Adapter v) {
        bottomPagerAdapter.rv2.setAdapter(v);
    }

    @Override
    public void showLoading(String tip) {
        rotateLoading.startAnimator(tip);
    }

    @Override
    public void showPagerLoading(String tip) {
        bottomRotateLoading.startAnimator(tip);
    }

    @Override
    public void hideLoading() {
        rotateLoading.stopAnimator();
    }

    @Override
    public void hidePagerLoading() {
        bottomRotateLoading.stopAnimator();
    }

    @Override
    public boolean onInterceptBack() {
        if (searchView.isSearchOpen()) {
            searchView.close();
            return true;
        }
        if (!searchView.isSearchOpen() && !searchView.getText().equals("")) {
            searchView.close();
            backClick();
            return true;
        }
        if (detailReturn.getVisibility() == View.VISIBLE) {
            mPresenter.gasStationPresent.orderReturnClick();
            return true;
        }

        return false;
    }
}
