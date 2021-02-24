package com.yongdd.o_der_re.server;

public class Menu {
    int menuId;
    int menuDelimiter;
    String menuName;
    int menuPrice;
    int menuHotIce;
    String menuImgPath;


    public Menu(){

    }

    public Menu( int menuId, int menuDelimiter, int menuHotIce, String menuImgPath, String menuName, int menuPrice ) {
        this.menuId = menuId;
        this.menuDelimiter = menuDelimiter;
        this.menuName = menuName;
        this.menuPrice = menuPrice;
        this.menuHotIce = menuHotIce;
        this.menuImgPath = menuImgPath;
    }

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public int getMenuPrice() {
        return menuPrice;
    }

    public void setMenuPrice(int menuPrice) {
        this.menuPrice = menuPrice;
    }

    public int getMenuHotIce() {
        return menuHotIce;
    }

    public void setMenuHotIce(int menuHotIce) {
        this.menuHotIce = menuHotIce;
    }

    public String getMenuImgPath() {
        return menuImgPath;
    }

    public void setMenuImgPath(String menuImgPath) {
        this.menuImgPath = menuImgPath;
    }
    public int getMenuDelimiter() {
        return menuDelimiter;
    }

    public void setMenuDelimiter(int menuDelimiter) {
        this.menuDelimiter = menuDelimiter;
    }

}
