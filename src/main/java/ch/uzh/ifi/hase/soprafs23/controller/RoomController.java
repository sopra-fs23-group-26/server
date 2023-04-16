package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.Room;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ch.uzh.ifi.hase.soprafs23.rest.dto.RoomPostDTO;

import java.io.IOException;
import java.sql.SQLException;

@RestController
public class RoomController {
    private final RoomService roomService;

    RoomController(RoomService roomService) {
        this.roomService = roomService;
    }


    @PostMapping("/undercover/rooms")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public RoomPostDTO createRoomUndercover(@RequestBody RoomPostDTO roomPostDTO) throws IOException, SQLException{
        Room roomInput = DTOMapper.INSTANCE.convertRoomPostDTOtoEntity(roomPostDTO);
        System.out.println("------------roomInput------------");
        System.out.println(roomInput.getGameName());
        System.out.println(roomInput.getOwnerId());
        Room createdRoom = roomService.createRoom(roomInput);
        return DTOMapper.INSTANCE.convertEntityToRoomPostDTO(createdRoom);
    }


}
