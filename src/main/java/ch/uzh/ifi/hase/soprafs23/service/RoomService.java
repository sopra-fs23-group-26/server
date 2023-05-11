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
//        newRoom.setName(UUID.randomUUID().toString().substring(0, 5));// 创建的时候会随机生成一个名字，然后用户可以自己改到时候
        try{
            checkIfRoomExists(newRoom.getName());
            System.out.println("creating a room 1");
            newRoom.setGameName(newRoom.getGameName());
            System.out.println("creating a room 2");
            newRoom.setOwnerId(newRoom.getOwnerId());
            System.out.println("creating a room 3");
            newRoom.setName(newRoom.getName());
            System.out.println("creating a room 4");
            User newUser = userService.getUserById(newRoom.getOwnerId());
            System.out.println("creating a room 5");

            newUser.setRoom(newRoom);
            System.out.println("creating a room 6");
            newRoom.getPlayers().add(newUser);
            System.out.println("creating a room 7");

            System.out.println("creating a room  ROOOMMMM");
            System.out.println(newRoom);

            newRoom = (Room) roomRepository.save(newRoom);
            System.out.println("creating a room 8");
            roomRepository.flush();
            System.out.println("creating a room 9");

            log.info("Created Information for Room: {}", newRoom);
            System.out.println("newroomid: "+newRoom.getId());
            return newRoom;
        }
        catch(Exception e){
            System.out.println("creating a room exception");
            System.out.println(e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A room with the same has been created.", e);
        }
    }


    private void checkIfRoomExists(String name){
        System.out.println("name");
        System.out.println(name);
        Room room = roomRepository.findByName(name);
        System.out.println("creating a room");
        System.out.println(room);
        if(room!=null){
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "The room name provided is not unique. Please try a new name");
        }
    }

    public List<Room> getAllRooms(){

        List allRooms = roomRepository.findAll();
        log.info("find Information for all Rooms: {}", allRooms);
        return allRooms;

    }

    public Room getARoom(long roomId){
        Room room = roomRepository.findById(roomId);
        return room;
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
        roomRepository.flush();
    }

    public void leaveARoom(long userId, long roomId){
        User user = userService.getUserById(userId);
        Room room = roomRepository.findById(roomId);
        room.getPlayers().remove(user);
        user.setRoom(null);
        System.out.println("----------room players----------");
        roomRepository.save(room);
        roomRepository.flush();

    }

    public void deleteARoom(long roomId){
        Room roomToDelete = roomRepository.findById(roomId);
        for(User user: roomToDelete.getPlayers()){
            user.setRoom(null);
        }

        roomRepository.delete(roomToDelete);
        roomRepository.flush();

    }

}


