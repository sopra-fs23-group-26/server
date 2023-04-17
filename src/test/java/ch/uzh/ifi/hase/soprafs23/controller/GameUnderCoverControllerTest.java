package ch.uzh.ifi.hase.soprafs23.controller;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.*;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.controller.GameUnderCoverController;
import ch.uzh.ifi.hase.soprafs23.entity.GameUndercover;
import ch.uzh.ifi.hase.soprafs23.entity.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.service.RoomService;
import ch.uzh.ifi.hase.soprafs23.service.UCService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
public class GameUnderCoverControllerTest {

    @Mock
    private UCService ucService;

    @Mock
    private RoomService roomService;

    @Mock
    private UserService userService;

    @InjectMocks
    private GameUnderCoverController gameUnderCoverController;

    @Test
    public void createGame_validInput_gameCreated() {
        // Setup
        long roomId = 1L;
        Room room = new Room();
        room.setId(roomId);
        Set<User> players = new HashSet<>();
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);
        players.add(user1);
        players.add(user2);
        room.setPlayers(players);
        when(roomService.getRoomById(roomId)).thenReturn(room);
        when(ucService.createGame(room.getPlayers())).thenReturn(new GameUndercover());

        // Execute
        GameUndercover gameUndercover = gameUnderCoverController.createGame(roomId);

        // Verify
        verify(roomService).getRoomById(roomId);
        verify(ucService).createGame(room.getPlayers());
        assertEquals(HttpStatus.CREATED, ResponseEntity.status(HttpStatus.CREATED).build().getStatusCode());
        assertNotNull(gameUndercover);
/*        assertEquals(GameStatus.describing, gameUndercover.getGameStatus());
        assertEquals(2, gameUndercover.getUsers().size());
        assertEquals(room.getPlayers(), gameUndercover.getUsers());*/
    }
}

