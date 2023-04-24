package ch.uzh.ifi.hase.soprafs23.controller;


import ch.uzh.ifi.hase.soprafs23.entity.GameUndercover;
import ch.uzh.ifi.hase.soprafs23.entity.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.service.RoomService;
import ch.uzh.ifi.hase.soprafs23.service.UCService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
        GameUndercover gameundercover= ucService.createGame(room);
        return gameundercover;
    }

    /*
    * when a user ends his/her description, call this put method to set currentPlayer to the next user
    * or if all users have finished, set the game status to voting.*/
    @PutMapping("/undercover/{gameId}/users/{userId}/description")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameUndercover describe(@PathVariable("gameId") long gameId, @PathVariable long userId, @RequestBody String description){
        GameUndercover gameUndercover = ucService.getGameById(gameId);
        User describedUser = userService.getUserById(userId);
        describedUser.setDescription(description);
        return ucService.describe(gameUndercover, describedUser);
    }

    /*
    * when a round of voting finished, use this put mapping
    * to update the voting result and determine whether game ends*/
    @PutMapping("/undercover/{gameId}/votes")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameUndercover vote(@PathVariable("gameId") long gameId,@RequestBody User votedUser){
        GameUndercover gameUndercover = ucService.getGameById(gameId);
        return ucService.vote(gameUndercover, votedUser);
    }




}
