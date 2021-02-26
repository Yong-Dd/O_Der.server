package com.yongdd.o_der_re.server;

public class UserId {
    String userId;
    User user;

    public UserId(String userId, User user) {
        this.userId = userId;
        this.user = user;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
