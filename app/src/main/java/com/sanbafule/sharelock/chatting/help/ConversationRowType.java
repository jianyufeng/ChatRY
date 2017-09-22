package com.sanbafule.sharelock.chatting.help;


/**
 * Administrator
 * 作者:Created by ShareLock on 2016/12/14 14:15
 * cd : 三八妇乐
 * 描述：
 */
public enum ConversationRowType {

    // 私聊
    PRIVATE("private", Integer.valueOf(1)),
    // 群聊
    GROUP("group", Integer.valueOf(2)),
    // 系统消息
    SYSTEM("system", Integer.valueOf(3)),
    // 新闻
    NEWS("news", Integer.valueOf(4)),
    // 商城
    SHOP("shop", Integer.valueOf(5)),
    // 会议
    MEETING("meeting", Integer.valueOf(6)),
    // 好友添加
    FRIEND_ADD("add_friend", Integer.valueOf(7)),
    //好友删除
    FRIEND_DELETE("delete_friend", Integer.valueOf(8)),
    // 好友同意/拒绝
    FRIENDS_ADD_REQUEST_REPLY("friends_add_request_reply", Integer.valueOf(9)),
    // 群组邀请  （用户接收）
    GROUP_INVITE("group_invite", Integer.valueOf(10)),
    // 邀请人同意 （管理员接收）
    GROUP_AGREE("group_agree", Integer.valueOf(11)),
    // 群组解散
    GROUP_DISMISS("group_dismiss", Integer.valueOf(12)),
    // 用户申请加入群组的操作（用户主动加入群组）{管理员接收}
    APPLY_ADD_GROUP_REQUEST("apply_add_group_request", Integer.valueOf(13)),
    // 用户申请加入群组的回执（管理员发给用户的恢复）（用户接收）
    APPLY_ADD_GROUP_REQUEST_REPLY("apply_add_group_request_reply", Integer.valueOf(14)),
    //被踢出群组
    GROUP_KICK("group_kick", Integer.valueOf(15));


    private final Integer mId;
    private final String mDefaultValue;

    private ConversationRowType(String defaultValue, Integer id) {
        this.mId = id;
        this.mDefaultValue = defaultValue;
    }

    public Integer getId() {
        return this.mId;
    }

    public String getDefaultValue() {
        return this.mDefaultValue;
    }

    public static ConversationRowType fromValue(String value) {
        ConversationRowType[] values = values();
        int cc = values.length;
        for (int i = 0; i < cc; i++) {
            if (values[i].mDefaultValue.equals(value)) {
                return values[i];
            }
        }
        return null;
    }

    public static ConversationRowType fromValue(Integer value) {
        ConversationRowType[] values = values();
        int cc = values.length;
        for (int i = 0; i < cc; i++) {
            if (values[i].mId.equals(value)) {
                return values[i];
            }
        }
        return null;
    }

}
