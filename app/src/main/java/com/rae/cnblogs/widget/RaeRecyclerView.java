package com.rae.cnblogs.widget;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import skin.support.widget.SkinCompatBackgroundHelper;
import skin.support.widget.SkinCompatSupportable;

/**
 * RecycleView
 * Created by ChenRui on 2016/12/3 17:26.
 */
public class RaeRecyclerView extends XRecyclerView implements SkinCompatSupportable {

    private SkinCompatBackgroundHelper mBackgroundTintHelper;

    private RaeLoadMoreView mFootView;


    public RaeRecyclerView(Context context) {
        super(context);
        init(null, 0);
    }

    public RaeRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public RaeRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    public void setBackgroundResource(@DrawableRes int resId) {
        super.setBackgroundResource(resId);
        if (this.mBackgroundTintHelper != null) {
            this.mBackgroundTintHelper.onSetBackgroundResource(resId);
        }

    }

    private void init(AttributeSet attrs, int defStyle) {

        this.mBackgroundTintHelper = new SkinCompatBackgroundHelper(this);
        this.mBackgroundTintHelper.loadFromAttributes(attrs, defStyle);

        setLayoutManager(new LinearLayoutManager(getContext()));
        setPullRefreshEnabled(false);
        mFootView = new RaeLoadMoreView(getContext());
        mFootView.setVisibility(GONE);
        setLoadingMoreProgressStyle(ProgressStyle.BallScaleMultiple);
        setFootView(mFootView);
    }

    /**
     * 设置没有更多数据的文本文字
     *
     * @param resId 资源
     */
    public void setNoMoreText(int resId) {
        mFootView.setNoMoreText(getResources().getString(resId));
    }


    @Override
    public void setLoadingMoreProgressStyle(int style) {
        mFootView.setProgressStyle(style);
    }

    /**
     * 是否在顶部
     */
    public boolean isOnTop() {
        if (getChildCount() == 0) {
            return true;
        }
        int top = getChildAt(0).getTop();
        if (top != 0) {
            return false;
        }

        RecyclerView.LayoutManager layoutManager = getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            int position = ((LinearLayoutManager) layoutManager).findFirstCompletelyVisibleItemPosition();
            if (position <= 1) {
                return true;
            } else if (position == -1) {
                position = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                return position == 0;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            boolean allViewAreOverScreen = true;
            int[] positions = ((StaggeredGridLayoutManager) layoutManager).findFirstVisibleItemPositions(null);
            for (int position : positions) {
                if (position == 0) {
                    return true;
                }
                if (position != -1) {
                    allViewAreOverScreen = false;
                }
            }
            if (allViewAreOverScreen) {
                positions = ((StaggeredGridLayoutManager) layoutManager).findFirstVisibleItemPositions(null);
                for (int position : positions) {
                    if (position == 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void applySkin() {
        if (this.mBackgroundTintHelper != null) {
            this.mBackgroundTintHelper.applySkin();
        }
    }
}
