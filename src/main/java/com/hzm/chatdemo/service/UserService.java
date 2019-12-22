package com.hzm.chatdemo.service;

import com.hzm.chatdemo.pojo.Users;
import com.hzm.chatdemo.vo.FriendRequestVO;
import com.hzm.chatdemo.vo.MyFriendsVO;

import java.util.List;

public interface UserService {
    /**
     * 查询用户名是否存在
     * @param username
     * @return
     */
    public boolean queryUserNameIsExist(String username);

    /**
     * 查询用户是否存在
     * @param username
     * @param pwd
     * @return
     */
    public Users queryUserForLogin(String username,String pwd);

    /**
     * 用户注册
     * @param users
     * @return
     */
    public Users saveUser(Users users);

    /**
     * 修改用户记录
     * @param users
     */
    public Users updateUserInfo(Users users);

    /**
     * 搜索朋友的前置条件
     * @param myUserId
     * @param friendUsername
     * @return
     */
    public Integer preconditionSearchFriends(String myUserId,String friendUsername);

    /**
     * 根据用户名查询用户对象
     * @param username
     * @return
     */
    public Users queryUserInfoByUsername(String username);

    /**
     * 添加好友请求记录保存至数据库
     * @param myUserId
     * @param friendUsername
     */
    void sendFriendRequest(String myUserId, String friendUsername);

    /**
     * 查询好友请求
     * @param acceptUserId
     * @return
     */
    List<FriendRequestVO> queryFriendRequestList(String acceptUserId);

    /**
     * 删除好友请求记录
     * @param sendUserId
     * @param acceptUserId
     */
    public void deleteFriendRequest(String sendUserId,String acceptUserId);

    /**
     * 通过好友请求:
     * 1.保存好友
     * 2.逆向保存好友
     * 3.删除好友请求记录
     * @param sendUserId
     * @param acceptUserId
     */
    public void passFriendRequest(String sendUserId,String acceptUserId);

    /**
     * 查询好友列表
     * @param userId
     * @return
     */
    public List<MyFriendsVO> queryMyFriends(String userId);
}
