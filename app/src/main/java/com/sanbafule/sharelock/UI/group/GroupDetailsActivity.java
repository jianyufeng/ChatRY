package com.sanbafule.sharelock.UI.group;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.SApplication;
import com.sanbafule.sharelock.S_interface.GetContactInfoListener;
import com.sanbafule.sharelock.UI.base.BaseActivity;
import com.sanbafule.sharelock.UI.contact.ContactListSelectActivity;
import com.sanbafule.sharelock.UI.user.UserInfoActivity;
import com.sanbafule.sharelock.business.ContactInfoBiz;
import com.sanbafule.sharelock.business.GroupMemberService;
import com.sanbafule.sharelock.business.GroupService;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.comm.help.ShareLockManager;
import com.sanbafule.sharelock.db.ContactInfoSqlManager;
import com.sanbafule.sharelock.db.GroupInfoSqlManager;
import com.sanbafule.sharelock.db.GroupMemberSqlManager;
import com.sanbafule.sharelock.global.ReceiveAction;
import com.sanbafule.sharelock.global.Url;
import com.sanbafule.sharelock.modle.ContactInfo;
import com.sanbafule.sharelock.modle.GroupInfo;
import com.sanbafule.sharelock.modle.GroupMember;
import com.sanbafule.sharelock.util.DialogUtils;
import com.sanbafule.sharelock.util.ToastUtil;
import com.sanbafule.sharelock.view.SelectPictureView;
import com.sanbafule.sharelock.view.SingleSettingItem;
import com.sanbafule.sharelock.view.customRecycleView.ExpandRecyclerView;
import com.sanbafule.sharelock.view.customRecycleView.RecycleViewBaseAdapter;
import com.sanbafule.sharelock.view.customRecycleView.RecycleViewBaseHolder;
import com.sanbafule.sharelock.view.customRecycleView.RecyclerViewClick;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import io.rong.imlib.model.Conversation;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.utils.ImageCaptureManager;


/**
 * 作者:Created by 简玉锋 on 2016/11/25 15:56
 * 邮箱: jianyufeng@38.hn
 * 群组详情
 * <p>
 * 此界面的groupId 是没加前缀的
 */

public class GroupDetailsActivity extends BaseActivity implements View.OnClickListener {
    private Context mContext;
    //群组成员
    private ExpandRecyclerView membersContainer;
    //群图像
    private SingleSettingItem groupIconItem;
    //群组名称
    private SingleSettingItem groupNameItem;
    //群组公告
    private SingleSettingItem singleSettingItem;
    //群组权限
    private SingleSettingItem groupPermission;
    //群组免打扰
    private SingleSettingItem groupMsgNotice;
    //群组历史消息清除
    private SingleSettingItem groupClearMsg;
    //群组成员适配器
    private RecycleViewBaseAdapter<GroupMember, RecycleViewBaseHolder> groupMemberAdapter;
    //群组成员
    private ArrayList<GroupMember> groupMembers;

    //解散群组
    private Button groupDissolveOrQuit;
    //成员主动退出群组
    private Button memberQuit;

    //群组id
    private String groupId;
    //群组详情
    private GroupInfo groupInfo;

    private int spanCount = 5;
    //自己的角色
    private int selfRole;

    //更新群组图像
    private SelectPictureView selectPicture;
    //为了获取图片路径
    private ImageCaptureManager captureManager;

    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        //设置导航栏内容
        getToolBarHelper().setToolBarStates(R.drawable.topbar_back_bt, getString(R.string.group_details_title), null, -1, this);
        //获取群组ID
        groupId = getIntent().getStringExtra(SString.GROUP_ID);
        //群组Id为空或不存在
        if (TextUtils.isEmpty(groupId)) {
            finish();
            return;
        }
        userName = ShareLockManager.getInstance().getUserName();
        //注册获取群组详情 注册解散群组  注册退出群组  注册移除群组
        registerReceiver(new String[]{ReceiveAction.ACTION_DISMISS_GROUP, ReceiveAction.ACTION_QUIT_GROUP,
                ReceiveAction.ACTION_REMOVE_MEMBER});
        initData();
        initView();
        asynData();
        logicControlView();
        refreshData();
    }

    //群组列表界面需要刷新的控件可以在此进行添加
    private void refreshData() {
        if (groupInfo == null) {
            return;
        }
        String url = groupInfo.getG_header();
        if (!TextUtils.isEmpty(url) && !url.startsWith(Url.CORE_IP)) {
            url = Url.CORE_IP + url;
        }
        //群组图像
        Glide.with(this)
                .load(url)
                .error(R.drawable.group_default_icon)
                .fitCenter()
                .crossFade()
                .placeholder(R.mipmap.ic_launcher)
                .into(groupIconItem.getRightImage());
        //群组名称
        String name = groupInfo.getG_name();
        if (TextUtils.isEmpty(name)) {
            name = SString.GROUP_PRE;
        }
        groupNameItem.setRightText(name);

        //群组公告
        String declare = groupInfo.getG_declared();
        if (TextUtils.isEmpty(declare)) {
            declare = "";
        }
        singleSettingItem.setRightText(declare);

        //根据群组的加入权限设置 加入的文本类型
        int g_permission = groupInfo.getG_permission();
        if (g_permission >= 0 && g_permission < 3) {
            groupPermission.setRightText(getResources().getStringArray(R.array.group_permissions)[g_permission]);
        }
        //自己的角色  通过遍历成员获取
//        selfRole = GroupMemberSqlManager.getMemberRole(groupId, ShareLockManager.getInstance().getUserName());
        for (GroupMember me : groupMembers) {
            if (me.getU_username().equals(userName))
                selfRole = me.getGur_identity();
        }
        if (selfRole > 0) {  //添加邀请加入的入口  前提是群组的管理员或者群主
            GroupMember groupMember = new GroupMember();
            groupMember.setU_username(SString.GROUP_ADD_FLAG);
            groupMembers.add(groupMember);

        }
        //设置解散还是退出群组
        groupDissolveOrQuit.setText(selfRole == 2 ? getString(R.string.group_dissolve) : getString(R.string.group_quit));
        ToastUtil.showMessage(groupMembers.size() + "selfRole" + selfRole);
        //群组成员的适配器  刷新群组成员
        if (groupMemberAdapter != null && groupMembers != null) {
            //刷新数据
            groupMemberAdapter.fillList(groupMembers);
            //设置群组数量

            changeGroupInfo();
        }
    }

    private void changeGroupInfo() {
        //更角色决定是否显示群组名称修改标志  要根据群组成员的角色
        boolean showNext = false;
        if (selfRole > 0) {
            showNext = true;
        }
        groupIconItem.setShowNextPage(showNext);
        groupIconItem.setEnabled(showNext);
        groupNameItem.setShowNextPage(showNext);
        groupNameItem.setEnabled(showNext);
        singleSettingItem.setShowNextPage(showNext);
        singleSettingItem.setEnabled(showNext);
        groupPermission.setShowNextPage(showNext);
        groupPermission.setEnabled(showNext);
        groupClearMsg.setShowNextPage(showNext);

    }

    @Override
    public int getLayoutId() {
        return R.layout.group_details;
    }


    protected void initData() {  //本该走异步方法  有问题2016-12-14
        //获取群组详情
        groupInfo = GroupInfoSqlManager.getGroupInfo(groupId);
        //获取群组成员  从群组成员表
        groupMembers = GroupMemberSqlManager.getGroupMembers(groupId);
        if (groupMembers == null) {
            groupMembers = new ArrayList<>();
        }
    }

    protected void initView() {
        membersContainer = (ExpandRecyclerView) findViewById(R.id.group_members);
        groupIconItem = (SingleSettingItem) findViewById(R.id.group_icon);
        groupIconItem.setOnClickListener(this); //设置群组图像的点击事件
        groupNameItem = (SingleSettingItem) findViewById(R.id.group_name);
        groupNameItem.setOnClickListener(this);//设置群组名称的点击事件
        singleSettingItem = (SingleSettingItem) findViewById(R.id.group_declare);
        singleSettingItem.setOnClickListener(this);//设置群组公告的点击事件
        groupPermission = (SingleSettingItem) findViewById(R.id.group_permission);
        groupPermission.setOnClickListener(this);//设置群组权限的点击事件
        groupMsgNotice = (SingleSettingItem) findViewById(R.id.group_msg_notice);
        //设置群组消息通知的点击事件
        groupMsgNotice.getCheckedTextView().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ToastUtil.showMessage("" + isChecked);
            }
        });
        groupClearMsg = (SingleSettingItem) findViewById(R.id.group_clean_msg);
        groupClearMsg.setOnClickListener(this); //设置群组历史消息清除的点击事件
        //群主解散按钮  成员退出群组按钮
        groupDissolveOrQuit = (Button) findViewById(R.id.group_dissolve_or_quit);
        groupDissolveOrQuit.setOnClickListener(this); //设置群解散点击事件
    }


    protected void asynData() {
        showCoverLayout();
        //异步数据
        GroupService.getGroupAndMemberDetails(groupId, new GroupService.Callback<GroupInfo, ArrayList<GroupMember>>() {
            @Override
            public void success(GroupInfo s, ArrayList<GroupMember> m) {
                if (s == null || m == null) {
                    return;
                }
                closeCoverLayout();
                groupInfo = s;  //获取新的群组详情 刷新群组详情数据
                groupMembers = m;  // 获取群组成员  刷新群组成员
                refreshData();

            }

            @Override
            public void fail() {
                ToastUtil.showMessage(getString(R.string.group_details_get_err));
                closeCoverLayout();
            }
        });


    }


    protected void logicControlView() {
        //设置布局管理器
        GridLayoutManager manager = new GridLayoutManager(this, spanCount);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        membersContainer.setLayoutManager(manager);
        membersContainer.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top = (int) getResources().getDimension(R.dimen.android_width_18);
            }
        });
        //设置适配器
        groupMemberAdapter = new RecycleViewBaseAdapter<GroupMember, RecycleViewBaseHolder>(this) {
            @Override
            public RecycleViewBaseHolder createCustomViewHolder(ViewGroup parent, int viewType) {
                //创建布局
                return new RecycleViewBaseHolder(parent, R.layout.item_group_details_group_member);
            }

            @Override
            public void bindCustomViewHolder(final RecycleViewBaseHolder holder, int position) {
                //绑定数据
                GroupMember item = getItem(position);
                if (item == null) {

                    return;
                }
                //显示添加成员的加号
                if (item.getU_username().equals(SString.GROUP_ADD_FLAG)) {
                    holder.setImageResource(R.id.group_member_icon, R.drawable.add_group_member_icon);
                    holder.setText(R.id.group_member_name, "");
                    return;
                }
                //群名片
                String name = item.getGur_card();
                if (TextUtils.isEmpty(name)) {
                    //账号
                    name = item.getU_username();
                }
                holder.setText(R.id.group_member_name, name);
                ContactInfo account = ContactInfoSqlManager.getContactInfoFormuserName(item.getU_username());
                if (account != null) {
                    //成员图像
                    String url = account.getU_header();
                    holder.setImageResource(R.id.group_member_icon, ShareLockManager.getImgUrl(url));
                } else {
                    ContactInfoBiz.getContactInfo(item.getU_username(), new GetContactInfoListener() {
                        @Override
                        public void getContactInfo(ContactInfo contactInfo) {
                            if (contactInfo != null) {
                                String url = contactInfo.getU_header();
                                holder.setImageResource(R.id.group_member_icon, ShareLockManager.getImgUrl(url));
                            }
                        }
                    });
                }
            }
        };
        //设置适配器
        membersContainer.setAdapter(groupMemberAdapter);
        //设置成员的点击事件
        groupMemberAdapter.setItemClickAndLongClick(new RecyclerViewClick.Click() {
            @Override
            public void onItemClick(View view, int position) {
                //获取成员 跳转到群组成员的详情
                GroupMember item = groupMemberAdapter.getItem(position);
                if (item == null) {  //点击的可能是 头部或底部视图
                    return;
                }
                //跳转到邀请成员界面
                if (item.getU_username().equals(SString.GROUP_ADD_FLAG)) {
                    startActivityForResult(new Intent(mContext, ContactListSelectActivity.class)
                            .putExtra(SString.GROUP_ID, groupId), 0x1a);
                    return;

                }
                //是自己
                if (item.getU_username().equals(ShareLockManager.getInstance().getUserName())) {
                    startActivity(new Intent(mContext, UserInfoActivity.class)
                            .putExtra(SString.TARGETNAME, item.getU_username()));
                    return;
                }
                //跳转到群组成员的详
                ShareLockManager.startGpMbDtlActy(mContext, item.getGid(), item.getU_username());
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
//        membersContainer.addOnItemTouchListener(new RecycleViewItemClickListener(membersContainer,
//                new RecycleViewItemClickListener.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(View view, int position) {
//
//
//                    }
//
//                    @Override
//                    public void onItemLongClick(View view, int position) {
//
//                    }
//                }));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left: //回退按钮事件
                hideSoftKeyboard();
                finish();
                break;
            case R.id.group_icon: //跳转到群组图像设置界面

                hideSoftKeyboard();
                //使用PopupWindow 设置群图像
                selectPicture = new SelectPictureView(this, R.array.select_pic);
                selectPicture.setOnItemClick(new RecyclerViewClick.Click() {
                    @Override
                    public void onItemClick(View view, int position) {

                        // 根据传入的数据集合的位置进行判断  初始位置重0开始
            /*
            <item>拍照</item>
            <item>从相册选择</item>
            <item>使用默认图像</item>
            <item>取消</item>
            */
                        selectPicture.dismiss();//隐藏popWindow
                        switch (position) {
                            case 0: //拍照
                                try {
                                    if (captureManager == null) {
                                        captureManager = new ImageCaptureManager(mContext);
                                    }
                                    Intent intent = captureManager.dispatchTakePictureIntent();
                                    startActivityForResult(intent, ImageCaptureManager.REQUEST_TAKE_PHOTO);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    ToastUtil.showMessage(getString(R.string.open_camera_err));
                                }
                                break;
                            case 1://从相册选择
                                PhotoPicker.PhotoPickerBuilder builder = PhotoPicker.builder();
                                builder.setPhotoCount(1);
                                builder.setPreviewEnabled(false);
                                builder.setShowCamera(false);
                                builder.setShowGif(true);
                                builder.start(GroupDetailsActivity.this);
                                break;
                            case 2://使用默认图像
                                //1 上传图片到服务器 2 成功后设置到本地
                                //使用 SString.GROUP_PRE 作为默认的标记
                                upLoadPic(SString.GROUP_PRE);
                                break;
                            case 3: //取消
                                GroupDetailsActivity.this.selectPicture.dismiss();
                                break;
                        }

                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        Toast.makeText(view.getContext(), getResources().getStringArray(R.array.select_pic)[position], Toast.LENGTH_SHORT).show();

                    }
                });
                selectPicture.showAtLocation(findViewById(R.id.top_layout),
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);


                break;
            case R.id.group_name: //跳转到群组名称设置界面
                startActivityForResult(new Intent(mContext, EditGroupInfoActivity.class)
                        .putExtra(SString.EDIT_GROUPINFO_TITLE, getString(R.string.group_name))
                        .putExtra(SString.EDIT_GROUPINFO_CONTENT, groupInfo.getG_name()), 0x1b);

                break;
            case R.id.group_declare: //跳转到群组公告设置界面
                String g_declared = groupInfo.getG_declared();
                if (TextUtils.isEmpty(g_declared)) {
                    g_declared = "";
                }
                startActivityForResult(new Intent(mContext, EditGroupInfoActivity.class)
                        .putExtra(SString.EDIT_GROUPINFO_TITLE, getString(R.string.group_declare))
                        .putExtra(SString.EDIT_GROUPINFO_CONTENT, g_declared), 0x1c);

                break;
            case R.id.group_permission: //跳转到群组权限设置界面
                startActivityForResult(new Intent(mContext, EditGroupInfoPermissionActivity.class)
                        .putExtra(SString.EDIT_GROUPINFO_TITLE, getString(R.string.group_permission))
                        .putExtra(SString.EDIT_GROUPINFO_CONTENT, groupInfo.getG_permission()), 0x1d);

                break;
            case R.id.group_clean_msg: //清除群组消息
                final SelectPictureView clearMsg = new SelectPictureView(this, R.array.clear_msg);
                clearMsg.showAtLocation(findViewById(R.id.top_layout),
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                clearMsg.setOnItemClick(new RecyclerViewClick.Click() {
                    @Override
                    public void onItemClick(View view, int position) {
                        clearMsg.dismiss();//隐藏popWindow
                        switch (position) {
                            case 0:
                                clearMesg();
                                break;
                            case 1:
                                clearMsg.dismiss();
                                break;
                        }
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                    }
                });
                break;
            case R.id.group_dissolve_or_quit: //群组解散逻辑处理

                groupDissolveOrQuit.setEnabled(false);
                showDiglog();
                if (selfRole == 2) { //群主解散群组
                    GroupMemberService.DismissGroup(groupId, new GroupMemberService.Callback<String>() {
                        @Override
                        public void success(String s) {
                            //需要发送广播通知到该群组相关界面  关闭界面
                            groupDissolveOrQuit.setEnabled(true);
                            closeDialog();
                        }

                        @Override
                        public void fail() {
                            groupDissolveOrQuit.setEnabled(true);
                            closeDialog();
                            ToastUtil.showMessage(getString(R.string.dismiss_group_fail));

                        }
                    });

                } else { //退出群组
                    GroupMemberService.quitGroup(groupId, new GroupMemberService.Callback<String>() {
                        @Override
                        public void success(String s) {
                            groupDissolveOrQuit.setEnabled(true);
                            //需要发送广播通知到该群组相关界面  关闭相关界面
                            closeDialog();
                        }

                        @Override
                        public void fail() {
                            groupDissolveOrQuit.setEnabled(true);
                            closeDialog();
                            ToastUtil.showMessage(getString(R.string.dismiss_group_fail));

                        }
                    });
                }
                break;
        }
    }

    public void clearMesg() {
        DialogUtils.showBasicWithTile(this, R.string.dialog_hint_title, R.string.clean, R.string.clean, R.string.negative, true, new DialogUtils.DialogInterface() {
            @Override
            public void onPositiveClickListener(@NonNull MaterialDialog dialog) {
                showDiglog();
                GroupService.deleteMessage(Conversation.ConversationType.GROUP, ShareLockManager.addGroupPre(groupId));
                ToastUtil.showMessage(getString(R.string.dismiss_group_success));
                closeDialog();


            }

            @Override
            public void onNegativeClickListener(@NonNull MaterialDialog dialog) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            // 拍照
            case ImageCaptureManager.REQUEST_TAKE_PHOTO:
                if (captureManager.getCurrentPhotoPath() != null) {
                    captureManager.galleryAddPic();
                    // 照片地址
                    String imagePaht = captureManager.getCurrentPhotoPath();
                    if (!TextUtils.isEmpty(imagePaht)) {
                        upLoadPic(imagePaht);
                    } else {
                        ToastUtil.showMessage(getString(R.string.get_pic_err));
                    }
                }
                break;
            //单选
            case PhotoPicker.REQUEST_CODE:
                if (data != null) {
                    ArrayList<String> photos =
                            data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                    for (int i = 0; i < photos.size(); i++) {
                        String imagePaht = photos.get(i);
                        if (!TextUtils.isEmpty(imagePaht)) {
                            upLoadPic(imagePaht);
                        } else {
                            ToastUtil.showMessage(getString(R.string.get_pic_err));
                        }
                    }
                }
                break;
            //邀请好友加入群组
            case 0x1a:
                //选择邀请联系人的结果
                if (data == null) {
                    return;
                }
                //获取邀请的人
                ArrayList<String> selectContact = data.getStringArrayListExtra(SString.GROUP_ADD_FLAG);
                if (selectContact == null || selectContact.isEmpty()) {
                    return;
                }
                //调用邀请成员加入群组的接口
                showDiglog();
                for (int i = 0; i < selectContact.size(); i++) {
                    GroupMemberService.inviteMembers(groupInfo, selectContact.get(i), new GroupMemberService.Callback<String>() {
                        @Override
                        public void success(String s) {
                            ToastUtil.showMessage(getString(R.string.invite_success_tip));
                            closeDialog();

                        }

                        @Override
                        public void fail() {
                            ToastUtil.showMessage(getString(R.string.invite_fail_tip));
                            closeDialog();
                        }
                    });
                }
                break;
            //修改群组名称
            case 0x1b:
                if (data == null) {
                    return;
                }
                String resultName = data.getStringExtra(SString.EDIT_GROUPINFO_CONTENT);
                if (TextUtils.isEmpty(resultName)) {
                    return;
                }
                showDiglog();
                GroupService.changeGroupName(groupId, resultName, new GroupService.Callback<String, Void>() {
                    @Override
                    public void success(String s, Void aVoid) {
                        closeDialog();
                        groupInfo.setG_name(s);
                        groupNameItem.setRightText(s);
                    }

                    @Override
                    public void fail() {
                        closeDialog();
                        ToastUtil.showMessage(getString(R.string.change_fail));
                    }
                });
                break;
            //修改群组公告
            case 0x1c:
                if (data == null) {
                    return;
                }
                String resultDeclare = data.getStringExtra(SString.EDIT_GROUPINFO_CONTENT);
                if (TextUtils.isEmpty(resultDeclare)) {
                    return;
                }
                showDiglog();
                GroupService.changeGroupDeclared(groupId, resultDeclare, new GroupService.Callback<String, Void>() {
                    @Override
                    public void success(String d, Void aVoid) {
                        closeDialog();
                        groupInfo.setG_declared(d);
                        singleSettingItem.setRightText(d);
                    }

                    @Override
                    public void fail() {
                        closeDialog();
                        ToastUtil.showMessage(getString(R.string.change_fail));
                    }
                });
                break;
            //修改加入群组权限
            case 0x1d:
                if (data == null) {
                    return;
                }
                int permission = data.getIntExtra(SString.EDIT_GROUPINFO_CONTENT, 0);
                if (permission < 0) {
                    return;
                }
                showDiglog();
                GroupService.changeGroupPermission(groupId, permission, new GroupService.Callback<Integer, Void>() {
                    @Override
                    public void success(Integer p, Void aVoid) {
                        closeDialog();
                        groupInfo.setG_permission(p);
                        groupPermission.setRightText(getResources().getStringArray(R.array.group_permissions)[p]);
                    }

                    @Override
                    public void fail() {
                        closeDialog();
                        ToastUtil.showMessage(getString(R.string.change_fail));
                    }
                });
                break;
        }

    }

    //设置图像控件是否可点击  因为在上传中不能再次上传
    public void setGroupIconEnable(boolean enable) {
        groupIconItem.getRightImage().setEnabled(enable);
    }

    //上传群图像
    public void upLoadPic(String path) {
        // 异步压缩图片  之后上传图片
        showDiglog();
        setGroupIconEnable(false);
        ImageAsyncTask imageAsyncTask = new ImageAsyncTask(this, new ImageAsyncTask.ImgCallBack() {
            @Override
            public void callBackImg(final String thumb) {
                //压缩完
                if (!TextUtils.isEmpty(thumb)) {
                    //上传图片 上传到服务器
                    GroupService.uploadPicture(thumb, new GroupService.Callback<String, Void>() {
                        @Override
                        public void success(String s, Void v) { //拿到服务器返回的标记群组图像  才可以上传创建群组
                            if (TextUtils.isEmpty(s)) {
                                callBackErr();
                                return;
                            }
                            //上传到服务器完后，调用更改群组图像接口
                            GroupService.changeGroupIcon(groupId, s, new GroupService.Callback<String, Void>() {
                                @Override
                                public void success(String s, Void aVoid) {  //返回了图像 暂时没用使用了本地上传的图
                                    closeDialog();
                                    setGroupIconEnable(true);
                                    groupIconItem.getRightImage().setImageURI(Uri.fromFile(new File(thumb)));
                                }

                                @Override
                                public void fail() {
                                    callBackErr();
                                }
                            });


                        }

                        @Override
                        public void fail() {
                            callBackErr();

                        }
                    });
                } else {
                    callBackErr();
                }
            }

            @Override
            public void callBackErr() {
                closeDialog();
                setGroupIconEnable(true);
                ToastUtil.showMessage(SApplication.getInstance().getString(R.string.upload_picture_err));
            }
        });
        imageAsyncTask.execute(path);
    }

    // 广播接收器  接受群组详情及群组成员插入完成的广播
    @Override
    protected void handleReceiver(Context context, Intent intent) {
        super.handleReceiver(context, intent);
        if (ReceiveAction.ACTION_DISMISS_GROUP.equals(intent.getAction())) {
            /**1  接受到群组解散的广播
             2 判断是否是当前群组
             3如果是 关闭当前界面
             */
            String delGroupId = intent.getStringExtra(SString.GROUP_ID);
            if (!TextUtils.isEmpty(delGroupId) && delGroupId.equals(ShareLockManager.subGroupId(groupId))) {
                finish();
            }
        } else if (ReceiveAction.ACTION_QUIT_GROUP.equals(intent.getAction())) {
            /**1  接受到群组退出的广播
             2 判断是否是当前群组
             3如果是 关闭当前界面
             */
            String delGroupId = intent.getStringExtra(SString.GROUP_ID);
            if (!TextUtils.isEmpty(delGroupId) && delGroupId.equals(ShareLockManager.subGroupId(groupId))) {
                finish();
            }
        } else if (ReceiveAction.ACTION_REMOVE_MEMBER.equals(intent.getAction())) { //移除群组成员的通知   有问题  2016-12-22
            String removeMember = intent.getStringExtra(SString.MEMBER);
            if (!TextUtils.isEmpty(removeMember)) {
                initData();
                refreshData();
            }
        }
    }

}
