package com.rae.cnblogs.presenter.impl;

import android.content.Context;
import android.text.TextUtils;

import com.rae.cnblogs.AppMobclickAgent;
import com.rae.cnblogs.R;
import com.rae.cnblogs.presenter.ILoginPresenter;
import com.rae.cnblogs.sdk.ApiDefaultObserver;
import com.rae.cnblogs.sdk.Empty;
import com.rae.cnblogs.sdk.UserProvider;
import com.rae.cnblogs.sdk.api.IUserApi;
import com.rae.cnblogs.sdk.bean.UserInfoBean;
import com.rae.cnblogs.sdk.utils.ApiEncrypt;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.analytics.MobclickAgent;

import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import retrofit2.HttpException;

/**
 * 登录逻辑
 * Created by ChenRui on 2017/1/19 0019 14:56.
 */
public class LoginPresenterImpl extends BasePresenter<ILoginPresenter.ILoginView> implements ILoginPresenter {

    private IUserApi mUserApi;
    private boolean mFromLogin;
    private String mBlogApp;

    public LoginPresenterImpl(Context context, ILoginView view) {
        super(context, view);
        mUserApi = getApiProvider().getUserApi();
    }

    @Override
    public void login() {
        String userName = mView.getUserName();
        String pwd = mView.getPassword();
        if (isEmpty(userName)) {
            mView.onLoginError(mContext.getString(R.string.error_username_empty));
            return;
        }
        if (isEmpty(pwd)) {
            mView.onLoginError(mContext.getString(R.string.error_password_empty));
            return;
        }

        // 清除cookie
        UserProvider.getInstance().logout();

        mFromLogin = true;
        createObservable(mUserApi.login(ApiEncrypt.encrypt(userName), ApiEncrypt.encrypt(pwd)))
                .flatMap(new Function<Empty, ObservableSource<UserInfoBean>>() {
                    @Override
                    public ObservableSource<UserInfoBean> apply(Empty empty) throws Exception {
                        return createObservable(mUserApi.getUserBlogAppInfo()); // 获取BlogApp信息
                    }
                })
                .flatMap(new Function<UserInfoBean, ObservableSource<UserInfoBean>>() {
                    @Override
                    public ObservableSource<UserInfoBean> apply(UserInfoBean userInfoBean) throws Exception {
                        mBlogApp = userInfoBean.getBlogApp();
                        return createObservable(mUserApi.getUserInfo(userInfoBean.getBlogApp())); // 获取用户信息
                    }
                })
                .subscribe(new ApiDefaultObserver<UserInfoBean>() {
                    @Override
                    public void onError(Throwable e) {
                        // 统计错误信息
                        AppMobclickAgent.onLoginEvent(mApplicationContext, "ERROR", false, getLog(e));
                        // 报告错误信息
                        CrashReport.postCatchedException(e);

                        if (e instanceof HttpException) {
                            HttpException ex = (HttpException) e;
                            if (ex.code() == 503) {
                                onError("[503]服务器拒绝连接，请重试一下吧。");
                                return;
                            }
                        }
                        super.onError(e);
                    }


                    private String getLog(Throwable e) {
                        if (e == null || e.getMessage() == null) return "没有错误信息";
                        return e.getMessage();
                    }

                    @Override
                    protected void onError(String msg) {

                        if (!TextUtils.isEmpty(msg) && msg.contains("验证码")) {
                            // 验证码错误
                            mView.onLoginVerifyCodeError();
                        } else {
                            mView.onLoginError(msg);
                        }
                    }

                    @Override
                    protected void accept(UserInfoBean data) {
                        if (TextUtils.isEmpty(data.getUserId())) {
                            mView.onLoginError("获取用户信息失败");
                            AppMobclickAgent.onLoginEvent(mApplicationContext, "ERROR", false, "没有获取到用户ID");
                            return;
                        }

                        // 如果blogApp为空则重新设置，这里的blogApp一定不为空了
                        if (TextUtils.isEmpty(data.getBlogApp())) {
                            data.setBlogApp(mBlogApp);
                        }

                        // 保存登录信息
                        UserProvider.getInstance().setLoginUserInfo(data);

                        // 统计登录事件
                        AppMobclickAgent.onLoginEvent(mApplicationContext, data.getBlogApp(), true, null);
                        // 友盟统计用户
                        if (mFromLogin) {
                            MobclickAgent.onProfileSignIn(data.getBlogApp());
                        }

                        // [重要] 同步Cookie登录信息
                        UserProvider.getInstance().syncFormCookieJar();

                        mView.onLoginSuccess(data);
                    }
                });
    }

    @Override
    public void loadUserInfo() {

    }

    @Override
    public void cancel() {
        cancelRequest();
    }

    @Override
    public void start() {
    }

}
