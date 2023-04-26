package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.RoomRepository;
import ch.uzh.ifi.hase.soprafs23.entity.Room;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.mockito.Mockito.when;

class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RoomService roomService;

    private Room testRoom;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);

        //given
        testRoom = new Room();
        testRoom.setId(1L);
        testRoom.setName("test room");
        testRoom.setGameName("test game");

        //save two users to the room
        List<User> players = new ArrayList<>();
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        players.add(user1);
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
        players.add(user2);
        testRoom.setPlayers(new HashSet<>(players));

        // when -> any object is being saved in the userRepository -> return the dummy
        // testUser
        when(roomRepository.save(Mockito.any())).thenReturn(testRoom);
    }

    @Test
    public void testGetRoomById() {
        // Mock the room repository to return the mock room object when findById is called with argument 1L
        when(roomRepository.findById(1L)).thenReturn(testRoom);

        // Call the getRoomById method of the roomService
        Room returnedRoom = roomService.getRoomById(1L);

        // Assert that the returned room is the same as the mock room object
        assert returnedRoom.equals(testRoom);
    }

}