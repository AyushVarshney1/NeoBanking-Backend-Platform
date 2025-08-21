package com.neobank.neobankaccountservice.grpc;

import auth.AuthRequest;
import auth.AuthResponse;
import auth.AuthServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceGrpcClient {
    private static final Logger log = LoggerFactory.getLogger(AuthServiceGrpcClient.class);
    private final AuthServiceGrpc.AuthServiceBlockingStub blockingStub;

    public AuthServiceGrpcClient(@Value("${auth.service.address:localhost}") String serverAddress,
                                 @Value("${auth.service.grpc.port:9001}") int serverPort) {

        log.info("Connecting to Auth service GRPC service at {}:{}", serverAddress, serverPort);

        ManagedChannel channel = ManagedChannelBuilder.forAddress(serverAddress, serverPort).usePlaintext().build();

        blockingStub = AuthServiceGrpc.newBlockingStub(channel);
    }

    public String extractUserId(String token) {

        AuthRequest request = AuthRequest.newBuilder().setToken(token).build();

        AuthResponse response = blockingStub.extractUserId(request);

        log.info("Received response from Auth service via GRPC: {}", response);

        return response.getUserId();
    }
}