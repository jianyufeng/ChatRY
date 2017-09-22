package com.sanbafule.sharelock.UI.group;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.UI.base.BaseActivity;
import com.sanbafule.sharelock.business.GroupMemberService;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.modle.BanMember;
import com.sanbafule.sharelock.util.ToastUtil;
import com.sanbafule.sharelock.view.customRecycleView.ExpandRecyclerView;
import com.sanbafule.sharelock.view.customRecycleView.RecycleViewBaseAdapter;
import com.sanbafule.sharelock.view.customRecycleView.RecycleViewBaseHolder;
import com.sanbafule.sharelock.view.customRecycleView.RecycleViewItemClickListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 作者:Created by 简玉锋 on 2016/12/1 17:40
 * 邮箱: jianyufeng@38.hn
 * 群组成员的禁言时间选项
 */
public class GroupMemberBanActivity extends BaseActivity implements View.OnClickListener {
    private ExpandRecyclerView timesSelect; //选择禁言时间
    private Button confirm;//确定禁言
    private Context mContext;
    private List banTimes;
    private int selected = -1;
    private RecycleViewBaseAdapter<String, RecycleViewBaseHolder> adapter;
    private String groupId;
    private String account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        //设置导航栏内容
        getToolBarHelper().setToolBarStates(R.drawable.topbar_back_bt, getString(R.string.group_member_ban_time), null, -1, this);
        //获取群组id 和成员的账号
        groupId = getIntent().getStringExtra(SString.GROUP_ID);
        account = getIntent().getStringExtra(SString.GROUP_MEMBER_ACCOUNT);
        if (TextUtils.isEmpty(groupId) || TextUtils.isEmpty(account)) {
            finish();
            return;
        }
        initData();
        initView();
        asynData();
        logicControlView();

    }

    protected void initData() {
        GroupMemberService.checkBanMenmbers(groupId, new GroupMemberService.Callback<ArrayList<BanMember>>() {
            @Override
            public void success(ArrayList<BanMember> banMembers) {
                if (banMembers != null) {
                    //查看的人是否被禁言
                    for (BanMember banMember : banMembers) {
                        if (banMember.getUserId().equals(account)) {
                            getToolBarHelper().setToolBarStates(R.drawable.topbar_back_bt,
                                    getString(R.string.group_member_ban_time),
                                    getString(R.string.group_member_cancel_ban), -1, GroupMemberBanActivity.this);
                            break;
                        }
                    }
                }
            }

            @Override
            public void fail() {

            }
        });

    }


    protected void initView() {
        timesSelect = (ExpandRecyclerView) findViewById(R.id.times); //设置群组成员详情
        confirm = (Button) findViewById(R.id.member_ban_confirm); //设置群组成员详情
        confirm.setOnClickListener(this);
    }


    protected void asynData() {


    }


    protected void logicControlView() {
        //设置管理器
        LinearLayoutManager manager = new LinearLayoutManager(this);
        timesSelect.setLayoutManager(manager);
        //数据
        String[] stringArray = getResources().getStringArray(R.array.member_ban);
        banTimes = Arrays.asList(stringArray);
        adapter = new RecycleViewBaseAdapter<String, RecycleViewBaseHolder>(this, banTimes) {
            @Override
            public RecycleViewBaseHolder createCustomViewHolder(ViewGroup parent, int viewType) {
                return new RecycleViewBaseHolder(parent, R.layout.item_member_ban_time);
            }

            @Override
            public void bindCustomViewHolder(RecycleViewBaseHolder holder, int position) {
                String item = getItem(position);
                if (TextUtils.isEmpty(item)) {
                    return;
                }
                holder.setText(R.id.ban_time, item);
                ((CheckBox) holder.getView(R.id.select)).setChecked(position == selected);
            }
        };
        timesSelect.setAdapter(adapter);
        //设置禁言时间
        timesSelect.addOnItemTouchListener(new RecycleViewItemClickListener(timesSelect, new RecycleViewItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                selected = position;
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        }));

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_group_member_ban;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left: //回退按钮事件
                hideSoftKeyboard();
                finish();
                break;
            case R.id.member_ban_confirm: //确定禁言提交
                Long banTimes = getBanTimes();
                if (banTimes < 0) {
                    ToastUtil.showMessage(getString(R.string.group_member_ban_tip));
                    return;
                }
                showDiglog();
                GroupMemberService.setGroupMenmberBan(groupId, account, banTimes, new GroupMemberService.Callback<String>() {
                    @Override
                    public void success(String s) {
                        closeDialog();
                        ToastUtil.showMessage(getString(R.string.group_member_ban_success));
                        finish();
                    }

                    @Override
                    public void fail() {
                        closeDialog();
                        ToastUtil.showMessage(getString(R.string.group_member_ban_fail));

                    }
                });
                break;
            case R.id.right_text: //解除禁言
                showDiglog();
                GroupMemberService.cancelGroupMenmberBan(groupId, account, new GroupMemberService.Callback<String>() {
                    @Override
                    public void success(String s) {
                        closeDialog();
                        finish();
                        ToastUtil.showMessage(getString(R.string.group_member_cancel_ban_success));

                    }

                    @Override
                    public void fail() {
                        closeDialog();
                        ToastUtil.showMessage(getString(R.string.group_member_cancel_ban_fail));
                    }
                });

                break;
        }
    }

    /*  <item>10分钟</item>
      <item>1小时</item>
      <item>5小时</item>
      <item>12小时</item>
      <item>1天</item>*/
    private Long getBanTimes() {
        Long time = -1L;
        switch (selected) {
            case 0:
                time = 10L;
                break;
            case 1:
                time = 1L * 60;
                break;
            case 2:
                time = 5 * 60L;
                break;
            case 3:
                time = 12 * 60L;
                break;
            case 4:
                time = 24 * 60L;
                break;
            default:
                break;
        }
        return time;
    }
}
