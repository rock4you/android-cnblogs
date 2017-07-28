package com.rae.cnblogs.service.task;

import android.text.TextUtils;
import android.util.Log;

import com.rae.cnblogs.sdk.api.IBlogApi;
import com.rae.cnblogs.sdk.api.INewsApi;
import com.rae.cnblogs.sdk.bean.BlogBean;
import com.rae.cnblogs.sdk.bean.BlogType;
import com.rae.cnblogs.sdk.db.DbBlog;

import java.io.IOException;

import retrofit2.Response;

/**
 * 任务执行
 * Created by ChenRui on 2017/7/27 0027 16:37.
 */
public class BlogContentTask implements Runnable {

    private final DbBlog mDbBlog;
    private IBlogApi mBlogApi;
    private INewsApi mNewsApi;
    private String mBlogId;

    public BlogContentTask(IBlogApi blogApi, INewsApi newsApi, DbBlog dbBlog, String blogId) {
        mBlogApi = blogApi;
        mNewsApi = newsApi;
        mDbBlog = dbBlog;
        mBlogId = blogId;
    }

    @Override
    public void run() {
        if (TextUtils.isEmpty(mBlogId)) {
            return;
        }

        BlogBean blogInfo = mDbBlog.getBlog(mBlogId);
        if (!TextUtils.isEmpty(blogInfo.getContent())) {
            return;
        }

        BlogType type = BlogType.typeOf(blogInfo.getBlogType());

        try {
            Log.i("rae", "正在执行任务：[" + blogInfo.getBlogType() + "] --> " + mBlogId);
            Response<String> response = createBlogTask(type, mBlogId);
            String content = response.body();
            mDbBlog.updateBlogContent(mBlogId, blogInfo.getBlogType(), content);
            Log.i("rae", "执行任务成功：[" + blogInfo.getBlogType() + "] --> " + mBlogId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Response<String> createBlogTask(BlogType type, String blogId) throws IOException {

        if (type == BlogType.NEWS) {
            return mNewsApi.syncGetNewsContent(blogId).execute();
        }
        if (type == BlogType.KB) {
            return mBlogApi.syncGetKbContent(blogId).execute();
        }
        return mBlogApi.syncGetBlogContent(blogId).execute();
    }

}
