package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.GameHistory;
import ch.uzh.ifi.hase.soprafs23.entity.SudokuResponse;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.GameHistoryRepository;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Controller
public class GameSudokuController {

    private final UserService userService;
    private final GameHistoryRepository gameHistoryRepository;

    public GameSudokuController(UserService userService, GameHistoryRepository gameHistoryRepository) {
        this.userService = userService;
        this.gameHistoryRepository = gameHistoryRepository;
    }


    @GetMapping("/sudoku/create")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String[][] getQuestion(@RequestBody String requiredDifficulty) {

        RestTemplate restTemplate = new RestTemplate();

        SudokuResponse response = restTemplate.getForObject("https://sudoku-api.vercel.app/api/dosuku", SudokuResponse.class);

        String[][] value = null;
        while (value == null || !response.getNewBoard().getGrids().get(0).getDifficulty().equals(requiredDifficulty)) {
            response = restTemplate.getForObject("https://sudoku-api.vercel.app/api/dosuku", SudokuResponse.class);
            assert response != null;
            value = response.getNewBoard().getGrids().get(0).getValue();
        }

        return value;
    }

    //if the answer is correct, return true
    @PutMapping("/sudoku/validation/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Boolean validateAnswer(@PathVariable("userId") long userId, @RequestBody String[][] answer, String difficulty){
        Boolean IsPass = isValidSudoku(answer);
        if(IsPass){
            User user = userService.getUserById(userId);
            GameHistory gameHistory = new GameHistory();
            gameHistory.setUsername(user.getUsername());
            gameHistory.setGameName("Sudoku");
            gameHistory.setWinOrLose("Win");
            gameHistory.setTime(LocalDateTime.now());
            if(Objects.equals(difficulty, "Hard")){
                gameHistory.setEarnedPoint("+6");
                user.setScore(user.getScore()+6);
            }else if(Objects.equals(difficulty, "Medium")){
                gameHistory.setEarnedPoint("+4");
                user.setScore(user.getScore()+4);
            } else if (Objects.equals(difficulty, "Easy")) {
                gameHistory.setEarnedPoint("+2");
                user.setScore(user.getScore()+2);
            }
            gameHistoryRepository.save(gameHistory);
            gameHistoryRepository.flush();
        }
        return IsPass;
    }

    public boolean isValidSudoku(String[][] board) {
        // check each row
        for (int row = 0; row < 9; row++) {
            if (!isValidRow(board, row)) {
                return false;
            }
        }

        // check each column
        for (int col = 0; col < 9; col++) {
            if (!isValidCol(board, col)) {
                return false;
            }
        }

        // check each 3x3 subgrid
        for (int row = 0; row < 9; row += 3) {
            for (int col = 0; col < 9; col += 3) {
                if (!isValidSubgrid(board, row, col)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean isValidRow(String[][] board, int row) {
        Set<String> set = new HashSet<>();
        for (int col = 0; col < 9; col++) {
            String num = board[row][col];
            if (num.equals(".")) {
                continue;
            }
            if (!set.add(num)) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidCol(String[][] board, int col) {
        Set<String> set = new HashSet<>();
        for (int row = 0; row < 9; row++) {
            String num = board[row][col];
            if (num.equals(".")) {
                continue;
            }
            if (!set.add(num)) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidSubgrid(String[][] board, int row, int col) {
        Set<String> set = new HashSet<>();
        for (int i = row; i < row + 3; i++) {
            for (int j = col; j < col + 3; j++) {
                String num = board[i][j];
                if (num.equals(".")) {
                    continue;
                }
                if (!set.add(num)) {
                    return false;
                }
            }
        }
        return true;
    }


}
