package com.zhuo.auto.compose.example.module;

import com.zhuo.auto.compose.core.annotation.AutoComposableBeanDesc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @Author wangzhuo
 * @Date: 2022/8/30 23:22
 */
@Component
@AutoComposableBeanDesc("安装电池")
public class AssembleBattery implements AssembleCarParts {

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
                purchaseBattery.getExecuteResult(),
                makeCarFrame.getExecuteResult());
        return true;
    }

}
