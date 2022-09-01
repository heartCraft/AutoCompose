package com.zhuo.auto.compose.example.module;

import com.zhuo.auto.compose.core.annotation.AutoComposableBeanDesc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @Author wangzhuo
 * @Date: 2022/8/30 23:07
 */
@Component
@AutoComposableBeanDesc("安装电子设备")
public class AssembleAppliances implements AssembleCarParts {

    @Autowired
    private PurchaseBattery purchaseBattery;

    @Autowired
    private MakeCarFrame makeCarFrame;

    @Override
    public Boolean syncExecute() {
        if (purchaseBattery.getExecuteResult() == null || makeCarFrame.getExecuteResult() == null) {
            return false;
        }
        // log
        System.out.printf("%s %s: 安装%s至%s%n",
                LocalDateTime.now(),
                Thread.currentThread().getName(),
                "适配（" + purchaseBattery.getExecuteResult() + "）的电子设备",
                makeCarFrame.getExecuteResult());
        return true;
    }
}
