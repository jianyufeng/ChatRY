package com.sanbafule.sharelock.adapter;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.S_interface.HttpInterface;
import com.sanbafule.sharelock.business.HttpBiz;
import com.sanbafule.sharelock.business.ImageBiz;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.comm.help.ShareLockManager;
import com.sanbafule.sharelock.db.ContactInfoSqlManager;
import com.sanbafule.sharelock.db.GroupMemberSqlManager;
import com.sanbafule.sharelock.global.MyString;
import com.sanbafule.sharelock.global.Url;
import com.sanbafule.sharelock.modle.Contact;
import com.sanbafule.sharelock.modle.ContactInfo;
import com.sanbafule.sharelock.util.ContactComparator;
import com.sanbafule.sharelock.util.PinyinUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *
 */
public class ContactSelectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<String> mContactList; // 联系人名称List（转换成拼音）
    private List<Contact> contacts; // 最终结果（包含分组的字母）
    private List<String> characterList; // 字母List
    private List<Contact> resultList;
    private ArrayList<String> selectedContacts;
    private RecyclerView recyclerView;
    RecyclerWrapAdapter adapter;
    private String groupId;
    private List<Contact> userNameList;

    public enum ITEM_TYPE {
        ITEM_TYPE_CHARACTER,
        ITEM_TYPE_CONTACT
    }

    public ContactSelectAdapter(Context context, List<Contact> contactList, String groupId) {
        this.groupId = groupId;
        selectedContacts = new ArrayList<>();
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        if (contactList == null) {
            contactList = new ArrayList<>();
        }
        this.contacts = contactList;
        handleContact();
    }

    public void refreshData(List<Contact> contacts) {
        if (contacts == null || contacts.isEmpty()) {
            return;
        }
        this.contacts = contacts;
        handleContact();
        notifyDataSetChanged();
    }

    private void handleContact() {
        userNameList = new ArrayList<>();
        for (int i = 0; i < contacts.size(); i++) {
            Contact contact = contacts.get(i);
            if (MyString.hasData(contacts.get(i).getName())) {
                userNameList.add(contact);//保存所有 显示的 拼音
            }
        }
        Collections.sort(userNameList, new ContactComparator());//显示的 排序

        // 现根据userName排序
        //fori
        resultList = new ArrayList<>();  //添加 分类实体对象 后的实际数据
        characterList = new ArrayList<>(); //保存 分类的 首字母

        for (int i = 0; i < userNameList.size(); i++) {
            Contact contact = userNameList.get(i);
            String name = contact.getName();
            String character = (name.charAt(0) + "").toUpperCase(Locale.ENGLISH);
            if (!characterList.contains(character)) {
                if (character.hashCode() >= "A".hashCode() && character.hashCode() <= "Z".hashCode()) { // 是字母
                    characterList.add(character);
                    resultList.add(new Contact(character, ContactAdapter.ITEM_TYPE.ITEM_TYPE_CHARACTER.ordinal()));
                } else {
                    if (!characterList.contains("#")) {
                        characterList.add("#");
                        resultList.add(new Contact("#", ContactAdapter.ITEM_TYPE.ITEM_TYPE_CHARACTER.ordinal()));
                    }
                }
            }
            contact.setType(ContactAdapter.ITEM_TYPE.ITEM_TYPE_CONTACT.ordinal());
            resultList.add(contact);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (recyclerView == null) { //获取 recyclerView 的引用为了刷新shuju
            recyclerView = (RecyclerView) parent;
        }
        if (viewType == ITEM_TYPE.ITEM_TYPE_CHARACTER.ordinal()) {
            return new CharacterHolder(mLayoutInflater.inflate(R.layout.item_character, parent, false));
        } else {
            return new ContactHolder(mLayoutInflater.inflate(R.layout.item_contact, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof CharacterHolder) {
            ((CharacterHolder) holder).mTextView.setText(resultList.get(position).getU_username());
        } else if (holder instanceof ContactHolder) {
            final ContactHolder contactHolder = (ContactHolder) holder;
            final String userName = resultList.get(position).getU_username();
            // 查询数据库
            final ContactInfo info = ContactInfoSqlManager.getContactInfoFormuserName(userName);
            if (info != null) {
                setContactInfo((ContactHolder) holder, info);
                Glide.
                        with(mContext).
                        load(ShareLockManager.getImgUrl(info.getU_header())).
                        error(R.drawable.icon_touxiang_persion_gray).
                        fitCenter().
                        crossFade(300).
                        into(contactHolder.mImageView);
            } else {
                // 下载联系人
                try {
                    ArrayMap<String, String> map = new ArrayMap<>();
                    map.put("u_username", userName);
                    HttpBiz.httpGetBiz(Url.GET_CONTACTINFO, map, new HttpInterface() {
                        @Override
                        public void onFailure() {

                        }

                        @Override
                        public void onSucceed(String s) {
                            try {
                                JSONObject object = new JSONObject(s);
                                if (SString.getSuccess(object)) {
                                    JSONObject result = SString.getResult(object);
                                    Gson gson = new Gson();
                                    final ContactInfo info = gson.fromJson(result.toString(), ContactInfo.class);
                                    ContactInfoSqlManager.insertContactInfo(info);
                                    setContactInfo((ContactHolder) holder, info);
                                    ImageBiz.showImage(mContext, ((ContactHolder) holder).mImageView, ShareLockManager.getImgUrl(info.getU_header()));


//                                    DownImgBiz biz = new DownImgBiz();
//                                    biz.downLoadHeardImg(mContext, , info.getU_header(), userName, R.drawable.icon_touxiang_persion_gray);


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
            contactHolder.selected.setVisibility(View.VISIBLE);
            contactHolder.mTextView.setText(userName);
            //判断是不是群组中的成员
            if (GroupMemberSqlManager.isExitGroupMember(groupId, userName)) {
                //是
                contactHolder.selected.setEnabled(false);
            } else {
                //不是
                contactHolder.selected.setEnabled(true);

            }
            if (contactHolder.selected.isEnabled()) {
                if (selectedContacts.contains(userName)) {
                    contactHolder.selected.setChecked(true);
                } else {
                    contactHolder.selected.setChecked(false);
                }

                contactHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (selectedContacts == null) {
                            return;
                        }
                        if (selectedContacts.contains(userName)) {
                            selectedContacts.remove(userName);
                        } else {
                            selectedContacts.add(userName);
                        }
                        if (adapter == null) {
                            adapter = (RecyclerWrapAdapter) recyclerView.getAdapter();
                        }
                        adapter.notifyItemChanged(position + adapter.getHeadersCount());

                    }
                });
            }

        }

    }

    private void setContactInfo(ContactHolder holder, ContactInfo info) {
        holder.mTextView.setText(info.getName());
        // 判断是否是高管并显示
        if (info.getUser_type().equals(SString.MEMBER) && info.getUser_level().equals(SString.GAOGUAN)) {
            holder.mTagImageView.setVisibility(View.VISIBLE);
        } else {
            holder.mTagImageView.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemViewType(int position) {

        return resultList.get(position).getType();
    }

    @Override
    public int getItemCount() {

        return resultList == null ? 0 : resultList.size();
    }

    public class CharacterHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        CharacterHolder(View view) {
            super(view);

            mTextView = (TextView) view.findViewById(R.id.character);
        }
    }

    public class ContactHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        ImageView mImageView;
        ImageView mTagImageView;
        CheckBox selected;

        ContactHolder(View view) {
            super(view);

            mTextView = (TextView) view.findViewById(R.id.contact_name);
            mImageView = (ImageView) view.findViewById(R.id.contact_heard_iv);
            mTagImageView = (ImageView) view.findViewById(R.id.img_tag);
            selected = (CheckBox) view.findViewById(R.id.contactitem_select_cb);

        }
    }

    public int getScrollPosition(String character) {
        if (characterList.contains(character)) {
            for (int i = 0; i < resultList.size(); i++) {
                if (resultList.get(i).getU_username().equals(character)) {
                    return i + 1;
                }
            }
        }

        return -1; // -1不会滑动
    }

    //获取到选择的联系人
    public ArrayList<String> getSelectedContacts() {
        return selectedContacts;
    }
}
