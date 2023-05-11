package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Repository("roomRepository")

public interface RoomRepository extends JpaRepository<Room, Long> {

    Room findByName(String name);

    Room findById(long id);

    List<Room> findAll();

    void delete(Room room);


//
//    @Modifying
//    @Transactional
//    @Query("UPDATE User u SET u.room = :room WHERE u.id = :userId")
//    void addUserToRoom(@Param("userId") long userId, @Param("room") Room room);
//
//
//
//
}
