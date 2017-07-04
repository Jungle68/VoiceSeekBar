package jungle68.com.library.core;

import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;

import static jungle68.com.library.utils.Utils.dp2px;
import static jungle68.com.library.utils.Utils.sp2px;


/**
 * @Describe
 * @Author Jungle68
 * @Date 2016/7/3
 * @Contact master.jungle68@gmail.com
 */

public class VoiceSeekBarBuilder {

    float min;
    float max;
    float progress;
    boolean floatType;
    int trackSize;
    int secondTrackSize;
    int thumbRadius;
    int thumbRadiusOnDragging;
    int trackColor;
    int secondTrackColor;
    int thumbColor;
    int sectionCount;
    boolean showSectionMark;
    boolean autoAdjustSectionMark;
    boolean showSectionText;
    int sectionTextSize;
    int sectionTextColor;
    @TextPosition
    int sectionTextPosition;
    int sectionTextInterval;
    boolean showThumbText;
    int thumbTextSize;
    int thumbTextColor;
    boolean showProgressInFloat;
    boolean touchToSeek;
    boolean seekBySection;
    int bubbleColor;
    int bubbleTextSize;
    int bubbleTextColor;
    boolean alwaysShowBubble;

    private VoiceSeekBar mBubbleSeekBar;

    VoiceSeekBarBuilder(VoiceSeekBar bubbleSeekBar) {
        mBubbleSeekBar = bubbleSeekBar;
    }

    public void build() {
        mBubbleSeekBar.config(this);
    }

    public VoiceSeekBarBuilder min(float min) {
        this.min = min;
        this.progress = min;
        return this;
    }

    public VoiceSeekBarBuilder max(float max) {
        this.max = max;
        return this;
    }

    public VoiceSeekBarBuilder progress(float progress) {
        this.progress = progress;
        return this;
    }

    public VoiceSeekBarBuilder floatType() {
        this.floatType = true;
        return this;
    }

    public VoiceSeekBarBuilder trackSize(int dp) {
        this.trackSize = dp2px(dp);
        return this;
    }

    public VoiceSeekBarBuilder secondTrackSize(int dp) {
        this.secondTrackSize = dp2px(dp);
        return this;
    }

    public VoiceSeekBarBuilder thumbRadius(int dp) {
        this.thumbRadius = dp2px(dp);
        return this;
    }

    public VoiceSeekBarBuilder thumbRadiusOnDragging(int dp) {
        this.thumbRadiusOnDragging = dp2px(dp);
        return this;
    }

    public VoiceSeekBarBuilder trackColor(@ColorInt int color) {
        this.trackColor = color;
        this.sectionTextColor = color;
        return this;
    }

    public VoiceSeekBarBuilder secondTrackColor(@ColorInt int color) {
        this.secondTrackColor = color;
        this.thumbColor = color;
        this.thumbTextColor = color;
        this.bubbleColor = color;
        return this;
    }

    public VoiceSeekBarBuilder thumbColor(@ColorInt int color) {
        this.thumbColor = color;
        return this;
    }

    public VoiceSeekBarBuilder sectionCount(@IntRange(from = 1) int count) {
        this.sectionCount = count;
        return this;
    }

    public VoiceSeekBarBuilder showSectionMark() {
        this.showSectionMark = true;
        return this;
    }

    public VoiceSeekBarBuilder autoAdjustSectionMark() {
        this.autoAdjustSectionMark = true;
        return this;
    }

    public VoiceSeekBarBuilder showSectionText() {
        this.showSectionText = true;
        return this;
    }

    public VoiceSeekBarBuilder sectionTextSize(int sp) {
        this.sectionTextSize = sp2px(sp);
        return this;
    }

    public VoiceSeekBarBuilder sectionTextColor(@ColorInt int color) {
        this.sectionTextColor = color;
        return this;
    }

    public VoiceSeekBarBuilder sectionTextPosition(@TextPosition int position) {
        this.sectionTextPosition = position;
        return this;
    }

    public VoiceSeekBarBuilder sectionTextInterval(@IntRange(from = 1) int interval) {
        this.sectionTextInterval = interval;
        return this;
    }

    public VoiceSeekBarBuilder showThumbText() {
        this.showThumbText = true;
        return this;
    }

    public VoiceSeekBarBuilder thumbTextSize(int sp) {
        this.thumbTextSize = sp2px(sp);
        return this;
    }

    public VoiceSeekBarBuilder thumbTextColor(@ColorInt int color) {
        thumbTextColor = color;
        return this;
    }

    public VoiceSeekBarBuilder showProgressInFloat() {
        this.showProgressInFloat = true;
        return this;
    }

    public VoiceSeekBarBuilder touchToSeek() {
        this.touchToSeek = true;
        return this;
    }

    public VoiceSeekBarBuilder seekBySection() {
        this.seekBySection = true;
        return this;
    }

    public VoiceSeekBarBuilder bubbleColor(@ColorInt int color) {
        this.bubbleColor = color;
        return this;
    }

    public VoiceSeekBarBuilder bubbleTextSize(int sp) {
        this.bubbleTextSize = sp2px(sp);
        return this;
    }

    public VoiceSeekBarBuilder bubbleTextColor(@ColorInt int color) {
        this.bubbleTextColor = color;
        return this;
    }

    public VoiceSeekBarBuilder alwaysShowBubble() {
        this.alwaysShowBubble = true;
        return this;
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }

    public float getProgress() {
        return progress;
    }

    public boolean isFloatType() {
        return floatType;
    }

    public int getTrackSize() {
        return trackSize;
    }

    public int getSecondTrackSize() {
        return secondTrackSize;
    }

    public int getThumbRadius() {
        return thumbRadius;
    }

    public int getThumbRadiusOnDragging() {
        return thumbRadiusOnDragging;
    }

    public int getTrackColor() {
        return trackColor;
    }

    public int getSecondTrackColor() {
        return secondTrackColor;
    }

    public int getThumbColor() {
        return thumbColor;
    }

    public int getSectionCount() {
        return sectionCount;
    }

    public boolean isShowSectionMark() {
        return showSectionMark;
    }

    public boolean isAutoAdjustSectionMark() {
        return autoAdjustSectionMark;
    }

    public boolean isShowSectionText() {
        return showSectionText;
    }

    public int getSectionTextSize() {
        return sectionTextSize;
    }

    public int getSectionTextColor() {
        return sectionTextColor;
    }

    public int getSectionTextPosition() {
        return sectionTextPosition;
    }

    public int getSectionTextInterval() {
        return sectionTextInterval;
    }

    public boolean isShowThumbText() {
        return showThumbText;
    }

    public int getThumbTextSize() {
        return thumbTextSize;
    }

    public int getThumbTextColor() {
        return thumbTextColor;
    }

    public boolean isShowProgressInFloat() {
        return showProgressInFloat;
    }

    public boolean isTouchToSeek() {
        return touchToSeek;
    }

    public boolean isSeekBySection() {
        return seekBySection;
    }

    public int getBubbleColor() {
        return bubbleColor;
    }

    public int getBubbleTextSize() {
        return bubbleTextSize;
    }

    public int getBubbleTextColor() {
        return bubbleTextColor;
    }

    public boolean isAlwaysShowBubble() {
        return alwaysShowBubble;
    }
}
