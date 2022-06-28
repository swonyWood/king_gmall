package com.atguigu.gmall.front.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Kingstu
 * @date 2022/6/24 12:37
 * @description
 */
@Controller
public class LoginController {

    @GetMapping("/login.html")
    public  String loginPage() {
        return "login";
    }
}
