package com.yongdd.o_der_re.server;

import android.os.Parcel;
import android.os.Parcelable;

public class Banner implements Parcelable {
    String titleName;
    String menuName;
    String menuDesc;
    int menuId;
    String menuImgPath;

    public Banner(){}

    public Banner(String titleName, String menuName, String menuDesc, int menuId, String menuImgPath) {
        this.titleName = titleName;
        this.menuName = menuName;
        this.menuDesc = menuDesc;
        this.menuId = menuId;
        this.menuImgPath = menuImgPath;
    }

    protected Banner(Parcel in) {
        titleName = in.readString();
        menuName = in.readString();
        menuDesc = in.readString();
        menuId = in.readInt();
        menuImgPath = in.readString();
    }

    public static final Creator<Banner> CREATOR = new Creator<Banner>() {
        @Override
        public Banner createFromParcel(Parcel in) {
            return new Banner(in);
        }

        @Override
        public Banner[] newArray(int size) {
            return new Banner[size];
        }
    };

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getMenuDesc() {
        return menuDesc;
    }

    public void setMenuDesc(String menuDesc) {
        this.menuDesc = menuDesc;
    }

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public String getMenuImgPath() {
        return menuImgPath;
    }

    public void setMenuImgPath(String menuImgPath) {
        this.menuImgPath = menuImgPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(titleName);
        dest.writeString(menuName);
        dest.writeString(menuDesc);
        dest.writeInt(menuId);
        dest.writeString(menuImgPath);
    }
}
