package com.neobank.neobanktransactionservice.grpc;

import auth.AuthRequest;
import auth.AuthResponse;
import auth.AuthServiceGrpc;
import com.neobank.neobanktransactionservice.dto.AuthGrpcResponseDto;
import com.neobank.neobanktransactionservice.exception.UnauthorizedException;
import com.neobank.neobanktransactionservice.exception.UserNotFoundException;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
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

    public AuthGrpcResponseDto extractUserIdAndEmail(String token) {

        AuthRequest request = AuthRequest.newBuilder().setToken(token).build();

        try {
            AuthResponse response = blockingStub.extractUserIdAndEmail(request);
            log.info("Received response from Auth service via GRPC: {}", response);
            AuthGrpcResponseDto authGrpcResponseDto = new  AuthGrpcResponseDto();
            authGrpcResponseDto.setUserId(response.getUserId());
            authGrpcResponseDto.setEmail(response.getEmail());
            return authGrpcResponseDto;

        } catch (StatusRuntimeException e) {
            switch (e.getStatus().getCode()) {
                case UNAUTHENTICATED -> {
                    log.warn("Invalid or expired token: {}", e.getStatus().getDescription());
                    throw new UnauthorizedException(
                            e.getStatus().getDescription() != null ? e.getStatus().getDescription() : "Invalid token"
                    );
                }
                case NOT_FOUND -> {
                    log.warn("User not found for token: {}", e.getStatus().getDescription());
                    throw new UserNotFoundException(
                            e.getStatus().getDescription() != null ? e.getStatus().getDescription() : "User ID not found"
                    );
                }
                default -> {
                    log.error("Auth service gRPC error: {} - {}", e.getStatus().getCode(), e.getMessage());
                    throw new RuntimeException("Auth service error: " + e.getStatus().getDescription(), e);
                }
            }
        }
    }
}