package com.example.board.controller;


import com.example.board.dto.BoardDto;
import com.example.board.dto.ResponseDto;
import com.example.board.service.BoardService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;
    private static final int PAGE_SIZE = 10;

    // 생성자 주입
    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    // 게시글 목록 조회
    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "0") int page, Model model) {

        // 게시글 목록 조회
        List<BoardDto.ListResponse> boards = boardService.getBoardList(page, PAGE_SIZE);

        // 전체 게시글 수 조회
        int totalCount = boardService.getTotalBoardCount();

        // 전체 페이지 수 계산
        int totalPages = (int) Math.ceil((double) totalCount / PAGE_SIZE); // Math.ceil => 올림 함수

        // 모델에 데이터  추가
        model.addAttribute("boards", boards);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "board/list"; // html 로 보내는 리턴

    }


    // 게시글 작성 페이지
    @GetMapping("/write")
    public String writeForm(Model model, HttpSession session) {

        // 로그인 여부 확인
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/user/login?redirect=/board/write";
        }

        model.addAttribute("boardRequest", new BoardDto.Request());
        return "board/write";
    }

    // 게시글 등록
    @PostMapping("/write")
    public String write(@Valid @ModelAttribute("boardRequest") BoardDto.Request request,
                        BindingResult bindingResult,
                        HttpSession session,
                        Model model) {
        // 로그인 여부 확인
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/user/login?redirect=/board/write";
        }

        // 유효성 검사 실패 시 작성 페이지로 다시 이동
        if (bindingResult.hasErrors()) {
            return "board/write";
        }

        try {
            // 게시글 등록
            Long boardId = boardService.createBoard(request, userId);
            return "redirect:/board/view/" + boardId;
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "board/write";
        }
    }

    /**
     * 게시글 상세 조회
     */
    @GetMapping("/view/{id}")
    public String view(@PathVariable Long id, Model model, HttpSession session) {
        try {
            // 게시글 상세 조회
            BoardDto.DetailResponse board = boardService.getBoardDetail(id);

            // 모델에 데이터 추가
            model.addAttribute("board", board);

            // 로그인한 사용자 ID (수정/삭제 권한 확인용)
            model.addAttribute("currentUserId", session.getAttribute("userId"));

            return "board/view";
        } catch (IllegalArgumentException e) {
            return "redirect:/board/list";
        }
    }

    /**
     * 게시글 수정 페이지 표시
     */
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model, HttpSession session) {
        // 로그인 여부 확인
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/user/login?redirect=/board/edit/" + id;
        }

        try {
            // 게시글 상세 조회
            BoardDto.DetailResponse board = boardService.getBoardDetail(id);

            // 작성자 확인
            if (!board.getUserId().equals(userId)) {
                return "redirect:/board/view/" + id;
            }

            // 수정 폼에 데이터 바인딩
            BoardDto.Request request = new BoardDto.Request();
            request.setTitle(board.getTitle());
            request.setContent(board.getContent());

            model.addAttribute("boardId", id);
            model.addAttribute("boardRequest", request);

            return "board/edit";
        } catch (IllegalArgumentException e) {
            return "redirect:/board/list";
        }
    }

    /**
     * 게시글 수정 처리
     */
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id,
                       @Valid @ModelAttribute("boardRequest") BoardDto.Request request,
                       BindingResult bindingResult,
                       HttpSession session,
                       Model model) {
        // 로그인 여부 확인
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/user/login?redirect=/board/edit/" + id;
        }

        // 유효성 검사 실패 시 수정 페이지로 다시 이동
        if (bindingResult.hasErrors()) {
            model.addAttribute("boardId", id);
            return "board/edit";
        }

        try {
            // 게시글 수정
            boardService.updateBoard(id, request, userId);
            return "redirect:/board/view/" + id;
        } catch (IllegalArgumentException e) {
            model.addAttribute("boardId", id);
            model.addAttribute("errorMessage", e.getMessage());
            return "board/edit";
        }
    }

    /**
     * 게시글 삭제 처리
     */
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, HttpSession session) {
        // 로그인 여부 확인
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/user/login";
        }

        try {
            // 게시글 삭제
            boardService.deleteBoard(id, userId);
            return "redirect:/board/list";
        } catch (IllegalArgumentException e) {
            return "redirect:/board/view/" + id;
        }
    }

    /**
     * REST API: 게시글 목록 조회
     */
    @GetMapping("/api/list")
    @ResponseBody
    public ResponseDto<List<BoardDto.ListResponse>> listApi(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<BoardDto.ListResponse> boards = boardService.getBoardList(page, size);
        return ResponseDto.success("게시글 목록 조회 성공", boards);
    }

    /**
     * REST API: 게시글 상세 조회
     */
    @GetMapping("/api/view/{id}")
    @ResponseBody
    public ResponseDto<BoardDto.DetailResponse> viewApi(@PathVariable Long id) {
        try {
            BoardDto.DetailResponse board = boardService.getBoardDetail(id);
            return ResponseDto.success("게시글 조회 성공", board);
        } catch (IllegalArgumentException e) {
            return ResponseDto.fail(e.getMessage());
        }
    }

    /**
     * REST API: 게시글 등록
     */
    @PostMapping("/api/write")
    @ResponseBody
    public ResponseDto<Long> writeApi(
            @Valid @RequestBody BoardDto.Request request,
            HttpSession session) {

        // 로그인 여부 확인
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseDto.fail("로그인이 필요합니다.");
        }

        try {
            Long boardId = boardService.createBoard(request, userId);
            return ResponseDto.success("게시글 등록 성공", boardId);
        } catch (IllegalArgumentException e) {
            return ResponseDto.fail(e.getMessage());
        }
    }

    /**
     * REST API: 게시글 수정
     */
    @PutMapping("/api/edit/{id}")
    @ResponseBody
    public ResponseDto<BoardDto.DetailResponse> editApi(
            @PathVariable Long id,
            @Valid @RequestBody BoardDto.Request request,
            HttpSession session) {

        // 로그인 여부 확인
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseDto.fail("로그인이 필요합니다.");
        }

        try {
            BoardDto.DetailResponse board = boardService.updateBoard(id, request, userId);
            return ResponseDto.success("게시글 수정 성공", board);
        } catch (IllegalArgumentException e) {
            return ResponseDto.fail(e.getMessage());
        }
    }

    /**
     * REST API: 게시글 삭제
     */
    @DeleteMapping("/api/delete/{id}")
    @ResponseBody
    public ResponseDto<Boolean> deleteApi(
            @PathVariable Long id,
            HttpSession session) {

        // 로그인 여부 확인
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseDto.fail("로그인이 필요합니다.");
        }

        try {
            boolean result = boardService.deleteBoard(id, userId);
            return ResponseDto.success("게시글 삭제 성공", result);
        } catch (IllegalArgumentException e) {
            return ResponseDto.fail(e.getMessage());
        }
    }


}
