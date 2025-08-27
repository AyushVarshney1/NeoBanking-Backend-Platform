package com.neobank.authservice.grpc;

import auth.*;
import com.neobank.authservice.exception.InvalidTokenException;
import com.neobank.authservice.exception.UserNotFoundException;
import com.neobank.authservice.model.User;
import com.neobank.authservice.service.AuthService;
import io.grpc.Status;
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
    public void extractUserIdAndEmail(AuthRequest authRequest, StreamObserver<AuthResponse> responseObserver) {
        log.info("extractUserIdAndEmail request received and token is : {}", authRequest.getToken());

        try {
            User user = authService.extractUser(authRequest.getToken());

            AuthResponse response = AuthResponse.newBuilder()
                    .setUserId(user.getId().toString())
                    .setEmail(user.getEmail())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (InvalidTokenException e) {
            log.error("Invalid or expired JWT: {}", e.getMessage());
            responseObserver.onError(
                    Status.UNAUTHENTICATED
                            .withDescription(e.getMessage())
                            .augmentDescription(e.getMessage())
                            .asRuntimeException()
            );

        } catch (UserNotFoundException e) {
            log.error("User not found for token: {}", e.getMessage());
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("User ID/Email not found")
                            .asRuntimeException()
            );

        } catch (Exception e) {
            log.error("Unexpected error in extractUserId", e);
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Auth Service internal error")
                            .augmentDescription(e.getMessage())
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void extractUserIdAndRole(AuthRoleRequest authRoleRequest, StreamObserver<AuthRoleResponse> responseObserver) {
        log.info("extractUserIdAndRole request received and token is : {}", authRoleRequest.getToken());

        try {
            User user = authService.extractUser(authRoleRequest.getToken());

            AuthRoleResponse response = AuthRoleResponse.newBuilder()
                    .setUserId(user.getId().toString())
                    .setRole(user.getRole().toString())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (InvalidTokenException e) {
            log.error("Invalid or expired JWT: {}", e.getMessage());
            responseObserver.onError(
                    Status.UNAUTHENTICATED
                            .withDescription(e.getMessage())
                            .augmentDescription(e.getMessage())
                            .asRuntimeException()
            );

        } catch (UserNotFoundException e) {
            log.error("User not found for token: {}", e.getMessage());
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("User ID/Email not found")
                            .asRuntimeException()
            );

        } catch (Exception e) {
            log.error("Unexpected error in extractUserId", e);
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Auth Service internal error")
                            .augmentDescription(e.getMessage())
                            .asRuntimeException()
            );
        }
    }
}
