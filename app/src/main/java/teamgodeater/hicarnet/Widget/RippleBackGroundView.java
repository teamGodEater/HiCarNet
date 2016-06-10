package teamgodeater.hicarnet.Widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;

import teamgodeater.hicarnet.Help.Utils;
import teamgodeater.hicarnet.R;

/**
 * Created by G on 2016/5/19 0019.
 */
public class RippleBackGroundView extends RoundedImageView {

    float downX;
    float DownY;
    int color = Color.argb(10, 0, 0, 0);
    float maxRadius = 0;
    Paint paint;
    Paint textPaint;
    boolean isTouch = false;
    private ObjectAnimator animator;
    float radius = 0;
    int textSize;
    int textColor;
    String text = "";
    private float textBaseLine;

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        changeTextSet();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        if (text == null) {
            this.text = "";
        } else {
            this.text = text;
        }
    }

    public int getTextSize() {
        return textSize;
    }

    //dp
    public void setTextSize(int textSize) {
        this.textSize = Utils.dp2Px(textSize);
        changeTextSet();
    }

    private void changeTextSet() {
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        changeTextLine();
    }

    private void changeTextLine() {
        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
        textBaseLine = (getHeight() - fontMetrics.bottom - fontMetrics.top) / 2f;
    }

    public RippleBackGroundView(Context context) {
        this(context, null);
    }

    public RippleBackGroundView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RippleBackGroundView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isInEditMode()) {
            return;
        }
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RippleBackGroundView);
        isRounded = a.getBoolean(R.styleable.RippleBackGroundView_is_rounded, false);
        textSize = a.getDimensionPixelOffset(R.styleable.RippleBackGroundView_text_size, Utils.dp2Px(16));
        textColor = a.getColor(R.styleable.RippleBackGroundView_text_color, Color.WHITE);
        text = a.getString(R.styleable.RippleBackGroundView_text);
        a.recycle();

        if (text == null) {
            text = "";
        }

        setClickable(true);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setLinearText(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        changeTextSet();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (isInEditMode()) {
            return;
        }

        maxRadius = (int) Math.sqrt(w * w + h * h);
        changeTextLine();
    }


    public void setRadius(float radius) {
        this.radius = radius;
        invalidate();
    }

    Rect rect;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isClickable()) {
            return false;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            isTouch = true;
            rect = new Rect(getLeft(), getTop(), getRight(), getBottom());
            downX = event.getX();
            DownY = event.getY();

            animator = ObjectAnimator.ofFloat(this, "radius", maxRadius / 3f, maxRadius);
            animator.setInterpolator(new AccelerateInterpolator());
            animator.start();

        }
       else  if (event.getAction() == MotionEvent.ACTION_MOVE && isTouch) {
            if (!rect.contains(getLeft() + (int) event.getX(), getTop() + (int) event.getY())) {
                animator.cancel();
                isTouch = false;
                setRadius(0);
            }
            downX = event.getX();
            DownY = event.getY();
        } else if (isTouch && event.getAction() == MotionEvent.ACTION_CANCEL) {
            animator.cancel();
            isTouch = false;
            setRadius(0);
        } else if (isTouch && event.getAction() == MotionEvent.ACTION_UP) {
            animator.cancel();
            setRadius(0);
            animator = ObjectAnimator.ofFloat(this, "radius", maxRadius / 10f, maxRadius / 3f);
            animator.setInterpolator(new AccelerateInterpolator());
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    setClickable(false);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    setClickable(true);
                    isTouch = false;
                    if (hasOnClickListeners()) {
                        callOnClick();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    setClickable(true);
                    isTouch = false;
                    if (hasOnClickListeners()) {
                        callOnClick();
                    }
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            animator.start();
        }
        return true;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isInEditMode()) {
            return;
        }

        if (!text.isEmpty()) {
            canvas.drawText(text, getWidth() / 2f, textBaseLine, textPaint);
        }
        if (isTouch) {
            canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, maxRadius, paint);
            canvas.drawCircle(downX, DownY, radius, paint);
        }

    }
}
