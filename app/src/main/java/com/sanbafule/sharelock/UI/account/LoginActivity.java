package com.sanbafule.sharelock.UI.account;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sanbafule.sharelock.MainActivity;
import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.SApplication;
import com.sanbafule.sharelock.S_interface.HttpInterface;
import com.sanbafule.sharelock.UI.base.BaseActivity;
import com.sanbafule.sharelock.business.HttpBiz;
import com.sanbafule.sharelock.chatting.help.ReceiveMessageHelp;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.comm.help.FileAccessor;
import com.sanbafule.sharelock.comm.help.ShareLockManager;
import com.sanbafule.sharelock.comm.sp.ShareLockPreferenceSettings;
import com.sanbafule.sharelock.comm.sp.ShareLockPreferences;
import com.sanbafule.sharelock.db.ContactSqlManager;
import com.sanbafule.sharelock.db.GroupSqlManager;
import com.sanbafule.sharelock.global.Url;
import com.sanbafule.sharelock.modle.ClientUser;
import com.sanbafule.sharelock.modle.Contact;
import com.sanbafule.sharelock.modle.Group;
import com.sanbafule.sharelock.util.ToastUtil;
import com.sanbafule.sharelock.view.editText.ShareLockClearEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InvalidClassException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.rong.imlib.RongIMClient;

public class LoginActivity extends BaseActivity implements View.OnClickListener, TextView.OnEditorActionListener {

    private static final String TAG = "LoginActivity";

    @Bind(R.id.et_userName)
    ShareLockClearEditText et_userName;
    @Bind(R.id.et_password)
    ShareLockClearEditText et_passWord;
    @Bind(R.id.bt_login)
    Button bt_login;
    @Bind(R.id.login_sc_view)
    ScrollView scrollView;
    @Bind(R.id.login_layout)
    LinearLayout login_Layout;
    private String userName;
    private String passWord;
    private InputMethodManager imm;
    private String u_userName;
    private int mCurrentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        getToolBarHelper().setToolBarStates(R.drawable.topbar_back_bt, R.string.login, R.string.register, -1, this);
        if (getIntent() != null) {
            mCurrentPosition = getIntent().getIntExtra("CurrentPosition", 0);
        }

        setListener();
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    private void setListener() {
        et_userName.setOnClickListener(this);
        et_passWord.setOnClickListener(this);
        et_passWord.setOnEditorActionListener(this);
        bt_login.setOnClickListener(this);
        login_Layout.setOnClickListener(this);
        login_Layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                login_Layout.getWindowVisibleDisplayFrame(r);

                int screenHeight = login_Layout.getRootView().getHeight();
                int heightDifference = screenHeight - (r.bottom - r.top);
                if (heightDifference > 30) {

                    changeScrollView();
                }
            }
        });

    }

    private void changeScrollView() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (scrollView != null) {
                    scrollView.scrollTo(0, scrollView.getHeight());
                }

            }
        }, 100);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bt_login:
                // 隐藏然键盘
                hideSoftKeyboard();
                try {
                    login();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.login_layout:
                hideSoftKeyboard();
                break;
            case R.id.et_userName:
                changeScrollView();
                break;
            case R.id.et_password:
                changeScrollView();
                break;
            case R.id.left:
                hideSoftKeyboard();
                finish();
                break;
            case R.id.right_text:
                // 跳转到注册界面

                break;
            default:
                break;
        }
    }

    // 登录下载联系人
    private void login() throws IOException {
        showDiglog();
        userName = et_userName.getText().toString().trim();
        passWord = et_passWord.getText().toString().trim();
        ArrayMap<String, String> map = new ArrayMap();
        map.put("u_username", userName);
        map.put("u_password", passWord);
        HttpBiz.httpPostBiz(Url.LOGIN_URI, map, new HttpInterface() {
            @Override
            public void onFailure() {
                closeDialog();
                ToastUtil.showMessage(R.string.login_again);
            }

            @Override
            public void onSucceed(String s) {
                Log.i(TAG, "onSucceed: " + s);
                try {
                    JSONObject object = new JSONObject(s);
                    if (SString.getSuccess(object)) {
                        JSONObject r = SString.getResult(object);
                        u_userName = r.getString("u_username");
                        Gson gson = new Gson();
                        ClientUser user = gson.fromJson(r.toString(), ClientUser.class);
                        downLodeContactsAndGroup();
                        saveClientUser(user);
                        FileAccessor.initFileAccess(u_userName);
                        connect();
                    } else {
                        ToastUtil.showMessage(SString.getMessage(object));
                        closeDialog();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    closeDialog();
                } catch (IOException e) {
                    e.printStackTrace();
                    closeDialog();
                }


            }
        });


    }

    private void connect() {
       RongIMClient.getInstance().disconnect();
        RongIMClient.connect(ShareLockManager.getInstance().getClentUser().getRongLibToken(), new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
                // 重新获取token
                getRongToken();

            }

            @Override
            public void onSuccess(String s) {
                // 跳转到主页面s
                closeDialog();
                startActivity();
                RongIMClient.setOnReceiveMessageListener(ReceiveMessageHelp.getInstance(SApplication.getInstance()));
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                closeDialog();
                ToastUtil.showMessage("登录失败,请重新登录");
            }
        });
        //设置融云的连接状态
        RongIMClient.setConnectionStatusListener(ReceiveMessageHelp.getInstance(SApplication.getInstance()));


    }

    private void getRongToken() {
        ArrayMap<String, String> map = new ArrayMap();
        map.put("userName", ShareLockManager.getInstance().getUserName());

        try {
            HttpBiz.httpPostBiz(Url.GET_RONG_TOKEN, map, new HttpInterface() {
                @Override
                public void onFailure() {

                }

                @Override
                public void onSucceed(String s) {
                    connect();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveClientUser(ClientUser user) {
        try {
            ShareLockManager.getInstance().saveClientUser(user);
            ShareLockPreferences.savePreference(ShareLockPreferenceSettings.ISLOGIN, true, true);
        } catch (InvalidClassException e) {
            e.printStackTrace();
        }

    }

    private void startActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("CurrentPosition", mCurrentPosition);
        intent.setAction(SString.LOGINACTION);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
                    /*隐藏软键盘*/
            InputMethodManager imm = (InputMethodManager) v
                    .getContext().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
            if (imm.isActive()) {
                imm.hideSoftInputFromWindow(
                        v.getApplicationWindowToken(), 0);
            }
            try {
                login();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        return false;
    }


    private void downLodeContactsAndGroup() throws IOException {

        if (u_userName == null) {
            return;
        }
        ArrayMap<String, String> map = new ArrayMap<>();
        map.put("u_username", u_userName);
        HttpBiz.httpGetBiz(Url.GET_CONTANSANDGROUPLIST, map, new HttpInterface() {
            @Override
            public void onFailure() {
//                closeDialog();

            }

            @Override
            public void onSucceed(String s) {
//                closeDialog();
                JSONObject object = null;
                try {
                    object = new JSONObject(s);
                    boolean isSuccess = SString.getSuccess(object);
                    if (isSuccess) {
                        JSONObject r = SString.getResult(object);
                        JSONObject friend = r.getJSONObject("firends");
                        JSONArray friendData = friend.getJSONArray("data");
                        Gson gson = new Gson();
                        List<Contact> contacts = gson.fromJson(friendData.toString(), new TypeToken<List<Contact>>() {
                        }.getType());
                        ContactSqlManager.insertContacts(contacts);
                        JSONObject group = r.getJSONObject("groups");
                        JSONArray groupData = group.getJSONArray("data");
                        List<Group> groups = gson.fromJson(groupData.toString(), new TypeToken<List<Group>>() {
                        }.getType());
                        GroupSqlManager.insertGroupList(groups);
                    } else {
                        String message = SString.getMessage(object);
                        ToastUtil.showMessage(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


    }

}
