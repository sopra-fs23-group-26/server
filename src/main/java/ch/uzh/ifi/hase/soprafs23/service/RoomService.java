package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.Room;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import ch.uzh.ifi.hase.soprafs23.repository.RoomRepository;

@Service
@Transactional
public class RoomService {

    private final RoomRepository roomRepository;

    @Autowired
    public RoomService(@Qualifier("roomRepository")RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public Room getRoomById(long roomId) {
        return roomRepository.findById(roomId);
    }
}
