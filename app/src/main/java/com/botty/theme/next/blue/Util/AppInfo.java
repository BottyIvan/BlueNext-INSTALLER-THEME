package com.botty.theme.next.blue.Util;

/**
 * Created by ivanbotty on 29/05/14.
 */
import android.graphics.drawable.Drawable;

public class AppInfo {
    public String code = null;
    public String name = null;
    Drawable img;
    boolean selected = false;

    public AppInfo(String code, String name,Drawable img, boolean selected) {
        super();
        this.code = code;
        this.name = name;
        this.img =img;
        this.selected = selected;
    }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Drawable getImage() {
        return img;
    }
    public void setImage(Drawable img) {
        this.img = img;
    }
    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
