package com.zigsun.mobile.module;

/**
 * Created by Luo on 2015/7/16.
 */
public class ContactsLetterItem extends ContactItem{
    public String getLetter() {
        return letter;
    }

    private String letter;

    public ContactsLetterItem(String letter) {
        this.letter = letter;
    }

    @Override
    public String getSortLetters() {
        return getLetter();
    }
}
