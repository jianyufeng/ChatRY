package com.sanbafule.sharelock.UI.groupNotice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.UI.base.BaseActivity;
import com.sanbafule.sharelock.business.GroupMemberService;
import com.sanbafule.sharelock.business.GroupNoticeSetvice;
import com.sanbafule.sharelock.comm.help.ShareLockManager;
import com.sanbafule.sharelock.db.ContactInfoSqlManager;
import com.sanbafule.sharelock.global.Url;
import com.sanbafule.sharelock.modle.ContactInfo;
import com.sanbafule.sharelock.modle.InviteJoin;
import com.sanbafule.sharelock.util.ToastUtil;
import com.sanbafule.sharelock.view.customRecycleView.ExpandRecyclerView;
import com.sanbafule.sharelock.view.customRecycleView.RecycleViewBaseAdapter;
import com.sanbafule.sharelock.view.customRecycleView.RecycleViewBaseHolder;
import com.sanbafule.sharelock.view.customRecycleView.RecycleViewLinearDivider;

import java.util.ArrayList;
import java.util.Collections;

/**
 * 作者:Created by 简玉锋 on 2016/12/19 14:19
 * 邮箱: jianyufeng@38.hn
 * 邀请加入相关
 * 邀请加入群组的记录
 * 包括  1 自己邀请其他人   2 其他人邀请自己
 * 所以 要根据邀请人分类显示。
 */

public class InviteJoinActivity extends BaseActivity implements View.OnClickListener {
    @Override
    public int getLayoutId() {
        return R.layout.activity_group_request_list;
    }

    //邀请的列表容器
    private ExpandRecyclerView groupInviteContainer;
    //空view
    private View emptyView;
    //适配器
    private RecycleViewBaseAdapter<InviteJoin, RecycleViewBaseHolder> adapter;
    //邀请加入数据
    private ArrayList<InviteJoin> groupInviteRequests;
    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        getToolBarHelper().setToolBarStates(R.drawable.topbar_back_bt, getString(R.string.invite_join_title), null, -1, this);

        initData();
        initView();
        asynData();
        logicControlView();
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    //群组列表界面需要刷新的控件可以在此进行添加
    private void refreshData() {
        if (adapter == null) {
            return;
        }
        //添加数据并更新
        adapter.fillList(groupInviteRequests);
    }


    public void initData() {

    }

    public void initView() {
        groupInviteContainer = (ExpandRecyclerView) findViewById(R.id.recycle_view_id);
        emptyView = findViewById(R.id.id_empty_view);
    }

    public void asynData() {
        //异步数据
        // 1 主线程回调
        GroupNoticeSetvice.getInviteRequestList(new GroupNoticeSetvice.Callback<ArrayList<InviteJoin>>() {
            @Override
            public void success(ArrayList<InviteJoin> r) {
                if (r == null) {
                    return;
                }
                groupInviteRequests = r;  //保存数据
                Collections.reverse(groupInviteRequests);
                refreshData(); //刷新数据
            }

            @Override
            public void fail() {

            }
        });

    }


    public void logicControlView() {
        //设置布局管理器
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        groupInviteContainer.setLayoutManager(manager);
        //设置空View
        groupInviteContainer.setEmptyView(emptyView);
        //设置分割线
        groupInviteContainer.addItemDecoration(new RecycleViewLinearDivider(this, LinearLayoutManager.VERTICAL));    //创建群组列表的适配器
        //创建群组列表的适配器
        adapter = new RecycleViewBaseAdapter<InviteJoin, RecycleViewBaseHolder>(this) {
            @Override
            public RecycleViewBaseHolder createCustomViewHolder(ViewGroup parent, int viewType) {
                return new RecycleViewBaseHolder(parent, R.layout.item_invit_join);
            }

            @Override
            public void bindCustomViewHolder(RecycleViewBaseHolder holder, int position) {
                final InviteJoin item = getItem(position);
                if (item == null) {
                    return;
                }
                /**
                 * 判断邀请人是不是自己
                 * 1 如果是自己 则是 我邀请别人加入我的群组
                 *      （1）未处理显示是：您邀请 李四 加入群组   待处理
                 *      （2）处理后显示是：别人同意/拒绝了您的邀请
                 * 2 如果是别人  则是别人邀请我加入别人的群组
                 *      （1）未处理显示是：李四邀请你加入 群组       同意  或 拒绝   按钮
                 *      （2）处理后显示是：李四邀请你加入 群组      已同意 / 已拒绝   结果
                 *
                 */
                //邀请请人的昵称
                String grl_inviter = item.getGrl_inviter();//获取邀请人

                String url = null;
                //如果邀请人是自己
                if (grl_inviter.equals(ShareLockManager.getInstance().getUserName())) {
                    //你邀请 李四 加入群组 **  待审核
                    //获取被邀请人的详情
                    String grl_invitee = item.getGrl_invitee();
                    ContactInfo contactInfo = ContactInfoSqlManager.getContactInfoFormuserName(grl_invitee);
                    if (contactInfo != null) {
                        //获取被邀请人的昵称
                        if (!TextUtils.isEmpty(contactInfo.getU_nickname())) {
                            grl_invitee = contactInfo.getU_nickname();
                        }

                        //获取被邀请人的图像
                        url = contactInfo.getU_header();
                    }

                    String grl_gname = item.getGrl_gname();//邀请的群组名称
                    if (TextUtils.isEmpty(grl_gname)) {
                        grl_gname = item.getGrl_gid();  //群组id
                    }

                    //设置数据
                    holder.setText(R.id.request_content, getString(R.string.invite_join_g_name_my, grl_invitee, grl_gname));
                    //图像应该被邀请人
                    if (!TextUtils.isEmpty(url) && !url.startsWith(Url.CORE_IP)) {
                        url = Url.CORE_IP + url;
                    }
                    holder.setImageResource(R.id.icon, url);
                    //显示处理的结果
                    int gal_status = item.getGrl_status();//操作状态 0-未处理 1-未同意 2-已同意
                    switch (gal_status) {
                        case 0: //未处理
                            //显示邀请操作
                            holder.setText(R.id.applicant_name,getString(R.string.invite_join_title));
                            holder.setVisibility(R.id.select_reply, View.GONE);
                            holder.setVisibility(R.id.select_result, View.VISIBLE);
                            holder.setText(R.id.select_result, getString(R.string.request_join_waiting));
                            break;
                        case 1: //拒绝
                        case 2: //同意
                            //显示被邀请人
                            holder.setText(R.id.applicant_name,grl_invitee);
                            holder.setVisibility(R.id.select_reply, View.GONE);
                            holder.setVisibility(R.id.select_result, View.GONE);
                            holder.setText(R.id.request_content, gal_status == 1 ?
                                    getString(R.string.invite_result_reject, grl_gname)
                                    : getString(R.string.invite_result_agree, grl_gname));
                            break;
                        default:
                            break;
                    }


                } else {   //如果邀请人是别人

                    ContactInfo contactInfo = ContactInfoSqlManager.getContactInfoFormuserName(grl_inviter);

                    if (contactInfo != null) {
                        //获取邀请人的信息
                        if (!TextUtils.isEmpty(contactInfo.getU_nickname())) {
                            grl_inviter = contactInfo.getU_nickname();
                        }
                        url = contactInfo.getU_header();
                    }
                    //邀请人的昵称
                    holder.setText(R.id.applicant_name, grl_inviter);


                    String grl_gname = item.getGrl_gname();//邀请的群组名称
                    if (TextUtils.isEmpty(grl_gname)) {
                        grl_gname = item.getGrl_gid();  //群组id
                    }
                    holder.setText(R.id.request_content, getString(R.string.invite_join_g_name, grl_gname));
                    //图像显示邀请人的图像
                    if (!TextUtils.isEmpty(url) && !url.startsWith(Url.CORE_IP)) {
                        url = Url.CORE_IP + url;
                    }
                    //邀请人的图像
                    holder.setImageResource(R.id.icon, url);

                    //显示处理的结果
                    int grl_status = item.getGrl_status(); //操作状态 0-未处理 1-未同意 2-已同意
                    switch (grl_status) {
                        case 0: //未处理
                            holder.setVisibility(R.id.select_reply, View.VISIBLE);
                            holder.setVisibility(R.id.select_result, View.GONE);
                            //设置处理逻辑
                            //同意
                            holder.setOnClickListener(R.id.agree, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //同意
                                    replyRequest(true, item);
                                }
                            });
                            //拒绝
                            holder.setOnClickListener(R.id.reject, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    replyRequest(false, item);
                                }
                            });
                            break;
                        case 1: //拒绝
                        case 2: //同意
                            holder.setVisibility(R.id.select_reply, View.GONE);
                            holder.setVisibility(R.id.select_result, View.VISIBLE);
                            holder.setText(R.id.select_result, grl_status == 1 ? getString(R.string.rejected) : getString(R.string.agreed));
                            break;
                        default:
                            break;
                    }


                }


            }
        };
        //设置适配器
        groupInviteContainer.setAdapter(adapter);
    }

    // //回复申请
    public void replyRequest(final boolean isAgree, final InviteJoin request) {
        if (TextUtils.isEmpty(request.getGrl_id())) {
            return;
        }
        showDiglog();
        GroupMemberService.replyInvite(isAgree, request.getGrl_id(), new GroupMemberService.Callback<String>() {
            @Override
            public void success(String s) {
                closeDialog();
                request.setGrl_status(isAgree ? 2 : 1);
                adapter.updateItem(request);

            }

            @Override
            public void fail() {
                closeDialog();
                ToastUtil.showMessage(getString(R.string.dismiss_group_fail));
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left:
                hideSoftKeyboard();
                finish();
                break;
        }
    }

    // 广播接收器  接受新的加入群组通知   暂时未做 ？？？？？？？？？？？？
    @Override
    protected void handleReceiver(Context context, Intent intent) {
        super.handleReceiver(context, intent);
    }
}
