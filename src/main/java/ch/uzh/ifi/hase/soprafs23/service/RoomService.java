package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.RoomStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
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


    private final UserRepository userRepository;





    private final Logger log = LoggerFactory.getLogger(RoomService.class);

    @Autowired
    public RoomService(@Qualifier("roomRepository")RoomRepository roomRepository, UserRepository userRepository) {
        this.roomRepository = roomRepository;

        this.userRepository = userRepository;
    }



    public Room getRoomById(long roomId) {
        return roomRepository.findById(roomId);
    }

    public Room createRoom(Room newRoom){
        try{
            checkIfRoomExists(newRoom.getName());
            newRoom.setGameName(newRoom.getGameName());
            newRoom.setOwnerId(newRoom.getOwnerId());
            newRoom.setName(newRoom.getName());
            long ownerId = newRoom.getOwnerId();
            User newUser = userRepository.findById(ownerId);

            newUser.setRoom(newRoom);
            newRoom.getPlayers().add(newUser);

            newRoom = (Room) roomRepository.save(newRoom);
            roomRepository.flush();

            return newRoom;
        }
        catch(Exception e){
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
        checkIfRoomFull(roomId);

        User newUser = userRepository.findById(userId);
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
        User user = userRepository.findById(userId);
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

    public void checkIfRoomFull(long roomId){
        Room room = roomRepository.findById(roomId);
        Set<User> players = room.getPlayers();
        if(players.size()>=8){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "There has been 8 player in this room");
        }

    }

}


