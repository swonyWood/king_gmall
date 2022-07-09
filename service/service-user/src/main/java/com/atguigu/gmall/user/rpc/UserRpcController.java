package com.atguigu.gmall.user.rpc;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.user.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Kingstu
 * @date 2022/7/8 21:02
 */
@RestController
@RequestMapping("/rpc/inner/user")
public class UserRpcController {


    @Autowired
    UserAddressService userAddressService;

    /**
     * 获取用户收获地址列表
     * @return
     */
    @GetMapping("/address/list")
    public Result<List<UserAddress>> getUserAddress(){

        List<UserAddress> list = userAddressService.getUserAddress();
        return Result.ok(list);
    }
}
