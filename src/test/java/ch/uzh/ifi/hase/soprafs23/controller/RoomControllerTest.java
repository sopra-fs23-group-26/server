package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.RoomGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.RoomPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs23.service.RoomService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoomController.class)
class RoomControllerTest {
    @MockBean
    private RoomService roomService;


    @Autowired
    private MockMvc mockMvc;


    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The request body could not be created.%s", e.toString()));
        }
    }

    @Test
    void createARoom_valid_input() throws Exception {

        Room room = new Room();
        room.setOwnerId(1L);
        room.setName("a's game");
        room.setGameName("under cover");


        RoomPostDTO roomPostDTO = new RoomPostDTO();
        roomPostDTO.setGameName("under cover");
        roomPostDTO.setName("a's game");
        roomPostDTO.setOwnerId(1L);

        given(roomService.createRoom(Mockito.any())).willReturn(room);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/undercover/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(roomPostDTO));
        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
;
    }

    @Test
    void joinARoom_valid_input() throws Exception {

        Room room = new Room();
        room.setOwnerId(1L);
        room.setName("a's game");
        room.setGameName("under cover");


        RoomPostDTO roomPostDTO = new RoomPostDTO();
        roomPostDTO.setGameName("under cover");
        roomPostDTO.setName("a's game");
        roomPostDTO.setOwnerId(1L);

        Mockito.doNothing().when(roomService).joinARoom(Mockito.anyLong(), Mockito.anyLong());

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = put("/undercover/rooms/1/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(roomPostDTO));
        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isOk())
        ;
    }



    @Test
    void leaveARoom_valid_input() throws Exception {

        Room room = new Room();
        room.setOwnerId(1L);
        room.setName("a's game");
        room.setGameName("under cover");


        RoomPostDTO roomPostDTO = new RoomPostDTO();
        roomPostDTO.setGameName("under cover");
        roomPostDTO.setName("a's game");
        roomPostDTO.setOwnerId(1L);

        Mockito.doNothing().when(roomService).leaveARoom(Mockito.anyLong(), Mockito.anyLong());

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = delete("/undercover/rooms/1/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(roomPostDTO));
        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isOk())
        ;
    }

    @Test
    void deleteARoom_valid_input() throws Exception {

        Room room = new Room();
        room.setOwnerId(1L);
        room.setName("a's game");
        room.setGameName("under cover");


        RoomPostDTO roomPostDTO = new RoomPostDTO();
        roomPostDTO.setGameName("under cover");
        roomPostDTO.setName("a's game");
        roomPostDTO.setOwnerId(1L);

        roomService.createRoom(room);

        Mockito.doNothing().when(roomService).deleteARoom(Mockito.anyLong());

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = delete("/undercover/rooms/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(roomPostDTO));
        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isOk())
        ;
    }

    @Test
    void findAllRoomsUndercover() throws Exception {
        RoomGetDTO roomGetDTO = new RoomGetDTO();
        roomGetDTO.setGameName("under cover");
        roomGetDTO.setName("a's game");
        roomGetDTO.setOwnerId(1L);

        Room room = new Room();
        room.setGameName("under cover");
        room.setName("a's game");
        room.setOwnerId(1L);

        List<RoomGetDTO> rooms = new ArrayList<>();
        rooms.add(roomGetDTO);

        List<Room> roomList = new ArrayList<>();
        roomList.add(room);



        given(roomService.getAllRooms()).willReturn(roomList);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = get("/undercover/rooms")
                .contentType(MediaType.APPLICATION_JSON);
        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
        ;
    }


    @Test
    void findARoomUndercover() throws Exception {
        RoomGetDTO roomGetDTO = new RoomGetDTO();
        roomGetDTO.setGameName("under cover");
        roomGetDTO.setName("a's game");
        roomGetDTO.setOwnerId(1L);

        Room room = new Room();
        room.setGameName("under cover");
        room.setName("a's game");
        room.setOwnerId(1L);





        given(roomService.getARoom(anyLong())).willReturn(room);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = get("/undercover/rooms/1")
                .contentType(MediaType.APPLICATION_JSON);
        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
        ;
    }



}