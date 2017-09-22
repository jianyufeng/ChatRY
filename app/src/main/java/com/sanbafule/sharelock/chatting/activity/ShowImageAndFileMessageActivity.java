package com.sanbafule.sharelock.chatting.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.UI.base.BaseActivity;
import com.sanbafule.sharelock.chatting.Interface.GetLatestMessagesCallBack;
import com.sanbafule.sharelock.chatting.help.SChattingHelp;
import com.sanbafule.sharelock.chatting.modle.ViewImageInfo;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.comm.help.ShareLockManager;
import com.sanbafule.sharelock.modle.Group;
import com.sanbafule.sharelock.util.DensityUtil;
import com.sanbafule.sharelock.util.ToastUtil;
import com.sanbafule.sharelock.view.customRecycleView.ExpandRecyclerView;
import com.sanbafule.sharelock.view.customRecycleView.RecycleViewBaseAdapter;
import com.sanbafule.sharelock.view.customRecycleView.RecycleViewBaseHolder;
import com.sanbafule.sharelock.view.customRecycleView.RecyclerViewClick;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.FileMessage;
import io.rong.message.ImageMessage;

import static io.rong.imlib.statistics.UserData.name;

public class ShowImageAndFileMessageActivity extends BaseActivity {

    @Bind(R.id.ListView)
    ExpandRecyclerView ListView;
    @Bind(R.id.transmit)
    ImageButton transmit;
    @Bind(R.id.collect)
    ImageButton collect;
    @Bind(R.id.remove)
    ImageButton remove;
    @Bind(R.id.bottom_list)
    LinearLayout linearLayout;
    // 所有消息的集合
    private List<Message> mList = new ArrayList<>();
    private RecycleViewBaseAdapter adapter;

    private static final int CONNT = 3;
    private int width;

    private boolean select;

    private ArrayList<Message> mSelectedList = new ArrayList<>();

    private String targetId;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getToolBarHelper().setToolBarStates(R.drawable.topbar_back_bt, "聊天文件", "选择", -1, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.left:
                        hideSoftKeyboard();
                        finish();
                        break;
                    case R.id.right_text:
                        // 显示多有的选择器
                        select = !select;
                        if (select) {
                            getToolBarHelper().getRightTextView().setText("取消");
                            linearLayout.setVisibility(View.VISIBLE);
                        } else {
                            getToolBarHelper().getRightTextView().setText("选择");
                            linearLayout.setVisibility(View.GONE);
                        }
                        mSelectedList.clear();
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        });
        intent=getIntent();
        targetId=intent.getStringExtra(SString.TARGETNAME);
        int displayWidth = DensityUtil.getDisplayWidth(ShowImageAndFileMessageActivity.this);
        int i = DensityUtil.dip2px(20L);
        width = (displayWidth - i) / CONNT;
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        ListView.setLayoutManager(manager);

        ListView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top = (int) getResources().getDimension(R.dimen.android_width_10);
                int i = parent.getChildLayoutPosition(view);
                if (i % CONNT == CONNT - 1) {
                    outRect.right = (int) getResources().getDimension(R.dimen.android_width_10);
                } else if (i % CONNT == 0) {
                    outRect.left = (int) getResources().getDimension(R.dimen.android_width_10);
                } else {
                    outRect.left = (int) getResources().getDimension(R.dimen.android_width_10);
                    outRect.right = (int) getResources().getDimension(R.dimen.android_width_10);
                }

            }
        });
        adapter = new RecycleViewBaseAdapter<Message, RecycleViewBaseHolder>(this) {
            @Override
            public RecycleViewBaseHolder createCustomViewHolder(ViewGroup parent, int viewType) {
                return new RecycleViewBaseHolder(parent, R.layout.item_image_file);
            }

            @Override
            public void bindCustomViewHolder(RecycleViewBaseHolder holder, int position) {
                final Message item = getItem(position);
                MessageContent messageContent = item.getContent();
                if (item == null || messageContent == null) return;
                ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
                layoutParams.height = layoutParams.width = width;
                holder.itemView.setLayoutParams(layoutParams);
                if (messageContent instanceof ImageMessage) {
                    holder.getView(R.id.image).setVisibility(View.VISIBLE);
                    holder.setImageUrl(R.id.image, ((ImageMessage) messageContent).getThumUri());
                } else if (messageContent instanceof FileMessage) {
                    holder.getView(R.id.image).setVisibility(View.GONE);
                    holder.getView(R.id.content_view).setBackgroundColor(Color.BLUE);
                    holder.setImageUrl(R.id.image, ((ImageMessage) messageContent).getThumUri());
                }
                if (select) {
                    holder.getView(R.id.selected).setVisibility(View.VISIBLE);
                    ((CheckBox) holder.getView(R.id.selected)).setChecked(false);
                } else {
                    holder.getView(R.id.selected).setVisibility(View.GONE);
                }
                ((CheckBox) holder.getView(R.id.selected)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            mSelectedList.add(item);
                        } else {
                            mSelectedList.remove(item);
                        }
                    }
                });
            }

        };
        adapter.setItemClickAndLongClick(click);
        ListView.setAdapter(adapter);
        initData();

    }

    /**
     * 点击事件
     */
    private RecyclerViewClick.Click click = new RecyclerViewClick.Click() {
        @Override
        public void onItemClick(View view, int position) {
            // 点击跳转页面
            final Message item = (Message) adapter.getItem(position);
            MessageContent content = item.getContent();
            if (item == null || content == null) return;
            // 查看大图
            if (content instanceof ImageMessage) {
                SChattingHelp.showImageList(item, ShowImageAndFileMessageActivity.this);

            } else if (content instanceof FileMessage) {

            }

        }

        @Override
        public void onItemLongClick(View view, int position) {

        }
    };

    private void initData() {

        RongIMClient.getInstance().getLatestMessages(Conversation.ConversationType.PRIVATE, targetId, 1000, new RongIMClient.ResultCallback<List<Message>>() {
            @Override
            public void onSuccess(List<Message> messageList) {
                if (messageList == null || messageList.isEmpty()) return;
                for (int i = 0; i < messageList.size(); i++) {
                    MessageContent messageContent = messageList.get(i).getContent();
                    if (messageContent instanceof ImageMessage || messageContent instanceof FileMessage) {
                        mList.add(messageList.get(i));
                    }
                }

                adapter.appendList(mList);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                adapter.appendList(mList);
            }
        });
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_show_image_and_file_message;
    }


    @OnClick({R.id.transmit, R.id.collect, R.id.remove})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.transmit:
                // 转发
               if(mSelectedList.isEmpty()){
                   ToastUtil.showMessage("请选择要转发的消息");
                   return;
               }else {
                   for (int i = 0; i <mSelectedList.size() ; i++) {
                       Message message = mSelectedList.get(i);
                       MessageContent content = message.getContent();
                       if(content instanceof ImageMessage){
                           if(((ImageMessage) content).getLocalUri()!=null){
                               continue;
                           }else {
                               ToastUtil.showMessage("本地文件不存在，请重新下载");
                           return;
                           }
                       }
                   }
               }
               Intent intent=new Intent(this,TranspondMessageActivity.class);
                intent.putParcelableArrayListExtra("SelectMessageList",mSelectedList);
                startActivity(intent);
                break;
            case R.id.collect:
                // 收藏
                ToastUtil.showMessage("收藏" + mSelectedList.size());
                break;
            case R.id.remove:
                // 移除
                ToastUtil.showMessage("移除" + mSelectedList.size());
                break;
        }
    }
}
