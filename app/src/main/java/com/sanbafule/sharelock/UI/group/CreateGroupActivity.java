package com.sanbafule.sharelock.UI.group;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sanbafule.sharelock.MainActivity;
import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.SApplication;
import com.sanbafule.sharelock.UI.base.BaseActivity;
import com.sanbafule.sharelock.business.GroupService;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.db.GroupSqlManager;
import com.sanbafule.sharelock.modle.Group;
import com.sanbafule.sharelock.util.ToastUtil;
import com.sanbafule.sharelock.view.AddIconImageView;
import com.sanbafule.sharelock.view.SelectPictureView;
import com.sanbafule.sharelock.view.customRecycleView.RecyclerViewClick;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.utils.ImageCaptureManager;


/**
 * 作者:Created by 简玉锋 on 2016/11/21 18:17
 * 邮箱: jianyufeng@38.hn
 */
public class CreateGroupActivity extends BaseActivity implements View.OnClickListener, TextView.OnEditorActionListener {
    private View hideSoft;

    private Context context;
    //群图像
    private AddIconImageView groupIcon;
    //选择群组图像
    private SelectPictureView selectPicture;
    //群组名称
    private EditText mNameEdit;
    //确定提交
    private TextView confirm;

    //创建的群
    private Group group;
    // 群组名称的输入变化

    private String thumbPath;   //生成缩略图的路径

    private String groupHeader;  //上传图像成功后返回的字段  需要在创建群组的接口中作为参数传递
    //同意服务声明
    private CheckBox agreeRule;
    //查看声明
    private TextView checkRule;
    final private TextWatcher textWatcher = new TextWatcher() {
        private int fliteCounts = 0;

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            fliteCounts = s.toString().trim().length();
            if (fliteCounts > 0) {
                confirm.setEnabled(true);
                return;
            }
            confirm.setEnabled(false);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        //设置导航栏内容
        getToolBarHelper().setToolBarStates(R.drawable.topbar_back_bt, getString(R.string.group_list_create_group), getString(R.string.create_group_confirm), R.drawable.head_right_save_selector, this);
        confirm = getToolBarHelper().getRightTextView();
        confirm.setEnabled(false);
        initData();
        initView();
        asynData();
        logicControlView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_create_group;
    }


    protected void initData() {

    }


    protected void initView() {
        //群组图像的相关设置
        groupIcon = (AddIconImageView) findViewById(R.id.add_icon);
//        groupIcon.setTag(0); //通过设置标签查看是否添加群图像
        groupIcon.setOnClickListener(this);
        //群组名称的设置
        mNameEdit = (EditText) findViewById(R.id.group_name);
        mNameEdit.addTextChangedListener(textWatcher);
        mNameEdit.setOnEditorActionListener(this);
        agreeRule = (CheckBox) findViewById(R.id.agree_rule);
        checkRule = (TextView) findViewById(R.id.check_rule);
        checkRule.setOnClickListener(this);
        hideSoft = findViewById(R.id.hideSoft);
        hideSoft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideSoftKeyboard();
                return false;
            }
        });
    }


    protected void asynData() {

    }


    protected void logicControlView() {

    }

    private ImageCaptureManager captureManager;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left: //回退按钮事件
                hideSoftKeyboard();
                finish();
                break;
            case R.id.check_rule: //查看服务声明
                hideSoftKeyboard();
                ToastUtil.showMessage(getString(R.string.create_group_rule));
                break;
            case R.id.add_icon: //选择群组图像
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
                                        captureManager = new ImageCaptureManager(context);
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
                                builder.start(CreateGroupActivity.this);
                                break;
                            case 2://使用默认图像
                                //1 上传图片到服务器 2 成功后设置到本地
                                //使用 SString.GROUP_PRE 作为默认的标记
                                upLoadPic(SString.GROUP_PRE);
                                break;
                            case 3: //取消
                                CreateGroupActivity.this.selectPicture.dismiss();
                                break;
                        }
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        Toast.makeText(view.getContext(), getResources().getStringArray(R.array.select_pic)[position], Toast.LENGTH_SHORT).show();

                    }
                });
                selectPicture.showAtLocation(hideSoft,
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.right_text:    //确定按钮  提交创建群组
                //网络请求创建群组
                /**
                 * 创建群组的不走
                 *  1 上传群组图像 拿到 返回值
                 *
                 *  2  检查群组名称
                 *
                 *  3  创建群组
                 */
                createGroup();
                break;
            default:
                break;
        }
    }

    //创建群组
    private void createGroup() {
        String name = mNameEdit.getText().toString().trim();

        hideSoftKeyboard();
        if (TextUtils.isEmpty(groupHeader)) {
            ToastUtil.showMessage(getString(R.string.group_pic_tip));
            return;
        } else if (TextUtils.isEmpty(name)) {
            ToastUtil.showMessage(getString(R.string.group_name_tip));
            return;
        } else if (!agreeRule.isChecked()) {
            ToastUtil.showMessage(getString(R.string.create_group_rule_tip));
            return;
        }
//                else if (PatternUtils.isShuZiYing(name)){       //群组名称 正则匹配
//                    ToastUtil.showMessage(getString(R.string.group_name_format_err));
//                    return;
//                }
        showDiglog();  //显示提交
        GroupService.createGroup(name, groupHeader, new GroupService.Callback<String, Void>() {
            @Override
            public void success(String s, Void v) {
                closeDialog(); //关闭显示提交
                //创建群组成功需要添加到数据库中
                if (TextUtils.isEmpty(s)) {
                    ToastUtil.showMessage(getString(R.string.create_group_fail));
                    return;
                }
                //创建成功后需要跳转到会话界面  会收到群组的消息
                group = new Group();
                group.setG_id(s); //添加群组id
                GroupSqlManager.insertGroup(group); //插入数据库
                startActivity(new Intent(context, MainActivity.class));    //创建完成后跳转到会话界面
            }

            @Override
            public void fail() {
                closeDialog(); //关闭显示提交
                ToastUtil.showMessage(getString(R.string.create_group_fail));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
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
            }
        }
    }

    //上传群图像
    public void upLoadPic(String path) {
        // 异步压缩图片  之后上传图片
        groupIcon.setEnabled(false);
        ImageAsyncTask imageAsyncTask = new ImageAsyncTask(this, new ImageAsyncTask.ImgCallBack() {
            @Override
            public void callBackImg(String thumb) {
                if (!TextUtils.isEmpty(thumb)) {
                    thumbPath = thumb;
                    //上传图片
                    GroupService.uploadPicture(thumbPath, new GroupService.Callback<String, Void>() {
                        @Override
                        public void success(String s, Void v) { //拿到服务器返回的标记群组图像  才可以上传创建群组
                            groupIcon.setEnabled(true);
                            if (TextUtils.isEmpty(s)) {
                                return;
                            }
                            groupHeader = s;

                            groupIcon.setImageURI(Uri.fromFile(new File(thumbPath)));
                        }

                        @Override
                        public void fail() {
                            groupIcon.setEnabled(true);
                            ToastUtil.showMessage(SApplication.getInstance().getString(R.string.upload_picture_err));
                        }
                    });
                } else {
                    groupIcon.setEnabled(true);
                }
            }

            @Override
            public void callBackErr() {
                groupIcon.setEnabled(true);
            }
        });
        imageAsyncTask.execute(path);
    }
    //
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        switch (actionId){
            case EditorInfo.IME_ACTION_DONE:
                createGroup();
            break;
        }
        return false;
    }
}
