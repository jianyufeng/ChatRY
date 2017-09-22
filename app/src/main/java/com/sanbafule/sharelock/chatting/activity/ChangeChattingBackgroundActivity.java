package com.sanbafule.sharelock.chatting.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.UI.base.BaseActivity;
import com.sanbafule.sharelock.util.ToastUtil;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.utils.ImageCaptureManager;


/**
 * Administrator
 * 作者:Created by ShareLock on 2016/12/30 17:08
 * cd : 三八妇乐
 * 描述：更改聊天背景
 */
public class ChangeChattingBackgroundActivity extends BaseActivity {


    @Bind(R.id.system_bg)
    TextView systemBg;
    @Bind(R.id.photo_bg)
    TextView photoBg;
    @Bind(R.id.camera_bg)
    TextView cameraBg;
    //为了获取图片路径
    private ImageCaptureManager captureManager;
    @Override
    public int getLayoutId() {
        return R.layout.activity_change_chatting_background;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getToolBarHelper().setToolBarStates(R.drawable.topbar_back_bt, "聊天背景", null, -1, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                finish();
            }
        });
    }


    @OnClick({R.id.system_bg, R.id.photo_bg, R.id.camera_bg})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.system_bg:
                // 跳转到系统的相片选择
                startActivity(new Intent(this,SystemBackgroundChattingActivity.class));
                break;
            case R.id.photo_bg:
                // 选择相册
                PhotoPicker.PhotoPickerBuilder builder = PhotoPicker.builder();
                builder.setPhotoCount(1);
                builder.setPreviewEnabled(false);
                builder.setShowCamera(false);
                builder.setShowGif(true);
                builder.start(ChangeChattingBackgroundActivity.this);
                break;
            case R.id.camera_bg:
                // 选择拍照

                try {
                    if (captureManager == null) {
                        captureManager = new ImageCaptureManager(this);
                    }
                    Intent intent = captureManager.dispatchTakePictureIntent();
                    startActivityForResult(intent, ImageCaptureManager.REQUEST_TAKE_PHOTO);
                } catch (IOException e) {
                    e.printStackTrace();
                    ToastUtil.showMessage(getString(R.string.open_camera_err));
                }
                break;
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
//                        upLoadPic(imagePaht);
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
//                            upLoadPic(imagePaht);
                        } else {
                            ToastUtil.showMessage(getString(R.string.get_pic_err));
                        }
                    }
                }
                break;
//
        }

    }
}
