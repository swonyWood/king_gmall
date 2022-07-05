package com.atguigu.gmall.user.service;

import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.model.vo.user.LoginSuccessRespVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 */
public interface UserInfoService extends IService<UserInfo> {

    /**
     * 用户登录
     * @param info
     * @param ipAddress
     * @return
     */
    LoginSuccessRespVo login(UserInfo info, String ipAddress);

    /**
     * 退出
     * @param token
     */
    void logout(String token);
}
