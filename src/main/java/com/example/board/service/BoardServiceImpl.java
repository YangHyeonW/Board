package com.example.board.service;


import com.example.board.dto.BoardDto;
import com.example.board.model.Board;
import com.example.board.repository.BoardRepository;
import com.example.board.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;


    BoardServiceImpl(BoardRepository boardRepository, UserRepository userRepository) {
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
    }



    // 게시글 상세 조회
    @Override
    public BoardDto.DetailResponse getBoardDetail(Long id) {

        Board board = boardRepository.findById(id);

        // 조회 수 증가

        boardRepository.incrementViewCount(id);
        board.setViewCount(board.getViewCount() + 1);

        return new BoardDto.DetailResponse(
                board.getId(),
                board.getTitle(),
                board.getContent(),
                board.getUserId(),
                board.getUsername(),
                board.getViewCount(),
                board.getCreatedAt(),
                board.getUpdatedAt()
        );
    }


    // 게시글 목록 조회
    @Override
    public List<BoardDto.ListResponse> getBoardList(int page, int pageSize) {

        // 페이징 처리를 하려면 offset이란게 필요
        int offset = page * pageSize;

        List<Board> boards = boardRepository.findAll(offset,pageSize);

        return boards.stream()
                .map(board-> new BoardDto.ListResponse(
                        board.getId(),
                        board.getTitle(),
                        board.getUsername(),
                        board.getViewCount(),
                        board.getCreatedAt()
                ))
                .toList();
    }


    // 전체 게시글 수 조회
    @Override
    public int getTotalBoardCount() {
        return boardRepository.count();
    }


    // 게시글 등록
    @Override
    @Transactional
    public Long createBoard(BoardDto.@Valid Request request, Long userId) {


        // 게시글 객체 생성
        Board board = new Board();
        board.setTitle(request.getTitle());
        board.setContent(request.getContent());
        board.setUserId(userId);

        Board saveBoard = boardRepository.save(board);

        return saveBoard.getId();
    }


    // 게시글 수정
    @Override
    @Transactional
    public BoardDto.DetailResponse updateBoard(Long id, BoardDto.@Valid Request request, Long userId) {

        Board board = boardRepository.findById(id);
        board.setTitle(request.getTitle());
        board.setContent(request.getContent());

        Board updateBoard = boardRepository.save(board);


        return new BoardDto.DetailResponse(
                board.getId(),
                board.getTitle(),
                board.getContent(),
                board.getUserId(),
                board.getUsername(),
                board.getViewCount(),
                board.getCreatedAt(),
                board.getUpdatedAt()
        );
    }

    @Override
    @Transactional
    public boolean deleteBoard(Long id, Long userId) {
        Board board = boardRepository.findById(id);

        // 유효성 검사

        return boardRepository.deleteById(id);
    }
}
