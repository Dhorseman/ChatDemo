package com.hzm.chatdemo.pojo;

import java.util.Date;
import javax.persistence.*;

@Table(name = "chat_msg")
public class ChatMsg {
    @Id
    private String id;

    /**
     * 发送人id
     */
    @Column(name = "send_user_id")
    private String sendUserId;

    /**
     * 接收人id
     */
    @Column(name = "accept_user_id")
    private String acceptUserId;

    private String msg;

    /**
     * 签收、已读标记
     */
    @Column(name = "sign_flag")
    private Integer signFlag;

    @Column(name = "create_time")
    private Date createTime;

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
     * 获取发送人id
     *
     * @return send_user_id - 发送人id
     */
    public String getSendUserId() {
        return sendUserId;
    }

    /**
     * 设置发送人id
     *
     * @param sendUserId 发送人id
     */
    public void setSendUserId(String sendUserId) {
        this.sendUserId = sendUserId;
    }

    /**
     * 获取接收人id
     *
     * @return accept_user_id - 接收人id
     */
    public String getAcceptUserId() {
        return acceptUserId;
    }

    /**
     * 设置接收人id
     *
     * @param acceptUserId 接收人id
     */
    public void setAcceptUserId(String acceptUserId) {
        this.acceptUserId = acceptUserId;
    }

    /**
     * @return msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * @param msg
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * 获取签收、已读标记
     *
     * @return sign_flag - 签收、已读标记
     */
    public Integer getSignFlag() {
        return signFlag;
    }

    /**
     * 设置签收、已读标记
     *
     * @param signFlag 签收、已读标记
     */
    public void setSignFlag(Integer signFlag) {
        this.signFlag = signFlag;
    }

    /**
     * @return create_time
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}