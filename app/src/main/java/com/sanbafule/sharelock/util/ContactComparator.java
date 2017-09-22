package com.sanbafule.sharelock.util;

import com.sanbafule.sharelock.modle.Contact;

import java.util.Comparator;

/**
 * Created by Administrator on 2016/11/3.
 */
public class ContactComparator implements Comparator<Contact> {

    @Override
    public int compare(Contact o1, Contact o2) {
        String pinyin1 = PinyinUtils.getPingYin(o1.getName());
        String pinyin2 = PinyinUtils.getPingYin(o1.getName());
        int c1 = (pinyin1.charAt(0) + "").toUpperCase().hashCode();
        int c2 = (pinyin2.charAt(0) + "").toUpperCase().hashCode();
        boolean c1Flag = (c1 < "A".hashCode() || c1 > "Z".hashCode()); // 不是字母
        boolean c2Flag = (c2 < "A".hashCode() || c2 > "Z".hashCode()); // 不是字母
        if (c1Flag && !c2Flag) {
            return 1;
        } else if (!c1Flag && c2Flag) {
            return -1;
        }

        return c1 - c2;
    }

}
