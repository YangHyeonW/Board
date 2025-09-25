package com.example.board.service;

import com.example.board.dto.BoardDto;
import jakarta.validation.Valid;

import java.util.List;

public interface BoardService {
    BoardDto.DetailResponse getBoardDetail(Long id);

    List<BoardDto.ListResponse> getBoardList(int page, int pageSize);

    int getTotalBoardCount();

    Long createBoard(BoardDto.@Valid Request request, Long userId);

    BoardDto.DetailResponse updateBoard(Long id, BoardDto.@Valid Request request, Long userId);

    boolean deleteBoard(Long id, Long userId);
}
