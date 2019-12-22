package com.hzm.chatdemo.bo;

public class UsersBO {
    private String userId;
    private String facedata;
    private String nickname;
    private String code;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFacedata() {
        return facedata;
    }

    public void setFacedata(String facedata) {
        this.facedata = facedata;
    }
}
