package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.GameHistory;
import ch.uzh.ifi.hase.soprafs23.entity.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.RoomRepository;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs23.repository.GameHistoryRepository;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class GameHistoryServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private GameHistoryRepository gameHistoryRepository;

    @InjectMocks
    private UserService userService;

    @InjectMocks
    private RoomService roomService;

    @InjectMocks
    private GameHistoryService gameHistoryService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

    }

    @Test
    void getLatestGameHistory_notEmpty() throws SQLException, IOException{
        User user = new User();
        user.setUsername("user");

        GameHistory gameHistory = new GameHistory();
        List<GameHistory> gameHistoryList = new ArrayList<>();
        gameHistoryList.add(gameHistory);

        when(gameHistoryRepository.findByUsername(anyString())).thenReturn(gameHistoryList);

        assertEquals(gameHistoryList.get(0), gameHistoryService.getLatestGameHistory(user));
    }

    @Test
    void getLatestGameHistory_isEmpty() throws SQLException, IOException{
        User user = new User();
        user.setUsername("user");

        GameHistory gameHistory = new GameHistory();
        List<GameHistory> gameHistoryList = new ArrayList<>();

        when(gameHistoryRepository.findByUsername(anyString())).thenReturn(gameHistoryList);

        try{
            gameHistoryService.getLatestGameHistory(user);
            fail("An exception should have been thrown");
        }
        catch (ResponseStatusException ex){
            assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
            assertEquals("Can't find this user's game history!", ex.getReason());
        }
    }

    @Test
    void getGameHistory_notEmpty() throws SQLException, IOException{
        User user = new User();
        user.setUsername("user");

        GameHistory gameHistory = new GameHistory();
        List<GameHistory> gameHistoryList = new ArrayList<>();
        gameHistoryList.add(gameHistory);

        when(gameHistoryRepository.findByUsername(anyString())).thenReturn(gameHistoryList);

        assertEquals(gameHistoryList, gameHistoryService.getGameHistory(user));
    }

    @Test
    void getGameHistory_isEmpty() throws SQLException, IOException{
        User user = new User();
        user.setUsername("user");

        GameHistory gameHistory = new GameHistory();
        List<GameHistory> gameHistoryList = new ArrayList<>();

        when(gameHistoryRepository.findByUsername(anyString())).thenReturn(gameHistoryList);

        try{
            gameHistoryService.getGameHistory(user);
            fail("An exception should have been thrown");
        }
        catch (ResponseStatusException ex){
            assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
            assertEquals("Can't find this user's game history!", ex.getReason());
        }
    }
}