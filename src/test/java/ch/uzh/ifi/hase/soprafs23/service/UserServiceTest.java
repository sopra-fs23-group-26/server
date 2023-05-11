package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.RoomRepository;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;


import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // given
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUsername");

        // when -> any object is being save in the userRepository -> return the dummy
        // testUser
        when(userRepository.save(Mockito.any())).thenReturn(testUser);
    }

    @Test
    public void createUser_validInputs_success() throws SQLException, IOException {
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setId(1L);
        testUser.setPassword("1234");

        // when -> any object is being save in the userRepository -> return the dummy
        // testUser
        User createdUser = userService.createUser(testUser);

        // then
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(testUser.getId(), createdUser.getId());
        assertEquals(testUser.getUsername(), createdUser.getUsername());
        assertNotNull(createdUser.getId());
    }

    @Test
    public void createUser_duplicateInputs_throwsException() throws SQLException, IOException {
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setId(1L);
        testUser.setPassword("1234");

        // given -> a first user has already been created
        userService.createUser(testUser);

        // when -> setup additional mocks for UserRepository
        when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

        // then -> attempt to create second user with same user -> check that an error
        // is thrown
        assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
    }


    @Test
    void testGetUsers() {
        // arrange
        List<User> userList = new ArrayList<>();
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("L");
        userList.add(user1);
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("P");
        userList.add(user2);

        when(userRepository.findAll()).thenReturn(userList);

        // act
        List<User> result = userService.getUsers();

        // assert
        assertEquals(userList.size(), result.size());
        assertEquals(userList.get(0).getId(), result.get(0).getId());
        assertEquals(userList.get(0).getUsername(), result.get(0).getUsername());
        assertEquals(userList.get(1).getId(), result.get(1).getId());
        assertEquals(userList.get(1).getUsername(), result.get(1).getUsername());
    }


    @Test
    void testCheckEmptyStringWithEmptyString() throws IOException, SQLException {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository);
        User user = new User();
        user.setUsername(" ");
        user.setPassword("password");
        try {
            userService.createUser(user);
            fail("Expected ResponseStatusException was not thrown.");
        } catch (ResponseStatusException ex) {
            assertEquals(HttpStatus.CONFLICT, ex.getStatus());
            assertEquals("username cannot be empty space.", ex.getReason());
        }
    }

    @Test
    void testCheckEmptyStringWithLongString() throws IOException, SQLException {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository);
        User user = new User();
        user.setUsername("this string is too long");
        user.setPassword("password");
        try {
            userService.createUser(user);
            fail("Expected ResponseStatusException was not thrown.");
        } catch (ResponseStatusException ex) {
            assertEquals(HttpStatus.CONFLICT, ex.getStatus());
            assertEquals("username should be reduced to within 14 characters.", ex.getReason());
        }
    }



    @Test
    void testUserLoginWithCorrectPassword() throws IOException, SQLException {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository);
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("testPassword");

        User mockedUser = new User();
        mockedUser.setUsername("testUser");
        mockedUser.setPassword("testPassword");

        when(userRepository.findByUsername("testUser")).thenReturn(mockedUser);
        User result = userService.UserLogin(user);

        assertEquals(mockedUser, result);
    }

    @Test
    void testUserLoginWithIncorrectPassword() throws IOException, SQLException {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository);
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("wrongPassword");

        User mockedUser = new User();
        mockedUser.setUsername("testUser");
        mockedUser.setPassword("testPassword");

        when(userRepository.findByUsername("testUser")).thenReturn(mockedUser);

        try {
            userService.UserLogin(user);
            fail("Expected ResponseStatusException was not thrown.");
        } catch (ResponseStatusException ex) {
            assertEquals(HttpStatus.CONFLICT, ex.getStatus());
            assertEquals("Wrong Password!", ex.getReason());
        }
    }

    @Test
    void testUserLoginWithUnregisteredUser() throws IOException, SQLException {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository);
        User user = new User();
        user.setUsername("unregisteredUser");
        user.setPassword("testPassword");

        when(userRepository.findByUsername("unregisteredUser")).thenReturn(null);

        try {
            userService.UserLogin(user);
            fail("Expected ResponseStatusException was not thrown.");
        } catch (ResponseStatusException ex) {
            assertEquals(HttpStatus.CONFLICT, ex.getStatus());
            assertEquals("Unregistered User!", ex.getReason());
        }
    }



    @Test
    void testGetUserByIdWithValidId() throws IOException, SQLException {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository);
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("testPassword");

        when(userRepository.findById(1L)).thenReturn(user);
        User result = userService.getUserById(1L);

        assertEquals(user, result);
    }



    @Test
    void testAddFriendAdding() {
        // Prepare test data
        User user1 = new User();
        user1.setUsername("L");
        user1.setFriends(new ArrayList<>());
        User user2 = new User();
        user2.setUsername("J");
        user2.setFriends(new ArrayList<>());

        userRepository.save(user1);
        userRepository.save(user2);

        // Test adding friend
        userService.addFriend(user1, user2, 1);
        assertTrue(user2.getFriends().contains(user1));
    }


    @Test
    void testAddFriendAccepting() {
        // Prepare test data
        User user1 = new User();
        user1.setUsername("L");
        user1.setFriends(new ArrayList<>());
        User user2 = new User();
        user2.setUsername("J");
        user2.setFriends(new ArrayList<>());

        userRepository.save(user1);
        userRepository.save(user2);

        // Test accepting friend
        userService.addFriend(user1, user2, 2);
        assertTrue(user2.getFriends().contains(user1));
    }


    @Test
    void testAddFriendRejecting() {
        // Prepare test data
        User user1 = new User();
        user1.setUsername("L");
        User user2 = new User();
        user2.setUsername("J");
        user2.setFriends(new ArrayList<>());
        List<User> user1Friends = new ArrayList<>();
        user1Friends.add(user2);
        user1.setFriends(user1Friends);


        userRepository.save(user1);
        userRepository.save(user2);

        // Test rejecting friend
        userService.addFriend(user1, user2, 3);
        assertFalse(user1.getFriends().contains(user2));
    }




    @Test
    public void testValidateInvitedUserNameWhenUserNotFound() {
        // Prepare test data
        String username = "NotExist";

        // Test case
        try {
            userService.validateInvitedUserName(username);
            fail("An exception should have been thrown");
        } catch (ResponseStatusException ex) {
            assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
            assertEquals("no user called " + username, ex.getReason());
        }
    }

    @Test
    public void testValidateInvitedUserNameWhenUserIsInARoom() {
        // Prepare test data
        User user = new User();
        user.setUsername("L");
        Room room = new Room();
        Set<User> players = new HashSet<>();
        players.add(user);
        room.setPlayers(players);
        roomRepository.save(room);
        user.setRoom(room);
        userRepository.save(user);

        when(userRepository.findByUsername("L")).thenReturn(user);

        // Test case
        try {
            userService.validateInvitedUserName("L");
            fail("An exception should have been thrown");
        } catch (ResponseStatusException ex) {
            assertEquals(HttpStatus.CONFLICT, ex.getStatus());
            assertEquals("L has been in a room", ex.getReason());
        }
    }



    @Test
    public void testUpdateAllFields() {
        // Arrange
        User userToBeUpdated = new User();
        userToBeUpdated.setUsername("L");
        userToBeUpdated.setPassword("p1");
        userToBeUpdated.setImage("old");
        User updateUserInfo = new User();
        updateUserInfo.setUsername("J");
        updateUserInfo.setPassword("p1");
        updateUserInfo.setImage("new");
        when(userRepository.findByUsername(updateUserInfo.getUsername())).thenReturn(null);

        // Act
        userService.update(userToBeUpdated, updateUserInfo);

        // Assert
        assertEquals(userToBeUpdated.getUsername(), updateUserInfo.getUsername());
        assertEquals(userToBeUpdated.getPassword(), updateUserInfo.getPassword());
        assertEquals(userToBeUpdated.getImage(), updateUserInfo.getImage());
    }




}
