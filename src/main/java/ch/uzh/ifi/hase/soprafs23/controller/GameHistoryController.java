package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.GameHistory;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.service.GameHistoryService;
import ch.uzh.ifi.hase.soprafs23.service.UCService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class GameHistoryController {
    private final GameHistoryService gameHistoryService;

    private final UserService userService;

    private final UCService ucService;

    GameHistoryController(GameHistoryService gameHistoryService, UserService userService, UCService ucService){
        this.gameHistoryService = gameHistoryService;
        this.userService = userService;
        this.ucService=ucService;
    }


    @GetMapping("/gameHistory/{userId}/latestRecord")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameHistory getLatestRecord(@PathVariable("userId") long userId){
        User user = userService.getUserById(userId);
        if(user==null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Can't find user!");
        }
        return gameHistoryService.getLatestGameHistory(user);
    }

    @GetMapping("/gameHistory/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<GameHistory> getHistory(@PathVariable("userId") long userId){
        User user = userService.getUserById(userId);
        if(user==null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Can't find user!");
        }
        return gameHistoryService.getGameHistory(user);
    }




}
