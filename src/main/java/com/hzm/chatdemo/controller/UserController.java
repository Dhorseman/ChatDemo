package com.hzm.chatdemo.controller;

import com.hzm.chatdemo.bo.UsersBO;
import com.hzm.chatdemo.enums.OperatorFriendRequestTypepEnum;
import com.hzm.chatdemo.enums.SearchFriendsStatusEnum;
import com.hzm.chatdemo.pojo.Users;
import com.hzm.chatdemo.service.UserService;
import com.hzm.chatdemo.utils.FastDFSClient;
import com.hzm.chatdemo.utils.FileUtils;
import com.hzm.chatdemo.utils.HorseJSONResult;
import com.hzm.chatdemo.utils.MD5Utils;
import com.hzm.chatdemo.utils.QRCodeUtils;
import com.hzm.chatdemo.vo.MyFriendsVO;
import com.hzm.chatdemo.vo.UsersVO;
import org.apache.catalina.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/horseUser")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private FastDFSClient fastDFSClient;



   @PostMapping("registOrLogin")
    public HorseJSONResult registOrLogin(@RequestBody Users user) throws Exception {
       System.out.println("1");
        //非空判断
       if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())){
            return HorseJSONResult.errorMsg("用户名或密码不能为空！");
       }
        //通过用户名判断用户是否存在，存在则登录
        boolean usernameIsExist = userService.queryUserNameIsExist(user.getUsername());
        Users userResult = null;
       if (usernameIsExist){
           //1.1登录
           userResult = userService.queryUserForLogin(user.getUsername(),MD5Utils.getMD5Str(user.getPassword()));
           if (userResult == null){
               return HorseJSONResult.errorMsg("用户名或密码错误！");
           }
       }else{
           //1.2注册
           user.setNickname(user.getUsername());
           user.setFaceImage("");
           user.setFaceImageBig("");
           user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
           userResult = userService.saveUser(user);
       }
       UsersVO userVO = new UsersVO();
       //通过BeanUtils.copyProperties快速拷贝类的属性值
       BeanUtils.copyProperties(userResult,userVO);
       System.out.println("2");
       return HorseJSONResult.ok(userVO);
   }

    /**
     *
     * @return
     */
   @PostMapping("/uploadFaceBase64")
    public HorseJSONResult uploadFaceBase64(@RequestBody UsersBO usersBO) throws Exception{

       System.out.println("3");
       //获取前端传过来的base64字符串，然后转换为文件对象再上传
       String base64Data = usersBO.getFacedata();
       //定义一个存储地址
       String userFacePath = "/Users/bojackslittletrick/Documents/tmp" + usersBO.getUserId() + "userface64.png";
       //通过spring提供的工具类将base64转换成byte数组，再通过Apache提供的工具类输出成文件
       FileUtils.base64ToFile(userFacePath,base64Data);
       //转换成Multipart文件（spring的一种类型文件）,最终上传的是Multipart文件
       MultipartFile faceFile = FileUtils.fileToMultipart(userFacePath);
       //通过fastDFS上传,传入参数类型为Multipart文件，返回一个文件地址，
       //此时会fastDFS会自动存储一份原图和缩略图，缩略图地址为原url+"80*80"(application中定义的大小).png
       String url = fastDFSClient.uploadBase64(faceFile);
       System.out.println(url);
        //获取缩略图的URL,将原地址进行切分,再组装
       String thump = "_80*80.";
       String arr[] = url.split("\\.");
       String thumpImgUrl = arr[0] + thump +arr[1];
       //更新用户头像
       Users users = new Users();
       users.setId(usersBO.getUserId());
       users.setFaceImage(thumpImgUrl);
       users.setFaceImageBig(url);
       users = userService.updateUserInfo(users);
       return HorseJSONResult.ok(users);
   }

    /**
     * 设置用户昵称
     * @param usersBO
     * @return
     * @throws Exception
     */
   @PostMapping("/setNickname")
   public HorseJSONResult setNickname(@RequestBody UsersBO usersBO) throws Exception{
       System.out.println("4");
       Users users = new Users();
       if (StringUtils.isEmpty(usersBO.getUserId()) || "".equals(usersBO.getUserId())){
           return HorseJSONResult.errorMsg("用户id不合法");
       }
       users.setId(usersBO.getUserId());
       if (StringUtils.isEmpty(usersBO.getNickname()) || "".equals(usersBO.getNickname()) || usersBO.getNickname().length()>8){
           return HorseJSONResult.errorMsg("昵称不能为空，且长度不能超过8个字符");
       }
       users.setNickname(usersBO.getNickname());

       Users result = userService.updateUserInfo(users);
       System.out.println(result.getUsername());
       return  HorseJSONResult.ok(result);
   }

    /**
     * 搜索好友接口，根据账号作匹配查询而不是模糊查询，不能添加自己为好友，不能添加已有好友
     * @param myUserId
     * @param friendUsername
     * @return
     * @throws Exception
     */
   @PostMapping("/search")
    public HorseJSONResult searchUser(String myUserId,String friendUsername) throws Exception{
       //非空判断
       if (StringUtils.isBlank(myUserId) || StringUtils.isBlank(friendUsername)){

           return HorseJSONResult.errorMsg("添加用户名不能为空！");
       }
       //前置条件 -1 .搜索的用户如果不存在，返回消息
       //前置条件 -2 .搜索的用户是自己，返回消息
       //前置条件 -3 .搜索的用户已为好友，返回消息
       System.out.println("5");
       Integer status = userService.preconditionSearchFriends(myUserId,friendUsername);
       if (status == SearchFriendsStatusEnum.SUCCESS.status){
           Users users = userService.queryUserInfoByUsername(friendUsername);
           UsersVO usersVO = new UsersVO();
           BeanUtils.copyProperties(users,usersVO);
           return HorseJSONResult.ok(usersVO);
       }else {
           String errorMsg = SearchFriendsStatusEnum.getMsgByKey(status);
           return HorseJSONResult.errorMsg(errorMsg);
       }
   }

    /**
     * 发送添加好友的请求
     * @param myUserId
     * @param friendUsername
     * @return
     * @throws Exception
     */
    @PostMapping("/addFriendRequest")
    public HorseJSONResult addFriendRequest(String myUserId,String friendUsername) throws Exception{
        //非空判断
        if (StringUtils.isBlank(myUserId) || StringUtils.isBlank(friendUsername)){

            return HorseJSONResult.errorMsg("添加用户名不能为空！");
        }
        //前置条件 -1 .搜索的用户如果不存在，返回消息
        //前置条件 -2 .搜索的用户是自己，返回消息
        //前置条件 -3 .搜索的用户已为好友，返回消息
        System.out.println("5");
        Integer status = userService.preconditionSearchFriends(myUserId,friendUsername);
        if (status == SearchFriendsStatusEnum.SUCCESS.status){
            userService.sendFriendRequest(myUserId,friendUsername);
        }else {
            String errorMsg = SearchFriendsStatusEnum.getMsgByKey(status);
            return HorseJSONResult.errorMsg(errorMsg);
        }
        return HorseJSONResult.ok();
    }

    @PostMapping("/queryFriendRequest")
    public HorseJSONResult queryFriendRequest(String userId){
        System.out.println("6");
        //非空判断
        if (StringUtils.isBlank(userId)){
            return HorseJSONResult.errorMsg("用户名不能为空！");
        }
        //查询用户接收到的朋友申请
        return HorseJSONResult.ok(userService.queryFriendRequestList(userId));

    }

    /**
     * 接收方 通过或者忽略好友请求
     * @param acceptUserId
     * @param sendUserId
     * @param operType
     * @return
     */
    @PostMapping("/operFriendRequest")
    public HorseJSONResult operFriendRequest(String acceptUserId,String sendUserId,Integer operType){
        //非空判断
        if (StringUtils.isBlank(acceptUserId) || StringUtils.isBlank(sendUserId) || operType==null){
            return HorseJSONResult.errorMsg("不能为空！");
        }
        //1.如果operType没有对应的枚举值，则抛出异常
        if (StringUtils.isBlank(OperatorFriendRequestTypepEnum.getMsgByType(operType))){
            return HorseJSONResult.errorMsg("找不到对应的操作符");
        }
        if (operType == OperatorFriendRequestTypepEnum.IGNORE.type){
            //2.判断如果忽略好友请求，则直接删除好友请求的数据库表记录
            System.out.println("c");
            userService.deleteFriendRequest(sendUserId,acceptUserId);
        }
        //3.如果通过好友请求，则互相增加好友记录到数据库，然后删除好友请求的数据库表记录
        else if(operType == OperatorFriendRequestTypepEnum.PASS.type){
            System.out.println("d");
            userService.passFriendRequest(sendUserId,acceptUserId);
        }
        //4.数据库查询好友列表
        List<MyFriendsVO> myFriends = userService.queryMyFriends(acceptUserId);
        return HorseJSONResult.ok(myFriends);

    }


    /**
     * 查询好友列表
     * @param userId
     * @return
     */
    @PostMapping("/myFriends")
    public HorseJSONResult myFriends(String userId){
        System.out.println("8");
        //非空判断
        if (StringUtils.isBlank(userId)){
            return HorseJSONResult.errorMsg("用户id不合法！");
        }
        //1.数据库查询好友列表
        List<MyFriendsVO> myFriends = userService.queryMyFriends(userId);

        for (MyFriendsVO m:myFriends){
            System.out.println(m.getFriendNickname());
        }
        return HorseJSONResult.ok(myFriends);
    }
}
