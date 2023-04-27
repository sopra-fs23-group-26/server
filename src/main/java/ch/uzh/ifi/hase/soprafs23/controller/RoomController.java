package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.Room;
import ch.uzh.ifi.hase.soprafs23.rest.dto.RoomGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ch.uzh.ifi.hase.soprafs23.rest.dto.RoomPostDTO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
        System.out.println("gamename: "+roomInput.getGameName());
        System.out.println("ownerid: "+roomInput.getOwnerId());
        Room createdRoom = roomService.createRoom(roomInput);
        return DTOMapper.INSTANCE.convertEntityToRoomPostDTO(createdRoom);
    }




    @GetMapping("/undercover/rooms")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public List<RoomGetDTO> findAllRoomsUndercover() throws IOException, SQLException{
        List<RoomGetDTO> roomList = new ArrayList<>();
        List allRooms = roomService.getAllRooms();
        for(int i = 0; i<allRooms.size(); i++){
            roomList.add(DTOMapper.INSTANCE.convertEntityToRoomGetDTO((Room) allRooms.get(i)));
        }
        return roomList;
    }


    @GetMapping("/undercover/rooms/{roomId}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public RoomGetDTO findARoomUndercover(@PathVariable("roomId") long roomId) throws IOException, SQLException{
        Room room = roomService.getARoom(roomId);
        return DTOMapper.INSTANCE.convertEntityToRoomGetDTO(room);
    }


    @PutMapping("/undercover/rooms/{roomId}/{userId}")
    @ResponseBody
    public RoomGetDTO joinARoom(@PathVariable("roomId") long roomId, @PathVariable("userId") long userId) throws IOException, SQLException{
        System.out.println("/undercover/rooms/{id}");
        roomService.joinARoom(userId, roomId);
        return DTOMapper.INSTANCE.convertEntityToRoomGetDTO(roomService.getRoomById(roomId));
    }


    @DeleteMapping("/undercover/rooms/{roomId}/{userId}")
    @ResponseBody
    public RoomGetDTO leaveARoom(@PathVariable("roomId") long roomId, @PathVariable("userId") long userId) throws IOException, SQLException{
        System.out.println("/undercover/rooms/{id}");
        roomService.leaveARoom(userId, roomId);
        return DTOMapper.INSTANCE.convertEntityToRoomGetDTO(roomService.getRoomById(roomId));
    }











}
