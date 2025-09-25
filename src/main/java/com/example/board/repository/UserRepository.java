package com.example.board.repository;

import com.example.board.model.User;
import java.util.Optional;

/**
 * 사용자 리포지토리 인터페이스
 * - 사용자 데이터에 접근하는 메서드를 정의합니다.
 */
public interface UserRepository {

    /**
     * 사용자 저장
     * @param user 저장할 사용자 객체
     * @return 저장된 사용자 객체 (ID 포함)
     */
    User save(User user);

    /**
     * ID로 사용자 조회
     * @param id 사용자 ID
     * @return 사용자 객체 (Optional로 래핑)
     */
    Optional<User> findById(Long id);

    /**
     * 사용자 이름으로 사용자 조회
     * @param username 사용자 이름
     * @return 사용자 객체 (Optional로 래핑)
     */
    Optional<User> findByUsername(String username);

    /**
     * 이메일로 사용자 조회
     * @param email 이메일
     * @return 사용자 객체 (Optional로 래핑)
     */
    Optional<User> findByEmail(String email);

    /**
     * 사용자 이름 존재 여부 확인
     * @param username 사용자 이름
     * @return 존재 여부
     */
    boolean existsByUsername(String username);

    /**
     * 이메일 존재 여부 확인
     * @param email 이메일
     * @return 존재 여부
     */
    boolean existsByEmail(String email);
}
