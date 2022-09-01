package com.zhuo.auto.compose.core.exception;

/**
 * 编排执行前，检测到数据缓存未清理的异常
 *
 * @Author wangzhuo
 * @Date: 2020/9/15 18:09
 */
public class MissDataHolderResetException extends RuntimeException {

    public MissDataHolderResetException() {
        super("Error! Miss DataHolder Reset.");
    }
}
