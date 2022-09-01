package com.zhuo.auto.compose.example.module;

import com.zhuo.auto.compose.core.annotation.AutoComposableBeanDesc;
import com.zhuo.auto.compose.core.autocomposable.ReactorAutoComposable;
import com.zhuo.auto.compose.example.RequestCarTypeHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @Author wangzhuo
 * @Date: 2022/8/30 22:40
 */
@Component
@AutoComposableBeanDesc("生产汽车end")
public class MakeCarEnd implements ReactorAutoComposable.Sync<String> {

    @Autowired
    private List<AssembleCarParts> assembleCarPartsList;

    @Autowired
    private RequestCarTypeHolder requestCarTypeHolder;

    @Override
    public String syncExecute() {
        String car = requestCarTypeHolder.get() +
                (assembleCarPartsList.stream()
                        .map(AssembleCarParts::getExecuteResult)
                        .allMatch(b -> Optional.ofNullable(b).orElse(false))
                        ? "生产成功"
                        : "生产失败");
        // log
        System.out.printf("%s %s: %s%n%n",
                LocalDateTime.now(),
                Thread.currentThread().getName(),
                car);

        return car;
    }
}
