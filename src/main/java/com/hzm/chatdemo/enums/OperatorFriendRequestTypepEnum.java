package com.hzm.chatdemo.enums;

/**
 * 忽略或通过好友请求的枚举
 */
public enum OperatorFriendRequestTypepEnum {
    IGNORE(0,"忽略"),
    PASS(1,"通过");

    public final Integer type;
    public final String msg;

    OperatorFriendRequestTypepEnum(Integer type,String msg){
        this.type = type;
        this.msg = msg;
    }
    public Integer getType(){
        return type;
    }
    public static String getMsgByType(Integer type){
        for (OperatorFriendRequestTypepEnum operType : OperatorFriendRequestTypepEnum.values()){
            if (operType.getType() == type){
                return operType.msg;
            }
        }
        return null;
    }
}
