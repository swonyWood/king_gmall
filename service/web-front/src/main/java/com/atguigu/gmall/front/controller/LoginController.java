package com.atguigu.gmall.front.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Kingstu
 * @date 2022/6/24 12:37
 * @description
 */
@Controller
public class LoginController {

    @GetMapping("/login.html")
    public  String loginPage(@RequestParam(value = "originUrl",defaultValue = "http://www.gmall.com")
                                         String originUrl, Model model) {
        //TODO ?后面的参数没了
        model.addAttribute("originUrl",originUrl);
        return "login";
    }
}
