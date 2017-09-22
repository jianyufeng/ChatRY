package com.sanbafule.sharelock.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.view.customRecycleView.ExpandRecyclerView;
import com.sanbafule.sharelock.view.customRecycleView.RecycleViewBaseAdapter;
import com.sanbafule.sharelock.view.customRecycleView.RecycleViewBaseHolder;
import com.sanbafule.sharelock.view.customRecycleView.RecyclerViewClick;

import java.util.Arrays;
import java.util.List;

/**
 * 自定义选择图像菜单选项，popWindow 的形式
 * Created by Jorstin on 2015/3/18.
 */
public class SelectPictureView extends PopupWindow {
    private final RecycleViewBaseAdapter<String, RecycleViewBaseHolder> adapter;
    private View mMenuView;
    public ExpandRecyclerView recyclerView;
    public SelectPictureView(final Activity context, int resourceIdArray) {
        super(context);
        String[] stringArray = context.getResources().getStringArray(resourceIdArray);
        List<String> strings = Arrays.asList(stringArray);//数据
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.select_pic_popwindow_menu,null);//加载布局
        recyclerView = (ExpandRecyclerView) mMenuView.findViewById(R.id.comm_popup_list);//查找数组容器
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top = (int) context.getResources().getDimension(R.dimen.android_width_5);
            }
        });
        adapter = new RecycleViewBaseAdapter<String, RecycleViewBaseHolder>(context,strings) {
            @Override
            public RecycleViewBaseHolder createCustomViewHolder(ViewGroup parent, int viewType) {
                return new RecycleViewBaseHolder(parent, R.layout.item_select_pic_menu);
            }

            @Override
            public void bindCustomViewHolder(RecycleViewBaseHolder holder, int position) {
                String item = getItem(position);
                if (TextUtils.isEmpty(item)){
                    return;
                }
                holder.setText(R.id.item_tv,item);
            }//设置适配器

        };
        recyclerView.setAdapter(adapter);
//        if (itemsOnClick != null) {
//            recyclerView.addOnItemTouchListener(itemsOnClick);
//        }
        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);     //给popWindow添加视图
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT); //设置尺寸  使用充满屏幕 为显示Dialog 的样子
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x00000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = mMenuView.findViewById(R.id.pop_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                       dismiss();
                    }
                }
                return true;
            }
        });

    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
//        recyclerView.setAnimation( AnimationUtils.loadAnimation(recyclerView.getContext(),R.anim.select_pic_popshow_anim));
    }

    //设置item的点击事件
    public void setOnItemClick(RecyclerViewClick.Click itemsOnClick){
        if (recyclerView!=null && itemsOnClick!=null && adapter!=null){
            adapter.setItemClickAndLongClick(itemsOnClick);
        }

    }

}

