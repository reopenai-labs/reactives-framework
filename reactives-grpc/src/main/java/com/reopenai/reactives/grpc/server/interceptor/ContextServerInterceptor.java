package com.reopenai.reactives.grpc.server.interceptor;

import com.reopenai.reactives.grpc.common.metadata.GrpcKeys;
import io.grpc.*;

/**
 * Created by Allen Huang
 */
public class ContextServerInterceptor implements ServerInterceptor {

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
        Context context = Context.current();
        for (GrpcKeys value : GrpcKeys.values()) {
            String payload = metadata.get(value.getMetadataKey());
            context = context.withValue(value.getContextKey(), payload);
        }
        return Contexts.interceptCall(context, serverCall, metadata, serverCallHandler);
    }

}
