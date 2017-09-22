package com.sanbafule.sharelock.UI.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.S_interface.HttpInterface;
import com.sanbafule.sharelock.UI.account.LoginActivity;
import com.sanbafule.sharelock.UI.user.ChangeUserInfoActivity;
import com.sanbafule.sharelock.UI.user.QRCodeActivity;
import com.sanbafule.sharelock.adapter.CommonAdapter;
import com.sanbafule.sharelock.adapter.ViewHolder;
import com.sanbafule.sharelock.business.HttpBiz;
import com.sanbafule.sharelock.chatting.Interface.OnEventListener;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.comm.help.ShareLockManager;
import com.sanbafule.sharelock.global.MyString;
import com.sanbafule.sharelock.global.Url;
import com.sanbafule.sharelock.modle.ClientUser;
import com.sanbafule.sharelock.modle.ExtraServices;
import com.sanbafule.sharelock.util.LogUtil;
import com.sanbafule.sharelock.util.NetUtils;
import com.sanbafule.sharelock.util.ThreadPool;
import com.sanbafule.sharelock.util.ToastUtil;
import com.sanbafule.sharelock.view.DatailSettingItem;
import com.sanbafule.sharelock.view.WrapRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.sanbafule.sharelock.global.Code.READ;
import static com.sanbafule.sharelock.global.Code.SAVE;

/**
 * 个人信息页面
 */
public class UserInfoFragment extends Fragment implements View.OnClickListener {

    WrapRecyclerView serviceList;
    SwipeRefreshLayout refreshAndLoadLayout;
    // 未登录的遮罩画面
    private LinearLayout unLoginCoverLayuot;
    // 真正显示的画面
    private FrameLayout userInfoLayout;
    // 登录按钮
    private Button mButton_Login;
    // 注册按钮
    private Button mButton_register;

    private DatailSettingItem userInfoItem;
    private View view;
    private ArrayList<ExtraServices> mList;
    private ClientUser user;
    private CommonAdapter<ExtraServices> adapter;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            switch (message.what) {
                case SAVE:
                    LogUtil.i("保存成功");
                    break;
                case READ:
                    //获取缓存结果
                    Serializable extraServices = (Serializable) message.obj;
                    if (extraServices != null && extraServices instanceof ArrayList) {
                        mList = (ArrayList<ExtraServices>) extraServices;
                    }
                    if (adapter == null) {
                        adapter = new CommonAdapter<ExtraServices>(getActivity(), R.layout.extra_setvices_gv_item, mList) {
                            @Override
                            public void convert(ViewHolder holder, ExtraServices extraServices) {
                                holder.setText(R.id.title, extraServices.getBm_name());
                            }
                        };
                    } else {
                        adapter.refreshData(mList);
                    }
                    adapter.setOnEvrntListenner(new OnEventListener() {
                        @Override
                        public void OnClickListener(int postion) {

                            if (mList == null || mList.isEmpty()) return;
                            String url = mList.get(postion).getBm_url();

                        }

                        @Override
                        public void OnLongClickListener(int postion) {

                        }
                    });
                    serviceList.setAdapter(adapter);
            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragement_user_info, container, false);
        unLoginCoverLayuot = (LinearLayout) view.findViewById(R.id.userInfo_cover_layout);
        userInfoLayout = (FrameLayout) view.findViewById(R.id.userInfo_Layout);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        refreshAndLoadLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_and_load_layout_id);
        serviceList = (WrapRecyclerView) view.findViewById(R.id.service_list);
        mButton_Login = (Button) unLoginCoverLayuot.findViewById(R.id.login_bt);
        mButton_register = (Button) unLoginCoverLayuot.findViewById(R.id.register_bt);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (ShareLockManager.getInstance().isLogin()) {
            userInfoLayout.setVisibility(View.VISIBLE);
            unLoginCoverLayuot.setVisibility(View.GONE);

            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            serviceList.setLayoutManager(layoutManager);
            View headView = LayoutInflater.from(getActivity()).inflate(R.layout.head_userinfo_recyclerview, null);
            userInfoItem = (DatailSettingItem) headView.findViewById(R.id.user_details);
            //设置修改个人信息的点击事件
            userInfoItem.setOnClickListener(this);
            //二维码点击事件
            userInfoItem.qrcode.setOnClickListener(this);
            serviceList.addHeaderView(headView);
            refreshAndLoadLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshData();

                }
            });
            refreshData();
            showViewWhitData();
        } else {
            userInfoLayout.setVisibility(View.GONE);
            unLoginCoverLayuot.setVisibility(View.VISIBLE);
            mButton_Login.setOnClickListener(this);
            mButton_register.setOnClickListener(this);
        }


    }


    private void refreshData() {
        if (NetUtils.isConnected(getActivity())) {
            //获取数据
            initData();
        } else {
            refreshAndLoadLayout.setRefreshing(false);
            showViewWhitData();
        }
    }

    private void showViewWhitData() {
        setUserData();
        ThreadPool.getInstnce().readObject(handler, SString.EXTRA_SERVICE_CACHE_NAME, READ);
    }


    private void setUserData() {
        user = ShareLockManager.getInstance().getClentUser();
        if (user == null) return;
        userInfoItem.setNickName(!MyString.hasData(user.getU_nickname()) ? "" : user.getU_nickname());
        userInfoItem.setAccountString(!MyString.hasData(user.getU_username()) ? "" : user.getU_username());
        //自己的图像
        String url = user.getU_header();
        if (!TextUtils.isEmpty(url) && !url.startsWith(Url.CORE_IP)) {
            url = Url.CORE_IP + url;
        }
        Glide.with(this)
                .load(url)
                .error(R.drawable.group_default_icon)
                .fitCenter()
                .crossFade()
                .placeholder(R.mipmap.ic_launcher)
                .into(userInfoItem.getIcon());
        //设置等级
        String user_type = user.getUser_type();
        if (!TextUtils.isEmpty(user_type)) {
            if (user_type.equals(SString.MEMBER)) {
                userInfoItem.setManagerVisibility(false);
                userInfoItem.setLevelVisibility(true);
                String user_level = user.getUser_level();
                if (!TextUtils.isEmpty(user_level)) {
                    userInfoItem.setLevelString(user_level);
                }
            } else {
                userInfoItem.setManagerVisibility(true);
                userInfoItem.setLevelVisibility(false);
                String user_level = user.getUser_level();
                if (!TextUtils.isEmpty(user_level)) {
                    userInfoItem.setLevelString(user_level);
                }
            }
        }

    }


    private void initData() {
        user = ShareLockManager.getInstance().getClentUser();
        if (user == null) return;
        String user_type = user.getUser_type();
        String user_level = user.getUser_level();
        int user_Id = user.getU_id();
        if (!MyString.hasData(user_type) || !MyString.hasData(user_level) || user_Id <= 0) {
            return;
        }
        ArrayMap map = new ArrayMap();
        map.put("user_type", user_type);
        map.put("user_level", user_level);
        map.put("user_id", String.valueOf(user_Id));
        try {
            HttpBiz.httpGetBiz(Url.ME, map, new HttpInterface() {
                @Override
                public void onFailure() {

                    refreshAndLoadLayout.setRefreshing(false);
                }

                @Override
                public void onSucceed(String s) {
                    try {
                        refreshAndLoadLayout.setRefreshing(false);
                        JSONObject object = new JSONObject(s);
                        if (SString.getSuccess(object)) {
                            JSONObject result = SString.getResult(object);
                            JSONObject userDetail = result.getJSONObject("userDetail");
                            JSONArray array = result.getJSONArray("moduleData");
                            Gson gson = new Gson();
                            user = gson.fromJson(userDetail.toString(), ClientUser.class);
                            mList = gson.fromJson(array.toString(), new TypeToken<List<ExtraServices>>() {
                            }.getType());
                            if (user != null) {
                                saveClientUser(user);
                            }
                            if (mList != null && !mList.isEmpty()) {
                                ThreadPool.getInstnce().saveCache(handler, mList, SString.EXTRA_SERVICE_CACHE_NAME, SAVE);
                            }
                        }

                        showViewWhitData();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveClientUser(ClientUser user) {
        ShareLockManager.getInstance().setClientUser(user);
//        try {
//            ShareLockPreferences.savePreference(ShareLockPreferenceSettings.CLIENTUSER,
//                    user.toString(), true);
//        } catch (InvalidClassException e) {
//            e.printStackTrace();
//        }

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            // 登录
            case R.id.login_bt:
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.putExtra("CurrentPosition", 4);
                getActivity().startActivity(intent);
                break;
            // 注册
            case R.id.register_bt:
                ToastUtil.showMessage("注册");

                break;
            case R.id.user_details: //修改个人信息
                ToastUtil.showMessage("修改个人信息");
                startActivity(new Intent(getActivity(), ChangeUserInfoActivity.class));


                break;
            case R.id.qrcode: //二维码跳转
                ToastUtil.showMessage("二维码跳转");
                startActivity(new Intent(getActivity(), QRCodeActivity.class));
                break;
        }

    }
}
