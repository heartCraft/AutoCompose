package com.zhuo.auto.compose.core.exception;

/**
 * 编排执行中，检测到数据缓存丢失（报错的组件类，缺失数据缓存传递，请检查代码）。
 *
 * @Author wangzhuo
 * @Date: 2020/9/15 18:09
 */
public class LostDataHolderException extends RuntimeException {

    public LostDataHolderException(Class clazz) {
        super("Error! Lost DataHolder: " + clazz.getSimpleName() + ".class");
    }
}
