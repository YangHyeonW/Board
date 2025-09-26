package com.example.board.repository;

import com.example.board.model.Board;

import java.util.List;

public interface BoardRepository {
    Board findById(Long id);

    List<Board> findAll(int offset, int pageSize);

    int count();

    Board save(Board board);

    boolean deleteById(Long id);

    void incrementViewCount(Long id);
}
