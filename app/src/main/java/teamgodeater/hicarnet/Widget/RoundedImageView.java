package teamgodeater.hicarnet.Widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.ImageView;

import teamgodeater.hicarnet.R;

/**
 * Created by G on 2016/5/19 0019.
 */
public class RoundedImageView extends ImageView {


    int maxRadius;
    Path path;
    int inSideColor;
    int inSidewidth;
    private Paint paint;
    boolean isRounded = true;

    public void setRounded(boolean rounded) {
        isRounded = rounded;
    }

    public void setInSideColor(int inSideColor) {
        this.inSideColor = inSideColor;
        paint.setColor(inSideColor);
    }

    public void setInSidewidth(int inSidewidth) {
        this.inSidewidth = inSidewidth;
        paint.setStrokeWidth(inSidewidth);
    }

    public RoundedImageView(Context context) {
        this(context, null);
    }

    public RoundedImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundedImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isInEditMode()) {
            return;
        }

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RoundedImageView);
        inSideColor = a.getColor(R.styleable.RoundedImageView_border_incolor, Color.WHITE);
        inSidewidth = a.getDimensionPixelSize(R.styleable.RoundedImageView_border_width, -1);
        a.recycle();
        paint = new Paint();
        path = new Path();
        setPaint();
    }

    private void setPaint() {
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColor(inSideColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(inSidewidth);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (isInEditMode()) {
            return;
        }

        maxRadius = Math.min(w, h) / 2;
        path.reset();
        path.addCircle(w / 2, h / 2, maxRadius, Path.Direction.CW);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (isInEditMode()) {
            super.onDraw(canvas);
            return;
        }

        if (isRounded) {
            canvas.clipPath(path);
        }
        super.onDraw(canvas);

        if (inSidewidth > 0) {
            canvas.drawPath(path, paint);
        }

    }

}
