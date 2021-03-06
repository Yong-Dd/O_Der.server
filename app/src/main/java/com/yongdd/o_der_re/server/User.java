package com.yongdd.o_der_re.server;

public class User {
    String userEmail;
    String userName;
    String userPhoneNumber;
    int userStamp;
    String userToken;

    public User(){}

    public User(String userEmail, String userName, String userPhoneNumber, int userStamp, String userToken) {
        this.userEmail = userEmail;
        this.userName = userName;
        this.userPhoneNumber = userPhoneNumber;
        this.userStamp = userStamp;
        this.userToken = userToken;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public int getUserStamp() {
        return userStamp;
    }

    public void setUserStamp(int userStamp) {
        this.userStamp = userStamp;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }
}
