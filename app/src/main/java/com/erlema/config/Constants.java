package com.erlema.config;

/**
 * @author bxtyfufff
 *         下午10:09:06
 */
public class Constants {
    /**
     * 常量
     */
    public static String Bmob_APPID = "9fd7951ba1ce3845c956ccbb8620424e";
    public static final String QQ_APPID = "1104713141";
    /**
     * 当前 DEMO 应用的 APP_KEY，第三方应用应该使用自己的 APP_KEY 替换该 APP_KEY
     */
    public static final String APP_KEY = "2058680160";

    /**
     * 当前 DEMO 应用的回调页，第三方应用可以使用自己的回调页。
     * 建议使用默认回调页：https://api.weibo.com/oauth2/default.html
     */
    public static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";

    /**
     * WeiboSDKDemo 应用对应的权限，第三方开发者一般不需要这么多，可直接设置成空即可。
     * 详情请查看 Demo 中对应的注释。
     */
    public static final String SCOPE =
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";
    /**
     * 正常消息
     */
    public static final int ACTION_NORMAL = 0;
    /**
     * 跳转到消息中心的消息
     */
    public static final int ACTION_MESSAGECENTER = 1;
    /**
     * 调转到物品详情页面的消息
     */
    public static final int ACTION_GOODSDETAIL = 2;
    /**
     * 调转到求购详情
     */
    public static final int ACTION_WGOODSDETAIL = 3;

    /**
     * 物品正常状态
     */
    public static final int GOODS_STATUS_NORMAL = 0;
    /**
     * 物品审核中状态
     */
    public static final int GOODS_STATUS_VISIT = 1;
    /**
     * 买家物品交易完成状态
     */
    public static final int GOODS_STATUS_SALD = 2;
    /**
     * 删除操作
     */
    public static final int RESULT_DELETE = 1;
    /**
     * 完成交易操作操作
     */
    public static final int RESULT_COMPLETE = 2;

}
