package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.GameHistory;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs23.service.GameHistoryService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.hamcrest.MatcherAssert.assertThat;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(GameHistoryController.class)
public class GameHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private GameHistoryService gameHistoryService;

    @Test
    void getLatestRecord_userNotNull() throws Exception{
        User user = new User();

        GameHistory gameHistory = new GameHistory();

        when(userService.getUserById(anyLong())).thenReturn(user);
        when(gameHistoryService.getLatestGameHistory(any(User.class))).thenReturn(gameHistory);

        MockHttpServletRequestBuilder request = get("/gameHistory/1/latestRecord")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void getLatestRecord_userIsNull() throws Exception{
        User user = new User();

        GameHistory gameHistory = new GameHistory();

        when(userService.getUserById(anyLong())).thenReturn(null);
        when(gameHistoryService.getLatestGameHistory(any(User.class))).thenReturn(gameHistory);

        MockHttpServletRequestBuilder request = get("/gameHistory/1/latestRecord")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    void getRecord_userNotNull() throws Exception{
        User user = new User();

        GameHistory gameHistory = new GameHistory();

        List<GameHistory> gameHistoryList = new ArrayList<>();
        gameHistoryList.add(gameHistory);

        when(userService.getUserById(anyLong())).thenReturn(user);
        when(gameHistoryService.getGameHistory(any(User.class))).thenReturn(gameHistoryList);

        MockHttpServletRequestBuilder request = get("/gameHistory/1")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk());

    }

    @Test
    void getRecord_userIsNull() throws Exception{
        User user = new User();

        GameHistory gameHistory = new GameHistory();

        List<GameHistory> gameHistoryList = new ArrayList<>();
        gameHistoryList.add(gameHistory);

        when(userService.getUserById(anyLong())).thenReturn(null);
        when(gameHistoryService.getGameHistory(any(User.class))).thenReturn(gameHistoryList);

        MockHttpServletRequestBuilder request = get("/gameHistory/1")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }
}