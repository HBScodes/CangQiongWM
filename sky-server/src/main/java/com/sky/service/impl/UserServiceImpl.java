package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    //微信服务接口地址
    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;

    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    public User wxLogin(UserLoginDTO userLoginDTO) {

        //调用 getOpenid私有方法，获取当前微信用户的Openid
        String openid = getOpenid(userLoginDTO.getCode());

        // 判断 openid 是否为空，如果为空表示登录失败，抛出业务异常
        if (openid == null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        // 判断当前用户是否为新用户
        User user = userMapper.getByOpenid(openid);

        // 如果是新用户,自动完成注册
        if (user == null){
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();

            userMapper.insert(user);
        }

        //返回这个用户对象
        return user;
    }

    /**
     * 调用微信接口服务，获取微信用户的 openid
     * @param code
     * @return
     */
    private String getOpenid(String code){
        // 调用微信接口服务，获取当前微信用户的Openid
        // 使用 HashMap 封装请求参数
        HashMap<String, String> map = new HashMap<>();
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code",code);
        map.put("grant_type","authorization_code");
        // 获得请求得到的返回参数
        String json = HttpClientUtil.doGet(WX_LOGIN, map);
        // 将返回参数转换为 JSON 格式
        JSONObject jsonObject = JSON.parseObject(json);
        //获取 openid
        String openid = jsonObject.getString("openid");

        return openid;
    }
}
