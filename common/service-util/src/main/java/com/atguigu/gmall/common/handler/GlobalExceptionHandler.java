package com.atguigu.gmall.common.handler;

import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Kingstu
 * @date 2022/6/22 12:47
 * @description
 */
//@ControllerAdvice
//@ResponseBody

/**
 * 处理全局异常
 * 1.所有的业务异常都是new GmallException
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常直接根据业务码响应错误
     * @param e
     * @return
     */
    @ExceptionHandler(GmallException.class)
    public Result handleBizException(GmallException e){

        Result<String> result = new Result<>();
        result.setCode(e.getCode());
        result.setMessage(e.getMessage());
        result.setData("");

        return result;
    }


    @ExceptionHandler(Exception.class)
    public Result handleBizException(Exception e){
        Result<Object> fail = Result.fail();
        fail.setMessage(e.getMessage());
        return fail;
    }
}
