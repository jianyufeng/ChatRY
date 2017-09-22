package com.sanbafule.sharelock.chatting.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.chatting.activity.GroupChattingActivity;
import com.sanbafule.sharelock.chatting.help.ChattingListClickListener;
import com.sanbafule.sharelock.chatting.help.ChattingListLongClickListener;
import com.sanbafule.sharelock.chatting.help.ChattingRowType;
import com.sanbafule.sharelock.chatting.modle.BaseChattingRow;
import com.sanbafule.sharelock.chatting.modle.holder.BaseHolder;
import com.sanbafule.sharelock.chatting.modle.message.ChattingSystemRow;
import com.sanbafule.sharelock.chatting.modle.message.DescriptionRxRow;
import com.sanbafule.sharelock.chatting.modle.message.DescriptionTxRow;
import com.sanbafule.sharelock.chatting.modle.message.FileRxRow;
import com.sanbafule.sharelock.chatting.modle.message.FileTxRow;
import com.sanbafule.sharelock.chatting.modle.message.GifRxRow;
import com.sanbafule.sharelock.chatting.modle.message.GifTxRow;
import com.sanbafule.sharelock.chatting.modle.message.IChattingRow;
import com.sanbafule.sharelock.chatting.modle.message.ImageRxRow;
import com.sanbafule.sharelock.chatting.modle.message.ImageTxRow;
import com.sanbafule.sharelock.chatting.modle.message.LocationRxRow;
import com.sanbafule.sharelock.chatting.modle.message.LocationTxRow;
import com.sanbafule.sharelock.chatting.modle.message.ShareLockMessage;
import com.sanbafule.sharelock.chatting.modle.message.VideoRxRow;
import com.sanbafule.sharelock.chatting.modle.message.VideoTxRow;
import com.sanbafule.sharelock.chatting.modle.message.VoiceRxRow;
import com.sanbafule.sharelock.chatting.modle.message.VoiceTxRow;
import com.sanbafule.sharelock.chatting.util.ChattingsRowUtils;
import com.sanbafule.sharelock.util.DateUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.rong.imlib.model.Message;

/**
 *
 */
public class ChattingAdapter extends BaseAdapter {

    private static final String TAG = "ChatttingAdapter";
    protected View.OnClickListener mOnClickListener;
    /**
     * 当前语音播放的Item
     */
    public int mVoicePosition = -1;
    /**
     * 需要显示时间的Item position
     */
    private ArrayList<String> mShowTimePosition;
    /**
     * 初始化所有类型的聊天Item 集合
     */
    private HashMap<Integer, IChattingRow> mRowItems;
    /**
     * 时间显示控件的垂直Padding
     */
    private int mVerticalPadding;
    /**
     * 时间显示控件的横向Padding
     */
    private int mHorizontalPadding;
    /**
     * 消息联系人名称显示颜色
     */
    private ColorStateList[] mChatNameColor;
    private String mUsername;
    private long mThread = -1;
    private int mMsgCount = 18;
    private int mTotalCount = mMsgCount;
    public View.OnLongClickListener mOnLongClickListenner;

    private List<ShareLockMessage> list;
    private Context mContext;
    private LayoutInflater inflater;

    public ChattingAdapter(List<ShareLockMessage> list, Context context) {
        if (list == null) {
            list = new ArrayList<>();
        }
        this.list = list;
        this.mContext = context;
        inflater = LayoutInflater.from(context);
        mRowItems = new HashMap<Integer, IChattingRow>();
        mShowTimePosition = new ArrayList<String>();
        initRowItems();

        // 初始化聊天消息点击事件回调
        mOnClickListener = new ChattingListClickListener((GroupChattingActivity) mContext);
        mOnLongClickListenner = new ChattingListLongClickListener((GroupChattingActivity) mContext);
        mVerticalPadding = mContext.getResources().getDimensionPixelSize(R.dimen.android_width_3);
        mHorizontalPadding = mContext.getResources().getDimensionPixelSize(R.dimen.android_width_12);
        mChatNameColor = new ColorStateList[]{
                mContext.getResources().getColorStateList(R.color.white),
                mContext.getResources().getColorStateList(R.color.chatroom_user_displayname_color)};

    }

    public List<ShareLockMessage> getList() {
        return list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ShareLockMessage getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Long time;
        ShareLockMessage shareLockMessage = getItem(position);
        Message message = shareLockMessage.getMessage();
        if (shareLockMessage == null && message == null) {
            return null;
        }
        boolean showTimer = false;
        if (position == 0) {
            showTimer = true;
        } else {
            ShareLockMessage shareLockMessage1 = getItem(position - 1);
            Message message1 = shareLockMessage1.getMessage();
            if (message1.getMessageDirection() == Message.MessageDirection.SEND) {
                time = message1.getSentTime();
            } else {
                time = message1.getReceivedTime();
            }
            if (message.getMessageDirection() == Message.MessageDirection.SEND) {
                if (mShowTimePosition.contains(message.getMessageId()) || (message.getSentTime() - time >= 180000L)) {
                    showTimer = true;
                }
            } else if (message.getMessageDirection() == Message.MessageDirection.RECEIVE) {
                if (mShowTimePosition.contains(message.getMessageId()) || (message.getReceivedTime() - time >= 180000L)) {
                    showTimer = true;
                }
            }

        }
        int type = ChattingsRowUtils.getChattingMessageType(shareLockMessage);
        BaseChattingRow chattingRow = getBaseChattingRow(type, isSend(message));
        // 调用的的oncreateView
        View chatView = chattingRow.buildChatView(LayoutInflater.from(mContext), convertView);
        BaseHolder baseHolder = (BaseHolder) chatView.getTag();
        if (showTimer) {
            baseHolder.getChattingTime().setVisibility(View.VISIBLE);
            baseHolder.getChattingTime().setBackgroundResource(R.drawable.chat_tips_bg);
            if (message.getMessageDirection() == Message.MessageDirection.SEND) {
                baseHolder.getChattingTime().setText(DateUtils.getDateString(message.getSentTime(), DateUtils.SHOW_TYPE_CALL_LOG).trim());
            } else {
                baseHolder.getChattingTime().setText(DateUtils.getDateString(message.getReceivedTime(), DateUtils.SHOW_TYPE_CALL_LOG).trim());
            }

//            baseHolder.getChattingTime().setTextColor(mChatNameColor[0]);
            baseHolder.getChattingTime().setTextColor(Color.GRAY);
            baseHolder.getChattingTime().setPadding(mHorizontalPadding, mVerticalPadding, mHorizontalPadding, mVerticalPadding);
        } else {
            baseHolder.getChattingTime().setVisibility(View.GONE);
            baseHolder.getChattingTime().setShadowLayer(0.0F, 0.0F, 0.0F, 0);
            baseHolder.getChattingTime().setBackgroundResource(0);
        }

        // 调用的是绑定数据
        chattingRow.buildChattingBaseData(mContext, baseHolder, shareLockMessage, position);

        if (baseHolder.getChattingUser() != null && baseHolder.getChattingUser().getVisibility() == View.VISIBLE) {
//            baseHolder.getChattingUser().setTextColor(mChatNameColor[1]);
            baseHolder.getChattingUser().setTextColor(Color.BLACK);
            baseHolder.getChattingUser().setShadowLayer(0.0F, 0.0F, 0.0F, 0);
        }
        return chatView;

    }


    /**
     * 返回消息的类型
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        ShareLockMessage shareLockMessage = (ShareLockMessage) getItem(position);
        Message message = shareLockMessage.getMessage();
        if (shareLockMessage == null && message == null) {
            return 0;
        }
        return getBaseChattingRow(ChattingsRowUtils.getChattingMessageType(shareLockMessage), isSend(message)).getChatViewType();
    }


    /**
     * 根据消息类型返回相对应的消息Item
     *
     * @param rowType
     * @param isSend
     * @return
     */
    public BaseChattingRow getBaseChattingRow(int rowType, boolean isSend) {
        StringBuilder builder = new StringBuilder("C").append(rowType);
        if (isSend) {
            builder.append("T");
        } else {
            builder.append("R");
        }
        ChattingRowType fromValue = ChattingRowType.fromValue(builder.toString());
        IChattingRow iChattingRow = mRowItems.get(fromValue.getId().intValue());
        return (BaseChattingRow) iChattingRow;
    }

    /**
     * 消息类型数
     */
    @Override
    public int getViewTypeCount() {
        return ChattingRowType.values().length;
    }


    /**
     * 判断某条消息是收或发
     */

    public boolean isSend(Message message) {
        return message.getMessageDirection() == Message.MessageDirection.SEND;

    }


    /**
     * 初始化不同的聊天Item View
     */
    void initRowItems() {
        mRowItems.put(Integer.valueOf(1), new ImageRxRow(1));
        mRowItems.put(Integer.valueOf(2), new ImageTxRow(2));
        mRowItems.put(Integer.valueOf(3), new FileRxRow(3));
        mRowItems.put(Integer.valueOf(4), new FileTxRow(4));
        mRowItems.put(Integer.valueOf(5), new VoiceRxRow(5));
        mRowItems.put(Integer.valueOf(6), new VoiceTxRow(6));
        mRowItems.put(Integer.valueOf(7), new DescriptionRxRow(7));
        mRowItems.put(Integer.valueOf(8), new DescriptionTxRow(8));
        mRowItems.put(Integer.valueOf(9), new ChattingSystemRow(9));
        mRowItems.put(Integer.valueOf(10), new LocationRxRow(10));
        mRowItems.put(Integer.valueOf(11), new LocationTxRow(11));
//        mRowItems.put(Integer.valueOf(12), new CallRxRow(12));
//        mRowItems.put(Integer.valueOf(13), new CallTxRow(13));
        mRowItems.put(Integer.valueOf(12), new GifRxRow(12));
        mRowItems.put(Integer.valueOf(13), new GifTxRow(13));
        mRowItems.put(Integer.valueOf(14), new VideoRxRow(14));
        mRowItems.put(Integer.valueOf(15), new VideoTxRow(15));
    }

    public void addData(List<Message> data) {
        if (data == null || data.size() == 0) {
            return;
        }
        if (list == null) {
            list = new ArrayList<>();
        }

        for (int i = 0; i < data.size(); i++) {
            addData(data.get(i));
        }

    }


    public void addMessage(Message message) {
        ShareLockMessage shareLockMessage = new ShareLockMessage();
        shareLockMessage.setMessage(message);
        list.add(shareLockMessage);
        this.notifyDataSetChanged();
    }

    public void addData(Message message) {
        ShareLockMessage shareLockMessage = new ShareLockMessage();
        shareLockMessage.setMessage(message);
        list.add(0, shareLockMessage);
        this.notifyDataSetChanged();
    }


    public void addData(ShareLockMessage message) {
        list.add(message);
        this.notifyDataSetChanged();
    }

    public void cleaData() {
        list.clear();
        notifyDataSetChanged();
    }
    public boolean containsData(ShareLockMessage message){
        return list.contains(message);
    }
    /**
     * 删除消息
     *
     * @param shareLockMessage
     */
    public void removeData(ShareLockMessage shareLockMessage) {
        list.remove(shareLockMessage);
        this.notifyDataSetChanged();
    }

    public void removeAllData() {
        list.clear();
        this.notifyDataSetChanged();

    }

    public void refreshData(List<ShareLockMessage> datas) {
        this.list = datas;
        notifyDataSetChanged();

    }

    /**
     * @return the mOnClickListener
     */
    public View.OnClickListener getOnClickListener() {
        return mOnClickListener;
    }

    public View.OnLongClickListener getOnLongClickListenner() {
        return mOnLongClickListenner;
    }

    /**
     * 当前语音播放的位置
     *
     * @param position
     */
    public void setVoicePosition(int position) {
        mVoicePosition = position;
    }

    /**
     *
     */
    public void onPause() {
        mVoicePosition = -1;

    }


}
