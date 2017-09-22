package com.sanbafule.sharelock.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sanbafule.sharelock.MainActivity;
import com.sanbafule.sharelock.chatting.Interface.RoundNumberDragListener;
import com.sanbafule.sharelock.chatting.help.ConversationRowType;
import com.sanbafule.sharelock.chatting.modle.conversation.BaseConversationRow;
import com.sanbafule.sharelock.chatting.modle.conversation.FriendAddConversationRow;
import com.sanbafule.sharelock.chatting.modle.conversation.FriendAddRequestConversationRow;
import com.sanbafule.sharelock.chatting.modle.conversation.GroupAgreeConversationRow;
import com.sanbafule.sharelock.chatting.modle.conversation.GroupConversationRow;
import com.sanbafule.sharelock.chatting.modle.conversation.GroupDismissConversationRow;
import com.sanbafule.sharelock.chatting.modle.conversation.GroupInviteConversationRow;
import com.sanbafule.sharelock.chatting.modle.conversation.GroupKickConversationRow;
import com.sanbafule.sharelock.chatting.modle.conversation.GroupMemberAddGroupConversationRow;
import com.sanbafule.sharelock.chatting.modle.conversation.GroupMemberAddGroupRequestConversationRow;
import com.sanbafule.sharelock.chatting.modle.conversation.MeetingConversationRow;
import com.sanbafule.sharelock.chatting.modle.conversation.NewsConversationRow;
import com.sanbafule.sharelock.chatting.modle.conversation.PrivateConversationRow;
import com.sanbafule.sharelock.chatting.modle.conversation.SConversationRow;
import com.sanbafule.sharelock.chatting.modle.conversation.ShopConversationRow;
import com.sanbafule.sharelock.chatting.modle.conversation.SystemConversationRow;
import com.sanbafule.sharelock.chatting.modle.conversation.holder.FriendAddHolder;
import com.sanbafule.sharelock.chatting.modle.conversation.holder.FriendAddRequestHolder;
import com.sanbafule.sharelock.chatting.modle.conversation.holder.GroupAgreeHolder;
import com.sanbafule.sharelock.chatting.modle.conversation.holder.GroupDismissHolder;
import com.sanbafule.sharelock.chatting.modle.conversation.holder.GroupHolder;
import com.sanbafule.sharelock.chatting.modle.conversation.holder.GroupInviteHolder;
import com.sanbafule.sharelock.chatting.modle.conversation.holder.GroupKickHolder;
import com.sanbafule.sharelock.chatting.modle.conversation.holder.GroupMemberAddGroupHolder;
import com.sanbafule.sharelock.chatting.modle.conversation.holder.GroupMemberAddGroupRequestHolder;
import com.sanbafule.sharelock.chatting.modle.conversation.holder.MeetingHolder;
import com.sanbafule.sharelock.chatting.modle.conversation.holder.NewsHolder;
import com.sanbafule.sharelock.chatting.modle.conversation.holder.PrivateHolder;
import com.sanbafule.sharelock.chatting.modle.conversation.holder.ShopHolder;
import com.sanbafule.sharelock.chatting.modle.conversation.holder.SystemHolder;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.global.MyString;
import com.sanbafule.sharelock.view.SwipeLayout;
import com.sanbafule.sharelock.view.WrapRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;


/**
 * ShareLock
 * 作者:Created by ShareLock on 2016/12/14 11:32
 * cd : 三八妇乐
 * 描述：
 */
public class ConversationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SwipeLayout.OnSwipeListener {

    private MainActivity mContext;
    private LayoutInflater mLayoutInflater;
    private List<Conversation> mConversionList;
    private ArrayList<SwipeLayout> openedItems;
    private RoundNumberDragListener dragListener;
    /**
     * 初始化所有类型的聊天Item 集合
     */
    private HashMap<Integer, SConversationRow> mRowItems;
    private WrapRecyclerView recyclerView;

    public void setDragListener(RoundNumberDragListener dragListener) {
        this.dragListener = dragListener;
    }


    public ConversationAdapter(MainActivity context, List<Conversation> mConversionList, WrapRecyclerView recyclerView) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        if (mConversionList == null) {
            mConversionList = new ArrayList<>();
        }
        this.mConversionList = mConversionList;
        this.recyclerView = recyclerView;
        openedItems = new ArrayList<>();
        initRowItems();
    }


    /**
     * 初始化所有的View
     */
    private void initRowItems() {
        mRowItems = new HashMap<>();
        mRowItems.put(Integer.valueOf(1), new PrivateConversationRow(1));
        mRowItems.put(Integer.valueOf(2), new GroupConversationRow(2));
        mRowItems.put(Integer.valueOf(3), new SystemConversationRow(3));
        mRowItems.put(Integer.valueOf(4), new NewsConversationRow(4));
        mRowItems.put(Integer.valueOf(5), new ShopConversationRow(5));
        mRowItems.put(Integer.valueOf(6), new MeetingConversationRow(6));
        mRowItems.put(Integer.valueOf(7), new FriendAddConversationRow(7));
//        mRowItems.put(Integer.valueOf(8), new FriendDeleteConversationRow(8));
        mRowItems.put(Integer.valueOf(9), new FriendAddRequestConversationRow(9));
        mRowItems.put(Integer.valueOf(10), new GroupInviteConversationRow(10));
        mRowItems.put(Integer.valueOf(11), new GroupAgreeConversationRow(11));
        mRowItems.put(Integer.valueOf(12), new GroupDismissConversationRow(12));
        mRowItems.put(Integer.valueOf(13), new GroupMemberAddGroupConversationRow(13));
        mRowItems.put(Integer.valueOf(14), new GroupMemberAddGroupRequestConversationRow(14));
        mRowItems.put(Integer.valueOf(15), new GroupKickConversationRow(15));


    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ConversationRowType fromValue = ConversationRowType.fromValue(viewType);
        if (fromValue.getId() == 1) {
            return new PrivateHolder(abc(viewType));
        } else if (fromValue.getId() == 2) {
            return new GroupHolder(abc(viewType));
        } else if (fromValue.getId() == 3) {
            return new SystemHolder(abc(viewType));
        } else if (fromValue.getId() == 4) {
            return new NewsHolder(abc(viewType));
        } else if (fromValue.getId() == 5) {
            return new ShopHolder(abc(viewType));
        } else if (fromValue.getId() == 6) {
            return new MeetingHolder(abc(viewType));
        } else if (fromValue.getId() == 7) {
            return new FriendAddHolder(abc(viewType));
        }
// else if(fromValue.getId()==8){
//             return new FriendDeleteHolder(abc(viewType));
//         }
        else if (fromValue.getId() == 9) {
            return new FriendAddRequestHolder(abc(viewType));
        } else if (fromValue.getId() == 10) {
            return new GroupInviteHolder(abc(viewType));
        } else if (fromValue.getId() == 11) {
            return new GroupAgreeHolder(abc(viewType));
        } else if (fromValue.getId() == 12) {
            return new GroupDismissHolder(abc(viewType));
        } else if (fromValue.getId() == 13) {
            return new GroupMemberAddGroupHolder(abc(viewType));
        } else if (fromValue.getId() == 14) {
            return new GroupMemberAddGroupRequestHolder(abc(viewType));
        } else if (fromValue.getId() == 15) {
            return new GroupKickHolder(abc(viewType));
        }
        return null;
    }


    /***
     * 通过ViewType类型构建holder对象
     *
     * @param viewType
     * @return
     */
    private View abc(int viewType) {
        ConversationRowType fromValue = ConversationRowType.fromValue(viewType);
        SConversationRow sConversationRow = mRowItems.get(fromValue.getId().intValue());
        return sConversationRow.onCreateConversation(mLayoutInflater, recyclerView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        Conversation conversation = mConversionList.get(position);
//        LogUtil.i("发送时间"+conversation.getSentTime());
//        LogUtil.i("接受时间"+conversation.getReceivedTime());
        final BaseConversationRow mConversationRow = getBaseConversationRow(conversation);
        mConversationRow.buildConversationBaseData(holder, mContext, conversation, position, dragListener);

    }

    @Override
    public int getItemViewType(int position) {
        Conversation conversation = mConversionList.get(position);
        if (conversation == null) {
            return -1;
        }

        return getBaseConversationRow(conversation).getConversationViewType();
    }

    /**
     * // 根据Conversion确定type
     *
     * @param conversation
     * @return
     */
    public BaseConversationRow getBaseConversationRow(Conversation conversation) {

        MessageContent latestMessage = conversation.getLatestMessage();
        String senderUserId = conversation.getSenderUserId();
        StringBuilder builder = new StringBuilder();
        if (conversation.getConversationType() == Conversation.ConversationType.PRIVATE) {
            // 好友删除
//            if (latestMessage instanceof CommandNotificationMessage) {
//                if (((CommandNotificationMessage) latestMessage).getName().equals(SString.DELETE_FIREND_NTF)) {
//                    builder.append("delete_friend");
//                }
//            } else {
            builder.append("private");
//            }
        } else if (conversation.getConversationType() == Conversation.ConversationType.GROUP) {

            builder.append("group");
        } else if (conversation.getConversationType() == Conversation.ConversationType.SYSTEM) {
            if (senderUserId.startsWith("FirendAction")) {

                if (latestMessage instanceof TextMessage) {
                    final String content = ((TextMessage) latestMessage).getContent();
                    try {
                        JSONObject object = new JSONObject(content);
                        String operation = object.getString(SString.OPERATION);
                        // 添加好友
                        if (MyString.hasData(operation) && operation.equals(SString.ADD_FRIEND_OPERATION)) {
                            builder.append("add_friend");
                            // 好友同意回执
                        } else if (operation.equals(SString.FRIENDS_ADD_REQUEST_REPLY)) {
                            builder.append("friends_add_request_reply");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if (senderUserId.startsWith("GroupAction")) {

                if (latestMessage instanceof TextMessage) {
                    final String content = ((TextMessage) latestMessage).getContent();
                    try {
                        JSONObject object = new JSONObject(content);
                        String operation = object.getString(SString.OPERATION);
                        // 邀请群成员
                        if (MyString.hasData(operation) && operation.equals(SString.INVITATION_USER_ADD_GROUP)) {
                            builder.append("group_invite");
                            // 群组/管理同意
                        } else if (MyString.hasData(operation) && operation.equals(SString.INVITATION_USER_ADD_GROUP_REPLY)) {
                            builder.append("group_agree");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if (senderUserId.startsWith("GroupInfo")) {

                if (latestMessage instanceof TextMessage) {
                    final String content = ((TextMessage) latestMessage).getContent();
                    try {
                        JSONObject object = new JSONObject(content);
                        String operation = object.getString(SString.OPERATION);
                        // 邀请群成员
                        if (MyString.hasData(operation) && operation.equals(SString.DISMISS_GROUP)) {
                            builder.append("group_dismiss");
                            // 被踢出群组
                        }
                        if (MyString.hasData(operation) && operation.equals(SString.KICK_MESSAGE)) {
                            builder.append("group_kick");
                            // 群组/管理同意
                        } else if (MyString.hasData(operation) && operation.equals(SString.INVITATION_USER_ADD_GROUP_REPLY)) {
                            builder.append("group_agree");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if (senderUserId.startsWith("ManagerGroupAction")) {

                if (latestMessage instanceof TextMessage) {
                    final String content = ((TextMessage) latestMessage).getContent();
                    try {
                        JSONObject object = new JSONObject(content);
                        String operation = object.getString(SString.OPERATION);
                        // 邀请群成员
                        if (MyString.hasData(operation) && operation.equals(SString.APPLY_ADD_GROUP_REQUEST)) {
                            builder.append("apply_add_group_request");
                            // 群组/管理同意
                        } else if (MyString.hasData(operation) && operation.equals(SString.APPLY_ADD_GROUP_REQUEST_REPLY)) {
                            builder.append("apply_add_group_request_reply");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if (senderUserId.startsWith("NEWS")) {
                builder.append("news");
            } else if (senderUserId.startsWith("SHOP")) {
                builder.append("shop");
            } else if (senderUserId.startsWith("MEETING")) {
                builder.append("meeting");
            }

        }

        ConversationRowType fromValue = ConversationRowType.fromValue(builder.toString());
        if (fromValue == null) {
            SConversationRow sConversationRow = mRowItems.get(1);
            return (BaseConversationRow) sConversationRow;
        }
        SConversationRow sConversationRow = mRowItems.get(fromValue.getId().intValue());
        return (BaseConversationRow) sConversationRow;

    }


    @Override
    public void onClose(SwipeLayout layout) {
        openedItems.remove(layout);
    }

    @Override
    public void onOpen(SwipeLayout layout) {
        openedItems.add(layout);
    }

    @Override
    public void onStartOpen(SwipeLayout layout) {
        // 关闭所有已经打开的条目
        for (int i = 0; i < openedItems.size(); i++) {
            openedItems.get(i).close(true);
        }

        openedItems.clear();
    }

    @Override
    public void onStartClose(SwipeLayout layout) {

    }


    @Override
    public int getItemCount() {
        return mConversionList == null ? 0 : mConversionList.size();
    }

    public void refreshData(List<Conversation> datas) {
        if (datas == null) return;
        this.mConversionList = datas;
        notifyDataSetChanged();
    }

    public Conversation getItem(int pos) {
        if (mConversionList != null) {
            return mConversionList.get(pos);
        }
        return null;
    }

}
