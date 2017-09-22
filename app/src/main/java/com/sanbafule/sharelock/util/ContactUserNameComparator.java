package com.sanbafule.sharelock.util;

import com.sanbafule.sharelock.modle.Contact;

import java.util.Comparator;

/**
 * Created by Administrator on 2016/11/3.
 */
public class ContactUserNameComparator implements Comparator<Contact> {

    @Override
    public int compare(Contact contact1, Contact contact2) {
        //使用账号 排序

        //1 直接比较显示的 进行排序
        //2 获取 首字母
        // 3 添加小灰条 的实体



        String pinyin1 = PinyinUtils.getPingYin(contact1.getU_username());
        String pinyin2 = PinyinUtils.getPingYin(contact2.getU_username());
        return pinyin1.compareTo(pinyin2);
    }

}
