package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("roomRepository")
public interface RoomRepository extends JpaRepository<Room, Long> {

    Room findByName(String name);

    Room findById(long id);


}
