package com.reopenai.reactives.grpc.client.invoker;

import io.grpc.Channel;
import org.springframework.core.Ordered;

/**
 * Created by Allen Huang
 */
public interface GrpcClientInvokerFactory extends Ordered {

    GrpcClientInvoker create(Channel channel, GrpcMethodDetail methodDetail);

    @Override
    default int getOrder() {
        return Integer.MIN_VALUE;
    }

}
