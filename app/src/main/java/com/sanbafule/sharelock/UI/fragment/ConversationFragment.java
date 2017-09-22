package com.sanbafule.sharelock.UI.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sanbafule.sharelock.MainActivity;
import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.UI.account.LoginActivity;
import com.sanbafule.sharelock.adapter.ConversationAdapter;
import com.sanbafule.sharelock.adapter.RecyclerWrapAdapter;
import com.sanbafule.sharelock.chatting.Interface.ReceiveMessageCallback;
import com.sanbafule.sharelock.chatting.Interface.RoundNumberDragListener;
import com.sanbafule.sharelock.chatting.activity.ConversationSearchActivity;
import com.sanbafule.sharelock.chatting.help.ReceiveMessageHelp;
import com.sanbafule.sharelock.comm.help.ShareLockManager;
import com.sanbafule.sharelock.util.DensityUtil;
import com.sanbafule.sharelock.util.LogUtil;
import com.sanbafule.sharelock.util.ToastUtil;
import com.sanbafule.sharelock.view.BounceCircle;
import com.sanbafule.sharelock.view.NetWarnBannerView;
import com.sanbafule.sharelock.view.RoundNumber;
import com.sanbafule.sharelock.view.WrapRecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;


/**
 *
 */
public class ConversationFragment extends Fragment implements

        ReceiveMessageCallback,
        View.OnClickListener {

    public WrapRecyclerView recyclerView;
    private List<Conversation> conversationList;


    private ConversationAdapter adapter;
    private LinearLayoutManager layoutManager;
    private LinearLayout view;
    private ProgressBar progressBar;
    private TextView textView;
    private ImageView imageView_net, imageView_next;
    private View layout;
    private BounceCircle circle;
    private View searhLayout;

    // 真正的会话页面
    private FrameLayout conversationlayout;
    // 未登录的遮罩页面
    private LinearLayout unLoginCoverLayuot;

    // 登录按钮
    private Button mButton_Login;
    // 注册按钮
    private Button mButton_Register;
    //网络状态
    private NetWarnBannerView mBannerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_conversion, container, false);
        return layout;
    }

    public void connect() {
        ReceiveMessageHelp.getInstance(getActivity()).setReceiveMessageCallback(this);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        conversationlayout = (FrameLayout) layout.findViewById(R.id.conversation_list_layout);
        unLoginCoverLayuot = (LinearLayout) layout.findViewById(R.id.conversion_cover_layout);
        mButton_Login = (Button) unLoginCoverLayuot.findViewById(R.id.login_bt);
        mButton_Register = (Button) unLoginCoverLayuot.findViewById(R.id.register_bt);
        recyclerView = (WrapRecyclerView) layout.findViewById(R.id.conversion_list);
        view = (LinearLayout) layout.findViewById(R.id.net);
        progressBar = (ProgressBar) view.findViewById(R.id.net_isneting);
        textView = (TextView) view.findViewById(R.id.net_text);
        imageView_net = (ImageView) view.findViewById(R.id.net_notnet);
        imageView_next = (ImageView) view.findViewById(R.id.net_next);

    }

    @Override
    public void onResume() {
        super.onResume();
        circle = ((MainActivity) getActivity()).getCircle();
        if (ShareLockManager.getInstance().isLogin()) {
            unLoginCoverLayuot.setVisibility(View.GONE);
            conversationlayout.setVisibility(View.VISIBLE);
            connect();
            initData();
        } else {
            unLoginCoverLayuot.setVisibility(View.VISIBLE);
            conversationlayout.setVisibility(View.GONE);
            mButton_Login.setOnClickListener(this);
            mButton_Register.setOnClickListener(this);
        }
    }


    public void initData() {

        RongIMClient.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                if (conversations == null) {
                    conversationList = new ArrayList<>();
                } else {
                    conversationList = conversations;
                }
                setData();
                updateConnectState();
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                LogUtil.i("conversion is null" + errorCode);
            }
        });

    }


    private void setData() {
        if (adapter == null) {
            layoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layoutManager);
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.head_search_net_layout, recyclerView, false);
            searhLayout = view.findViewById(R.id.searchLayout);
            mBannerView = (NetWarnBannerView) view.findViewById(R.id.net_warn);
            searhLayout.setOnClickListener(this);
            recyclerView.addHeaderView(view);
            adapter = new ConversationAdapter((MainActivity) getActivity(), conversationList, recyclerView);
            adapter.setDragListener(new RoundNumberDragListener() {
                @Override
                public void onDown(RoundNumber view) {
                    int[] position = new int[2];
                    view.getLocationOnScreen(position);

                    int radius = view.getWidth() / 2;
                    circle.down(radius, position[0] + radius, position[1] - DensityUtil.getTopBarHeight(getActivity()) + radius, view.getMessage());
                    circle.setVisibility(View.VISIBLE); // 显示全屏范围的BounceCircle
                    view.setVisibility(View.INVISIBLE); // 隐藏固定范围的RoundNumber
                    circle.setOrginView(view);
                }

                @Override
                public void onMove(float curX, float curY) {
                    circle.move(curX, curY);
                }

                @Override
                public void onUp(final Conversation conversation, String name) {

                    RongIMClient.getInstance().clearMessagesUnreadStatus(conversation.getConversationType(), name, new RongIMClient.ResultCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean aBoolean) {
                            conversation.setUnreadMessageCount(0);
                            if (aBoolean) {
                                circle.up();
                            }
                            //在滑动会话小红点后 更新tab上的未读消息数
                            ((MainActivity) getActivity()).setUnReadCount();
//
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {

                        }
                    });


                }
            });
        } else {
            adapter.refreshData(conversationList);
        }
        recyclerView.setAdapter(adapter);


    }

    /***
     * 更新连接状态
     */
    public void updateConnectState() {
        if (!isAdded() || mBannerView == null) {
            return;
        }
        RongIMClient.ConnectionStatusListener.ConnectionStatus status = RongIMClient.getInstance().getCurrentConnectionStatus();
        if (status == RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTING) {
            mBannerView.setNetWarnText(getString(R.string.connecting_server));
            mBannerView.reconnect(true);
        } else if (status == RongIMClient.ConnectionStatusListener.ConnectionStatus.SERVER_INVALID) {
            mBannerView.setNetWarnText(getString(R.string.connect_server_error));
            mBannerView.reconnect(false);
        } else if (status == RongIMClient.ConnectionStatusListener.ConnectionStatus.NETWORK_UNAVAILABLE) {
            mBannerView.setNetWarnText(getString(R.string.net_not_use));
            mBannerView.reconnect(false);
        } else if (status == RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED) {
            mBannerView.hideWarnBannerView();
        }
    }

    public ConversationAdapter getAdapter() {
        return adapter;
    }


    public void onItemClick(View view, final int position, final RoundNumber dotView, String name) {
        final RecyclerWrapAdapter adapter = (RecyclerWrapAdapter) recyclerView.getAdapter();
        if (position > conversationList.size()) return;
        ToastUtil.showMessage(name + "::::" + position);
        final Conversation conversation = getAdapter().getItem(position - adapter.getHeadersCount());
        switch (view.getId()) {
            case R.id.top:
                // 普通消息的置顶
                LogUtil.i(name);
                //置顶
                RongIMClient.getInstance().setConversationToTop(conversation.getConversationType(), name, !conversation.isTop(), new RongIMClient.ResultCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if (aBoolean) {
//                            conversationList.remove(position - adapter.getHeadersCount());
//                            conversationList.add(0, conversation);
//                            adapter.notifyItemMoved(position, 1);
                            initData();
                        }
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {

                    }
                });
                break;
            case R.id.read:
                // 普通消息的标记已读
                RongIMClient.getInstance().clearMessagesUnreadStatus(conversation.getConversationType(), name, new RongIMClient.ResultCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        conversation.setUnreadMessageCount(0);
                        if (aBoolean && dotView != null && dotView.getVisibility() == View.VISIBLE) {
                            dotView.setVisibility(View.GONE);
                            adapter.notifyItemChanged(position);
                            ((MainActivity) getActivity()).setUnReadCount();
                        }
                        adapter.notifyItemChanged(position);
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {

                    }
                });

                break;
            case R.id.delete:
                // 普通消息的删除
                RongIMClient.getInstance().removeConversation(conversation.getConversationType(), name, new RongIMClient.ResultCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if (aBoolean) {
                            conversationList.remove(position - adapter.getHeadersCount());
                            adapter.notifyItemRemoved(position);
                        }

                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {

                    }
                });
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 跳转到登录界面
            case R.id.login_bt:
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.putExtra("CurrentPosition", 3);
                getActivity().startActivity(intent);
                break;
            // 跳转到注册界面
            case R.id.register_bt:

                break;
            case R.id.searchLayout:
                Intent intent1 = new Intent(getActivity(), ConversationSearchActivity.class);
//                intent1.putExtra(SString.TYPE,3);
//                intent1.putExtra("JJK",getActivity().getString(R.string.search_group_hint));
                getActivity().startActivity(intent1);
                break;
        }
    }


    @Override
    public void receiveMessage(Message message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initData();
                //更新导航栏的未读数
                ((MainActivity) getActivity()).setUnReadCount();
            }
        });

    }
}