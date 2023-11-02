package com.example.onbroading_login;

public class SpinnerItem {
    private int iconResId;
    private String text;

    public SpinnerItem(int iconResId, String text) {
        this.iconResId = iconResId;
        this.text = text;
    }

    public int getIconResId() {
        return iconResId;
    }

    public String getText() {
        return text;
    }
}
