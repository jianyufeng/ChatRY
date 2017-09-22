package com.sanbafule.sharelock.comm;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/10/9.
 */
public class SString {


    public static final String SP_FILE_NAME = "com.sanbafule.ShareLock_preferences";

    public static final String TYPE = "type";
    public static final String CONTACTINFO = "contactinfo";

    public static final String SUCCESS = "success";
    public static final String RESULT = "result";
    public static final String CMD_LOGOUT = "KICK_LOGIN";
    // 添加好友
    public static final String ADD_FRIEND_OPERATION = "FRIENDS_ADD_REQUEST";
    // 好友同意或拒绝
    public static final String FRIENDS_ADD_REQUEST_REPLY = "FRIENDS_ADD_REQUEST_REPLY";
    // 删除好友的命令
    public static final String DELETE_FIREND_NTF = "DELETE_FIREND_NTF";


    public static final String OPERATION = "operation";
    public static final String TARGETUSERID = "targetUserId";
    public static final String SOURCEUSERID = "sourceUserId";
    public static final String AGREE = "AGREE";
    public static final String DISAGREE = "DISAGREE";
    public static final String NAME = "name";
    public static final String BIG_IMG = "image";
    public static final String[] TITLE = {"", "介绍", "产品", "消息", "我的"};
    public static final String[] INFORMATION_TITLE = {"新闻资讯", "工程大事记", "工程推进会", "影音资讯"};
    public static final String[] IMAGEANDFILE_TITLE = {"全部", "图片", "文件"};

    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";
    public static final String TARGETNAME = "TargetName";


    // 群组邀请
    public static final String INVITATION_USER_ADD_GROUP = "INVITATION_USER_ADD_GROUP";
    //群组邀请的回执
    public static final String INVITATION_USER_ADD_GROUP_REPLY = "INVITATION_USER_ADD_GROUP_REPLY";
    // 解散群组
    public static final String DISMISS_GROUP = "DISMISS_GROUP";
    // 被踢出群组
    public static final String KICK_MESSAGE = "KICK_MESSAGE";
    // 用户申请加入群组的操作
    public static final String APPLY_ADD_GROUP_REQUEST = "APPLY_ADD_GROUP_REQUEST";
    // 加入群组的回执
    public static final String APPLY_ADD_GROUP_REQUEST_REPLY = "APPLY_ADD_GROUP_REQUEST_REPLY";
    //搜索界面的输入框显示的文本及提示文本
    public static final String SEARCH_HINT = "SEARCH_HINT";
    public static final String SEARCH_TEXT = "SEARCH_TEXT";

    //登录操作的action
    public static final String LOGINACTION = "loginaction";
    //群组相关的
    public static final String GROUP_PRE = "GROUP_";  //创建群组后返回群组id 需要添加的前缀  才是真正的群组id
    public static final String GROUP_ID = "GROUP_ID";  //群组id
    public static final String GROUP_MEMBER_ACCOUNT = "GROUP_MEMBER_ACCOUNT";  //群组id
    public static final String GROUP_ADD_FLAG = "add@group";
    public static final String EDIT_GROUPINFO_TITLE = "title";
    public static final String EDIT_GROUPINFO_CONTENT = "content";

    // 公司职员
    public static final String MEMBER = "MEMBER";
    // 外部会员
    public static final String EMPLOYEES = "EMPLOYEES";
    // 高管
    public static final String GAOGUAN = "GAOGUAN";
    // 联系人的个性签名
    public static final String CONTACT_SIGN = "contact_sign";
    //联系人的备注
    public static final String COMMENT_NAME = "comment_name";
    // 联系人的Id
    public static final String CONTACT_ID = "contact_id";
    // 联系人的name
    public static final String CONTACT_NAME = "contact_name";
    //会话类型
    public static final String CONVERSATION_TYPE = "conversation_type";

    // 服务的缓存路径
    public static final String EXTRA_SERVICE_CACHE_NAME = "extra_service.txt";
    //文字消息的内容
    public static final String CONTENT = "content";


    //系统类型的消息 分类
    public static final String FIREND_ACTION_ = "FirendAction_"; //好友间的操作
    public static final String GROUP_INFO_ = "GroupInfo_"; //群组间的移除成员 解散群组
    public static final String GROUP_ACTION_ = "GroupAction_"; //群组间的邀请  申请  同意拒绝
    public static final String MANAGER_GROUP_ACTION_ = "ManagerGroupAction_"; //群组间的移除成员 解散群组

    public static final String VIDEO_PATH = "video_path"; //视频路径
    public static final String IMG = "img"; //视频路径
    public static final String start_from = "start_from"; //MainActivity 是从哪里启动的
    public static final String notify_chat = "notify_chat"; //通知跳转到MainActivity

    public static final boolean getSuccess(JSONObject object) throws JSONException {
        return object.getBoolean(SString.SUCCESS);
    }

    public static final JSONObject getResult(JSONObject object) throws JSONException {
        return object.getJSONObject(SString.RESULT);
    }

    public static final String getMessage(JSONObject object) throws JSONException {
        return object.getString(SString.RESULT);
    }
}
