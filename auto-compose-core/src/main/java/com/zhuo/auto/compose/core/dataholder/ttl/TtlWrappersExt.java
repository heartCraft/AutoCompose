package com.zhuo.auto.compose.core.dataholder.ttl;

import com.alibaba.ttl.TtlUnwrap;
import com.alibaba.ttl.TtlWrappers;
import com.alibaba.ttl.spi.TtlEnhanced;
import com.alibaba.ttl.spi.TtlWrapper;
import com.google.common.util.concurrent.*;

import java.util.concurrent.CompletableFuture;

import static com.alibaba.ttl.TransmittableThreadLocal.Transmitter.*;

/**
 * @author wangzhuoc
 * @created 2024/12/06
 */
public class TtlWrappersExt {

    public static <V> CompletableFuture<V> wrapCompletableFuture(CompletableFuture<V> input) {
        CompletableFuture output = new CompletableFuture();
        input.handle(TtlWrappers.wrapBiFunction((result, throwable) -> {
            if (throwable != null) {
                output.completeExceptionally(throwable);
            } else {
                output.complete(result);
            }
            return null;
        }));
        return output;
    }

    public static <V> ListenableFuture<V> wrapListenableFuture(ListenableFuture<V> future) {
        SettableFuture<V> settableFuture = SettableFuture.create();
        Futures.addCallback(future, TtlWrappersExt.wrapFutureCallback(new FutureCallback<V>() {
            @Override
            public void onSuccess(V result) {
                settableFuture.set(result);
            }

            @Override
            public void onFailure(Throwable t) {
                settableFuture.setException(t);
            }
        }), MoreExecutors.directExecutor());
        return settableFuture;
    }


    /**
     * wrap {@link FutureCallback} to TTL FutureCallback.
     *
     * @param fc input {@link FutureCallback}
     * @return Wrapped {@link FutureCallback}
     * @see TtlUnwrap#unwrap(Object)
     * @since 2.12.4
     */
    public static <V> FutureCallback<V> wrapFutureCallback(FutureCallback<V> fc) {
        if (fc == null) {
            return null;
        } else if (fc instanceof TtlEnhanced) {
            return fc;
        } else {
            return new TtlFutureCallback<>(fc);
        }
    }

    private static class TtlFutureCallback<V> implements FutureCallback<V>, TtlWrapper<FutureCallback<V>>, TtlEnhanced {
        final FutureCallback<V> fc;
        final Object captured;

        TtlFutureCallback(FutureCallback<V> fc) {
            this.fc = fc;
            this.captured = capture();
        }

        @Override
        public void onSuccess(V v) {
            final Object backup = replay(captured);
            try {
                fc.onSuccess(v);
            } finally {
                restore(backup);
            }
        }

        @Override
        public void onFailure(Throwable throwable) {
            final Object backup = replay(captured);
            try {
                fc.onFailure(throwable);
            } finally {
                restore(backup);
            }
        }

        @Override
        public FutureCallback<V> unwrap() {
            return fc;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            TtlFutureCallback<?> that = (TtlFutureCallback<?>) o;

            return fc.equals(that.fc);
        }

        @Override
        public int hashCode() {
            return fc.hashCode();
        }

        @Override
        public String toString() {
            return this.getClass().getName() + " - " + fc.toString();
        }
    }

}
