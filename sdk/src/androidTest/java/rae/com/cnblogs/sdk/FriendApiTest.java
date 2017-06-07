package rae.com.cnblogs.sdk;

import com.github.raee.runit.AndroidRUnit4ClassRunner;
import com.rae.cnblogs.sdk.CnblogsApiFactory;
import com.rae.cnblogs.sdk.api.IFriendsApi;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 社交接口
 * Created by ChenRui on 2017/2/7 0007 15:34.
 */
@RunWith(AndroidRUnit4ClassRunner.class)
public class FriendApiTest extends BaseTest {

    private IFriendsApi mApi;

    @Override
    public void setup() {
        super.setup();
        mApi = CnblogsApiFactory.getInstance(mContext).getFriendApi();
    }

    @Test
    public void testBlogList() {
        runTest("testBlogList", mApi.getBlogList(1, "murongxiaopifu"));
    }

    @Test
    public void testFriendsInfo() {
        runTest("testFriendsInfo", mApi.getFriendsInfo("gaochundong"));
    }

    @Test
    public void testFeeds() throws Exception {
        runTest("testFeeds", mApi.getFeeds(1, "cs_net"));
    }

    @Test
    public void testFollow() {
        runTest("testFollow", mApi.follow("649b5d31-64f0-de11-ba8f-001cf0cd104b"));
    }

    @Test
    public void testUnFollow() {
        runTest("testFollow", mApi.unFollow("649b5d31-64f0-de11-ba8f-001cf0cd104b"));
    }
//
//    @Test
//    public void testFollowList() {
//        startTest(new Runnable() {
//            @Override
//            public void run() {
//                mApi.getFollowList(null, 1, listListener(UserInfoBean.class));
//            }
//        });
//    }
//
//    @Test
//    public void testFansList() {
//        startTest(new Runnable() {
//            @Override
//            public void run() {
//                mApi.getFansList(null, 1, listListener(UserInfoBean.class));
//            }
//        });
//    }
}
