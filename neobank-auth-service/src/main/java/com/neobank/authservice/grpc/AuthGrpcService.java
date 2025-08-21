package com.neobank.authservice.grpc;

import auth.AuthRequest;
import auth.AuthResponse;
import auth.AuthServiceGrpc;
import com.neobank.authservice.model.User;
import com.neobank.authservice.service.AuthService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
@RequiredArgsConstructor
public class AuthGrpcService extends AuthServiceGrpc.AuthServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(AuthGrpcService.class);
    private final AuthService authService;

    @Override
    public void extractUserId(AuthRequest authRequest, StreamObserver<AuthResponse> responseObserver) {
        log.info("extractUserId request received and token is : {}", authRequest.getToken());

        User user = authService.extractUser(authRequest.getToken());

        AuthResponse response = AuthResponse.newBuilder()
                .setUserId(user.getId().toString())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
