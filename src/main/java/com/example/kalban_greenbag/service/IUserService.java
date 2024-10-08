package com.example.kalban_greenbag.service;

import com.example.kalban_greenbag.dto.request.user.CreateUserRequest;
import com.example.kalban_greenbag.dto.request.user.LoginRequest;
import com.example.kalban_greenbag.dto.request.user.UpdateUserRequest;
import com.example.kalban_greenbag.dto.response.JwtAuthenticationResponse;
import com.example.kalban_greenbag.dto.response.user.UserResponse;
import com.example.kalban_greenbag.exception.BaseException;
import java.util.UUID;

public interface IUserService extends IGenericService<UserResponse> {
    JwtAuthenticationResponse login(LoginRequest loginRequest) throws BaseException;
    JwtAuthenticationResponse create(CreateUserRequest createUserRequest) throws BaseException;
    UserResponse updateUser(UUID userId, UpdateUserRequest updateUserRequest) throws BaseException;

}
