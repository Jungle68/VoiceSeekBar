package jungle68.com.library.core;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jungle68.com.library.R;
import jungle68.com.library.utils.Utils;

import static jungle68.com.library.core.TextPosition.BELOW_SECTION_MARK;
import static jungle68.com.library.core.TextPosition.BOTTOM_SIDES;
import static jungle68.com.library.core.TextPosition.NONE;
import static jungle68.com.library.core.TextPosition.SIDES;
import static jungle68.com.library.utils.Utils.dp2px;
import static jungle68.com.library.utils.Utils.float2String;
import static jungle68.com.library.utils.Utils.formatFloat;
import static jungle68.com.library.utils.Utils.sp2px;


/**
 * @Describe for voice line ,just like ios imessage voice view ; Modified in BubbleSeekBar
 * @Author Jungle68
 * @Date 2016/7/3
 * @Contact master.jungle68@gmail.com
 */

public class VoiceSeekBar extends View {

    public static final float DEFAULT_MIN = 0f;
    public static final float DEFAULT_MAX = 100.0f;
    public static final int DEFAULT_SECTION_LINE_OFFESET = 5; // 当 section line height =0 时，给予的默认值
    public static final int DEFAULT_TRACK_SIZE_OFFSET = 2; // 当 section line height =0 时，给予的默认值
    public static final int DEFAULT_TRACK_AND_TEXT_OFFSET = 15; // 当 section line height =0 时，给予的默认值
    public static final int DEFAULT_SECTION_COUNT = 60; // 当 section line height =0 时，给予的默认值
    public static final int DEFAULT_ANIM_DURATION = 200; // 当 section line height =0 时，给予的默认值
    public static final int DEFAULT_THUNMB_COLOR = Color.MAGENTA; // 当 section line height =0 时，给予的默认值


    private float mMin = DEFAULT_MIN; // min
    private float mMax = DEFAULT_MAX; // max
    private float mProgress; // real time value
    private int mTrackSize; // height of right-track(on the right of thumb)
    private int mSecondTrackSize; // height of left-track(on the left of thumb)
    private int mThumbRadius; // radius of thumb
    private int mThumbRadiusOnDragging; // radius of thumb when be dragging
    private int mTrackColor; // color of right-track
    private int mSecondTrackColor; // color of left-track
    private int mThumbColor; // color of thumb

    private int mSectionCount = DEFAULT_SECTION_COUNT; // shares of whole progress(max - min)
    private boolean isShowSectionMark; // show demarcation points or not
    private boolean isAutoAdjustSectionMark; // auto scroll to the nearest section_mark or not
    private int mSectionTextSize; // text size of section-text
    private int mSectionTextColor; // text color of section-text
    private Drawable mThumbSrc;

    @TextPosition
    private int mSectionTextPosition = BOTTOM_SIDES; // text position of section-text relative to track
    private int mSectionTextInterval; // the interval of two section-text
    private boolean isShowThumbText; // show real time progress-text under thumb or not
    private boolean mIsShowTrackLine;
    private int mThumbTextSize; // text size of progress-text
    private int mThumbTextColor; // text color of progress-text
    private boolean isTouchToSeek; // touch anywhere on track to quickly seek
    private boolean isSeekBySection; // seek by section, the progress may not be linear
    private long mAnimDuration = DEFAULT_ANIM_DURATION; // duration of animation
    private boolean isAlwaysShowBubble; // bubble shows all time
    private boolean mBubbleTextIsFloat;// show bubble-progress in float or not
    private int mBubbleColor;// color of bubble
    private int mBubbleTextSize; // text size of bubble-progress
    private int mBubbleTextColor; // text color of bubble-progress

    private float mDelta; // max - min
    private float mSectionValue; // (mDelta / mSectionCount)
    private float mThumbCenterX; // X coordinate of thumb's center
    private float mTrackLength; // pixel length of whole track
    private float mSectionOffset; // pixel length of one section
    private boolean isThumbOnDragging; // is thumb on dragging or not
    private int mTextSpace = DEFAULT_TRACK_AND_TEXT_OFFSET; // space between text and track
    private boolean triggerBubbleShowing;
    private boolean triggerSeekBySection;

    private OnProgressChangedListener mProgressListener; // progress changing listener
    private float mLeft; // space between left of track and left of the view
    private float mRight; // space between right of track and left of the view
    private Paint mPaint;
    private Rect mRectText;

    private WindowManager mWindowManager;
    private BubbleView mBubbleView;
    private int mBubbleRadius;
    private float mBubbleCenterRawSolidX;
    private float mBubbleCenterRawSolidY;
    private float mBubbleCenterRawX;
    private WindowManager.LayoutParams mLayoutParams;
    private int[] mPoint = new int[2];
    private boolean isTouchToSeekAnimEnd = true;
    private float mPreSecValue; // previous SectionValue
    private VoiceSeekBarBuilder mConfigBuilder; // config attributes
    private List<Integer> voiceData = new ArrayList<>();
    private float mMaxLineHeight;
    private int mYSeekTrackCenterTop;

    public VoiceSeekBar(Context context) {
        this(context, null);
    }

    public VoiceSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VoiceSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.VoiceSeekBar, defStyleAttr, 0);
            mMin = a.getFloat(R.styleable.VoiceSeekBar_jungle68_min, DEFAULT_MIN);
            mMax = a.getFloat(R.styleable.VoiceSeekBar_jungle68_max, DEFAULT_MAX);
            if (mMin > mMax) {
                throw new IllegalArgumentException("this need min<max");
            }
            mProgress = a.getFloat(R.styleable.VoiceSeekBar_jungle68_progress, mMin);
            if (mProgress < mMin || mProgress > mMax) {
                throw new IndexOutOfBoundsException("progress out of bunds");
            }
            mTrackSize = a.getDimensionPixelSize(R.styleable.VoiceSeekBar_jungle68_track_size, dp2px(2));
            mIsShowTrackLine = a.getBoolean(R.styleable.VoiceSeekBar_jungle68_is_show_track_line, true);
            mSecondTrackSize = a.getDimensionPixelSize(R.styleable.VoiceSeekBar_jungle68_second_track_size,
                    mTrackSize + dp2px(2));
            mThumbRadius = a.getDimensionPixelSize(R.styleable.VoiceSeekBar_jungle68_thumb_radius,
                    mSecondTrackSize + dp2px(2));
            mThumbRadiusOnDragging = a.getDimensionPixelSize(R.styleable.VoiceSeekBar_jungle68_thumb_radius,
                    mSecondTrackSize * 5);
            mSectionCount = a.getInteger(R.styleable.VoiceSeekBar_jungle68_section_count, DEFAULT_SECTION_COUNT);
            mTrackColor = a.getColor(R.styleable.VoiceSeekBar_jungle68_track_color,
                    ContextCompat.getColor(context, R.color.track_color));
            mSecondTrackColor = a.getColor(R.styleable.VoiceSeekBar_jungle68_second_track_color,
                    ContextCompat.getColor(context, R.color.second_rack_color));
            mThumbColor = a.getColor(R.styleable.VoiceSeekBar_jungle68_thumb_color, DEFAULT_THUNMB_COLOR);
            mThumbSrc = a.getDrawable(R.styleable.VoiceSeekBar_jungle68_thumb_src);
            mSectionTextSize = a.getDimensionPixelSize(R.styleable.VoiceSeekBar_jungle68_section_text_size, sp2px(14));
            mSectionTextColor = a.getColor(R.styleable.VoiceSeekBar_jungle68_section_text_color, mTrackColor);
            isSeekBySection = a.getBoolean(R.styleable.VoiceSeekBar_jungle68_seek_by_section, true);
            isTouchToSeek = a.getBoolean(R.styleable.VoiceSeekBar_jungle68_touch_to_seek, false);
            int pos = a.getInteger(R.styleable.VoiceSeekBar_jungle68_section_text_position, SIDES);
            switch (pos) {
                case 0:
                    mSectionTextPosition = SIDES;
                    break;
                case 1:
                    mSectionTextPosition = BOTTOM_SIDES;
                    break;
                case 2:
                    mSectionTextPosition = BELOW_SECTION_MARK;
                    break;
                default:
            }

            mSectionTextInterval = a.getInteger(R.styleable.VoiceSeekBar_jungle68_section_text_interval, 1);
            isShowThumbText = a.getBoolean(R.styleable.VoiceSeekBar_jungle68_show_thumb_text, true);
            mThumbTextSize = a.getDimensionPixelSize(R.styleable.VoiceSeekBar_jungle68_thumb_text_size, sp2px(14));
            mThumbTextColor = a.getColor(R.styleable.VoiceSeekBar_jungle68_thumb_text_color, mSecondTrackColor);
            mBubbleColor = a.getColor(R.styleable.VoiceSeekBar_jungle68_bubble_color, mSecondTrackColor);
            mBubbleTextSize = a.getDimensionPixelSize(R.styleable.VoiceSeekBar_jungle68_bubble_text_size, sp2px(14));
            mBubbleTextColor = a.getColor(R.styleable.VoiceSeekBar_jungle68_bubble_text_color, Color.WHITE);
            isShowSectionMark = a.getBoolean(R.styleable.VoiceSeekBar_jungle68_show_section_mark, true);
            isAutoAdjustSectionMark = a.getBoolean(R.styleable.VoiceSeekBar_jungle68_auto_adjust_section_mark, false);
            mBubbleTextIsFloat = a.getBoolean(R.styleable.VoiceSeekBar_jungle68_show_progress_in_float, true);
            mAnimDuration = a.getInteger(R.styleable.VoiceSeekBar_jungle68_anim_duration, DEFAULT_ANIM_DURATION);
            isAlwaysShowBubble = a.getBoolean(R.styleable.VoiceSeekBar_jungle68_always_show_bubble, false);
            a.recycle();
        }

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setTextAlign(Paint.Align.CENTER);

        mRectText = new Rect();

        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        // init BubbleView
        mBubbleView = new BubbleView(context);
        mBubbleView.setProgressText(mBubbleTextIsFloat ?
                String.valueOf(getProgressFloat()) : String.valueOf(getProgress()));

        initConfigByPriority();
        calculateRadiusOfBubble();
    }

    private void initConfigByPriority() {
        voiceData.clear();
        for (int i = 0; i <= mSectionCount; i++) { // 随机生成 voiceData 数据，形成高低不齐的线的偏移量
            int romdonY = (int) (Math.random() * ((getPaddingTop() + mThumbRadiusOnDragging) / 2));// mTop 的一半
            if (romdonY == 0) {
                romdonY = DEFAULT_SECTION_LINE_OFFESET;
            }
            if (romdonY + mYSeekTrackCenterTop > mMaxLineHeight) {
                mMaxLineHeight = romdonY + mYSeekTrackCenterTop;
            }
            voiceData.add(romdonY);
        }
        mDelta = mMax - mMin;
        mSectionValue = mDelta / mSectionCount;

        if (!isShowSectionMark && isAutoAdjustSectionMark) {
            isAutoAdjustSectionMark = false;
        }
        if (isSeekBySection) {
            mPreSecValue = mMin;
            if (mProgress != mMin) {
                mPreSecValue = mSectionValue;
            }
            isShowSectionMark = true;
            isAutoAdjustSectionMark = true;
            isTouchToSeek = false;
        }
        if (isAlwaysShowBubble) {
            setProgress(mProgress);
        }
    }

    /**
     * Calculate radius of bubble according to the Min and the Max
     */
    private void calculateRadiusOfBubble() {
        mPaint.setTextSize(mBubbleTextSize);

        // 计算滑到两端气泡里文字需要显示的宽度，比较取最大值为气泡的半径
        String text;
        if (mBubbleTextIsFloat) {
            text = float2String(mMin);
        } else {
            text = getMinText();
        }
        mPaint.getTextBounds(text, 0, text.length(), mRectText);
        int w1 = (mRectText.width() + mTextSpace * 2) >> 1;

        if (mBubbleTextIsFloat) {
            text = float2String(mMax);
        } else {
            text = getMaxText();
        }
        mPaint.getTextBounds(text, 0, text.length(), mRectText);
        int w2 = (mRectText.width() + mTextSpace * 2) >> 1;

        mBubbleRadius = dp2px(14); // default 14dp
        int max = Math.max(mBubbleRadius, Math.max(w1, w2));
        mBubbleRadius = max + mTextSpace;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height = mThumbRadiusOnDragging * 2; // 默认高度为拖动时thumb圆的直径
        if (isShowThumbText) {
            mPaint.setTextSize(mThumbTextSize);
            mPaint.getTextBounds("j", 0, 1, mRectText); // “j”是字母和阿拉伯数字中最高的
            height += mRectText.height() + mTextSpace; // 如果显示实时进度，则原来基础上加上进度文字高度和间隔
        }

        setMeasuredDimension(resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec), height);

        mLeft = getPaddingLeft() + mThumbRadiusOnDragging;
        mRight = getMeasuredWidth() - getPaddingRight() - mThumbRadiusOnDragging;
        if (isShowThumbText && mSectionTextPosition == NONE) {
            mPaint.setTextSize(mThumbTextSize);

            String text = getMinText();
            mPaint.getTextBounds(text, 0, text.length(), mRectText);
            float max = Math.max(mThumbRadiusOnDragging, mRectText.width() / 2f);
            mLeft = getPaddingLeft() + max + mTextSpace;

            text = getMaxText();
            mPaint.getTextBounds(text, 0, text.length(), mRectText);
            max = Math.max(mThumbRadiusOnDragging, mRectText.width() / 2f);
            mRight = getMeasuredWidth() - getPaddingRight() - max - mTextSpace;
        }

        mTrackLength = mRight - mLeft;
        mSectionOffset = mTrackLength * 1f / mSectionCount;

        mBubbleView.measure(widthMeasureSpec, heightMeasureSpec);

        locatePositionOnScreen();
    }

    /**
     * In fact there two parts of the BubbleSeeBar, they are the BubbleView and the SeekBar.
     * <p>
     * The BubbleView is added to Window by the WindowManager, so the only connection between
     * BubbleView and SeekBar is their origin raw coordinates on the screen.
     * <p>
     * It's easy to compute the coordinates(mBubbleCenterRawSolidX, mBubbleCenterRawSolidY) of point
     * when the Progress equals the Min. Then compute the pixel length increment when the Progress is
     * changing, the result is mBubbleCenterRawX. At last the WindowManager calls updateViewLayout()
     * to update the LayoutParameter.x of the BubbleView.
     * <p>
     * 气泡BubbleView实际是通过WindowManager动态添加的一个视图，因此与SeekBar唯一的位置联系就是它们在屏幕上的
     * 绝对坐标。
     * 先计算进度mProgress为mMin时BubbleView的中心坐标（mBubbleCenterRawSolidX，mBubbleCenterRawSolidY），
     * 然后根据进度来增量计算横坐标mBubbleCenterRawX，再动态设置LayoutParameter.x，就实现了气泡跟随滑动移动。
     */
    private void locatePositionOnScreen() {
        getLocationOnScreen(mPoint);

        mBubbleCenterRawSolidX = mPoint[0] + mLeft - mBubbleView.getMeasuredWidth() / 2f;
        mBubbleCenterRawX = mBubbleCenterRawSolidX + mTrackLength * (mProgress - mMin) / mDelta;
        mBubbleCenterRawSolidY = mPoint[1] - mBubbleView.getMeasuredHeight();
        mBubbleCenterRawSolidY -= dp2px(24);
        if (Utils.isMIUI()) {
            mBubbleCenterRawSolidY += dp2px(4);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float xLeft = getPaddingLeft();
        float xRight = getMeasuredWidth() - getPaddingRight();
        mYSeekTrackCenterTop = getPaddingTop() + mThumbRadiusOnDragging;

        // draw sectionText SIDES or BOTTOM_SIDES
        if (isShowThumbText && mSectionTextPosition == NONE) {
            xLeft = mLeft;
            xRight = mRight;
        }

        if ((!isShowThumbText) || mSectionTextPosition == SIDES) {
            xLeft += mThumbRadiusOnDragging;
            xRight -= mThumbRadiusOnDragging;
        }

        boolean isShowTextBelowSectionMark = mSectionTextPosition ==
                BELOW_SECTION_MARK;
        boolean conditionInterval = mSectionCount % 2 == 0;

        // draw sectionMark & sectionText BELOW_SECTION_MARK
        if (isShowTextBelowSectionMark || isShowSectionMark) {
            float junction = mTrackLength / mDelta * Math.abs(mProgress - mMin) + mLeft; // 交汇点
            mPaint.setTextSize(mSectionTextSize);
            mPaint.getTextBounds("0123456789", 0, "0123456789".length(), mRectText); // compute solid height

            float x_;
            float y_ = mYSeekTrackCenterTop + mRectText.height() + mThumbRadiusOnDragging + mTextSpace;
            System.out.println("mSectionCount = " + mSectionCount);
            for (int i = 0; i <= mSectionCount; i++) {
                x_ = xLeft + i * mSectionOffset;
                mPaint.setColor(x_ <= junction ? mSecondTrackColor : mTrackColor);
                // sectionMark

                mPaint.setStrokeWidth(mTrackSize);
                canvas.drawLine(x_, mYSeekTrackCenterTop - voiceData.get(i), x_, mYSeekTrackCenterTop + voiceData.get(i), mPaint);

                // sectionText belows section
                if (isShowTextBelowSectionMark) {
                    mPaint.setColor(mSectionTextColor);

                    if (mSectionTextInterval > 1) {
                        if (conditionInterval && i % mSectionTextInterval == 0) {
                            float m = mMin + mSectionValue * i;
                            canvas.drawText((int) m + "", x_, y_, mPaint);
                        }
                    } else {
                        float m = mMin + mSectionValue * i;
                        canvas.drawText((int) m + "", x_, y_, mPaint);
                    }
                }
            }
        }

        if (!isThumbOnDragging || isAlwaysShowBubble) {
            mThumbCenterX = mTrackLength / mDelta * (mProgress - mMin) + xLeft;
        }

        // draw thumbText
        if (isShowThumbText && !isThumbOnDragging && isTouchToSeekAnimEnd) {
            mPaint.setColor(mThumbTextColor);
            mPaint.setTextSize(mThumbTextSize);
            mPaint.getTextBounds("0123456789", 0, "0123456789".length(), mRectText); // compute solid height
            float y_ = mYSeekTrackCenterTop + mRectText.height() + mThumbRadiusOnDragging + mTextSpace;

            if ((mBubbleTextIsFloat && mSectionTextPosition == BOTTOM_SIDES &&
                    mProgress != mMin && mProgress != mMax)) {
                canvas.drawText(String.valueOf(getProgressFloat()), mThumbCenterX, y_, mPaint);
            } else {
                canvas.drawText(String.valueOf(getProgress()), mThumbCenterX, y_, mPaint);
            }
        }

        // draw track
        if (mIsShowTrackLine) {
            mPaint.setColor(mSecondTrackColor);
            mPaint.setStrokeWidth(mSecondTrackSize);
            canvas.drawLine(xLeft, mYSeekTrackCenterTop, mThumbCenterX, mYSeekTrackCenterTop, mPaint); // 中心线
            // draw second track
            mPaint.setColor(mTrackColor);
            mPaint.setStrokeWidth(mTrackSize);
            canvas.drawLine(mThumbCenterX, mYSeekTrackCenterTop, xRight, mYSeekTrackCenterTop, mPaint); // 第二条中心线
        }
        // draw thumb
        mPaint.setColor(mThumbColor);
        mPaint.setStrokeWidth(2 * mTrackSize);
        if (mThumbSrc != null) {
            Bitmap bitmap = ((BitmapDrawable) mThumbSrc).getBitmap();
            canvas.drawBitmap(bitmap, mThumbCenterX - bitmap.getWidth() / 2, mThumbRadiusOnDragging - bitmap.getHeight() / 2, mPaint);
        } else {
            canvas.drawLine(mThumbCenterX, 0, mThumbCenterX, mThumbRadiusOnDragging * 2, mPaint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        post(new Runnable() {
            @Override
            public void run() {
                requestLayout();
            }
        });
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        if (!isAlwaysShowBubble)
            return;

        if (visibility != VISIBLE) {
            hideBubble();
        } else {
            if (triggerBubbleShowing) {
                showBubble();
            }
        }
        super.onVisibilityChanged(changedView, visibility);
    }

    @Override
    protected void onDetachedFromWindow() {
        hideBubble();
        mBubbleView = null;
        super.onDetachedFromWindow();
    }

    float dx;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                isThumbOnDragging = isThumbTouched(event);
                if (isThumbOnDragging) {
                    if (isSeekBySection && !triggerSeekBySection) {
                        triggerSeekBySection = true;
                    }
                    if (isAlwaysShowBubble && !triggerBubbleShowing) {
                        triggerBubbleShowing = true;
                    }
                    showBubble();
                    invalidate();
                } else if (isTouchToSeek && isTrackTouched(event)) {
                    if (isAlwaysShowBubble) {
                        hideBubble();
                        triggerBubbleShowing = true;
                    }

                    mThumbCenterX = event.getX();
                    if (mThumbCenterX < mLeft) {
                        mThumbCenterX = mLeft;
                    }
                    if (mThumbCenterX > mRight) {
                        mThumbCenterX = mRight;
                    }
                    mProgress = (mThumbCenterX - mLeft) * mDelta / mTrackLength + mMin;
                    mBubbleCenterRawX = mBubbleCenterRawSolidX + mTrackLength * (mProgress - mMin) / mDelta;

                    showBubble();
                    invalidate();
                }

                dx = mThumbCenterX - event.getX();

                break;
            case MotionEvent.ACTION_MOVE:
                if (isThumbOnDragging) {
                    mThumbCenterX = event.getX() + dx;
                    if (mThumbCenterX < mLeft) {
                        mThumbCenterX = mLeft;
                    }
                    if (mThumbCenterX > mRight) {
                        mThumbCenterX = mRight;
                    }
                    mProgress = (mThumbCenterX - mLeft) * mDelta / mTrackLength + mMin;

                    mBubbleCenterRawX = mBubbleCenterRawSolidX + mTrackLength * (mProgress - mMin) / mDelta;
                    mLayoutParams.x = (int) (mBubbleCenterRawX + 0.5f);
                    mWindowManager.updateViewLayout(mBubbleView, mLayoutParams);
                    mBubbleView.setProgressText(mBubbleTextIsFloat ?
                            String.valueOf(getProgressFloat()) : String.valueOf(getProgress()));

                    invalidate();

                    if (mProgressListener != null) {
                        mProgressListener.onProgressChanged(getProgress(), getProgressFloat());
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isAutoAdjustSectionMark) {
                    if (isTouchToSeek) {
                        mBubbleView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isTouchToSeekAnimEnd = false;
                                autoAdjustSection();
                            }
                        }, isThumbOnDragging ? 0 : 300);
                    } else {
                        autoAdjustSection();
                    }
                } else if (isThumbOnDragging || isTouchToSeek) {
                    mBubbleView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mBubbleView.animate()
                                    .alpha(isAlwaysShowBubble ? 1f : 0f)
                                    .setDuration(mAnimDuration)
                                    .setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            if (!isAlwaysShowBubble) {
                                                hideBubble();
                                            }

                                            isThumbOnDragging = false;
                                            invalidate();

                                            if (mProgressListener != null) {
                                                mProgressListener.onProgressChanged(getProgress(),
                                                        getProgressFloat());
                                            }
                                        }

                                        @Override
                                        public void onAnimationCancel(Animator animation) {
                                            if (!isAlwaysShowBubble) {
                                                hideBubble();
                                            }

                                            isThumbOnDragging = false;
                                            invalidate();
                                        }
                                    })
                                    .start();

                        }
                    }, !isThumbOnDragging && isTouchToSeek ? 300 : 0);
                }

                if (mProgressListener != null) {
                    mProgressListener.getProgressOnActionUp(getProgress(), getProgressFloat());
                }

                break;
        }

        return isThumbOnDragging || isTouchToSeek || super.onTouchEvent(event);
    }

    /**
     * Detect effective touch of thumb
     */
    private boolean isThumbTouched(MotionEvent event) {
        if (!isEnabled())
            return false;

        float x = mTrackLength / mDelta * (mProgress - mMin) + mLeft;
        float y = getMeasuredHeight() / 2f;
        return (event.getX() - x) * (event.getX() - x) + (event.getY() - y) * (event.getY() - y)
                <= (mLeft + dp2px(8)) * (mLeft + dp2px(8));
    }

    /**
     * Detect effective touch of track
     */
    private boolean isTrackTouched(MotionEvent event) {
        if (!isEnabled())
            return false;

        return event.getX() >= getPaddingLeft() && event.getX() <= getMeasuredWidth() - getPaddingRight()
                && event.getY() >= getPaddingTop() && event.getY() <= getPaddingTop() + mThumbRadiusOnDragging * 2;
    }

    /**
     * Showing the Bubble depends the way that the WindowManager adds a Toast type view to the Window.
     * <p>
     * 显示气泡
     * 原理是利用WindowManager动态添加一个与Toast相同类型的BubbleView，消失时再移除
     */
    private void showBubble() {
        if (mBubbleView == null || mBubbleView.getParent() != null) {
            return;
        }

        if (mLayoutParams == null) {
            mLayoutParams = new WindowManager.LayoutParams();
            mLayoutParams.gravity = Gravity.START | Gravity.TOP;
            mLayoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            mLayoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            mLayoutParams.format = PixelFormat.TRANSLUCENT;
            mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
            // MIUI禁止了开发者使用TYPE_TOAST，Android 7.1.1 对TYPE_TOAST的使用更严格
            if (Utils.isMIUI() || Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;
            } else {
                mLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
            }
        }
        mLayoutParams.x = (int) (mBubbleCenterRawX + 0.5f);
        mLayoutParams.y = (int) (mBubbleCenterRawSolidY + 0.5f);

        mBubbleView.setAlpha(0);
        mBubbleView.setVisibility(VISIBLE);
        mBubbleView.animate().alpha(1f).setDuration(mAnimDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        mWindowManager.addView(mBubbleView, mLayoutParams);
                    }
                }).start();
        mBubbleView.setProgressText(mBubbleTextIsFloat ?
                String.valueOf(getProgressFloat()) : String.valueOf(getProgress()));
    }

    /**
     * Auto scroll to the nearest section mark
     */
    private void autoAdjustSection() {
        int i;
        float x = 0;
        for (i = 0; i <= mSectionCount; i++) {
            x = i * mSectionOffset + mLeft;
            if (x <= mThumbCenterX && mThumbCenterX - x <= mSectionOffset) {
                break;
            }
        }

        BigDecimal bigDecimal = BigDecimal.valueOf(mThumbCenterX);
        float x_ = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
        boolean onSection = x_ == x; // 就在section处，不作valueAnim，优化性能

        AnimatorSet animatorSet = new AnimatorSet();

        ValueAnimator valueAnim = null;
        if (!onSection) {
            if (mThumbCenterX - x <= mSectionOffset / 2f) {
                valueAnim = ValueAnimator.ofFloat(mThumbCenterX, x);
            } else {
                valueAnim = ValueAnimator.ofFloat(mThumbCenterX, (i + 1) * mSectionOffset + mLeft);
            }
            valueAnim.setInterpolator(new LinearInterpolator());
            valueAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mThumbCenterX = (float) animation.getAnimatedValue();
                    mProgress = (mThumbCenterX - mLeft) * mDelta / mTrackLength + mMin;

                    mBubbleCenterRawX = mBubbleCenterRawSolidX + mThumbCenterX - mLeft;
                    mLayoutParams.x = (int) (mBubbleCenterRawX + 0.5f);
                    if (mBubbleView.getParent() != null) {
                        mWindowManager.updateViewLayout(mBubbleView, mLayoutParams);
                    }
                    mBubbleView.setProgressText(mBubbleTextIsFloat ?
                            String.valueOf(getProgressFloat()) : String.valueOf(getProgress()));

                    invalidate();

                    if (mProgressListener != null) {
                        mProgressListener.onProgressChanged(getProgress(), getProgressFloat());
                    }
                }
            });
        }

        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(mBubbleView, View.ALPHA, isAlwaysShowBubble ? 1 : 0);

        if (onSection) {
            animatorSet.setDuration(mAnimDuration).play(alphaAnim);
        } else {
            animatorSet.setDuration(mAnimDuration).playTogether(valueAnim, alphaAnim);
        }
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isAlwaysShowBubble) {
                    hideBubble();
                }

                mProgress = (mThumbCenterX - mLeft) * mDelta / mTrackLength + mMin;
                isThumbOnDragging = false;
                isTouchToSeekAnimEnd = true;
                invalidate();

                if (mProgressListener != null) {
                    mProgressListener.getProgressOnFinally(getProgress(), getProgressFloat());
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                if (!isAlwaysShowBubble) {
                    hideBubble();
                }

                mProgress = (mThumbCenterX - mLeft) * mDelta / mTrackLength + mMin;
                isThumbOnDragging = false;
                isTouchToSeekAnimEnd = true;
                invalidate();
            }
        });
        animatorSet.start();
    }

    /**
     * The WindowManager removes the BubbleView from the Window.
     */
    private void hideBubble() {
        mBubbleView.setVisibility(GONE); // 防闪烁
        if (mBubbleView.getParent() != null) {
            mWindowManager.removeViewImmediate(mBubbleView);
        }
    }

    /**
     * When BubbleSeekBar's parent view is scrollable, must listener to it's scrolling and call this
     * method to correct the offsets.
     */
    public void correctOffsetWhenContainerOnScrolling() {
        locatePositionOnScreen();

        if (mBubbleView.getParent() != null) {
            postInvalidate();
        }
    }

    private String getMinText() {
        return String.valueOf((int) mMin);
    }

    private String getMaxText() {
        return String.valueOf((int) mMax);
    }

    public void setProgress(float progress) {
        mProgress = progress;

        mBubbleCenterRawX = mBubbleCenterRawSolidX + mTrackLength * (mProgress - mMin) / mDelta;

        if (mProgressListener != null) {
            mProgressListener.onProgressChanged(getProgress(), getProgressFloat());
            mProgressListener.getProgressOnFinally(getProgress(), getProgressFloat());
        }
        if (isAlwaysShowBubble) {
            hideBubble();

            int[] location = new int[2];
            getLocationOnScreen(location);
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    showBubble();
                    triggerBubbleShowing = true;
                }
            }, location[0] == 0 && location[1] == 0 ? 200 : 0);
        }

        postInvalidate();
    }

    public int getProgress() {
        if (isSeekBySection && triggerSeekBySection) {
            float half = mSectionValue / 2;

            if (mProgress >= mPreSecValue) { // increasing
                if (mProgress >= mPreSecValue + half) {
                    mPreSecValue += mSectionValue;
                    return Math.round(mPreSecValue);
                } else {
                    return Math.round(mPreSecValue);
                }
            } else { // reducing
                if (mProgress >= mPreSecValue - half) {
                    return Math.round(mPreSecValue);
                } else {
                    mPreSecValue -= mSectionValue;
                    return Math.round(mPreSecValue);
                }
            }
        }

        return Math.round(mProgress);
    }

    public float getProgressFloat() {
        try {
            return formatFloat(mProgress);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public int getSectionCount() {
        return mSectionCount;
    }

    public void setSectionCount(int sectionCount) {
        mSectionCount = sectionCount;
        initConfigByPriority();
        calculateRadiusOfBubble();

    }

    public OnProgressChangedListener getOnProgressChangedListener() {
        return mProgressListener;
    }

    public void setOnProgressChangedListener(OnProgressChangedListener onProgressChangedListener) {
        mProgressListener = onProgressChangedListener;
    }

    void config(VoiceSeekBarBuilder builder) {
        mMin = builder.min;
        mMax = builder.max;
        mProgress = builder.progress;
        mTrackSize = builder.trackSize;
        mSecondTrackSize = builder.secondTrackSize;
        mThumbRadius = builder.thumbRadius;
        mThumbRadiusOnDragging = builder.thumbRadiusOnDragging;
        mTrackColor = builder.trackColor;
        mSecondTrackColor = builder.secondTrackColor;
        mThumbColor = builder.thumbColor;
        mSectionCount = builder.sectionCount;
        isShowSectionMark = builder.showSectionMark;
        isAutoAdjustSectionMark = builder.autoAdjustSectionMark;
        mSectionTextSize = builder.sectionTextSize;
        mSectionTextColor = builder.sectionTextColor;
        mSectionTextPosition = builder.sectionTextPosition;
        mSectionTextInterval = builder.sectionTextInterval;
        isShowThumbText = builder.showThumbText;
        mThumbTextSize = builder.thumbTextSize;
        mThumbTextColor = builder.thumbTextColor;
        mBubbleTextIsFloat = builder.showProgressInFloat;
        isTouchToSeek = builder.touchToSeek;
        isSeekBySection = builder.seekBySection;
        mBubbleColor = builder.bubbleColor;
        mBubbleTextSize = builder.bubbleTextSize;
        mBubbleTextColor = builder.bubbleTextColor;
        isAlwaysShowBubble = builder.alwaysShowBubble;

        initConfigByPriority();
        calculateRadiusOfBubble();

        if (mProgressListener != null) {
            mProgressListener.onProgressChanged(getProgress(), getProgressFloat());
            mProgressListener.getProgressOnFinally(getProgress(), getProgressFloat());
        }

        mConfigBuilder = null;

        requestLayout();
    }

    public VoiceSeekBarBuilder getConfigBuilder() {
        if (mConfigBuilder == null) {
            mConfigBuilder = new VoiceSeekBarBuilder(this);
        }

        mConfigBuilder.min = mMin;
        mConfigBuilder.max = mMax;
        mConfigBuilder.progress = mProgress;
        mConfigBuilder.trackSize = mTrackSize;
        mConfigBuilder.secondTrackSize = mSecondTrackSize;
        mConfigBuilder.thumbRadius = mThumbRadius;
        mConfigBuilder.thumbRadiusOnDragging = mThumbRadiusOnDragging;
        mConfigBuilder.trackColor = mTrackColor;
        mConfigBuilder.secondTrackColor = mSecondTrackColor;
        mConfigBuilder.thumbColor = mThumbColor;
        mConfigBuilder.sectionCount = mSectionCount;
        mConfigBuilder.showSectionMark = isShowSectionMark;
        mConfigBuilder.autoAdjustSectionMark = isAutoAdjustSectionMark;
        mConfigBuilder.sectionTextSize = mSectionTextSize;
        mConfigBuilder.sectionTextColor = mSectionTextColor;
        mConfigBuilder.sectionTextPosition = mSectionTextPosition;
        mConfigBuilder.sectionTextInterval = mSectionTextInterval;
        mConfigBuilder.showThumbText = isShowThumbText;
        mConfigBuilder.thumbTextSize = mThumbTextSize;
        mConfigBuilder.thumbTextColor = mThumbTextColor;
        mConfigBuilder.showProgressInFloat = mBubbleTextIsFloat;
        mConfigBuilder.touchToSeek = isTouchToSeek;
        mConfigBuilder.seekBySection = isSeekBySection;
        mConfigBuilder.bubbleColor = mBubbleColor;
        mConfigBuilder.bubbleTextSize = mBubbleTextSize;
        mConfigBuilder.bubbleTextColor = mBubbleTextColor;
        mConfigBuilder.alwaysShowBubble = isAlwaysShowBubble;

        return mConfigBuilder;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("save_instance", super.onSaveInstanceState());
        bundle.putFloat("progress", mProgress);

        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mProgress = bundle.getFloat("progress");
            super.onRestoreInstanceState(bundle.getParcelable("save_instance"));
            mBubbleView.setProgressText(mBubbleTextIsFloat ?
                    String.valueOf(getProgressFloat()) : String.valueOf(getProgress()));
            if (isAlwaysShowBubble) {
                setProgress(mProgress);
            }

            return;
        }

        super.onRestoreInstanceState(state);
    }


    /**
     * Listen to progress onChanged, onActionUp, onFinally
     */
    public interface OnProgressChangedListener {

        void onProgressChanged(int progress, float progressFloat);

        void getProgressOnActionUp(int progress, float progressFloat);

        void getProgressOnFinally(int progress, float progressFloat);
    }

    /**
     * Listener adapter
     * <br/>
     * usage like {@link AnimatorListenerAdapter}
     */
    public static abstract class OnProgressChangedListenerAdapter implements OnProgressChangedListener {

        @Override
        public void onProgressChanged(int progress, float progressFloat) {
        }

        @Override
        public void getProgressOnActionUp(int progress, float progressFloat) {
        }

        @Override
        public void getProgressOnFinally(int progress, float progressFloat) {
        }
    }

    /**
     * bubble
     */
    private class BubbleView extends View {

        private Paint mBubblePaint;
        private Path mBubblePath;
        private RectF mBubbleRectF;
        private Rect mRect;
        private String mProgressText = "";

        BubbleView(Context context) {
            this(context, null);
        }

        BubbleView(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        BubbleView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);

            mBubblePaint = new Paint();
            mBubblePaint.setAntiAlias(true);
            mBubblePaint.setTextAlign(Paint.Align.CENTER);

            mBubblePath = new Path();
            mBubbleRectF = new RectF();
            mRect = new Rect();
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            setMeasuredDimension(3 * mBubbleRadius, 3 * mBubbleRadius);

            mBubbleRectF.set(getMeasuredWidth() / 2f - mBubbleRadius, 0,
                    getMeasuredWidth() / 2f + mBubbleRadius, 2 * mBubbleRadius);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            mBubblePath.reset();
            float x0 = getMeasuredWidth() / 2f;
            float y0 = getMeasuredHeight() - mBubbleRadius / 3f;
            mBubblePath.moveTo(x0, y0);
            float x1 = (float) (getMeasuredWidth() / 2f - Math.sqrt(3) / 2f * mBubbleRadius);
            float y1 = 3 / 2f * mBubbleRadius;
            mBubblePath.quadTo(
                    x1 - dp2px(2), y1 - dp2px(2),
                    x1, y1
            );
            mBubblePath.arcTo(mBubbleRectF, 150, 240);

            float x2 = (float) (getMeasuredWidth() / 2f + Math.sqrt(3) / 2f * mBubbleRadius);
            mBubblePath.quadTo(
                    x2 + dp2px(2), y1 - dp2px(2),
                    x0, y0
            );
            mBubblePath.close();

            mBubblePaint.setColor(mBubbleColor);
            canvas.drawPath(mBubblePath, mBubblePaint);

            mBubblePaint.setTextSize(mBubbleTextSize);
            mBubblePaint.setColor(mBubbleTextColor);
            mBubblePaint.getTextBounds(mProgressText, 0, mProgressText.length(), mRect);
            Paint.FontMetrics fm = mBubblePaint.getFontMetrics();
            float baseline = mBubbleRadius + (fm.descent - fm.ascent) / 2f - fm.descent;
            canvas.drawText(mProgressText, getMeasuredWidth() / 2f, baseline, mBubblePaint);
        }

        void setProgressText(String progressText) {
            if (progressText != null && !mProgressText.equals(progressText)) {
                mProgressText = progressText;
                invalidate();
            }
        }
    }

}