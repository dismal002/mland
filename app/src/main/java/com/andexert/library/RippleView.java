package com.andexert.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import androidx.annotation.ColorRes;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import com.dismal.marshmallowland.R;

/* JADX INFO: loaded from: classes.dex */
public class RippleView extends RelativeLayout {
    private int HEIGHT;
    private int WIDTH;
    private boolean animationRunning;
    private Handler canvasHandler;
    private int durationEmpty;
    private int frameRate;
    private GestureDetector gestureDetector;
    private Boolean hasToZoom;
    private Boolean isCentered;
    private OnRippleCompleteListener onCompletionListener;
    private Bitmap originBitmap;
    private Paint paint;
    private float radiusMax;
    private int rippleAlpha;
    private int rippleColor;
    private int rippleDuration;
    private int ripplePadding;
    private Integer rippleType;
    private final Runnable runnable;
    private ScaleAnimation scaleAnimation;
    private int timer;
    private int timerEmpty;
    private float x;
    private float y;
    private int zoomDuration;
    private float zoomScale;

    public interface OnRippleCompleteListener {
        void onComplete(RippleView rippleView);
    }

    public RippleView(Context context) {
        super(context);
        this.frameRate = 10;
        this.rippleDuration = 400;
        this.rippleAlpha = 90;
        this.radiusMax = 0.0f;
        this.animationRunning = false;
        this.timer = 0;
        this.timerEmpty = 0;
        this.durationEmpty = -1;
        this.x = -1.0f;
        this.y = -1.0f;
        this.runnable = new Runnable() { // from class: com.andexert.library.RippleView.1
            @Override // java.lang.Runnable
            public void run() {
                RippleView.this.invalidate();
            }
        };
    }

    public RippleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.frameRate = 10;
        this.rippleDuration = 400;
        this.rippleAlpha = 90;
        this.radiusMax = 0.0f;
        this.animationRunning = false;
        this.timer = 0;
        this.timerEmpty = 0;
        this.durationEmpty = -1;
        this.x = -1.0f;
        this.y = -1.0f;
        this.runnable = new Runnable() { // from class: com.andexert.library.RippleView.1
            @Override // java.lang.Runnable
            public void run() {
                RippleView.this.invalidate();
            }
        };
        init(context, attrs);
    }

    public RippleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.frameRate = 10;
        this.rippleDuration = 400;
        this.rippleAlpha = 90;
        this.radiusMax = 0.0f;
        this.animationRunning = false;
        this.timer = 0;
        this.timerEmpty = 0;
        this.durationEmpty = -1;
        this.x = -1.0f;
        this.y = -1.0f;
        this.runnable = new Runnable() { // from class: com.andexert.library.RippleView.1
            @Override // java.lang.Runnable
            public void run() {
                RippleView.this.invalidate();
            }
        };
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (!isInEditMode()) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RippleView);
            this.rippleColor = typedArray.getColor(R.styleable.RippleView_rv_color, getResources().getColor(R.color.rippelColor));
            this.rippleType = Integer.valueOf(typedArray.getInt(R.styleable.RippleView_rv_type, 0));
            this.hasToZoom = Boolean.valueOf(typedArray.getBoolean(R.styleable.RippleView_rv_zoom, false));
            this.isCentered = Boolean.valueOf(typedArray.getBoolean(R.styleable.RippleView_rv_centered, false));
            this.rippleDuration = typedArray.getInteger(R.styleable.RippleView_rv_rippleDuration, this.rippleDuration);
            this.frameRate = typedArray.getInteger(R.styleable.RippleView_rv_framerate, this.frameRate);
            this.rippleAlpha = typedArray.getInteger(R.styleable.RippleView_rv_alpha, this.rippleAlpha);
            this.ripplePadding = typedArray.getDimensionPixelSize(R.styleable.RippleView_rv_ripplePadding, 0);
            this.canvasHandler = new Handler();
            this.zoomScale = typedArray.getFloat(R.styleable.RippleView_rv_zoomScale, 1.03f);
            this.zoomDuration = typedArray.getInt(R.styleable.RippleView_rv_zoomDuration, 200);
            typedArray.recycle();
            this.paint = new Paint();
            this.paint.setAntiAlias(true);
            this.paint.setStyle(Paint.Style.FILL);
            this.paint.setColor(this.rippleColor);
            this.paint.setAlpha(this.rippleAlpha);
            setWillNotDraw(false);
            this.gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() { // from class: com.andexert.library.RippleView.2
                @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
                public void onLongPress(MotionEvent event) {
                    super.onLongPress(event);
                    RippleView.this.animateRipple(event);
                    RippleView.this.sendClickEvent(true);
                }

                @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    return true;
                }

                @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });
            setDrawingCacheEnabled(true);
            setClickable(true);
        }
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (this.animationRunning) {
            canvas.save();
            if (this.rippleDuration <= this.timer * this.frameRate) {
                this.animationRunning = false;
                this.timer = 0;
                this.durationEmpty = -1;
                this.timerEmpty = 0;
                if (Build.VERSION.SDK_INT != 23) {
                    canvas.restore();
                }
                invalidate();
                if (this.onCompletionListener != null) {
                    this.onCompletionListener.onComplete(this);
                    return;
                }
                return;
            }
            this.canvasHandler.postDelayed(this.runnable, this.frameRate);
            if (this.timer == 0) {
                canvas.save();
            }
            canvas.drawCircle(this.x, this.y, this.radiusMax * ((this.timer * this.frameRate) / this.rippleDuration), this.paint);
            this.paint.setColor(Color.parseColor("#ffff4444"));
            if (this.rippleType.intValue() == 1 && this.originBitmap != null && (this.timer * this.frameRate) / this.rippleDuration > 0.4f) {
                if (this.durationEmpty == -1) {
                    this.durationEmpty = this.rippleDuration - (this.timer * this.frameRate);
                }
                this.timerEmpty++;
                Bitmap tmpBitmap = getCircleBitmap((int) (this.radiusMax * ((this.timerEmpty * this.frameRate) / this.durationEmpty)));
                canvas.drawBitmap(tmpBitmap, 0.0f, 0.0f, this.paint);
                tmpBitmap.recycle();
            }
            this.paint.setColor(this.rippleColor);
            if (this.rippleType.intValue() != 1) {
                this.paint.setAlpha((int) (this.rippleAlpha - (this.rippleAlpha * ((this.timer * this.frameRate) / this.rippleDuration))));
            } else if ((this.timer * this.frameRate) / this.rippleDuration > 0.6f) {
                this.paint.setAlpha((int) (this.rippleAlpha - (this.rippleAlpha * ((this.timerEmpty * this.frameRate) / this.durationEmpty))));
            } else {
                this.paint.setAlpha(this.rippleAlpha);
            }
            this.timer++;
        }
    }

    @Override // android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.WIDTH = w;
        this.HEIGHT = h;
        this.scaleAnimation = new ScaleAnimation(1.0f, this.zoomScale, 1.0f, this.zoomScale, w / 2, h / 2);
        this.scaleAnimation.setDuration(this.zoomDuration);
        this.scaleAnimation.setRepeatMode(2);
        this.scaleAnimation.setRepeatCount(1);
    }

    public void animateRipple(MotionEvent event) {
        createAnimation(event.getX(), event.getY());
    }

    public void animateRipple(float x, float y) {
        createAnimation(x, y);
    }

    private void createAnimation(float x, float y) {
        if (isEnabled() && !this.animationRunning) {
            if (this.hasToZoom.booleanValue()) {
                startAnimation(this.scaleAnimation);
            }
            this.radiusMax = Math.max(this.WIDTH, this.HEIGHT);
            if (this.rippleType.intValue() != 2) {
                this.radiusMax /= 2.0f;
            }
            this.radiusMax -= this.ripplePadding;
            if (this.isCentered.booleanValue() || this.rippleType.intValue() == 1) {
                this.x = getMeasuredWidth() / 2;
                this.y = getMeasuredHeight() / 2;
            } else {
                this.x = x;
                this.y = y;
            }
            this.animationRunning = true;
            if (this.rippleType.intValue() == 1 && this.originBitmap == null) {
                this.originBitmap = getDrawingCache(true);
            }
            invalidate();
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (this.gestureDetector.onTouchEvent(event)) {
            animateRipple(event);
            sendClickEvent(false);
        }
        return super.onTouchEvent(event);
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent event) {
        onTouchEvent(event);
        return super.onInterceptTouchEvent(event);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendClickEvent(Boolean isLongClick) {
        if (getParent() instanceof AdapterView) {
            AdapterView<?> adapterView = (AdapterView) getParent();
            int position = adapterView.getPositionForView(this);
            long id = adapterView.getItemIdAtPosition(position);
            if (isLongClick.booleanValue()) {
                if (adapterView.getOnItemLongClickListener() != null) {
                    adapterView.getOnItemLongClickListener().onItemLongClick(adapterView, this, position, id);
                }
            } else if (adapterView.getOnItemClickListener() != null) {
                adapterView.getOnItemClickListener().onItemClick(adapterView, this, position, id);
            }
        }
    }

    private Bitmap getCircleBitmap(int radius) {
        Bitmap output = Bitmap.createBitmap(this.originBitmap.getWidth(), this.originBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        Rect rect = new Rect((int) (this.x - radius), (int) (this.y - radius), (int) (this.x + radius), (int) (this.y + radius));
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(this.x, this.y, radius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(this.originBitmap, rect, rect, paint);
        return output;
    }

    @ColorRes
    public void setRippleColor(int rippleColor) {
        this.rippleColor = getResources().getColor(rippleColor);
    }

    public int getRippleColor() {
        return this.rippleColor;
    }

    public RippleType getRippleType() {
        return RippleType.values()[this.rippleType.intValue()];
    }

    public void setRippleType(RippleType rippleType) {
        this.rippleType = Integer.valueOf(rippleType.ordinal());
    }

    public Boolean isCentered() {
        return this.isCentered;
    }

    public void setCentered(Boolean isCentered) {
        this.isCentered = isCentered;
    }

    public int getRipplePadding() {
        return this.ripplePadding;
    }

    public void setRipplePadding(int ripplePadding) {
        this.ripplePadding = ripplePadding;
    }

    public Boolean isZooming() {
        return this.hasToZoom;
    }

    public void setZooming(Boolean hasToZoom) {
        this.hasToZoom = hasToZoom;
    }

    public float getZoomScale() {
        return this.zoomScale;
    }

    public void setZoomScale(float zoomScale) {
        this.zoomScale = zoomScale;
    }

    public int getZoomDuration() {
        return this.zoomDuration;
    }

    public void setZoomDuration(int zoomDuration) {
        this.zoomDuration = zoomDuration;
    }

    public int getRippleDuration() {
        return this.rippleDuration;
    }

    public void setRippleDuration(int rippleDuration) {
        this.rippleDuration = rippleDuration;
    }

    public int getFrameRate() {
        return this.frameRate;
    }

    public void setFrameRate(int frameRate) {
        this.frameRate = frameRate;
    }

    public int getRippleAlpha() {
        return this.rippleAlpha;
    }

    public void setRippleAlpha(int rippleAlpha) {
        this.rippleAlpha = rippleAlpha;
    }

    public void setOnRippleCompleteListener(OnRippleCompleteListener listener) {
        this.onCompletionListener = listener;
    }

    public enum RippleType {
        SIMPLE(0),
        DOUBLE(1),
        RECTANGLE(2);

        int type;

        RippleType(int type) {
            this.type = type;
        }
    }
}
