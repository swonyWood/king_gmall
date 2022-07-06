package com.atguigu.gmall.common.retry;

import feign.RetryableException;
import feign.Retryer;

/**
 * @author Kingstu
 * @date 2022/7/6 9:48
 */
public class RequestNeverRetryer implements Retryer {
    @Override
    public void continueOrPropagate(RetryableException e) {
        throw e;
    }

    @Override
    public Retryer clone() {
        return this;
    }
}
