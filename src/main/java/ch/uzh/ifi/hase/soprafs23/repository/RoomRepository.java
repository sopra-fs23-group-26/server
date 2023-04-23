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


//    @Query("SELECT r FROM Room r WHERE r.id = :id")
//    Room findRoomByRoomId(@Param("id") long id);
//
//    @Query("SELECT r.players FROM Room r WHERE r.id = :id")
//    Set<User> findAllPlayersByRoomId(@Param("id") long id);
//
//
//    @Query("SELECT r.ownerId FROM Room r WHERE r.id = :id")
//    Long findOwnerIdByRoomId(@Param("id") long id);
//
//    @Modifying
//    @Query("UPDATE Room r SET r.players = :players WHERE r.id = :id")
//    void updatePlayersById(@Param("players") Set<User> players, @Param("id") Long id);




}
