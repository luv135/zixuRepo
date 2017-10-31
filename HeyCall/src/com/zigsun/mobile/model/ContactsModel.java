package com.zigsun.mobile.model;

import android.text.TextUtils;
import android.util.Log;

import com.zigsun.bean.UserInfo;
import com.zigsun.mobile.module.ContactItem;
import com.zigsun.mobile.utils.Utils;
import com.zigsun.ui.contact.CharacterParser;
import com.zigsun.ui.contact.ContactMgr;
import com.zigsun.ui.contact.SortModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Luo on 2015/6/19.
 */
public class ContactsModel {
    private static final String TAG = ContactsModel.class.getSimpleName();
    private CharacterParser characterParser;
    private PinyinComparator pinyinComparator;
    private List<SortModel> toAdapterItems = new ArrayList<>();
//    private List<SortModel> allContacts;// = new ArrayList<>();

    private class PinyinComparator implements Comparator<ContactItem> {

        public int compare(ContactItem o1, ContactItem o2) {
//            if (o1.getSortLetters().equals("@") || o2.getSortLetters().equals("#")) {
//                return -1;
//            } else if (o1.getSortLetters().equals("#")
//                    || o2.getSortLetters().equals("@")) {
//                return 1;
//            } else {

                final int i = o1.getSortLetters().compareTo(o2.getSortLetters());
                return i == 0 ? o1.getInfoStatus().ordinal() - o2.getInfoStatus().ordinal() : i;
//            }
        }

    }

    public ContactsModel() {
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();

//        prepareContacts();
    }

//    private void prepareContacts() {
//        List<UserInfo> items = ContactMgr.getContacts();
//        Log.d(TAG, "items.size()=" + items.size());
//        // List<GroupItem> groups = ContactMgr.getGroups();
//        // Log.d(TAG,"groups.size()=" + groups.size());
//        // 实例化汉字转拼音类
//
//        allContacts = filledData(items);
//        // 根据a-z进行排序源数据
//
//    }

    public String queryByID(long id) {
        List<UserInfo> items = ContactMgr.getContacts();
        for (UserInfo m : items) {
            if (m.getUlUserID() == id) return m.getStrUserName();
        }
        return "";
    }

    public List<SortModel> getEMptyAdapterItems() {
        return toAdapterItems;
    }

    public List<SortModel> getContactsList(String name) {
        Log.d(TAG, "getContactsList name :" + name);
        String buffer = dealName(name);
        final List<UserInfo> xy = ContactMgr.filterContacts(buffer);
//        return filledData(xy);
        Log.d(TAG, "getContactsList size :" + xy.size());
        toAdapterItems.clear();
        toAdapterItems.addAll(filledData(xy));

//
//        Log.d(TAG,"test:-----------------------------");
//        List<UserInfo> phoneContacts = ContactMgr.getPhoneContacts();
//        for (UserInfo i : phoneContacts) {
//            Log.d(TAG, i.toString());
//        }
//        Log.d(TAG,"-----------------------------");


        return toAdapterItems;
    }

    private String dealName(String name) {
        if (TextUtils.isEmpty(name)) return null;
        return "%" + name + "%";
//        StringBuffer buffer = new StringBuffer();
//        for (int i = 0; i < name.length(); i++) {
//            buffer.append("%").append(name.charAt(i));
//        }
//        buffer.append("%");
//        return buffer.toString();
    }


//    public void filterContacts(String name) {
//        toAdapterItems.clear();
//        if(name==null){
//            toAdapterItems.addAll(allContacts);
//        } else{
//            for(SortModel m: allContacts){
//                final String s = m.getName().toLowerCase();
//                final String s1 = name.toLowerCase();
//                for(int i=0;i<s1.length();i++){
//                    if(!s.contains()
//                }
//            }
//        }
//        Collections.sort(toAdapterItems, pinyinComparator);
//    }


    /**
     * @param tel userName 手机号码
     * @return
     */
    public String qureyName(String tel) {
        return getName(tel);
    }

    private String getName(String strUserName) {
        return getName(strUserName, ContactMgr.getPhoneContacts());
    }

    //private static NickNameCache nickNameCache = new NickNameCache();
    private static HashMap<String, String> nickCache = new HashMap<>();

    private String getName(String number, List<UserInfo> target) {
        String nickName = nickCache.get(number);
        if (nickName != null) {
            Log.d(TAG, "Cache exist: " + number + " - " + nickName);
            return nickName;
        }
        nickName = number;
        Log.d(TAG, "getName target size: " + target.size());
        for (UserInfo i : target) {
            final String replace = i.getStrUserName().replace(" ", "");
            Log.d(TAG, "replace: " + replace + " number:" + number + " : " + replace.equals(number));
            if (replace.equals(number)) {
                nickName = i.getStrNickName();
                Log.d(TAG, "equal strNickName: " + nickName);
                target.remove(i);
                break;
            }
        }
        Log.d(TAG, "put :" + nickCache.put(number, nickName));
        return nickName;
    }

    private List<ContactItem> filledData(List<UserInfo> items) {
        List<ContactItem> mSortList = new ArrayList<ContactItem>();
        final List<UserInfo> contacts = ContactMgr.getPhoneContacts();
        for (int i = 0; i < items.size(); i++) {
            ContactItem sortModel = Utils.wrapC(items.get(i));
            sortModel.setUlUserId(items.get(i).getUlUserID());
            sortModel.setName(items.get(i).getStrUserName());
            sortModel.setNickName(getName(items.get(i).getStrUserName(), contacts));
            // 汉字转换成拼音
            String pinyin = characterParser.getSelling(sortModel.getNickName());
            if (TextUtils.isEmpty(pinyin)) {
                pinyin = "#";
            }
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }
            mSortList.add(sortModel);
        }
        Collections.sort(mSortList, pinyinComparator);
        return mSortList;

    }


    public void delete(long userid) {
        ContactMgr.delMember(userid);
    }

    public List<SortModel> getEmptyItems() {
        toAdapterItems.clear();
        return toAdapterItems;
    }

    public void updateUserStatus(long id, byte st) {
//         List<SortModel> contactsList = getContactsList(null);
//        for (SortModel i : contactsList) {
//            Log.d(TAG, ((ContactItem)i).toString());
//        }
        Log.d(TAG, "updateUserStatus(long id, byte st)" + ContactMgr.updateStutus(id, st));
//       contactsList = getContactsList(null);
//        for (SortModel i : contactsList) {
//            Log.d(TAG, ((ContactItem)i).toString());
//        }
    }
}
