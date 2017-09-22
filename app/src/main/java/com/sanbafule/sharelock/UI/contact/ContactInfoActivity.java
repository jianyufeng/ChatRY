package com.sanbafule.sharelock.UI.contact;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.S_interface.GetContactInfoListener;
import com.sanbafule.sharelock.UI.account.EditConfigActivity;
import com.sanbafule.sharelock.UI.base.BaseActivity;
import com.sanbafule.sharelock.business.ContactInfoBiz;
import com.sanbafule.sharelock.business.ImageBiz;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.comm.help.ShareLockManager;
import com.sanbafule.sharelock.db.ContactInfoSqlManager;
import com.sanbafule.sharelock.global.MyString;
import com.sanbafule.sharelock.global.ReceiveAction;
import com.sanbafule.sharelock.modle.ContactInfo;
import com.sanbafule.sharelock.util.DialogUtils;
import com.sanbafule.sharelock.view.DatailSettingItem;
import com.sanbafule.sharelock.view.SingleSettingItem;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 联系人详情页面
 */
public class ContactInfoActivity extends BaseActivity implements View.OnClickListener {


    @Bind(R.id.contact_details)
    DatailSettingItem contactDetails;
    @Bind(R.id.contact_label)
    SingleSettingItem contactLabel;
    @Bind(R.id.contact_location)
    SingleSettingItem contactLocation;
    @Bind(R.id.contact_sign)
    SingleSettingItem contactSign;
    @Bind(R.id.btn_send)
    Button btnSend;
    private String name;
    private ContactInfo info;
    // 从哪个页面跳过来的
    private int type;
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getToolBarHelper().setToolBarStates(R.drawable.topbar_back_bt, R.string.contactinfo, R.string.more, -1, this);
        contactDetails.getIcon().setOnClickListener(this);
        intent = getIntent();
        name = intent.getStringExtra(SString.NAME);
        info = intent.getParcelableExtra(SString.CONTACTINFO);
        registerReceiver(new String[]{ReceiveAction.ACTION_DELETE_FRIEND});
    }

    private void setData() {
        contactDetails.setNickName(info.getName());
        contactDetails.setAccountString(info.getU_username());
        if(MyString.hasData(info.getU_signature())){
            contactSign.setRightText(info.getU_signature());
        }
        ImageBiz.showImage(ContactInfoActivity.this,contactDetails.getIcon(), ShareLockManager.getImgUrl(info.getU_header()),R.drawable.icon_touxiang_persion_gray);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_contact_info;
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (info != null) {
            setData();
        } else {
            if (!MyString.hasData(name)) {
                return;
            }
            info = ContactInfoSqlManager.getContactInfoFormuserName(name);
            if (info==null){
                initHttp();
            }else {
                setData();
            }

        }

    }

    private void initHttp() {
        ContactInfoBiz.getContactInfo(name, new GetContactInfoListener() {
            @Override
            public void getContactInfo(ContactInfo contactInfo) {
                info = contactInfo;
                setData();
            }
        });
    }


    @OnClick({R.id.contact_details, R.id.contact_label, R.id.contact_location, R.id.contact_sign, R.id.btn_send, R.id.icon, R.id.left, R.id.right_text})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.icon:
                // 展示大图
                startActivity(new Intent(this, ImageShowerActivity.class).putExtra(SString.BIG_IMG, info.getU_header()).putExtra(
                        "ContactInfo_ID", info.getU_username()));
                break;

            case R.id.contact_details:
                // 展示个人的二维码

              showQRCodeDialog();
                break;
            case R.id.contact_label:
                // 设置备注和个性签名
                if (info == null) return;
                Intent intent = new Intent(this, EditConfigActivity.class);
                if (MyString.hasData(info.getComment_name())) {
                    intent.putExtra(SString.COMMENT_NAME, info.getComment_name());
                } else {
                    intent.putExtra(SString.COMMENT_NAME, "");
                }
                intent.putExtra(SString.CONTACT_NAME, info.getU_username());
                intent.putExtra("type", 1);
                startActivityForResult(intent, 0x01);
                break;
            case R.id.contact_location:
                // 显示地址
                break;
            case R.id.contact_sign:
                /**
                 * 展示个性签名
                 */
                if (info == null) return;
                intent = new Intent(this, ContactSignActivity.class);
                if (MyString.hasData(info.getU_signature())) {
                    intent.putExtra(SString.CONTACT_SIGN, "");
                } else {
                    intent.putExtra(SString.CONTACT_SIGN, info.getU_signature());
                }
                startActivity(intent);
                break;
            case R.id.left:
                hideSoftKeyboard();
                finish();
                break;
            case R.id.right_text:
                intent = new Intent(this, ContactMoreActivity.class);
                intent.putExtra(SString.CONTACTINFO, info);
                startActivity(intent);
                break;
            case R.id.btn_send:
                // 发送消息
                ShareLockManager.startChattingActivity(this, info.getU_username(), null, true);
                break;
        }
    }

    /**
     * 显示联系人的二维码
     */
    private void showQRCodeDialog() {
        DialogUtils.showContactQRCodeDialog(this,info);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == 0x01) {
            String nickName = data.getStringExtra("NickName");
//            tv_comment.setText(nickName);
//            tv_nick.setVisibility(View.VISIBLE);
//            tv_nick.setText(contacts.getNickname());
        }

    }

    // 广播接收器  接受删除好友的广播
    @Override
    protected void handleReceiver(Context context, Intent intent) {
        super.handleReceiver(context, intent);
        if (ReceiveAction.ACTION_DELETE_FRIEND.equals(intent.getAction())) {
            /**1  删除好友
             */
            String delFriend = intent.getStringExtra(SString.TARGETNAME);
            if (!TextUtils.isEmpty(delFriend)) {
                if (delFriend.equals(name)) {
                    finish();
                }
            }
        }
    }
}
