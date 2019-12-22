package com.hzm.chatdemo.pojo;

import javax.persistence.*;

public class Users {
    @Id
    private String id;

    private String username;

    private String password;

    /**
     * 用户头像（小）
     */
    @Column(name = "face_image")
    private String faceImage;

    /**
     * 用户头像（大）
     */
    @Column(name = "face_image_big")
    private String faceImageBig;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 二维码
     */
    private String qrcode;

    /**
     * clientId针对手机用户，用作消息推送
     */
    private String cid;

    /**
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取用户头像（小）
     *
     * @return face_image - 用户头像（小）
     */
    public String getFaceImage() {
        return faceImage;
    }

    /**
     * 设置用户头像（小）
     *
     * @param faceImage 用户头像（小）
     */
    public void setFaceImage(String faceImage) {
        this.faceImage = faceImage;
    }

    /**
     * 获取用户头像（大）
     *
     * @return face_image_big - 用户头像（大）
     */
    public String getFaceImageBig() {
        return faceImageBig;
    }

    /**
     * 设置用户头像（大）
     *
     * @param faceImageBig 用户头像（大）
     */
    public void setFaceImageBig(String faceImageBig) {
        this.faceImageBig = faceImageBig;
    }

    /**
     * 获取用户昵称
     *
     * @return nickname - 用户昵称
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * 设置用户昵称
     *
     * @param nickname 用户昵称
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * 获取二维码
     *
     * @return qrcode - 二维码
     */
    public String getQrcode() {
        return qrcode;
    }

    /**
     * 设置二维码
     *
     * @param qrcode 二维码
     */
    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    /**
     * 获取clientId针对手机用户，用作消息推送
     *
     * @return cid - clientId针对手机用户，用作消息推送
     */
    public String getCid() {
        return cid;
    }

    /**
     * 设置clientId针对手机用户，用作消息推送
     *
     * @param cid clientId针对手机用户，用作消息推送
     */
    public void setCid(String cid) {
        this.cid = cid;
    }
}