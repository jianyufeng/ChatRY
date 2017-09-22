package com.sanbafule.sharelock.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sanbafule.sharelock.R;
import com.sanbafule.sharelock.S_interface.HttpInterface;
import com.sanbafule.sharelock.UI.contact.ContactInfoActivity;
import com.sanbafule.sharelock.business.HttpBiz;
import com.sanbafule.sharelock.business.ImageBiz;
import com.sanbafule.sharelock.comm.SString;
import com.sanbafule.sharelock.comm.help.ShareLockManager;
import com.sanbafule.sharelock.db.ContactInfoSqlManager;
import com.sanbafule.sharelock.global.MyString;
import com.sanbafule.sharelock.global.Url;
import com.sanbafule.sharelock.modle.Contact;
import com.sanbafule.sharelock.modle.ContactInfo;
import com.sanbafule.sharelock.util.ContactComparator;
import com.sanbafule.sharelock.view.WrapRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 *
 */
public class ContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<Contact> contacts; // 最终结果（包含分组的字母）
    private List<String> characterList; // 字母List
    private List<Contact> resultList;
    private WrapRecyclerView recyclerView;
    private List<Contact> contactList;

    public enum ITEM_TYPE {
        ITEM_TYPE_CHARACTER,
        ITEM_TYPE_CONTACT
    }

    public ContactAdapter(Context context, List<Contact> contactList, WrapRecyclerView recyclerView) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        if (contactList == null) {
            contactList = new ArrayList<>();
        }
        this.recyclerView = recyclerView;
        this.contacts = contactList;
        handleContact();
    }

    public void refreshData(List<Contact> contacts) {
        if (contacts == null || contacts.isEmpty()) {
            return;
        }
        this.contacts = contacts;
        handleContact();
        final RecyclerWrapAdapter adapter = (RecyclerWrapAdapter) recyclerView.getAdapter();
        adapter.notifyDataSetChanged();

    }

    private void handleContact() {
        contactList = new ArrayList<>();
        for (int i = 0; i < contacts.size(); i++) {
            Contact contact = contacts.get(i);
            if (MyString.hasData(contacts.get(i).getName())) {
                contactList.add(contact);
            }
        }
        Collections.sort(contactList, new ContactComparator());//显示的 排序

        // 现根据userName排序
        //fori
        resultList = new ArrayList<>();  //添加 分类实体对象 后的实际数据
        characterList = new ArrayList<>(); //保存 分类的 首字母

        for (int i = 0; i < contactList.size(); i++) {
            Contact contact = contactList.get(i);
            String name = contact.getName();
            String character = (name.charAt(0) + "").toUpperCase(Locale.ENGLISH);
            if (!characterList.contains(character)) {
                if (character.hashCode() >= "A".hashCode() && character.hashCode() <= "Z".hashCode()) { // 是字母
                    characterList.add(character);
                    resultList.add(new Contact(character, ITEM_TYPE.ITEM_TYPE_CHARACTER.ordinal()));
                } else {
                    if (!characterList.contains("#")) {
                        characterList.add("#");
                        resultList.add(new Contact("#", ITEM_TYPE.ITEM_TYPE_CHARACTER.ordinal()));
                    }
                }
            }
            contact.setType(ITEM_TYPE.ITEM_TYPE_CONTACT.ordinal());
            resultList.add(contact);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE.ITEM_TYPE_CHARACTER.ordinal()) {
            return new CharacterHolder(mLayoutInflater.inflate(R.layout.item_character, parent, false));
        } else {
            return new ContactHolder(mLayoutInflater.inflate(R.layout.item_contact, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CharacterHolder) {
            ((CharacterHolder) holder).mTextView.setText(resultList.get(position).getU_username());
        } else if (holder instanceof ContactHolder) {

            final String userName = resultList.get(position).getU_username();
            // 查询数据库
            final ContactInfo info = ContactInfoSqlManager.getContactInfoFormuserName(userName);


            if (info != null) {
                setContactInfo((ContactHolder) holder, info);
                ImageBiz.showImage(mContext, ((ContactHolder) holder).mImageView, ShareLockManager.getImgUrl(info.getU_header()), R.drawable.icon_touxiang_persion_gray);
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
                                    ImageBiz.showImage(mContext, ((ContactHolder) holder).mImageView, ShareLockManager.getImgUrl(info.getU_header()), R.drawable.icon_touxiang_persion_gray);
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

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ContactInfoActivity.class);
                    intent.putExtra(SString.NAME, userName);
                    intent.putExtra(SString.CONTACTINFO, info);
                    mContext.startActivity(intent);
                }
            });
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

        ContactHolder(View view) {
            super(view);

            mTextView = (TextView) view.findViewById(R.id.contact_name);
            mImageView = (ImageView) view.findViewById(R.id.contact_heard_iv);
            mTagImageView = (ImageView) view.findViewById(R.id.img_tag);

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
}
