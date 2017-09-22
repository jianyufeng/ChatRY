package com.sanbafule.sharelock;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.sanbafule.sharelock.UI.account.LoginActivity;
import com.sanbafule.sharelock.UI.base.BaseActivity;
import com.sanbafule.sharelock.UI.contact.ContactListActivity;
import com.sanbafule.sharelock.UI.fragment.CompanyIntroduceFragment;
import com.sanbafule.sharelock.UI.fragment.ConversationFragment;
import com.sanbafule.sharelock.UI.fragment.InformationFragment;
import com.sanbafule.sharelock.UI.fragment.ProductShowFragment;
import com.sanbafule.sharelock.UI.fragment.UserInfoFragment;
import com.sanbafule.sharelock.chatting.Interface.ReceiveMessageCallback;
import com.sanbafule.sharelock.chatting.help.ReceiveMessageHelp;
import com.sanbafule.sharelock.chatting.util.MessageNotificationManager;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.comm.help.ShareLockManager;
import com.sanbafule.sharelock.db.ContactObserve;
import com.sanbafule.sharelock.db.ContactSqlManager;
import com.sanbafule.sharelock.db.ContactsCache;
import com.sanbafule.sharelock.global.MyString;
import com.sanbafule.sharelock.global.ReceiveAction;
import com.sanbafule.sharelock.util.CommonUtil;
import com.sanbafule.sharelock.util.DensityUtil;
import com.sanbafule.sharelock.util.DialogUtils;
import com.sanbafule.sharelock.util.LogUtil;
import com.sanbafule.sharelock.util.ToastUtil;
import com.sanbafule.sharelock.view.BottomTab.MainNavigateTabBar;
import com.sanbafule.sharelock.view.BounceCircle;
import com.sanbafule.sharelock.view.RoundNumber;

import java.util.List;

import butterknife.Bind;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;


/**
 * 2016-10-8
 * ShareLock
 * 主页面
 */
public class MainActivity extends BaseActivity implements View.OnClickListener, ReceiveMessageCallback {


    private static final String TAG1 = "资讯";
    private static final String TAG2 = "介绍";
    private static final String TAG3 = "产品";
    private static final String TAG4 = "好友";
    private static final String TAG5 = "我的";
    private static final String[] TAGS = new String[]{TAG1, TAG2, TAG3, TAG4, TAG5};

    private static final String TAG = "MainActivity";
    @Bind(R.id.main_container)
    FrameLayout mainContainer;


    @Bind(R.id.mainTabBar)
    MainNavigateTabBar mNavigateTabBar;
    @Bind(R.id.circle)
    public BounceCircle circle;
    private View view;
    private Toolbar mToolbar;
    //顶部title
    private TextView topTitle;
    private View addImg;
    private View searchImg;
    private View friendsImg;
    private TextView newFriendDot;
    // 小红点
    private RoundNumber roundNumberView;
    private int mCurrentPosition;
    // 是否响应按键事件，如果一个气泡已经在响应，其它气泡就不响应，同一界面始终最多只有一个气泡响应按键
    public static boolean isTouchable = true;


    public BounceCircle getCircle() {
        return circle;
    }

    public MainNavigateTabBar getNavigateTabBar() {
        return mNavigateTabBar;
    }

    /***
     * IM 的准备工作
     */
    private void plan() {
        //注册消息接收者
        ReceiveMessageHelp.getInstance(this).setReceiveMessageCallback(this);

        // 注册联系人观察者
        ContactSqlManager.registerMsgObserver(ContactObserve.getInstance());
        // 异步获取联系人缓存
        ContactsCache.getInstance().load();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTopBar();
        mNavigateTabBar = (MainNavigateTabBar) findViewById(R.id.mainTabBar);
        mNavigateTabBar.onRestoreInstanceState(savedInstanceState);
        mNavigateTabBar.addTab(InformationFragment.class, new MainNavigateTabBar.TabParam(R.drawable.android_news_bottom, R.drawable.android_news_bottom_select, TAG1));
        mNavigateTabBar.addTab(CompanyIntroduceFragment.class, new MainNavigateTabBar.TabParam(R.drawable.android_introduce_bottom, R.drawable.android_introduce_bottom_select, TAG2));
        mNavigateTabBar.addTab(ProductShowFragment.class, new MainNavigateTabBar.TabParam(R.drawable.android_product_bottom, R.drawable.android_product_bottom_select, TAG3));
        mNavigateTabBar.addTab(ConversationFragment.class, new MainNavigateTabBar.TabParam(R.drawable.android_friends, R.drawable.android_friends_select, TAG4));
        mNavigateTabBar.addTab(UserInfoFragment.class, new MainNavigateTabBar.TabParam(R.drawable.android_my, R.drawable.android_my_select, TAG5));
        mNavigateTabBar.setTabSelectListener(new MainNavigateTabBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(MainNavigateTabBar.ViewHolder holder) {
                if (holder != null) {
                    int position = CommonUtil.indexOfArr(TAGS, holder.tag);
                    if (position > 2 && !ShareLockManager.getInstance().isLogin()) {
                        setRightImgVisable(false, false, false);
                    } else {
                        setRightHeadImg(position);
                    }
                    setTopBarTitle(position);
                    //更新当前的Tab位置
                    mCurrentPosition = position;
                    //设置会话界面的红点是否可以拖动
                    setUnredDotDrag(mCurrentPosition);
                }

            }

        });

        if (ShareLockManager.getInstance().isLogin()) {
            /***
             * 检测是否需要登录
             */
            plan();
        }


        circle.setFinishListener(new BounceCircle.FinishListener() {
            @Override
            public void onFinish(View view) {
                if (view != null && view == roundNumberView) {
                    //通知会话清除每条item的未读数- 爆炸
                    RongIMClient.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
                        @Override
                        public void onSuccess(final List<Conversation> c) {
                            if (c != null) {
                                for (int i = 0; i < c.size(); i++) {
                                    Conversation cc = c.get(i);
                                    final int j = i;
                                    RongIMClient.getInstance().clearMessagesUnreadStatus(cc.getConversationType(), cc.getTargetId()
                                            , new RongIMClient.ResultCallback<Boolean>() {
                                                @Override
                                                public void onSuccess(Boolean aBoolean) {
                                                    if (j == c.size() - 1) {
                                                        if (aBoolean) {
                                                            Fragment fragment = mNavigateTabBar.getFragment(TAG4);
                                                            if (fragment != null && fragment instanceof ConversationFragment) {
                                                                ((ConversationFragment) fragment).initData();

                                                            }
                                                        }
                                                    }

                                                }

                                                @Override
                                                public void onError(RongIMClient.ErrorCode errorCode) {

                                                }
                                            });
                                }
                            }
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {

                        }
                    });
                } else {
                    ToastUtil.showMessage("炸了");
                }


            }
        });
        //注册网络监听器
        registerReceiver(new String[]{ReceiveAction.ACTION_CONNECT_STATE});
    }

    //设置会话界面的红点是否可以拖动
    private void setUnredDotDrag(int pos) {
        roundNumberView = mNavigateTabBar.getNubDot(3);
        if (roundNumberView != null) {
            roundNumberView.setDrag(pos == 3);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //更新导航栏的未读数
        if (ShareLockManager.getInstance().isLogin()) {
            setUnReadCount();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null && MyString.hasData(intent.getAction())) {
            // 从登录界面跳转过来的
            if (intent.getAction().equals(SString.LOGINACTION)) {
                plan();
                mCurrentPosition = intent.getIntExtra("CurrentPosition", 0);
                if (TAGS[mCurrentPosition] != null) {
                    mNavigateTabBar.showFragment(TAGS[mCurrentPosition], mCurrentPosition);
                    setTopBarTitle(mCurrentPosition);
                    setRightHeadImg(mCurrentPosition);
                }

            } else {

            }


        }

    }

    /**
     * 初始化导航条
     */
    private void initTopBar() {

        view = findViewById(R.id.main_top_bar);
        mToolbar = (Toolbar) view.findViewById(R.id.id_tool_bar);
        // 顶部ActionBar设置监听
        addImg = findViewById(R.id.actionBar_menu_container);
        addImg.setOnClickListener(this);
        searchImg = findViewById(R.id.actionBar_search_container);
        searchImg.setOnClickListener(this);
        friendsImg = findViewById(R.id.actionBar_friends_container);
        newFriendDot = (TextView) findViewById(R.id.newfriend_myunreadtext);
        friendsImg.setOnClickListener(this);

        //根据提供的子Fragment index 切换到对应的页面
        // 顶部title设置监听
        topTitle = (TextView) findViewById(R.id.tv_middle_title);
        setSupportActionBar(mToolbar);
        setTopBarTitle(0);
        setRightImgVisable(false, true, false);
        setUnredDotDrag(0);
    }

    private void setTopBarTitle(int position) {
        topTitle.setText(SString.TITLE[position]);
    }

    //默认搜索新闻资讯
    public int searchType = 4;
    public String searchTitle = "搜索新闻资讯";

    public void setType(int type, String title) {
        searchType = type;
        searchTitle = title;
    }

    private void setRightHeadImg(int position) {
        switch (position) {
            case 0:
                if (InformationFragment.mInstance != null) {
//                    InformationFragment.mInstance.setRightHeadImg();
                } else {
                    //设置默认
                    setRightImgVisable(false, true, false);
                }
                break;
            case 1:
                setRightImgVisable(false, false, false);
                break;
            case 2:
                setType(6, "搜索产品");
                setRightImgVisable(false, true, false);
                break;
            case 3:
                setRightImgVisable(true, false, true);
                break;
            case 4:
                setRightImgVisable(true, false, true);
                break;
            default:
                setRightImgVisable(false, false, false);
                break;
        }
    }

    public void setRightImgVisable(boolean add, boolean search, boolean friends) {
        //显示加号和朋友
        if (add && friends) {
            if (addImg.getVisibility() == View.GONE) {
                addImg.setVisibility(View.VISIBLE);
            }
            if (searchImg.getVisibility() == View.VISIBLE) {
                searchImg.setVisibility(View.GONE);
            }
            if (friendsImg.getVisibility() == View.GONE) {
                friendsImg.setVisibility(View.VISIBLE);
            }
        }
        //只显示搜索
        if (search) {
            if (searchImg.getVisibility() == View.GONE) {
                searchImg.setVisibility(View.VISIBLE);
            }
            if (addImg.getVisibility() == View.VISIBLE) {
                addImg.setVisibility(View.GONE);
            }
            if (friendsImg.getVisibility() == View.VISIBLE) {
                friendsImg.setVisibility(View.GONE);
            }
        }
        //全部隐藏
        if (!add && !search && !friends) {
            if (addImg.getVisibility() == View.VISIBLE) {
                addImg.setVisibility(View.GONE);
            }
            if (searchImg.getVisibility() == View.VISIBLE) {
                searchImg.setVisibility(View.GONE);
            }
            if (friendsImg.getVisibility() == View.VISIBLE) {
                friendsImg.setVisibility(View.GONE);
            }
        }

    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mNavigateTabBar.onSaveInstanceState(outState);
    }


    public void setUnReadCount() {
        RongIMClient.getInstance().getTotalUnreadCount(new RongIMClient.ResultCallback<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                int totalUnreadCount = integer;
                mNavigateTabBar.updateMainTabUnread(totalUnreadCount, 3);
                setUnredDotDrag(mCurrentPosition);
                // 小红点的拖动
                roundNumberView.setClickListener(new RoundNumber.ClickListener() {

                    @Override
                    public void onDown() {
                        int[] position = new int[2];
                        roundNumberView.getLocationOnScreen(position);

                        int radius = roundNumberView.getWidth() / 2;
                        circle.down(radius, position[0] + radius, position[1] - DensityUtil.getTopBarHeight(MainActivity.this) + radius, roundNumberView.getMessage());

                        circle.setVisibility(View.VISIBLE); // 显示全屏范围的BounceCircle

                        roundNumberView.setVisibility(View.INVISIBLE); // 隐藏固定范围的RoundNumber
                        circle.setOrginView(roundNumberView);
                    }

                    @Override
                    public void onMove(float curX, float curY) {
                        circle.move(curX, curY);
                    }

                    @Override
                    public void onUp() {
                        //先获取会话  在清除未读数
                        circle.up();
                    }
                });
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                LogUtil.i("Error" + errorCode);
            }
        });

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.actionBar_friends_container:
                startActivity(new Intent(this, ContactListActivity.class));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ContactSqlManager.unregisterMsgObserver(ContactObserve.getInstance());
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }


    @Override
    public void receiveMessage(Message message) {
        //更新导航栏的未读数
        setUnReadCount();
    }

    @Override
    public boolean isNeedToolBar() {
        return false;
    }

    @Override
    protected void handleReceiver(Context context, Intent intent) {
        //登出的广播
        if (intent != null && intent.getAction().equals(ReceiveAction.ACTION_KICK_OFF)) {
            //弹出踢出的对话框
            showKickOffDiaLog();

        } else if (intent.getAction().equals(ReceiveAction.ACTION_CONNECT_STATE)) {
            //融云连接状态发生变化
            Fragment fragment = mNavigateTabBar.getFragment(TAG4);
            if (fragment != null && fragment instanceof ConversationFragment) {
                ((ConversationFragment) fragment).updateConnectState();
            }
        }
    }

    //登出的对话框
    public void showKickOffDiaLog() {
        DialogUtils.showBasicWithTile(this, getString(R.string.dialog_hint_title), "您的账号已在异地登录，请重新登录", R.string.positive, -1, false, new DialogUtils.DialogInterface() {
            @Override
            public void onPositiveClickListener(@NonNull MaterialDialog dialog) {
                //隐藏小红点
                roundNumberView.setVisibility(View.GONE);
                //取消所有的通知
                MessageNotificationManager.getInstance().cancelCCPNotificationAll();
                dialog.dismiss();
                ShareLockManager.getInstance().logout();
                //重新登录
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }

            @Override
            public void onNegativeClickListener(@NonNull MaterialDialog dialog) {
                dialog.dismiss();
            }
        });
    }

    //程序后台运行
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}

