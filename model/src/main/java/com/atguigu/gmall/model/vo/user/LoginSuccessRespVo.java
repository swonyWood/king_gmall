package com.atguigu.gmall.model.vo.user;

import lombok.Data;

/**
 * @author Kingstu
 * @date 2022/7/4 19:49
 */
@Data
public class LoginSuccessRespVo {

    private String token;
    private String nickName;
}
