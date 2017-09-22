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
import com.sanbafule.sharelock.db.GroupInfoSqlManager;
import com.sanbafule.sharelock.global.Url;
import com.sanbafule.sharelock.modle.ContactInfo;
import com.sanbafule.sharelock.modle.GroupInfo;
import com.sanbafule.sharelock.modle.RequestJoin;
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
 * 申请加入相关
 * 申请加入群组的记录
 * 包括  1 自己申请加入其他人的群组   2 其他人申请加入自己的群组
 * 所以 要根据被申请人分类显示。
 */

public class RequestJoinActivity extends BaseActivity implements View.OnClickListener {
    @Override
    public int getLayoutId() {
        return R.layout.activity_group_request_list;
    }

    //请求加入群组的列表容器
    private ExpandRecyclerView groupRequestsContainer;
    //空view
    private View emptyView;
    //适配器
    private RecycleViewBaseAdapter<RequestJoin, RecycleViewBaseHolder> adapter;
    //请求加入数据
    private ArrayList<RequestJoin> groupJoinRequests;
    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        getToolBarHelper().setToolBarStates(R.drawable.topbar_back_bt, getString(R.string.request_join_title), null, -1, this);

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
        adapter.fillList(groupJoinRequests);
    }


    public void initData() {

    }

    public void initView() {
        groupRequestsContainer = (ExpandRecyclerView) findViewById(R.id.recycle_view_id);
        emptyView = findViewById(R.id.id_empty_view);
    }

    public void asynData() {
        //异步数据
        // 1 主线程回调
        GroupNoticeSetvice.getJoinRequestList(new GroupNoticeSetvice.Callback<ArrayList<RequestJoin>>() {
            @Override
            public void success(ArrayList<RequestJoin> r) {
                if (r == null) {
                    return;
                }
                groupJoinRequests = r;  //保存数据
                Collections.reverse(groupJoinRequests);
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
        groupRequestsContainer.setLayoutManager(manager);
        //设置空View
        groupRequestsContainer.setEmptyView(emptyView);
        //设置分割线
        groupRequestsContainer.addItemDecoration(new RecycleViewLinearDivider(this, LinearLayoutManager.VERTICAL));    //创建群组列表的适配器
        //创建群组列表的适配器
        adapter = new RecycleViewBaseAdapter<RequestJoin, RecycleViewBaseHolder>(this) {
            @Override
            public RecycleViewBaseHolder createCustomViewHolder(ViewGroup parent, int viewType) {
                return new RecycleViewBaseHolder(parent, R.layout.item_request_join);
            }

            @Override
            public void bindCustomViewHolder(RecycleViewBaseHolder holder, int position) {
                final RequestJoin item = getItem(position);
                if (item == null) {
                    return;
                }
                //审核人
                String gal_reviewers = item.getGal_reviewers();//获取审核人
                String gal_applicant = item.getGal_applicant(); //申请请人
/**
 * 判断审核人是不是自己
 * 1 如果是自己 则是别人申请加入我的群组
 *      （1）未处理显示是：别人申请加入我的群组     同意  或 拒绝   按钮
 *      （2）处理后显示是：别人申请加入我的群组   已同意 / 已拒绝   结果
 * 2 如果是别人  则是我申请加入别人的群组
 *      （1）未处理显示是：自己申请加入别人的群组     待处理
 *      （2）处理后显示是：别人同意/拒绝了您的加群申请
 *
 *
 *
 *
 */
                String url = null;
                //审核人是自己
                if (gal_reviewers.equals(ShareLockManager.getInstance().getUserName())) {
                    //获取申请人的昵称及图像   //一般 本地数据库没有  需要网络获取
                    ContactInfo contactInfo = ContactInfoSqlManager.getContactInfoFormuserName(gal_applicant);

                    //申请人的图像
                    if (contactInfo != null) {
                        //昵称
                        if (!TextUtils.isEmpty(contactInfo.getU_nickname())) {
                            gal_applicant = contactInfo.getU_nickname();
                        }
                        url = contactInfo.getU_header();
                    }
                    holder.setText(R.id.applicant_name, gal_applicant);
                    String gal_gname = item.getGal_gname();//申请的群组名称
                    if (TextUtils.isEmpty(gal_gname)) {
                        gal_gname = item.getGal_gid();  //群组id
                    }
                    //设置数据
                    holder.setText(R.id.request_content, getString(R.string.request_join_g_name, gal_gname));
                    //图像 显示申请人的图像
                    if (!TextUtils.isEmpty(url) && !url.startsWith(Url.CORE_IP)) {
                        url = Url.CORE_IP + url;
                    }
                    holder.setImageResource(R.id.icon, url);

                    //显示处理的结果
                    int gal_status = item.getGal_status();//操作状态 0-未处理 1-未同意 2-已同意
                    switch (gal_status) {
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
                            holder.setText(R.id.select_result, gal_status == 1 ? getString(R.string.rejected) : getString(R.string.agreed));
                            break;
                        default:
                            break;
                    }

                } else {//审核人是别人

                    //自己申请加入群组 **  待审核

                    //群组详情
                    GroupInfo groupInfo = GroupInfoSqlManager.getGroupInfo(item.getGal_gid());
                    //获取审核人的详情
                    ContactInfo contactInfo = ContactInfoSqlManager.getContactInfoFormuserName(gal_reviewers);
                    if (contactInfo != null) {
                        //获取审核人的昵称
                        if (!TextUtils.isEmpty(contactInfo.getU_nickname())) {
                            gal_reviewers = contactInfo.getU_nickname();
                        }
                    }
                    if (groupInfo != null) { //获取群图像
                        url = groupInfo.getG_header();
                    }

                    String gal_gname = item.getGal_gname();//申请的群组名称
                    if (TextUtils.isEmpty(gal_gname)) {
                        gal_gname = item.getGal_gid();  //群组id
                    }

                    //设置数据
                    holder.setText(R.id.request_content, getString(R.string.request_join_g_name_my, gal_gname));
                    //图像应该显示群组的图像    //处理人是谁？？？？？？？？？？？？？？？？？？？？？？
                    if (!TextUtils.isEmpty(url) && !url.startsWith(Url.CORE_IP)) {
                        url = Url.CORE_IP + url;
                    }
                    holder.setImageResource(R.id.icon, url);

                    //显示处理的结果
                    int gal_status = item.getGal_status();//操作状态 0-未处理 1-未同意 2-已同意
                    switch (gal_status) {
                        case 0: //未处理
                            //群组申请
                            holder.setText(R.id.applicant_name, getString(R.string.request_join_title));
                            holder.setVisibility(R.id.select_reply, View.GONE);
                            holder.setVisibility(R.id.select_result, View.VISIBLE);
                            holder.setText(R.id.select_result, getString(R.string.request_join_waiting));
                            break;
                        case 1: //拒绝
                        case 2: //同意
                            //显示审核人的名称
                            holder.setText(R.id.applicant_name, gal_gname);
                            holder.setVisibility(R.id.select_reply, View.GONE);
                            holder.setVisibility(R.id.select_result, View.GONE);
                            holder.setText(R.id.request_content, gal_status == 1 ?
                                    getString(R.string.request_result_reject, gal_reviewers)
                                    : getString(R.string.request_result_agree, gal_reviewers));
                            break;
                        default:
                            break;
                    }

                }


            }
        };
        //设置适配器
        groupRequestsContainer.setAdapter(adapter);
    }

    // //回复申请
    public void replyRequest(final boolean isAgree, final RequestJoin request) {
        if (TextUtils.isEmpty(request.getGal_id())) {
            return;
        }
        showDiglog();
        GroupMemberService.replyPropose(isAgree, request.getGal_id(), new GroupMemberService.Callback<String>() {
            @Override
            public void success(String s) {
                closeDialog();
                request.setGal_status(isAgree ? 2 : 1);
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
