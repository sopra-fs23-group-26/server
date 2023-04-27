package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.RoomStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import ch.uzh.ifi.hase.soprafs23.repository.RoomRepository;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
@Transactional
public class RoomService {

    private final RoomRepository roomRepository;
    private final UserService userService;





    private final Logger log = LoggerFactory.getLogger(RoomService.class);

    @Autowired
    public RoomService(@Qualifier("roomRepository")RoomRepository roomRepository, UserService userService) {
        this.roomRepository = roomRepository;
        this.userService = userService;
    }



    public Room getRoomById(long roomId) {
        return roomRepository.findById(roomId);
    }

    public Room createRoom(Room newRoom){
        newRoom.setName(UUID.randomUUID().toString().substring(0, 5));// 创建的时候会随机生成一个名字，然后用户可以自己改到时候
        newRoom.setGameName(newRoom.getGameName());
        newRoom.setOwnerId(newRoom.getOwnerId());
        User newUser = userService.getUserById(newRoom.getOwnerId());

        newUser.setRoom(newRoom);
        newRoom.getPlayers().add(newUser);

        newRoom = (Room) roomRepository.save(newRoom);
        roomRepository.flush();

        log.info("Created Information for Room: {}", newRoom);
        System.out.println("newroomid: "+newRoom.getId());
        return newRoom;}
//        else {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no such user, cant create a new room") ;
//        }
//    }

    public List<Room> getAllRooms(){

        List allRooms = roomRepository.findAll();
        log.info("find Information for all Rooms: {}", allRooms);
        return allRooms;

    }



    public void joinARoom(long userId, long roomId){
        User newUser = userService.getUserById(userId);
        Room room = roomRepository.findById(roomId);
        if(room.getRoomStatus()== RoomStatus.inGame){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The game has already started.");
        }
        room.getPlayers().add(newUser);
        newUser.setRoom(room);
        roomRepository.save(room);
    }

    public void leaveARoom(long userId, long roomId){
        User user = userService.getUserById(userId);
        Room room = roomRepository.findById(roomId);
        room.getPlayers().remove(user);
        user.setRoom(null);
        System.out.println("----------room players----------");
        roomRepository.save(room);

    }

}


