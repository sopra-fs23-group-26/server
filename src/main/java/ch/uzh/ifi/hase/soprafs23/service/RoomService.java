package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import ch.uzh.ifi.hase.soprafs23.repository.RoomRepository;

import java.util.UUID;

@Service
@Transactional
public class RoomService {

    private final RoomRepository roomRepository;
    private final Logger log = LoggerFactory.getLogger(RoomService.class);

    @Autowired
    public RoomService(@Qualifier("roomRepository")RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public Room getRoomById(long roomId) {
        return roomRepository.findById(roomId);
    }

    public Room createRoom(Room newRoom){
        newRoom.setName(UUID.randomUUID().toString());// 创建的时候会随机生成一个名字，然后用户可以自己改到时候
        newRoom.setGameName(newRoom.getGameName());
        newRoom.setOwnerId(newRoom.getOwnerId());
        newRoom = (Room) roomRepository.save(newRoom);
        roomRepository.flush();
        log.info("Created Information for Room: {}", newRoom);
        return newRoom;
    }

}


