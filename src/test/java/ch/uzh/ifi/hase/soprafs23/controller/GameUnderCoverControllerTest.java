package ch.uzh.ifi.hase.soprafs23.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.RequestEntity.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.*;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.entity.GameUndercover;
import ch.uzh.ifi.hase.soprafs23.entity.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.service.RoomService;
import ch.uzh.ifi.hase.soprafs23.service.UCService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(gameUnderCoverController).build();
    }

    @Test
    public void getGameByGameId_validGameId_gameReturned() throws Exception {
        // Setup
        long gameId = 1L;
        GameUndercover gameUndercover = new GameUndercover();
        gameUndercover.setId(gameId);
        gameUndercover.setGameStatus(GameStatus.describing);
        given(ucService.getGameById(gameId)).willReturn(gameUndercover);

        // Execute and Verify
        mockMvc.perform(get("/undercover/{gameId}", gameId))
                .andExpect(jsonPath("$.id").value(gameId))
                .andExpect(jsonPath("$.gameStatus").value(GameStatus.describing.toString()));

        verify(ucService, times(1)).getGameById(gameId);
    }

    @Test
    public void describe_validInput_gameUpdated() throws Exception {
        // Setup
        long gameId = 1L;
        long userId = 1L;
        String description = "test description";
        GameUndercover gameUndercover = new GameUndercover();
        long roomId = 1L;
        Room room = new Room();
        room.setId(roomId);
        gameUndercover.setRoom(room);

        gameUndercover.setId(gameId);
        User describedUser = new User();
        describedUser.setId(userId);
        describedUser.setDescription(description);
        gameUndercover.getUsers().add(describedUser);
        given(ucService.getGameById(gameId)).willReturn(gameUndercover);
        given(userService.getUserById(userId)).willReturn(describedUser);
        given(ucService.describe(gameUndercover, describedUser)).willReturn(gameUndercover);

        // Execute and Verify
        mockMvc.perform(MockMvcRequestBuilders.put("/undercover/{gameId}/users/{userId}/description", gameId, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(description))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(gameId))
                .andExpect(jsonPath("$.users").isArray())
                .andExpect(jsonPath("$.users[0].id").value(userId))
                .andExpect(jsonPath("$.users[0].description").value(description));

        verify(ucService, times(1)).getGameById(gameId);
        verify(userService, times(1)).getUserById(userId);
        verify(ucService, times(1)).describe(gameUndercover, describedUser);
    }

    @Test
    public void vote_validInput_gameUpdated() throws Exception {
        // Setup
        long gameId = 1L;
        long voteUserId = 1L;
        long votedUserId = 2L;
        User voteUser = new User();
        voteUser.setId(voteUserId);
        User votedUser = new User();
        votedUser.setId(votedUserId);
        GameUndercover gameUndercover = new GameUndercover();

        long roomId = 1L;
        Room room = new Room();
        Set<User> players = new HashSet<>();
        players.add(voteUser);
        players.add(votedUser);
        room.setPlayers(players);
        room.setId(roomId);
        gameUndercover.setRoom(room);

        gameUndercover.setId(gameId);
        given(ucService.getGameById(gameId)).willReturn(gameUndercover);
        given(userService.getUserById(voteUserId)).willReturn(voteUser);
        given(userService.getUserById(votedUserId)).willReturn(votedUser);
        given(ucService.voteAndCheckIfEnds(gameUndercover, votedUser)).willReturn(false);

        // Execute and Verify
        mockMvc.perform(MockMvcRequestBuilders.put("/undercover/{gameId}/votes/{voteUserId}/{votedUserId}", gameId, voteUserId, votedUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(ucService, times(1)).getGameById(gameId);
        verify(userService, times(1)).getUserById(voteUserId);
        verify(userService, times(1)).getUserById(votedUserId);
        verify(ucService, times(1)).voteAndCheckIfEnds(gameUndercover, votedUser);
    }

    @Test
    public void vote_gameEndsWithMostVotedUser_userRemovedAndGameStatusChanged() throws Exception {
        // Setup
        long gameId = 1L;
        long voteUserId = 2L;
        long votedUserId1 = 3L;
        long votedUserId2 = 4L;
        GameUndercover gameUndercover = new GameUndercover();
        gameUndercover.setId(gameId);
        gameUndercover.setGameStatus(GameStatus.voting);
        User voteUser = new User();
        voteUser.setId(voteUserId);
        User votedUser1 = new User();
        votedUser1.setId(votedUserId1);
        User votedUser2 = new User();
        votedUser2.setId(votedUserId2);

// Set up room with players
        long roomId = 1L;
        Room room = new Room();
        Set<User> players = new HashSet<>();
        players.add(voteUser);
        players.add(votedUser1);
        players.add(votedUser2);
        room.setPlayers(players);
        room.setId(roomId);
        gameUndercover.setRoom(room);

// Set up list of out users
        List<User> outUsers = new ArrayList<>();
        outUsers.add(votedUser1);
    }

    @Test
    public void vote_voteUserAlreadyOut_throwsForbidden() throws Exception {
        // Setup
        long gameId = 1L;
        long voteUserId = 1L;
        long votedUserId = 2L;
        User voteUser = new User();
        voteUser.setId(voteUserId);
        voteUser.setVoted(true);
        User votedUser = new User();
        votedUser.setId(votedUserId);
        GameUndercover gameUndercover = new GameUndercover();
        gameUndercover.setId(gameId);

        given(ucService.getGameById(gameId)).willReturn(gameUndercover);
        given(userService.getUserById(voteUserId)).willReturn(voteUser);
        given(userService.getUserById(votedUserId)).willReturn(votedUser);

        // Execute and Verify
        mockMvc.perform(MockMvcRequestBuilders.put("/undercover/{gameId}/votes/{voteUserId}/{votedUserId}", gameId, voteUserId, votedUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(ucService, times(1)).getGameById(gameId);
        verify(userService, times(1)).getUserById(voteUserId);
        verify(userService, times(1)).getUserById(votedUserId);
    }

    @Test
    public void vote_votedUserAlreadyOut_throwsForbidden() throws Exception {
        // Setup
        long gameId = 1L;
        long voteUserId = 1L;
        long votedUserId = 2L;
        User voteUser = new User();
        voteUser.setId(voteUserId);
        User votedUser = new User();
        votedUser.setId(votedUserId);
        votedUser.setVoted(true);
        GameUndercover gameUndercover = new GameUndercover();
        gameUndercover.setId(gameId);

        given(ucService.getGameById(gameId)).willReturn(gameUndercover);
        given(userService.getUserById(voteUserId)).willReturn(voteUser);
        given(userService.getUserById(votedUserId)).willReturn(votedUser);

        // Execute and Verify
        mockMvc.perform(MockMvcRequestBuilders.put("/undercover/{gameId}/votes/{voteUserId}/{votedUserId}", gameId, voteUserId, votedUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(ucService, times(1)).getGameById(gameId);
        verify(userService, times(1)).getUserById(voteUserId);
        verify(userService, times(1)).getUserById(votedUserId);
    }

    @Test
    public void createGame_gameExistsInRoom_gameUndercoverReturned() throws Exception {
        // Setup
        long roomId = 1L;
        Room room = new Room();
        room.setId(roomId);
        GameUndercover gameUndercover = new GameUndercover();
        room.setGameUndercover(gameUndercover);
        given(roomService.getRoomById(roomId)).willReturn(room);

        // Execute and Verify
        mockMvc.perform(MockMvcRequestBuilders.post("/undercover/rooms/{roomId}", roomId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(gameUndercover.getId()));

        verify(roomService, times(1)).getRoomById(roomId);
    }
}