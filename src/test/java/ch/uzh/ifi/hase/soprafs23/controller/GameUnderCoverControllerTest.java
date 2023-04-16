package ch.uzh.ifi.hase.soprafs23.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import ch.uzh.ifi.hase.soprafs23.controller.GameUnderCoverController;
import ch.uzh.ifi.hase.soprafs23.entity.GameUndercover;
import ch.uzh.ifi.hase.soprafs23.entity.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.service.RoomService;
import ch.uzh.ifi.hase.soprafs23.service.UCService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

@WebMvcTest(GameUnderCoverController.class)
class GameUnderCoverControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomService roomService;

    @MockBean
    private UCService ucService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

/*    @Test
    void testCreateGame() throws Exception {
        // create a mock room and set its id to 1
        Room room = new Room();
        room.setId(1L);
        room.setName("test room");
        room.setGameName("test game");

        // create a list of mock users to add to the room
        List<User> players = new ArrayList<>();
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        players.add(user1);
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
        players.add(user2);
        room.setPlayers(new HashSet<>(players));

        Room room1 = roomService.getRoomById(1L);

        // mock the behavior of roomService to return the mock room
        when(roomService.getRoomById(1L)).thenReturn(room);

        // mock the behavior of ucService.createGame to return a mock GameUndercover
        GameUndercover gameUndercover = new GameUndercover();
        when(ucService.createGame(room)).thenReturn(gameUndercover);

        // make the POST request to the controller endpoint
        MockHttpServletRequestBuilder postRequest = post("/undercover/rooms/1")
                .contentType(MediaType.APPLICATION_JSON);

        // assert that the response is not null
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect((jsonPath("$.players[0].id").value(user1.getId())));
    }*/

    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The request body could not be created.%s", e.toString()));
        }
    }

}
