package com.sanbafule.sharelock.UI.user;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sanbafule.sharelock.MainActivity;
import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.SApplication;
import com.sanbafule.sharelock.UI.account.LoginActivity;
import com.sanbafule.sharelock.UI.base.BaseActivity;
import com.sanbafule.sharelock.UI.group.ImageAsyncTask;
import com.sanbafule.sharelock.business.GroupService;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.comm.help.ShareLockManager;
import com.sanbafule.sharelock.comm.sp.ShareLockPreferenceSettings;
import com.sanbafule.sharelock.comm.sp.ShareLockPreferences;
import com.sanbafule.sharelock.global.Url;
import com.sanbafule.sharelock.modle.ClientUser;
import com.sanbafule.sharelock.util.ToastUtil;
import com.sanbafule.sharelock.view.SelectPictureView;
import com.sanbafule.sharelock.view.SingleSettingItem;
import com.sanbafule.sharelock.view.customRecycleView.RecyclerViewClick;

import java.io.File;
import java.io.IOException;
import java.io.InvalidClassException;
import java.util.ArrayList;

import butterknife.Bind;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.utils.ImageCaptureManager;


/**
 * 作者:Created by 简玉锋 on 2016/12/26 11:20
 * 邮箱: jianyufeng@38.hn
 * 修改个人信息界面
 */

public class ChangeUserInfoActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.change_icon)
    SingleSettingItem changeIcon;
    @Bind(R.id.change_name)
    SingleSettingItem changeName;
    @Bind(R.id.change_account)
    SingleSettingItem changeAccount;
    @Bind(R.id.change_password)
    SingleSettingItem changePassWord;
    @Bind(R.id.change_sign)
    SingleSettingItem changeSign;
    @Bind(R.id.change_qrcode)
    SingleSettingItem changeQrcode;
    @Bind(R.id.exit)
    SingleSettingItem exitSettingItem;
    //自己的信息
    private ClientUser clentUser;
    private Context mContext;
    //更新自己图像
    private SelectPictureView selectPicture;
    //为了获取图片路径
    private ImageCaptureManager captureManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        getToolBarHelper().setToolBarStates(R.drawable.topbar_back_bt, getString(R.string.change_user_title), null, -1, this);
        initData();
        initView();
        refreshData();
    }

    private void refreshData() {
        //自己的图像
        String url = clentUser.getU_header();
        if (!TextUtils.isEmpty(url) && !url.startsWith(Url.CORE_IP)) {
            url = Url.CORE_IP + url;
        }
        Glide.with(this)
                .load(url)
                .error(R.drawable.group_default_icon)
                .fitCenter()
                .crossFade()
                .placeholder(R.mipmap.ic_launcher)
                .into(changeIcon.getRightImage());
        //自己的昵称
        String u_nickname = clentUser.getU_nickname();
        String u_username = clentUser.getU_username();
        if (TextUtils.isEmpty(u_nickname)) {
            u_nickname = u_username;
        }
        changeName.setRightText(u_nickname);
        //自己的账号
        changeAccount.setRightText(u_username);
        //个性签名
        String u_signature = clentUser.getU_signature();
        if (!TextUtils.isEmpty(u_signature)) {
            changeSign.setRightText(u_signature);
        }

    }

    private void initData() {
        //获取自己的信息
        clentUser = ShareLockManager.getInstance().getClentUser();
        if (clentUser == null) {
            finish();
        }
    }

    private void initView() {
        //初始化右边的二维码小图片
        changeQrcode.getRightImage().setImageResource(R.drawable.android_my_qrcode);
        changeQrcode.setOnClickListener(this);
        changeIcon.setOnClickListener(this);
        changeSign.setOnClickListener(this);
        //职工可以修改密码
        if (SString.EMPLOYEES.equals(clentUser.getUser_type())) {
            changePassWord.setVisibility(View.VISIBLE);
            changePassWord.setOnClickListener(this);
        }
        exitSettingItem.setOnClickListener(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_change_user_info;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left:
                hideSoftKeyboard();
                finish();
                break;
            case R.id.exit:
                //退出登录  临时
                ShareLockManager.getInstance().logout();
                startActivities(new Intent[]{new Intent(mContext, MainActivity.class), new Intent(mContext, LoginActivity.class)});
                break;
            case R.id.change_password: //修改密码
                hideSoftKeyboard();
                startActivity(new Intent(mContext, ResetPasswordActivity.class));
                break;
            case R.id.change_qrcode: //查看二维码
                hideSoftKeyboard();
                startActivity(new Intent(mContext, QRCodeActivity.class));
                break;
            case R.id.change_sign: //修改个性签名
                hideSoftKeyboard();
                startActivityForResult(new Intent(mContext, ChangeUserSignActivity.class)
                        .putExtra(SString.GROUP_PRE, getString(R.string.change_user_sign_title)), 0x22);

                break;
            case R.id.change_icon:
                hideSoftKeyboard();
                //使用PopupWindow 设置自己图像
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
                                builder.start(ChangeUserInfoActivity.this);
                                break;
                            case 2://使用默认图像
                                //1 上传图片到服务器 2 成功后设置到本地
                                //使用 SString.GROUP_PRE 作为默认的标记
                                upLoadPic(SString.GROUP_PRE);
                                break;
                            case 3: //取消
                                ChangeUserInfoActivity.this.selectPicture.dismiss();
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
        }
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
            case 0x22: //修改个性签名成功
                changeSign.setRightText(clentUser.getU_signature());
                break;

        }

    }

    //上传自己的图像
    public void upLoadPic(String path) {
        // 异步压缩图片  之后上传图片
        showDiglog();
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
                            //上传到服务器完后，调用更改个人图像接口
                            GroupService.changeUserInfoIcon(String.valueOf(ShareLockManager.getInstance().getClentUser().getU_id()), s, new GroupService.Callback<String, Void>() {
                                @Override
                                public void success(String s, Void aVoid) {  //返回了图像 暂时没用使用了本地上传的图
                                    closeDialog();
                                    if (!TextUtils.isEmpty(s)) {
                                        clentUser.setU_header(s);
                                        try {
                                            //保存修改后的图像路径
                                            ShareLockPreferences.savePreference(ShareLockPreferenceSettings.CLIENTUSER,
                                                    clentUser.toString(), true);
                                            ShareLockManager.getInstance().setClientUser(clentUser);
                                        } catch (InvalidClassException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    changeIcon.getRightImage().setImageURI(Uri.fromFile(new File(thumb)));
                                }

                                @Override
                                public void fail() {
                                    callBackErr();
                                }
                            });
//
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
                ToastUtil.showMessage(SApplication.getInstance().getString(R.string.upload_picture_err));
            }
        });
        imageAsyncTask.execute(path);
    }
}
