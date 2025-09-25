package com.example.board.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import com.example.board.dto.ResponseDto;
import com.example.board.dto.UserDto;
import com.example.board.model.User;
import com.example.board.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * 사용자 컨트롤러
 * - 회원가입, 로그인 등 사용자 관련 요청을 처리합니다.
 */
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    // 생성자 주입
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 회원가입 페이지 표시
     */
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("registerRequest", new UserDto.RegisterRequest());
        return "user/register";
    }

    /**
     * 회원가입 처리
     */
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerRequest") UserDto.RegisterRequest registerRequest,
                           BindingResult bindingResult,
                           Model model) {
        // 유효성 검사 실패 시 회원가입 페이지로 다시 이동
        if (bindingResult.hasErrors()) {
            return "user/register";
        }

        try {
            // 회원가입 처리
            userService.register(registerRequest);
            return "redirect:/user/login?registered";
        } catch (IllegalArgumentException e) {
            // 중복 사용자 이름 또는 이메일 등의 예외 처리
            model.addAttribute("errorMessage", e.getMessage());
            return "user/register";
        }
    }

    /**
     * 로그인 페이지 표시
     */
    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("loginRequest", new UserDto.LoginRequest());
        return "user/login";
    }

    /**
     * 로그인 처리
     */
    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginRequest") UserDto.LoginRequest loginRequest,
                        BindingResult bindingResult,
                        HttpSession session,
                        Model model) {
        // 유효성 검사 실패 시 로그인 페이지로 다시 이동
        if (bindingResult.hasErrors()) {
            return "user/login";
        }

        // 로그인 처리
        Optional<User> userOptional = userService.login(loginRequest);

        if (userOptional.isPresent()) {
            // 로그인 성공 시 세션에 사용자 정보 저장
            User user = userOptional.get();
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());

            return "redirect:/board/list";
        } else {
            // 로그인 실패 시 에러 메시지 표시
            model.addAttribute("errorMessage", "사용자 이름 또는 비밀번호가 올바르지 않습니다.");
            return "user/login";
        }
    }

    /**
     * 로그아웃 처리
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // 세션 무효화
        session.invalidate();
        return "redirect:/user/login?logout";
    }

    /**
     * REST API: 회원가입
     */
    @PostMapping("/api/register")
    @ResponseBody
    public ResponseDto<UserDto.Response> registerApi(@Valid @RequestBody UserDto.RegisterRequest registerRequest) {
        try {
            UserDto.Response response = userService.register(registerRequest);
            return ResponseDto.success("회원가입이 완료되었습니다.", response);
        } catch (IllegalArgumentException e) {
            return ResponseDto.fail(e.getMessage());
        }
    }

    /**
     * REST API: 로그인
     */
    @PostMapping("/api/login")
    @ResponseBody
    public ResponseDto<UserDto.Response> loginApi(@Valid @RequestBody UserDto.LoginRequest loginRequest,
                                                  HttpSession session) {
        Optional<User> userOptional = userService.login(loginRequest);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());

            UserDto.Response response = new UserDto.Response(
                    user.getId(),
                    user.getUsername(),
                    user.getName(),
                    user.getEmail()
            );

            return ResponseDto.success("로그인이 완료되었습니다.", response);
        } else {
            return ResponseDto.fail("사용자 이름 또는 비밀번호가 올바르지 않습니다.");
        }
    }
}