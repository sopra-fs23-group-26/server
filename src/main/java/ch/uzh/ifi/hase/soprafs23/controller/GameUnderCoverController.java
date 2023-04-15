package ch.uzh.ifi.hase.soprafs23.controller;


import ch.uzh.ifi.hase.soprafs23.entity.GameUndercover;
import ch.uzh.ifi.hase.soprafs23.entity.Room;
import ch.uzh.ifi.hase.soprafs23.service.RoomService;
import ch.uzh.ifi.hase.soprafs23.service.UCService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class GameUnderCoverController {

    private final UCService ucService;
    private final RoomService roomService;

    GameUnderCoverController(UCService ucService, RoomService roomService) {
        this.ucService = ucService;
        this.roomService = roomService;
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
        return ucService.createGame(room);
    }


}
