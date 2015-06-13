package com.disycs.quizmo.model;

/**
 * Created by amine on 08/06/15.
 */
public class UserWrapper {
    String userName;
    String password;

    public UserWrapper(String userName, String password) {
        this.userName = userName;
        this.password = MyCryptoHelper.computePasswordHash(password);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = MyCryptoHelper.computePasswordHash(password);
    }
}
