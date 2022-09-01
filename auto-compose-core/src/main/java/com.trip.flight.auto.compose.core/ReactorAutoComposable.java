package com.trip.flight.auto.compose.core;

import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author wangzhuo
 * @Date: 2022/8/28 18:37
 */
public interface ReactorAutoComposable extends AutoComposable<Mono<Void>> {

    @Override
    default Mono<Void> executeTemplate(Optional<Stream<? extends Mono<Void>>> dependenciesOptional) {
        return dependenciesOptional
                .map(stream -> stream.collect(Collectors.toList()))
                .map(Mono::when)
                .orElse(Mono.empty())
                .then(Mono.defer(() -> Optional.ofNullable(this.postExecute(this.execute())).orElse(Mono.empty())).cache());
    }
}
