package com.sanbafule.sharelock.UI.contact;

import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.S_interface.GetContactInfoListener;
import com.sanbafule.sharelock.S_interface.HttpInterface;
import com.sanbafule.sharelock.UI.base.BaseActivity;
import com.sanbafule.sharelock.adapter.CommonAdapter;
import com.sanbafule.sharelock.adapter.ViewHolder;
import com.sanbafule.sharelock.business.ContactInfoBiz;
import com.sanbafule.sharelock.business.HttpBiz;
import com.sanbafule.sharelock.chatting.Interface.OnEventListener;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.comm.help.ShareLockManager;
import com.sanbafule.sharelock.db.ContactInfoSqlManager;
import com.sanbafule.sharelock.global.Url;
import com.sanbafule.sharelock.modle.ContactInfo;
import com.sanbafule.sharelock.modle.NewFriend;
import com.sanbafule.sharelock.util.CommonUtil;
import com.sanbafule.sharelock.util.ToastUtil;
import com.sanbafule.sharelock.view.customRecycleView.RecycleViewLinearDivider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import butterknife.Bind;


public class NewFriendListActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.newfriend_RecyclerView)
    RecyclerView newfriendRecyclerView;
    private ArrayMap<String, String> map;
    private List<NewFriend> list;

    private CommonAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getToolBarHelper().setToolBarStates(R.drawable.topbar_back_bt, R.string.newfirend, -1, -1, this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        map = new ArrayMap<>();
        CommonUtil.getMap(map);
        map.put("frl_friendsUserName", ShareLockManager.getInstance().getUserName());
        try {
            HttpBiz.httpPostWithRESTFulBiz(Url.SEARCH_NEWFRIENDLIST_REQUEST, map, new HttpInterface() {
                @Override
                public void onFailure() {
                    Log.i("新朋友列表", "连接失败");
                }

                @Override
                public void onSucceed(String s) {
                    Log.i("新朋友列表", s);
                    try {
                        JSONObject object = new JSONObject(s);
                        if (SString.getSuccess(object)) {
                            JSONArray data = SString.getResult(object).getJSONArray("data");
                            list = new Gson().fromJson(data.toString(), new TypeToken<List<NewFriend>>() {
                            }.getType());
                            if (list != null) {
                                setData();
                            }
                        } else {
                            ToastUtil.showMessage(SString.getMessage(object));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void setData() {
        if (adapter == null) {
            adapter = new CommonAdapter<NewFriend>(NewFriendListActivity.this, R.layout.item_newfriend, list) {
                @Override
                public void convert(final ViewHolder holder, final NewFriend newFriend) {
                    if (newFriend == null) {
                        return;
                    }
                    /**
                     * 根据邀请人是不是自己 判断
                     *  1  是自己
                     *      我加别人：
                     *      1 待处理
                     *      2 xx 同意
                     *      3 xx拒绝
                     *  2  是别人
                     *      别人邀请我：
                     *      1 同意 或拒绝
                     *      2 已同意
                     *      3 已拒绝
                     *
                     *
                     */
                    if (newFriend.getFrl_claimantUserName().equals(ShareLockManager.getInstance().getUserName())) {
                        //邀请人是自己  获取别人的详情
                        ContactInfo info = ContactInfoSqlManager.getContactInfoFormuserName(newFriend.getFrl_friendsUserName());
                        if (info != null) {
                            doMyApply(info, newFriend, holder);
                        } else {
                            ContactInfoBiz.getContactInfo(newFriend.getFrl_friendsUserName(), new GetContactInfoListener() {
                                @Override
                                public void getContactInfo(ContactInfo info) {
                                    doMyApply(info, newFriend, holder);
                                }
                            });
//
                        }

                    } else {
                        //别人
                        ContactInfo info = ContactInfoSqlManager.getContactInfoFormuserName(newFriend.getFrl_claimantUserName());
                        if (info != null) {
                            doNotMeApply(info, newFriend, holder);
                        } else {
                            ContactInfoBiz.getContactInfo(newFriend.getFrl_claimantUserName(), new GetContactInfoListener() {
                                @Override
                                public void getContactInfo(ContactInfo info) {
                                    doNotMeApply(info, newFriend, holder);
                                }
                            });
                        }
                    }


                }


            };
            LinearLayoutManager layoutManager = new LinearLayoutManager(NewFriendListActivity.this);
            newfriendRecyclerView.setLayoutManager(layoutManager);
            newfriendRecyclerView.setAdapter(adapter);
            newfriendRecyclerView.addItemDecoration(new RecycleViewLinearDivider(this, LinearLayoutManager.VERTICAL));
            adapter.setOnEvrntListenner(new OnEventListener() {
                @Override
                public void OnClickListener(int postion) {

                }

                @Override
                public void OnLongClickListener(int postion) {

                }
            });
        } else {
            adapter.refreshData(list);
        }

    }

    private void doMyApply(ContactInfo info, NewFriend newFriend, ViewHolder holder) {
        if (info == null) {
            return;
        }

        String name = info.getName();
        //处理的状态
        int frl_status = newFriend.getFrl_status();
        switch (frl_status) {
            case 0:
                //未处理
                holder.setText(R.id.name, getString(R.string.add_friend));
                holder.setText(R.id.content, getString(R.string.add_who_friend, name));
                holder.setVisibility(R.id.do_parent, true);
                holder.setVisibility(R.id.select_p, false);
                holder.setVisibility(R.id.select_result, true);
                holder.setText(R.id.select_result, getString(R.string.request_join_waiting));
                break;
            case 1:
                //拒绝
            case 2:
                //同意
                holder.setText(R.id.name, name);
                holder.setText(R.id.content, frl_status == 1 ? getString(R.string.add_friend_r_rejected) :
                        getString(R.string.add_friend_r_agreed));
                holder.setVisibility(R.id.do_parent, false);
                break;

        }

        holder.setImageUrl(R.id.icon, ShareLockManager.getImgUrl(info.getU_header()));
    }

    private void doNotMeApply(ContactInfo info, final NewFriend newFriend, final ViewHolder holder) {


        if (info == null) {
            return;
        }

        String name = info.getName();

        holder.setText(R.id.name, name);
        holder.setText(R.id.content, getString(R.string.add_me_friend));
        //处理的状态
        int frl_status = newFriend.getFrl_status();
        switch (frl_status) {
            case 0:
                //未处理
                holder.setVisibility(R.id.do_parent, true);
                holder.setVisibility(R.id.select_p, true);
                holder.setVisibility(R.id.select_result, false);
                holder.setOnClickListener(R.id.agree, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doAgreeOrReject(newFriend, true);
                    }
                });
                holder.setOnClickListener(R.id.reject, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doAgreeOrReject(newFriend, false);
                    }
                });
                break;
            case 1:
                //拒绝
            case 2:
                //同意
                holder.setVisibility(R.id.do_parent, true);
                holder.setVisibility(R.id.select_p, false);
                holder.setVisibility(R.id.select_result, true);
                holder.setText(R.id.select_result, frl_status == 1 ?
                        getString(R.string.rejected) : getString(R.string.agreed));
                break;

        }
        holder.setImageUrl(R.id.icon, ShareLockManager.getImgUrl(info.getU_header()));
    }



    @Override
    public int getLayoutId() {
        return R.layout.activity_new_friend_list;
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

    /**
     * 发送同意或拒绝的申请
     */
    private void doAgreeOrReject(final NewFriend newFriend, final boolean isAgree) {
        showDiglog();
        CommonUtil.getMap(map);
        map.put("isAgree", String.valueOf(isAgree ? 1 : 0));//是否同意 0-不同意 1-同意
        map.put("requestId", String.valueOf(newFriend.getFrl_id()));
        try {
            HttpBiz.httpPostBiz(Url.AGREE_OR_REFUSE, map, new HttpInterface() {
                @Override
                public void onFailure() {
                    closeDialog();
                    ToastUtil.showMessage(getString(R.string.dismiss_group_fail));
                }

                @Override
                public void onSucceed(String s) {
                    closeDialog();
                    try {
                        JSONObject object = new JSONObject(s);
                        if (SString.getSuccess(object)) {
                            // 同意
                            //拒绝
                            newFriend.setFrl_status(isAgree ? 2 : 1);
                            adapter.updateItem(newFriend);


                        } else {
                            ToastUtil.showMessage(SString.getMessage(object));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
