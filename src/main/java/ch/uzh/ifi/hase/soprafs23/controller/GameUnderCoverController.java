package ch.uzh.ifi.hase.soprafs23.controller;


import ch.uzh.ifi.hase.soprafs23.entity.GameUndercover;
import ch.uzh.ifi.hase.soprafs23.entity.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.service.RoomService;
import ch.uzh.ifi.hase.soprafs23.service.UCService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class GameUnderCoverController {

    private final UCService ucService;
    private final RoomService roomService;
    private final UserService userService;

    GameUnderCoverController(UCService ucService, RoomService roomService, UserService userService) {
        this.ucService = ucService;
        this.roomService = roomService;
        this.userService = userService;
    }

    @GetMapping("undercover/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameUndercover getGameByGameId(@PathVariable("gameId") long gameId){
        return ucService.getGameById(gameId);
    }

    /*create an undercover game by the following steps
    * add a player list from room object to the game
    * decide who is undercover and allocate word
    * set the current player and set game status
    * */
    @PostMapping("/undercover/rooms/{roomId}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public GameUndercover createGame(@PathVariable("roomId") long roomId) {
        Room room = roomService.getRoomById(roomId);
        if(room.getGameUndercover()!=null){
            return room.getGameUndercover();
        }
        GameUndercover gameundercover= ucService.createGame(room);
        System.out.println("------------gameInput------------");
        return gameundercover;
    }

    /*
    * when a user ends his/her description, call this put method to set currentPlayer to the next user
    * or if all users have finished, set the game status to voting.*/
    @PutMapping("/undercover/{gameId}/users/{userId}/description")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameUndercover describe(@PathVariable("gameId") long gameId, @PathVariable("userId") long userId, @RequestBody String description){
        GameUndercover gameUndercover = ucService.getGameById(gameId);
        User describedUser = userService.getUserById(userId);
        describedUser.setDescription(description);
        gameUndercover=ucService.describe(gameUndercover, describedUser);
        return gameUndercover;
    }

    /*
    * when a round of voting finished, use this put mapping
    * to update the voting result and determine whether game ends*/
    @PutMapping("/undercover/{gameId}/votes/{voteUserId}/{votedUserId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameUndercover vote(@PathVariable("gameId") long gameId, @PathVariable("voteUserId") long voteUserId,
    @PathVariable("votedUserId") long votedUserId){
        GameUndercover gameUndercover = ucService.getGameById(gameId);
        User voteUser = userService.getUserById(voteUserId);
        User votedUser = userService.getUserById(votedUserId);
        // verify if the vote is valid
        if(voteUser.isVoted()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are out, please wait until the game ends:)");
        }
        if(votedUser.isVoted()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You select someone who is out, please choose again");
        }
        // add vote and verify whether everyone has voted
        Boolean ifEnds = ucService.voteAndCheckIfEnds(gameUndercover, votedUser);

        if(!ifEnds){
            return gameUndercover;
        }else{
            //get user who gets most votes
            List<User> outUser = ucService.getOutUsers(gameUndercover);
            return ucService.vote(gameUndercover, outUser);
        }
    }




}
