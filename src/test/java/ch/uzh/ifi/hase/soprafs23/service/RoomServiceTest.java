package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.RoomStatus;
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

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@Service
@Transactional
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RoomService roomService;

    @InjectMocks
    private UserService userService;

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

    @Test
    public void testGetARoom() {
        // Mock the room repository to return the mock room object when findById is called with argument 1L
        when(roomRepository.findById(1L)).thenReturn(testRoom);

        // Call the getRoomById method of the roomService
        Room returnedRoom = roomService.getARoom(1L);

        // Assert that the returned room is the same as the mock room object
        assert returnedRoom.equals(testRoom);
    }

    @Test
    public void testGetAllRoom() {
        // Mock the room repository to return the mock room object when findById is called with argument 1L
        List<Room> list_room = new ArrayList<>();
        list_room.add(testRoom);
        when(roomRepository.findAll()).thenReturn(list_room);

        List<Room> returnedRoom = roomService.getAllRooms();
        // Assert that the returned room is the same as the mock room object
        assert returnedRoom.equals(list_room);
    }

    @Test
    public void testCreateRoom() {

        Room newRoom = new Room();
        newRoom.setName("test room");
        newRoom.setGameName("test game");
        newRoom.setOwnerId(1L);
        User newUser = new User();
        newUser.setId(1L);
        newUser.setUsername("test user");
        Mockito.when(userService.getUserById(1L)).thenReturn(newUser);
        Mockito.when(roomRepository.save(newRoom)).thenReturn(newRoom);

        //room with the same name already exists
        Mockito.when(roomRepository.findByName(newRoom.getName())).thenReturn(newRoom);
        try {
            roomService.createRoom(newRoom);
            fail("Expected exception not thrown.");
        } catch (ResponseStatusException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        }

        // room does not exist, successfully create a new room
        Mockito.when(roomRepository.findByName(newRoom.getName())).thenReturn(null);

        Room result = roomService.createRoom(newRoom);
        assertEquals(newRoom.getName(), result.getName());
        assertEquals(newRoom.getGameName(), result.getGameName());
        assertEquals(newRoom.getOwnerId(), result.getOwnerId());
        assertEquals(newUser.getRoom(), result);
        assertTrue(result.getPlayers().contains(newUser));

        Mockito.verify(roomRepository, Mockito.times(1)).save(newRoom);
        Mockito.verify(roomRepository, Mockito.times(1)).flush();
    }

    @Test
    public void testJoinARoom_Success() {
        // Arrange
        long userId = 1L;
        long roomId = 1L;
        User newUser = new User();
        newUser.setId(userId);
        newUser.setUsername("newUser");
        newUser.setPassword("password");
        Room room = new Room();
        room.setName("testRoom");
        room.setId(roomId);
        when(userRepository.findById(userId)).thenReturn(newUser);
        when(roomRepository.findById(roomId)).thenReturn(room);
        when(roomRepository.save(room)).thenReturn(room);

        // Act
        roomService.joinARoom(userId, roomId);

        // Assert
        assertEquals(room, newUser.getRoom());
        assertEquals(1, room.getPlayers().size());
        assertTrue(room.getPlayers().contains(newUser));
    }

    @Test
    public void testJoinARoom_GameAlreadyStarted() {
        // Arrange
        long userId = 1L;
        long roomId = 1L;
        User newUser = new User();
        newUser.setId(userId);
        newUser.setUsername("newUser");
        newUser.setPassword("password");
        Room room = new Room();
        room.setName("testRoom");
        room.setRoomStatus(RoomStatus.inGame);
        when(userRepository.findById(userId)).thenReturn(newUser);
        when(roomRepository.findById(roomId)).thenReturn(room);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> roomService.joinARoom(userId, roomId));
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("The game has already started.", exception.getReason());
        assertEquals(null, newUser.getRoom());
        assertEquals(0, room.getPlayers().size());

    }

    @Test
    public void testLeaveARoom_Success() {
        // Arrange
        long userId = 1L;
        long roomId = 1L;
        User newUser = new User();
        newUser.setId(userId);
        newUser.setUsername("newUser");
        newUser.setPassword("password");
        Room room = new Room();
        room.setName("testRoom");
        room.getPlayers().add(newUser);
        newUser.setRoom(room);
        when(userRepository.findById(userId)).thenReturn(newUser);
        when(roomRepository.findById(roomId)).thenReturn(room);
        when(roomRepository.save(room)).thenReturn(room);

        // Act
        roomService.leaveARoom(userId, roomId);

        // Assert
        assertNull(newUser.getRoom());
        assertEquals(0, room.getPlayers().size());

    }

    @Test
    public void testDeleteARoom_Success() {
        // Arrange
        long roomId = 1L;
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        user1.setPassword("password1");
        User user2 = new User();
        user1.setId(2L);
        user1.setUsername("user2");
        user1.setPassword("password2");
        Room room = new Room();
        room.setName("testRoom");
        room.setId(roomId);
        room.getPlayers().add(user1);
        room.getPlayers().add(user2);
        user1.setRoom(room);
        user2.setRoom(room);
        when(roomRepository.findById(roomId)).thenReturn(room);

        // Act
        roomService.deleteARoom(roomId);

        // Assert
        assertNull(user1.getRoom());
        assertNull(user2.getRoom());
    }
}