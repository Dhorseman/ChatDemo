package com.hzm.chatdemo.mapper;

import com.hzm.chatdemo.pojo.Users;
import com.hzm.chatdemo.utils.MyMapper;
import com.hzm.chatdemo.vo.FriendRequestVO;
import com.hzm.chatdemo.vo.MyFriendsVO;

import java.util.List;

public interface UsersMapperCustom extends MyMapper<Users> {
    public List<FriendRequestVO> queryFriendRequestList(String acceptUserId);

    public List<MyFriendsVO> queryMyFriends(String userId);
}