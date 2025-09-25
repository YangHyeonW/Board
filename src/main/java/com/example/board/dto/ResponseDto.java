package com.example.board.dto;

/**
 * 공통 응답 DTO 클래스
 * - API 응답의 일관된 형식을 제공합니다.
 * - 성공/실패 여부, 메시지, 데이터를 포함합니다.
 */
public class ResponseDto<T> {
    private boolean success;
    private String message;
    private T data;

    // 성공 응답 생성 메서드
    public static <T> ResponseDto<T> success(T data) {
        return new ResponseDto<>(true, "성공적으로 처리되었습니다.", data);
    }

    // 성공 응답 생성 메서드 (메시지 포함)
    public static <T> ResponseDto<T> success(String message, T data) {
        return new ResponseDto<>(true, message, data);
    }

    // 실패 응답 생성 메서드
    public static <T> ResponseDto<T> fail(String message) {
        return new ResponseDto<>(false, message, null);
    }

    // 생성자
    private ResponseDto(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Getter 메서드
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}