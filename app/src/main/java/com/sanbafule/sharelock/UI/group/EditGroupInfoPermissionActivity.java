package com.sanbafule.sharelock.UI.group;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.UI.base.BaseActivity;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.view.customRecycleView.ExpandRecyclerView;
import com.sanbafule.sharelock.view.customRecycleView.RecycleViewBaseAdapter;
import com.sanbafule.sharelock.view.customRecycleView.RecycleViewBaseHolder;
import com.sanbafule.sharelock.view.customRecycleView.RecycleViewItemClickListener;
import com.sanbafule.sharelock.view.customRecycleView.RecycleViewLinearDivider;

import java.util.Arrays;
import java.util.List;


public class EditGroupInfoPermissionActivity extends BaseActivity implements View.OnClickListener {
    private ExpandRecyclerView permissioncontainer;
    private String title;
    private String selecePer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        title = intent.getStringExtra(SString.EDIT_GROUPINFO_TITLE);
        //获取当前的加入权限
        selecePer = getResources().getStringArray(R.array.group_permissions)[intent.getIntExtra(SString.EDIT_GROUPINFO_CONTENT,0)];
        //设置导航栏内容
        getToolBarHelper().setToolBarStates(R.drawable.topbar_back_bt,
                getString(R.string.edit_group_title, title),
                getString(R.string.complete), R.drawable.head_right_save_selector, this);
        initView();
    }

    private void initView() {
        permissioncontainer = (ExpandRecyclerView) findViewById(R.id.group_permission_container);
        String[] stringArray = getResources().getStringArray(R.array.group_permissions);
        List<String> strings = Arrays.asList(stringArray);//数据
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        permissioncontainer.setLayoutManager(manager);
        permissioncontainer.addItemDecoration(new RecycleViewLinearDivider(this, LinearLayoutManager.VERTICAL));
        permissioncontainer.setAdapter(new RecycleViewBaseAdapter<String, RecycleViewBaseHolder>(this, strings) {
            @Override
            public RecycleViewBaseHolder createCustomViewHolder(ViewGroup parent, int viewType) {
                return new RecycleViewBaseHolder(parent, R.layout.item_group_permission);
            }

            @Override
            public void bindCustomViewHolder(RecycleViewBaseHolder holder, int position) {
                String item = getItem(position);
                if (TextUtils.isEmpty(item)) {
                    return;
                }
                holder.setText(R.id.permission_name, item);
                ((CheckBox) holder.getView(R.id.select)).setChecked(selecePer.equals(item));
            }//设置适配器

        });
        permissioncontainer.addOnItemTouchListener(new RecycleViewItemClickListener(permissioncontainer, new RecycleViewItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent();
                intent.putExtra(SString.EDIT_GROUPINFO_CONTENT,position);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_edit_group_permission;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left: //回退按钮事件
                hideSoftKeyboard();
                finish();
                break;
            default:
                break;
        }
    }
}
