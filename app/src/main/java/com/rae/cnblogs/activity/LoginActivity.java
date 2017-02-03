package com.rae.cnblogs.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.rae.cnblogs.AppRoute;
import com.rae.cnblogs.AppUI;
import com.rae.cnblogs.R;
import com.rae.cnblogs.RaeAnim;
import com.rae.cnblogs.dialog.DialogProvider;
import com.rae.cnblogs.dialog.IAppDialog;
import com.rae.cnblogs.dialog.IAppDialogClickListener;
import com.rae.cnblogs.presenter.CnblogsPresenterFactory;
import com.rae.cnblogs.presenter.ILoginPresenter;
import com.rae.cnblogs.sdk.CnblogsApiFactory;
import com.rae.cnblogs.sdk.bean.UserInfoBean;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 登录
 * Created by ChenRui on 2017/1/19 0019 9:59.
 */
public class LoginActivity extends BaseActivity implements ILoginPresenter.ILoginView
//        , WebLoginListener
{

    @BindView(com.rae.cnblogs.R.id.ll_login_container)
    View mLoginLayout;

    @BindView(R.id.et_login_user_name)
    EditText mUserNameView;

    @BindView(R.id.et_login_password)
    EditText mPasswordView;

    @BindView(R.id.btn_login)
    Button mLoginButton;

    @BindView(R.id.img_login_logo)
    ImageView mLogoView;

//    @BindView(R.id.ll_login_tips_layout)
//    View mTipsLayout;

//    @BindView(R.id.tv_login_tips)
//    TextView mTipsView;

    private ILoginPresenter mLoginPresenter;
    private AccountTextWatcher mAccountTextWatcher;

    private IAppDialog mErrorTipsDialog;

    private int mErrorTime; // 登录错误次数，达到3次以上提示用户是否跳转网页登录

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        overridePendingTransition(com.rae.cnblogs.R.anim.slide_in_bottom, 0);
        super.onCreate(savedInstanceState);
        setContentView(com.rae.cnblogs.R.layout.activity_login);
        mLoginPresenter = CnblogsPresenterFactory.getLoginPresenter(this, this);

        mBackView.setVisibility(View.INVISIBLE);
        mBackView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBackView.setVisibility(View.VISIBLE);
                RaeAnim.fadeIn(mBackView);
            }
        }, 300);

        mAccountTextWatcher = new AccountTextWatcher();
        addAccountTextListener(mAccountTextWatcher);

        mErrorTipsDialog = DialogProvider.create(this);
        mErrorTipsDialog.setButtonText(IAppDialog.BUTTON_NEGATIVE, getString(R.string.i_try_agin));
        mErrorTipsDialog.setButtonText(IAppDialog.BUTTON_POSITIVE, getString(R.string.go_web_login));
        mErrorTipsDialog.setOnClickListener(IAppDialog.BUTTON_POSITIVE, new IAppDialogClickListener() {
            @Override
            public void onClick(IAppDialog dialog, int buttonType) {
                AppRoute.jumpToWebLogin(getContext());
                dialog.dismiss();
            }
        });
    }

    private void addAccountTextListener(AccountTextWatcher watcher) {
        mUserNameView.addTextChangedListener(watcher);
        mPasswordView.addTextChangedListener(watcher);
    }

    private void removeAccountTextListener(AccountTextWatcher watcher) {
        mUserNameView.removeTextChangedListener(watcher);
        mPasswordView.removeTextChangedListener(watcher);
    }


    @Override
    public void finish() {
        super.finish();
        RaeAnim.fadeIn(mBackView);
        overridePendingTransition(0, com.rae.cnblogs.R.anim.slide_out_bottom);
    }

    /**
     * 登录点击
     */
    @OnClick(R.id.btn_login)
    public void onLoginClick() {
        showLoading();
        mLoginPresenter.login();
//        mLoginPresenter.loadUserInfo();
        removeAccountTextListener(mAccountTextWatcher);
        mLoginButton.setEnabled(false);
    }

    @Override
    public String getUserName() {
        return mUserNameView.getText().toString().trim();
    }

    @Override
    public String getPassword() {
        return mPasswordView.getText().toString().trim();
    }

    @Override
    public void onLoginSuccess(UserInfoBean userInfo) {
        onLoginCallback();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onLoginError(String message) {
        onLoginCallback();
        if (mErrorTime >= 3) {
            mErrorTipsDialog.setMessage(message + "\n" + getString(R.string.login_retry_error));
            mErrorTipsDialog.show();
            return;
        }

        AppUI.failed(this, message);
        mErrorTime++;
    }

    @Override
    public void onLoginVerifyCodeError() {
        onLoginCallback();
        mErrorTipsDialog.setMessage(getString(R.string.login_verify_code_error));
        mErrorTipsDialog.show();
    }

    private void onLoginCallback() {
        mLoginButton.setEnabled(true);
        addAccountTextListener(mAccountTextWatcher);
        // 结束动画效果
        stopAnim();
    }

    private void stopAnim() {
        AppUI.dismiss();
    }

    /**
     * 显示登录中
     */
    private void showLoading() {
        showLoading(getString(R.string.signing));
    }

    /**
     * 显示登录中
     *
     * @param msg
     */
    private void showLoading(String msg) {
        IAppDialog dialog = AppUI.loading(this, msg);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                CnblogsApiFactory.getInstance(getContext()).cancel();
                onLoginCallback();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppRoute.REQ_CODE_WEB_LOGIN && resultCode == RESULT_OK) {
            // 登录成功，获取用户信息
            mErrorTime = 0;
            showLoading(getString(R.string.loading_user_info));
            mLoginPresenter.loadUserInfo();
        }
    }

    private class AccountTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mLoginButton.setEnabled(mUserNameView.length() > 0 && mPasswordView.length() > 0);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
