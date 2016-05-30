package com.lapism.searchview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


public class SearchView extends FrameLayout implements View.OnClickListener { // Filter.FilterListener

    public static final int VERSION_TOOLBAR = 1000;
    public static final int VERSION_MENU_ITEM = 1001;
    public static final int VERSION_MARGINS_TOOLBAR_SMALL = 2000;
    public static final int VERSION_MARGINS_TOOLBAR_BIG = 2001;
    public static final int VERSION_MARGINS_MENU_ITEM = 2002;
    public static final int THEME_LIGHT = 3000;
    public static final int THEME_DARK = 3001;
    public static final int ANIMATION_DURATION = 300;
    private static int mIconColor = Color.BLACK;
    private static int mTextColor = Color.BLACK;
    private static int mTextHighlightColor = Color.BLACK;
    private final Context mContext;
    private OnQueryTextListener mOnQueryChangeListener = null;
    private OnOpenCloseListener mOnOpenCloseListener = null;
    private OnMenuClickListener mOnMenuClickListener = null;
    private OnVoiceClickListener mOnVoiceClickListener = null;
    private RecyclerView mRecyclerView;
    private View mShadowView;
    private View mDividerView;
    private CardView mCardView;
    private EditText mEditText;
    private ImageView mBackImageView;
    private ImageView mVoiceImageView;
    private ImageView mEmptyImageView;

    private SearchAdapter mSearchAdapter;
    private CharSequence mOldQueryText = "0";
    private SearchArrowDrawable mSearchArrow;
    private CharSequence mUserQuery = "";

    private String mVoiceSearchText = "Speak now";
    private int mVersion = VERSION_TOOLBAR;
    private int mAnimationDuration = ANIMATION_DURATION;
    private float mIsSearchArrowHamburgerState = SearchArrowDrawable.STATE_HAMBURGER;
    private boolean mShadow = true;
    private boolean mVoice = true;
    private boolean mIsSearchOpen = false;
    private SavedState mSavedState;
    private AndroidBug5497Workaround androidBug5497Workaround;

    boolean isItemClickClose = false;
    private OnBackClickListener mOnBackClickListener;

    // ---------------------------------------------------------------------------------------------
    public SearchView(Context context) {
        this(context, null);
    }

    public SearchView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
        initStyle(attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SearchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        initView();
        initStyle(attrs, defStyleAttr);
    }

    // ---------------------------------------------------------------------------------------------
    public static int getIconColor() {
        return mIconColor;
    }

    public void setIconColor(@ColorInt int color) {
        mIconColor = color;
        ColorFilter colorFilter = new PorterDuffColorFilter(mIconColor, PorterDuff.Mode.SRC_IN);

        if (mVersion == VERSION_TOOLBAR) {
            mSearchArrow.setColor(mIconColor);
        }
        if (mVersion == VERSION_MENU_ITEM) {
            mBackImageView.setColorFilter(colorFilter);
        }

        mVoiceImageView.setColorFilter(colorFilter);
        mEmptyImageView.setColorFilter(colorFilter);
    }

    public static int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(@ColorInt int color) {
        mTextColor = color;
        mEditText.setTextColor(mTextColor);
    }

    public static int getTextHighlightColor() {
        return mTextHighlightColor;
    }

    public void setTextHighlightColor(@ColorInt int color) {
        mTextHighlightColor = color;
    }

    // ---------------------------------------------------------------------------------------------
    private void initView() {
        LayoutInflater.from(mContext).inflate((R.layout.search_view), this, true);

        androidBug5497Workaround = new AndroidBug5497Workaround();

        mCardView = (CardView) findViewById(R.id.cardView);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_result);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setVisibility(View.GONE); // TODO INVISIBLE

        mDividerView = findViewById(R.id.view_divider);
        mDividerView.setVisibility(View.GONE);

        mShadowView = findViewById(R.id.view_shadow);
        mShadowView.setOnClickListener(this);
        mShadowView.setVisibility(View.GONE);

        mBackImageView = (ImageView) findViewById(R.id.imageView_arrow_back);
        mBackImageView.setOnClickListener(this);

        mVoiceImageView = (ImageView) findViewById(R.id.imageView_mic);
        mVoiceImageView.setOnClickListener(this);

        mEmptyImageView = (ImageView) findViewById(R.id.imageView_clear);
        mEmptyImageView.setOnClickListener(this);
        mEmptyImageView.setVisibility(View.GONE);

        mEditText = (EditText) findViewById(R.id.editText_input);
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                onSubmitQuery();
                return true;
            }
        });
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                mUserQuery = s;

                if (isItemClickClose) {
                    return;
                }

                if (!TextUtils.isEmpty(s) && mIsSearchArrowHamburgerState == SearchArrowDrawable.STATE_ARROW) {
                    checkVoiceArrowStatus(false);
                } else {
                    checkVoiceArrowStatus(true);
                }

                if (TextUtils.equals(s, mOldQueryText)) {
                    return;
                }

                if (mOnQueryChangeListener != null && !TextUtils.isEmpty(s)) {
                    mOnQueryChangeListener.onQueryTextChange(s.toString());
                }

                mOldQueryText = s.toString();
                startTextFilter();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    addFocus();
                } else {
                    removeFocus();
                }
            }
        });

        setVersion(mVersion);
        setVersionMargins(VERSION_MARGINS_TOOLBAR_SMALL);
        setTheme(THEME_LIGHT, true);
    }

    public void startTextFilter() {
        if (mSearchAdapter != null) {
            (mSearchAdapter).getFilter().filter(mEditText.getText());
        }
    }

    private void initStyle(AttributeSet attrs, int defStyleAttr) {
        TypedArray attr = mContext.obtainStyledAttributes(attrs, R.styleable.SearchView, defStyleAttr, 0);
        if (attr != null) {
            if (attr.hasValue(R.styleable.SearchView_search_version)) {
                setVersion(attr.getInt(R.styleable.SearchView_search_version, VERSION_TOOLBAR));
            }
            if (attr.hasValue(R.styleable.SearchView_search_version_margins)) {
                setVersionMargins(attr.getInt(R.styleable.SearchView_search_version_margins, VERSION_MARGINS_TOOLBAR_SMALL));
            }
            if (attr.hasValue(R.styleable.SearchView_search_theme)) {
                setTheme(attr.getInt(R.styleable.SearchView_search_theme, THEME_LIGHT), false);
            }
            if (attr.hasValue(R.styleable.SearchView_search_icon_color)) {
                setIconColor(attr.getColor(R.styleable.SearchView_search_icon_color, 0));
            }
            if (attr.hasValue(R.styleable.SearchView_search_background_color)) {
                setBackgroundColor(attr.getColor(R.styleable.SearchView_search_background_color, 0));
            }
            if (attr.hasValue(R.styleable.SearchView_search_text)) {
                setText(attr.getString(R.styleable.SearchView_search_text));
            }
            if (attr.hasValue(R.styleable.SearchView_search_text_color)) {
                setTextColor(attr.getColor(R.styleable.SearchView_search_text_color, 0));
            }
            if (attr.hasValue(R.styleable.SearchView_search_text_size)) {
                setTextSize(attr.getDimension(R.styleable.SearchView_search_text_size, 0));
            }
            if (attr.hasValue(R.styleable.SearchView_search_hint)) {
                setHint(attr.getString(R.styleable.SearchView_search_hint));
            }
            if (attr.hasValue(R.styleable.SearchView_search_hint_color)) {
                setHintColor(attr.getColor(R.styleable.SearchView_search_hint_color, 0));
            }
            if (attr.hasValue(R.styleable.SearchView_search_divider)) {
                setDivider(attr.getBoolean(R.styleable.SearchView_search_divider, false));
            }
            if (attr.hasValue(R.styleable.SearchView_search_voice)) {
                setVoice(attr.getBoolean(R.styleable.SearchView_search_voice, false));
            }
            if (attr.hasValue(R.styleable.SearchView_search_voice_text)) {
                setVoiceText(attr.getString(R.styleable.SearchView_search_voice_text));
            }
            if (attr.hasValue(R.styleable.SearchView_search_animation_duration)) {
                setAnimationDuration(attr.getInt(R.styleable.SearchView_search_animation_duration, mAnimationDuration));
            }
            if (attr.hasValue(R.styleable.SearchView_search_shadow)) {
                setShadow(attr.getBoolean(R.styleable.SearchView_search_shadow, false));
            }
            if (attr.hasValue(R.styleable.SearchView_search_shadow_color)) {
                setShadowColor(attr.getColor(R.styleable.SearchView_search_shadow_color, 0));
            }
            if (attr.hasValue(R.styleable.SearchView_search_elevation)) {
                setElevation(attr.getDimensionPixelSize(R.styleable.SearchView_search_elevation, 0));
            }
            attr.recycle();
        }
    }

    // ---------------------------------------------------------------------------------------------
    public void setTheme(int theme, boolean tint) {
        if (theme == THEME_LIGHT) {
            setBackgroundColor(ContextCompat.getColor(mContext, R.color.search_light_background));
            if (tint) {
                setIconColor(ContextCompat.getColor(mContext, R.color.search_light_icon));
                setHintColor(ContextCompat.getColor(mContext, R.color.search_light_hint));
                setTextColor(ContextCompat.getColor(mContext, R.color.search_light_text));
                setTextHighlightColor(ContextCompat.getColor(mContext, R.color.search_light_text_highlight));
            }
        }

        if (theme == THEME_DARK) {
            setBackgroundColor(ContextCompat.getColor(mContext, R.color.search_dark_background));
            if (tint) {
                setIconColor(ContextCompat.getColor(mContext, R.color.search_dark_icon));
                setHintColor(ContextCompat.getColor(mContext, R.color.search_dark_hint));
                setTextColor(ContextCompat.getColor(mContext, R.color.search_dark_text));
                setTextHighlightColor(ContextCompat.getColor(mContext, R.color.search_dark_text_highlight));
            }
        }
    }

    public void setVersion(int version) {
        mVersion = version;

        if (mVersion == VERSION_TOOLBAR) {
            mEditText.clearFocus();

            mSearchArrow = new SearchArrowDrawable(mContext);
            mBackImageView.setImageDrawable(mSearchArrow);
            mVoiceImageView.setImageResource(R.drawable.search_ic_mic_black_24dp);
            mEmptyImageView.setImageResource(R.drawable.search_ic_clear_black_24dp);
        }

        if (mVersion == VERSION_MENU_ITEM) {
            setVisibility(View.GONE);

            mBackImageView.setImageResource(R.drawable.search_ic_arrow_back_black_24dp);
            mVoiceImageView.setImageResource(R.drawable.search_ic_mic_black_24dp);
            mEmptyImageView.setImageResource(R.drawable.search_ic_clear_black_24dp);
        }
    }

    public void setVersionMargins(int version) {
        if (version == VERSION_MARGINS_TOOLBAR_SMALL) {
            int top = mContext.getResources().getDimensionPixelSize(R.dimen.search_toolbar_margin_top);
            int leftRight = mContext.getResources().getDimensionPixelSize(R.dimen.search_toolbar_margin_small_left_right);
            int bottom = 0;
            setVersionMargins(top, leftRight, bottom);
            return;
        } else if (version == VERSION_MARGINS_TOOLBAR_BIG) {
            int top = mContext.getResources().getDimensionPixelSize(R.dimen.search_toolbar_margin_top);
            int leftRight = mContext.getResources().getDimensionPixelSize(R.dimen.search_toolbar_margin_big_left_right);
            int bottom = 0;
            setVersionMargins(top, leftRight, bottom);
            return;

        } else if (version == VERSION_MARGINS_MENU_ITEM) {
            int margin = mContext.getResources().getDimensionPixelSize(R.dimen.search_menu_item_margin);
            setVersionMargins(margin, margin, margin);
            return;
        }
    }

    public void setVersionMargins(int top, int leftRight, int bottom) {
        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        );
        params.setMargins(leftRight, top, leftRight, bottom);
        mCardView.setLayoutParams(params);
        androidBug5497Workaround.setContentView(mCardView);
    }


    @Override
    public void setBackgroundColor(@ColorInt int color) {
        mCardView.setCardBackgroundColor(color);
    }

    public void setText(CharSequence text) {
        mEditText.setText(text);
    }

    public void setText(@StringRes int text) {
        mEditText.setText(text);
    }

    public void setTextSize(float size) {
        mEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    public void setHint(CharSequence hint) {
        mEditText.setHint(hint);
    }

    public void setHint(@StringRes int hint) {
        mEditText.setHint(hint);
    }

    public void setHintColor(@ColorInt int color) { // @ColorRes
        mEditText.setHintTextColor(color);
    }

    public void setDivider(boolean divider) {
        if (divider) {
            mRecyclerView.addItemDecoration(new SearchDivider(mContext));
        } else {
            mRecyclerView.removeItemDecoration(new SearchDivider(mContext));
        }
    }

    public void setVoice(boolean voice) {
        mVoice = voice;
        if (voice) {
            mVoiceImageView.setVisibility(View.VISIBLE);
        } else {
            mVoiceImageView.setVisibility(View.GONE);
        }
    }

    public void setVoiceText(String text) {
        mVoiceSearchText = text;
    }

    public void setAnimationDuration(int animationDuration) {
        mAnimationDuration = animationDuration;
    }

    public void setShadow(boolean shadow) {
        if (shadow) {
            mShadowView.setVisibility(View.VISIBLE);
        } else {
            mShadowView.setVisibility(View.GONE);
        }
        mShadow = shadow;
    }

    public void setShadowColor(@ColorInt int color) {
        mShadowView.setBackgroundColor(color);
    }

    @Override
    public void setElevation(float elevation) {
        mCardView.setMaxCardElevation(elevation);
        mCardView.setCardElevation(elevation);
        invalidate();
    }

    // ---------------------------------------------------------------------------------------------
    public void setQuery(CharSequence query) {
        if (query != null) {
            mEditText.setText(query);
            mEditText.setSelection(mEditText.length());
            mUserQuery = query;
        } else {
            mEditText.getText().clear();
        }
        if (!TextUtils.isEmpty(query)) {
            onSubmitQuery();
        }
    }

    public void addFocus() {
        mIsSearchOpen = true;
        if (!isItemClickClose) {
            setArrow(true);
        } else if (mOnBackClickListener != null) {
            mOnBackClickListener.onBackClick();
        }

        isItemClickClose = false;
        showSuggestions();
        if (mShadow) {
            fadeIn(mShadowView, mAnimationDuration);
        }
        showKeyboard();
        if (mVersion == VERSION_TOOLBAR) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mOnOpenCloseListener != null) {
                        mOnOpenCloseListener.onOpen();
                    }
                }
            }, mAnimationDuration);
        }
    }

    public void removeFocus() {
        mIsSearchOpen = false;
        if (mShadow) {
            mShadowView.setVisibility(View.GONE);
        }
        hideSuggestions();
        if (!isItemClickClose)
            setHamburger(true);
        hideKeyboard();
        if (mVersion == VERSION_TOOLBAR) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mOnOpenCloseListener != null) {
                        mOnOpenCloseListener.onClose();
                    }
                }
            }, mAnimationDuration);
        }
    }

    private void showSuggestions() {
        if (mSearchAdapter != null && mRecyclerView.getVisibility() == View.GONE) {
            mDividerView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
            fadeIn(mRecyclerView, mAnimationDuration);
            setText("");
            // mRecyclerView.setAlpha(0.0f);
            // mRecyclerView.animate().alpha(1.0f);
        }
    }

    private void hideSuggestions() {
        if (mRecyclerView.getVisibility() == View.VISIBLE) {
            mRecyclerView.setVisibility(View.GONE);
            mDividerView.setVisibility(View.GONE);
            fadeOut(mRecyclerView, mAnimationDuration);
        }
    }

    private void onSubmitQuery() {
        CharSequence query = mEditText.getText();
        if (query != null && TextUtils.getTrimmedLength(query) > 0) {
            if (mOnQueryChangeListener == null || !mOnQueryChangeListener.onQueryTextSubmit(query.toString())) {
                mEditText.setText(query);
            }
        }
    }

    private void checkVoiceArrowStatus(boolean status) {
        if (mVoice && status) {
            mVoiceImageView.setVisibility(View.VISIBLE);
            mEmptyImageView.setVisibility(View.GONE);
        } else {
            mVoiceImageView.setVisibility(View.GONE);
            mEmptyImageView.setVisibility(View.VISIBLE);
        }
    }


    public void open(boolean animate) {
        if (mVersion == VERSION_MENU_ITEM) {
            setVisibility(View.VISIBLE);

            if (animate) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    reveal();
                } else {
                    fadeOpen(mCardView, mAnimationDuration);
                }
            } else {
                mEditText.requestFocus();
                if (mEditText.length() > 0) {
                    mEditText.getText().clear();
                }
                if (mOnOpenCloseListener != null) {
                    mOnOpenCloseListener.onOpen();
                }
            }
        } else {
            mEditText.requestFocus();
            if (mEditText.length() > 0) {
                mEditText.getText().clear();
            }
        }
    }

    public void close() {
        isItemClickClose = false;
        mEditText.clearFocus();
        if (mEditText.length() > 0) {
            mEditText.getText().clear();
        }
        if (mIsSearchArrowHamburgerState != SearchArrowDrawable.STATE_HAMBURGER) {
            setHamburger(true);
        }
    }

    public void itemClickClose(String title) {
        isItemClickClose = true;
        mEditText.clearFocus();
        checkVoiceArrowStatus(true);
        mEditText.setText(title);
    }

    public boolean isSearchOpen() {
        return mIsSearchOpen;
    }

    public void setAdapter(SearchAdapter adapter) {
        mSearchAdapter = adapter;
        mRecyclerView.setAdapter(mSearchAdapter);
    }


    private void setArrow(boolean animate) {
        if (mSearchArrow != null && mVersion == VERSION_TOOLBAR) {
            if (animate) {
                mSearchArrow.setVerticalMirror(false);
                mSearchArrow.animate(SearchArrowDrawable.STATE_ARROW, mAnimationDuration);
            } else {
                mSearchArrow.setProgress(SearchArrowDrawable.STATE_ARROW);
            }
            mIsSearchArrowHamburgerState = SearchArrowDrawable.STATE_ARROW;
        }
    }

    private void setHamburger(boolean animate) {
        if (mSearchArrow != null && mVersion == VERSION_TOOLBAR) {
            if (animate) {
                mSearchArrow.setVerticalMirror(true);
                mSearchArrow.animate(SearchArrowDrawable.STATE_HAMBURGER, mAnimationDuration);
            } else {
                mSearchArrow.setProgress(SearchArrowDrawable.STATE_HAMBURGER);
            }
            mIsSearchArrowHamburgerState = SearchArrowDrawable.STATE_HAMBURGER;
        }
    }

    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) mEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditText, 0);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) mEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void reveal() {
        mCardView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mCardView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                revealOpen(mCardView, mAnimationDuration);
            }
        });
    }

    // implements ----------------------------------------------------------------------------------
    @Override
    public void onClick(View v) {
        if (v == mBackImageView || v == mShadowView) {
            if (mVersion == VERSION_TOOLBAR) {
                if (mIsSearchArrowHamburgerState == SearchArrowDrawable.STATE_HAMBURGER) {
                    if (mOnMenuClickListener != null) {
                        mOnMenuClickListener.onMenuClick();
                    }
                } else {

                    if (isItemClickClose) {
                        if (mOnBackClickListener != null) {
                            mOnBackClickListener.onBackClick();
                        }
                        setHamburger(true);
                    }
                    close();
                }
            }
        }
        if (v == mVoiceImageView) {
            if (mEditText.getText().length() > 0) {
                mEditText.getText().clear();
            }

            if (mOnVoiceClickListener != null) {
                mOnVoiceClickListener.onVoiceClick();
            }
        }
        if (v == mEmptyImageView) {
            if (mEditText.length() > 0) {
                mEditText.getText().clear();
            }
        }
    }

    // interfaces ----------------------------------------------------------------------------------
    public void setOnQueryTextListener(OnQueryTextListener listener) {
        mOnQueryChangeListener = listener;
    }

    public void setOnOpenCloseListener(OnOpenCloseListener listener) {
        mOnOpenCloseListener = listener;
    }

    public void setOnBackClickListener(OnBackClickListener listener) {
        mOnBackClickListener = listener;
    }

    public void setOnMenuClickListener(OnMenuClickListener listener) {
        mOnMenuClickListener = listener;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        mSavedState = new SavedState(superState);
        mSavedState.query = mUserQuery != null ? mUserQuery.toString() : null;
        mSavedState.isSearchOpen = mIsSearchOpen;
        return mSavedState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        mSavedState = (SavedState) state;
        if (mSavedState.isSearchOpen) {
            open(true);
            setQuery(mSavedState.query);
        }
        super.onRestoreInstanceState(mSavedState.getSuperState());
    }

    // ---------------------------------------------------------------------------------------------
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void revealOpen(View view, int duration) {

        int cx = view.getWidth() - mContext.getResources().getDimensionPixelSize(R.dimen.search_reveal);
        int cy = mContext.getResources().getDimensionPixelSize(R.dimen.search_height) / 2;

        if (cx != 0 && cy != 0) {
            float finalRadius = (float) Math.hypot(cx, cy);

            Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0.0f, finalRadius);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.setDuration(duration);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mEditText.requestFocus();
                    if (mEditText.length() > 0) {
                        mEditText.getText().clear();
                    }
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    if (mOnOpenCloseListener != null) {
                        mOnOpenCloseListener.onOpen();
                    }
                }
            });

            view.setVisibility(View.VISIBLE);
            anim.start();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void revealClose(final View view, int duration) {

        int cx = view.getWidth() - mContext.getResources().getDimensionPixelSize(R.dimen.search_reveal);
        int cy = mContext.getResources().getDimensionPixelSize(R.dimen.search_height) / 2;

        if (cx != 0 && cy != 0) {
            float initialRadius = (float) Math.hypot(cx, cy);

            Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, 0.0f);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.setDuration(duration);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    mEditText.clearFocus();
                    if (mEditText.length() > 0) {
                        mEditText.getText().clear();
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    view.setVisibility(View.GONE);
                    setVisibility(View.GONE);
                    if (mOnOpenCloseListener != null) {
                        mOnOpenCloseListener.onClose();
                    }
                }
            });
            anim.start();
        }
    }

    private void fadeIn(View view, int duration) {
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.setDuration(duration);

        view.setAnimation(anim);
        view.setVisibility(View.VISIBLE);
    }

    private void fadeOut(View view, int duration) {
        Animation anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.setDuration(duration);

        view.setAnimation(anim);
        view.setVisibility(View.GONE);
    }

    private void fadeOpen(View view, int duration) {
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.setDuration(duration);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // TODO same as REVEAL
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        view.setAnimation(anim);
        view.setVisibility(View.VISIBLE);
    }

    private void fadeClose(View view, int duration) {
        Animation anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.setDuration(duration);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        view.setAnimation(anim);
        view.setVisibility(View.GONE);
    }

    public interface OnBackClickListener {
        void onBackClick();
    }

    public interface OnQueryTextListener {
        boolean onQueryTextChange(String newText);

        boolean onQueryTextSubmit(String query);
    }

    public interface OnOpenCloseListener {
        void onClose();

        void onOpen();
    }

    public interface OnMenuClickListener {
        void onMenuClick();
    }

    public interface OnVoiceClickListener {
        void onVoiceClick();
    }

    public void setOnVoiceClickListener(OnVoiceClickListener listener) {
        mOnVoiceClickListener = listener;
    }

    private static class SavedState extends BaseSavedState {

        public static final Creator<SavedState> CREATOR =
                new Creator<SavedState>() {
                    @Override
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    @Override
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };

        String query;
        boolean isSearchOpen;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.query = in.readString();
            this.isSearchOpen = in.readInt() == 1;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeString(query);
            out.writeInt(isSearchOpen ? 1 : 0);
        }

    }

}

    /*
    private boolean mArrow = false;
    private boolean mHamburger = false;

    private void setArrow() {
        mArrow = true;
        setArrow(false);
    }

    private void setHamburger() {
        mHamburger = true;
        setHamburger(false);
    }*/
