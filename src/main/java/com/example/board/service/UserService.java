package com.example.board.service;

import com.example.board.dto.UserDto;
import com.example.board.model.User;

import java.util.Optional;

/**
 * 사용자 서비스 인터페이스
 * - 사용자 관련 비즈니스 로직을 정의합니다.
 */
public interface UserService {

    /**
     * 회원가입
     * @param registerRequest 회원가입 요청 DTO
     * @return 등록된 사용자 정보
     */
    UserDto.Response register(UserDto.RegisterRequest registerRequest);

    /**
     * 로그인
     * @param loginRequest 로그인 요청 DTO
     * @return 로그인 성공 시 사용자 정보, 실패 시 빈 Optional
     */
    Optional<User> login(UserDto.LoginRequest loginRequest);

    /**
     * 사용자 ID로 사용자 정보 조회
     * @param id 사용자 ID
     * @return 사용자 정보 DTO
     */
    UserDto.Response getUserById(Long id);
}