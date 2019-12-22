package com.hzm.chatdemo.service.impl;

import com.hzm.chatdemo.enums.SearchFriendsStatusEnum;
import com.hzm.chatdemo.mapper.FriendsRequestMapper;
import com.hzm.chatdemo.mapper.MyFriendsMapper;
import com.hzm.chatdemo.mapper.UsersMapper;
import com.hzm.chatdemo.mapper.UsersMapperCustom;
import com.hzm.chatdemo.pojo.FriendsRequest;
import com.hzm.chatdemo.pojo.MyFriends;
import com.hzm.chatdemo.pojo.Users;
import com.hzm.chatdemo.service.UserService;
import com.hzm.chatdemo.utils.FastDFSClient;
import com.hzm.chatdemo.utils.FileUtils;
import com.hzm.chatdemo.utils.QRCodeUtils;
import com.hzm.chatdemo.vo.FriendRequestVO;
import com.hzm.chatdemo.vo.MyFriendsVO;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private MyFriendsMapper myFriendsMapper;

    @Autowired
    private FriendsRequestMapper friendsRequestMapper;

    @Autowired
    private Sid sid;

    @Autowired
    private QRCodeUtils qrCodeUtils;

    @Autowired
    private FastDFSClient fastDFSClient;

    @Autowired
    private UsersMapperCustom usersMapperCustom;


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryUserNameIsExist(String username) {
        Users user = new Users();
        user.setUsername(username);
        Users result = usersMapper.selectOne(user);

        return result != null ? true : false;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserForLogin(String username, String pwd) {
        //工具类，通过example构造相对应的条件
        Example userExample = new Example(Users.class);
        Example.Criteria criteria = userExample.createCriteria();
        //传入对象属性名，和前端传过来的属性值，将数据库中的值同前端传递来的值进行比对
        criteria.andEqualTo("username",username);
        criteria.andEqualTo("password",pwd);

        Users result = usersMapper.selectOneByExample(userExample);
        return result;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users saveUser(Users users) {
        //生成一个唯一的id
        String userId = sid.nextShort();
        //生成二维码存储地址
        String qrCodePath = "/Users/bojackslittletrick/Documents/tmp/qrcode/"+userId+"qrcode.png";
        //自定义规则： [horse_qrcode:usernaem]
        qrCodeUtils.createQRCode(qrCodePath,"horse_qrcode:"+users.getUsername());
        //将二维码上传至服务器
        MultipartFile qrCodeFile = FileUtils.fileToMultipart(qrCodePath);
        String qrCodeURL = "";
        try {
            qrCodeURL = fastDFSClient.uploadQRCode(qrCodeFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        users.setQrcode(qrCodeURL);

        users.setId(userId);

        usersMapper.insert(users);
        return users;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users updateUserInfo(Users users) {
        usersMapper.updateByPrimaryKeySelective(users);
        return queryUserById(users.getId());
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Integer preconditionSearchFriends(String myUserId, String friendUsername) {
        //1.搜索的用户如果不存在
        Users users = queryUserInfoByUsername(friendUsername);
        if (users==null){
            return SearchFriendsStatusEnum.USER_NOT_EXIST.status;
        }
        //2.搜索的账号为自己
        if(users.getId().equals(myUserId)){
            return SearchFriendsStatusEnum.NOT_YOURSELF.status;
        }
        //3.搜索的账号已添加
        Example mfe = new Example(MyFriends.class);
        Example.Criteria mfc = mfe.createCriteria();
        mfc.andEqualTo("myUserId",myUserId);
        mfc.andEqualTo("myFriendUserId",users.getId());
        MyFriends myFriends = myFriendsMapper.selectOneByExample(mfe);
        if (myFriends != null){
            return SearchFriendsStatusEnum.ALREADY_FRIENDS.status;
        }

        return SearchFriendsStatusEnum.SUCCESS.status;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserInfoByUsername(String username){
        Example ue = new Example(Users.class);
        Example.Criteria uc = ue.createCriteria();
        uc.andEqualTo("username",username);
        return  usersMapper.selectOneByExample(ue);

    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void sendFriendRequest(String myUserId, String friendUsername) {

        //判断当前记录表是否存在该条记录
        Users friend = queryUserInfoByUsername(friendUsername);
        //1.查询发送好友请求记录表
        Example fre = new Example(FriendsRequest.class);
        Example.Criteria frc = fre.createCriteria();
        frc.andEqualTo("sendUserId",myUserId);
        frc.andEqualTo("acceptUserId",friend.getId());
        FriendsRequest friendRequest = friendsRequestMapper.selectOneByExample(fre);
        if (friendRequest == null){
            //如果不是你的好友，并且好友记录没有添加，则新增好友
            String requestId = sid.nextShort();
            FriendsRequest request = new FriendsRequest();
            request.setId(requestId);
            request.setSendUserId(myUserId);
            request.setAcceptUserId(friend.getId());
            request.setRequestDateTime(new Date());
            friendsRequestMapper.insert(request);
        }

    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<FriendRequestVO> queryFriendRequestList(String acceptUserId){
        return usersMapperCustom.queryFriendRequestList(acceptUserId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteFriendRequest(String sendUserId, String acceptUserId) {
        System.out.println("删除");
        Example fre = new Example(FriendsRequest.class);
        Example.Criteria frc = fre.createCriteria();
        frc.andEqualTo("sendUserId",sendUserId);
        frc.andEqualTo("acceptUserId",acceptUserId);
        friendsRequestMapper.deleteByExample(fre);

    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void passFriendRequest(String sendUserId, String acceptUserId) {
        System.out.println("通过");
        saveFriends(sendUserId,acceptUserId);
        saveFriends(acceptUserId,sendUserId);
        deleteFriendRequest(sendUserId,acceptUserId);
    }

    @Transactional(propagation =  Propagation.SUPPORTS)
    @Override
    public List<MyFriendsVO> queryMyFriends(String userId) {
        List<MyFriendsVO> myFriends= usersMapperCustom.queryMyFriends(userId);
        return myFriends;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    private void saveFriends(String sendUserId,String acceptUserId){
        MyFriends myFriends = new MyFriends();
        String recordId = sid.nextShort();
        myFriends.setId(recordId);
        myFriends.setMyUserId(sendUserId);
        myFriends.setMyFriendUserId(acceptUserId);
        myFriendsMapper.insert(myFriends);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    private Users queryUserById(String userId){
        return usersMapper.selectByPrimaryKey(userId);
    }
}
