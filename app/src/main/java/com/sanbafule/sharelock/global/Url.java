package com.sanbafule.sharelock.global;

/**
 * Created by Administrator on 2016/4/29.
 */
public final class Url {
    public static final String IP = "http://192.168.19.108:3200";    //核心层
    public static final String CORE_IP = "http://192.168.19.108/svn_web_core/";
    /**
     * 登录
     */
    public static final String LOGIN_URI = IP + "/user/appLogin";
    /**
     * 检测是否需要登录
     */
    public static final String IS_AGAIN_LOGIN = IP + "/user/isAgainLogin";
    /**
     * 获取融云token
     */
    public static final String GET_RONG_TOKEN = IP + "/rongLib/getUserToken";
    /**
     * 获取好友列表和群组列表
     */
    public static final String GET_CONTANSANDGROUPLIST = IP + "/user/searchFirendsAndGroup";
    /**
     * 查询指定用户的好友列表（带在线状态）/user/search
     */
    public static final String GET_CONTACT_LIST = IP + "/friends/assignUserSearch";
    /***
     * 查询用户基本信息
     */
    public static final String GET_CONTACTINFO = IP + "/user/search";
    /**
     * 发送添加好友的请求
     */
    public static final String ADD_FRIEND_REQUEST = IP + "/friends/addRequest";
    /**
     * 新朋友列表
     */
    public static final String SEARCH_NEWFRIENDLIST_REQUEST = IP + "/friends/searchRequest";
    /**
     * 添加或拒绝好友申请
     */
    public static final String AGREE_OR_REFUSE = IP + "/friends/agreeOrRefuseAddRequest";
    /**
     * 解除好友关系 双向
     */
    public static final String DELECT_FRIEND = IP + "/friends/unfriend";
    /**
     * 获取加入的群组
     */
    public static final String GET_GROUP_LIST = IP + "/groups/searchUser";
    /**
     * 上传图片  写死的里  上传到服务器
     */
    public static final String UPLOAD_PICTURE = CORE_IP + "upload/image";
    /**
     * 获取加入的群组
     */
    public static final String CET_GROUP_DETAILS = IP + "/groups/info";
    /**
     * 获取加入的群组
     */
    public static final String CREATE_GROUP = IP + "/groups/create";
    /**
     * 更改群组名称
     */
    public static final String CHANGE_GROUP_NAME = IP + "/groups/updateGroupName";
    /**
     * 更改群公告
     */
    public static final String CHANGE_GROUP_DECLARED = IP + "/groups/updateGroupDeclared";
    /**
     * 更新群申请权限
     */
    public static final String CHANGE_GROUP_PERMISSION = IP + "/groups/updateGroupPermission";
    /**
     * 更改群头像
     */
    public static final String UPDATE_GROUP_ICON = IP + "/groups/updateGroupHeader";

    /**
     * 邀请成员加入群组
     */
    public static final String INVITE_MEMBERS = IP + "/groups/invitationUserAddGroup";
    /**
     * 回复邀请加入群组的请求
     */
    public static final String REPLY_INVITE = IP + "/groups/agreeOrRefuseGroupRequest";
    /**
     * 申请加入群组
     */
    public static final String PROPOSE_JOIN = IP + "/groups/userApplyAdd";
    /**
     * 同意/拒绝用户加入群组申请
     */
    public static final String REPLY_PROPOSE = IP + "/groups/agreeOrRefuseAddGroupApply";
    /**
     * 群组设置管理员
     */
    public static final String SET_GROUP_ADMIN = IP + "/groups/setGroupAdministrator";
    /**
     * 群组取消管理员
     */
    public static final String CANCEL_GROUP_ADMIN = IP + "/groups/cancelGroupAdministrator";
    /**
     * 禁言群成员
     */
    public static final String BAN_MEMBER = IP + "/groups/gagGroupUser";
    /**
     * 解除禁言群成员
     */
    public static final String CANCEL_BAN_MEMBER = IP + "/groups/cancelGagGroupUser";
    /**
     * 查询被禁言群成员
     */
    public static final String CHECK_BAN_MEMBERS = IP + "/groups/rongGroupUserGagList";
    /**
     * 用户退出群组
     */
    public static final String QUIT_GROUP = IP + "/groups/quitGroup";
    /**
     * 解散群组
     */
    public static final String DISMISS_GROUP = IP + "/groups/dismissGroup";
    /**
     * 踢出群组
     */
    public static final String KICK_GROUP = IP + "/groups/kickGroupUser";
    /**
     * 加入群组请求列表
     */
    public static final String JOIN_REQUEST_LIST = IP + "/groups/searchGroupApplyRequest";
    /**
     * 加入群组请求列表
     */
    public static final String INVITE_REQUEST_LIST = IP + "/groups/searchInviterUserRequest";

    /**
     * 搜索群组
     */
    public static final String SEARCH_GROUP = IP + "/groups/search";

    /**
     * 更改用户头像
     */
    public static final String CHANGE_USER_ICON = IP + "/user/updateHeader";
    /**
     * 更改用户的个性签名
     */
    public static final String CHANGE_USER_SING = IP + "/user/updateSignature";

    /**
     * 获取用户的二维码
     */
    public static final String GET_QRCODE = IP + "/user/getQrCode";
    /**
     * 修改用户的密码  只限职工修改
     */
    public static final String RESET_PASSWORD = IP + "/user/updateEmployeesPwd";
    /**
     * 好友模块
     */
    public static final String ME = IP + "/permissions/getModuleMenu";
    /**
     * 查询群组详细
     */
    public static final String GET_GROUP_INFO = IP + "/groups/detail";
    /**
     * 修改个性签名
     */
    public static final String UPDATE_NOTE = IP + "/friends/updateNote";
    /**
     * 获取用户的黑名单列表
     */
    public static final String GET_BLACK_LIST = IP + "/rongLib/userBlacklistQuery";
    /**
     * 加入黑名单
     */
    public static final String ADD_BLACK_LIST = IP + "/rongLib/userBlacklistAdd";
    /**
     * 移除黑名单
     */
    public static final String REMOVE_BLACK_LIST = IP + "/rongLib/userBlacklistRemove";
    /**
     * 查看用户是否是在黑名单中
     */
    public static final String IS_BLACK_LIST = IP + "/friends/isBlack";

}
