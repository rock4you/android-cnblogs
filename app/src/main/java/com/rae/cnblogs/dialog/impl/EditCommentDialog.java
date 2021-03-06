package com.rae.cnblogs.dialog.impl;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;

import com.rae.cnblogs.AppRoute;
import com.rae.cnblogs.R;
import com.rae.cnblogs.sdk.UserProvider;
import com.rae.cnblogs.sdk.bean.BlogCommentBean;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 写评论
 * Created by ChenRui on 2017/1/31 23:17.
 */
public class EditCommentDialog extends SlideDialog {

    public interface OnEditCommentListener {
        /**
         * 当发布按钮点击的时候出发
         *
         * @param content     发表内容
         * @param parent      引用评论
         * @param isReference 是否引用评论内容
         */
        void onPostComment(String content, BlogCommentBean parent, boolean isReference);
    }

    @BindView(R.id.et_edit_comment_body)
    EditText mBodyView;

    @BindView(R.id.cb_ref_comment)
    CheckBox mReferenceView;

    private BlogCommentBean mBlogComment;

    private OnEditCommentListener mOnEditCommentListener;


    public EditCommentDialog(Context context) {
        super(context);
        setContentView(R.layout.dialog_blog_comment_edit);
        ButterKnife.bind(this);
    }

    @Override
    protected void onWindowLayout(Window window, WindowManager.LayoutParams attr) {
        super.onWindowLayout(window, attr);
        window.setDimAmount(0.5f);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public void show() {
        // 没有登录
        if (!UserProvider.getInstance().isLogin()) {
            AppRoute.jumpToLogin(getContext());
            return;
        }
        super.show();
        mReferenceView.setVisibility(View.GONE);
        mBodyView.setText("");
    }

    public void show(BlogCommentBean comment) {
        setBlogComment(comment);
        show();
        mReferenceView.setVisibility(View.VISIBLE);
        mReferenceView.setChecked(true);
        if (comment != null) {
            mReferenceView.setText("引用@" + comment.getAuthorName() + "的评论");
            mBodyView.setHint("回复：“" + subString(comment.getBody()) + "”");
        }
    }

    private String subString(String text) {
        if (TextUtils.isEmpty(text)) return text;
        if (text.length() < 20) return text;
        return text.substring(0, 20) + "...";
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mBodyView.setText("");
        mBlogComment = null;
    }

    public void setBlogComment(BlogCommentBean blogComment) {
        mBlogComment = blogComment;
    }

    public BlogCommentBean getBlogComment() {
        return mBlogComment;
    }

    public void setOnEditCommentListener(OnEditCommentListener onEditCommentListener) {
        mOnEditCommentListener = onEditCommentListener;
    }


    public boolean enableReferenceComment() {
        return mReferenceView.isChecked();
    }


    public String getCommentContent() {
        return mBodyView.getText().toString().trim();
    }

    @OnClick(R.id.btn_send_comment)
    void onSendClick() {
        if (TextUtils.isEmpty(getCommentContent())) {
            return;
        }

        if (mOnEditCommentListener != null) {
            mOnEditCommentListener.onPostComment(getCommentContent(), mBlogComment, mReferenceView.isChecked());
        }
    }
}
