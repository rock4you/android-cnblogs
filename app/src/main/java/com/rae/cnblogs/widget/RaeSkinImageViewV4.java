package com.rae.cnblogs.widget;


import android.content.Context;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.rae.cnblogs.ThemeCompat;

import skin.support.widget.SkinCompatBackgroundHelper;
import skin.support.widget.SkinCompatImageHelper;
import skin.support.widget.SkinCompatSupportable;

/**
 * 兼容4.4.4 问题
 */
public class RaeSkinImageViewV4 extends ImageView implements SkinCompatSupportable {

    private SkinCompatBackgroundHelper mBackgroundTintHelper;
    private SkinCompatImageHelper mImageHelper;

    public RaeSkinImageViewV4(Context context) {
        this(context, null);
        init();
    }

    public RaeSkinImageViewV4(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public RaeSkinImageViewV4(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mBackgroundTintHelper = new SkinCompatBackgroundHelper(this);
        this.mBackgroundTintHelper.loadFromAttributes(attrs, defStyleAttr);
        this.mImageHelper = new SkinCompatImageHelper(this);
        this.mImageHelper.loadFromAttributes(attrs, defStyleAttr);
        init();
    }

    private void init() {
        // 初始化的时候不用取反
        setAlpha(ThemeCompat.isNight() ? 0.3f : 1f);
    }

    @Override
    public void applySkin() {
        setAlpha(isNight() ? 0.3f : 1f);
        if (this.mBackgroundTintHelper != null) {
            this.mBackgroundTintHelper.applySkin();
        }

        if (this.mImageHelper != null) {
            this.mImageHelper.applySkin();
        }
    }

    public boolean isNight() {
        // 因为是先应用主题之后才会设置主题名称，所以这里取反。
        return !ThemeCompat.isNight();
    }


    public void setBackgroundResource(@DrawableRes int resId) {
        super.setBackgroundResource(resId);
        if (this.mBackgroundTintHelper != null) {
            this.mBackgroundTintHelper.onSetBackgroundResource(resId);
        }

    }

    public void setImageResource(@DrawableRes int resId) {
        if (this.mImageHelper != null) {
            this.mImageHelper.setImageResource(resId);
        }

    }

}
