package com.atguigu.gmall.user.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.IpUtil;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.model.vo.user.LoginSuccessRespVo;
import com.atguigu.gmall.user.service.UserInfoService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Kingstu
 * @date 2022/7/4 19:15
 */
@RestController
@RequestMapping("/api/user")
public class LoginController {

    UserInfoService userInfoService;

    //有参构造注入
    public LoginController(UserInfoService userInfoService){
        this.userInfoService = userInfoService;
    }

    /**
     * 登录
     * @param info
     * @param request
     * @return
     */
    @PostMapping("/passport/login")
    public Result login(@RequestBody UserInfo info,
                        HttpServletRequest request){

        String ipAddress = IpUtil.getIpAddress(request);
        LoginSuccessRespVo vo = userInfoService.login(info,ipAddress);
        return Result.ok(vo);
    }

    /**
     * 退出
     * 前端把token放请求头
     * @return
     */
    @GetMapping("/passport/logout")
    public Result logout(@RequestHeader("token") String token){
        userInfoService.logout(token);
        return Result.ok();

    }


}
