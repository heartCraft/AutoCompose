package com.trip.flight.auto.compose.core;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * @Author wangzhuo
 * @Date: 2022/8/30 23:33
 */
public interface CfAutoComposable extends AutoComposable<CompletableFuture<Void>> {

    @Override
    default CompletableFuture<Void> executeTemplate(Optional<Stream<? extends CompletableFuture<Void>>> dependenciesOptional) {
        return dependenciesOptional
                .map(stream -> stream.toArray(CompletableFuture[]::new))
                .map(CompletableFuture::allOf)
                .orElse(CompletableFuture.completedFuture(null))
                .thenCompose(v -> Optional.ofNullable(this.postExecute(this.execute())).orElse(CompletableFuture.completedFuture(null)));
    }
}
