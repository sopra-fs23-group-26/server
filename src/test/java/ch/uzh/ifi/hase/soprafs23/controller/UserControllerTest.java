package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPostDTO;
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

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @Test
  public void getAllUser_returnsAllUser() throws Exception {
    // given
    User user = new User();
    user.setPassword("Firstname Lastname");
    user.setUsername("firstname@lastname");

    List<User> allUsers = Collections.singletonList(user);

    // this mocks the UserService -> we define above what the userService should
    // return when getUsers() is called
    given(userService.getUsers()).willReturn(allUsers);

    // when
    MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(getRequest).andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].password", is(user.getPassword())))
        .andExpect(jsonPath("$[0].username", is(user.getUsername())));

      System.out.println("11111111111111111111111111111111111111");
  }

  //print to check whether correct
  @Test
  public void getSingleUser_notFound() throws Exception {
      MockHttpServletRequestBuilder getRequest = get("/users/100").contentType(MediaType.APPLICATION_JSON);
      mockMvc.perform(getRequest).andExpect(status().isNotFound()).andDo(print());
      System.out.println("2222222222222222222222222222222222222222");
  }


  @Test
  public void addUser_validInput_userCreated() throws Exception {
    // given
    User user = new User();
    user.setId(1L);
    user.setPassword("Test User");
    user.setUsername("testUsername");
    user.setToken("1");


    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setPassword("Test User");
    userPostDTO.setUsername("testUsername");

    given(userService.createUser(Mockito.any())).willReturn(user);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(postRequest)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(user.getId().intValue())))
        .andExpect(jsonPath("$.password", is(user.getPassword())))
        .andExpect(jsonPath("$.username", is(user.getUsername())));

      System.out.println("333333333333333333333333333333333333333");
  }


    @Test
    public void addUser_duplicateUsername_isConflict() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setPassword("Test5678");
        user.setUsername("John");
        user.setToken("1");

        User user2 = new User();
        user2.setId(2L);
        user2.setPassword("Test1234");
        user2.setUsername("John");
        user2.setToken("2");

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setPassword("Test5678");
        userPostDTO.setUsername("John");

        given(userService.createUser(Mockito.any())).willReturn(user);

        // when/then -> do the first request to create user
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        mockMvc.perform(postRequest)
                .andExpect(status().isCreated());

        // given - reset the mock to create the second user with same username
        given(userService.createUser(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.CONFLICT,
                String.format("The %s provided %s not unique. Therefore, the user could not be created!", "username", "is")));

        // when/then -> do the second request to create user with same username
        UserPostDTO userPostDTO2 = new UserPostDTO();
        userPostDTO2.setPassword("Test1234");
        userPostDTO2.setUsername("John");

        MockHttpServletRequestBuilder postRequest2 = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO2));

        mockMvc.perform(postRequest2)
                .andExpect(status().isConflict()).andDo(print());

        System.out.println("444444444444444444444444444444444444444444444");
    }

    //sorry for that I still use


    @Test
    public void updateUser_validInput_userUpdated() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setUsername("John");

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setId(1L);
        userPostDTO.setUsername("John2222");

        given(userService.getUserById(1L)).willReturn(user);
        given(userService.updateUser(user)).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isOk()).andDo(print());

        System.out.println("555555555555555555555555555555555555555");
    }

    @Test
    public void login_validCredentials_success() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setPassword("Test5678");
        user.setUsername("John");
        user.setToken("1");

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setPassword("Test5678");
        userPostDTO.setUsername("John");

        given(userService.getSingleUsername(Mockito.anyString())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.username", is(user.getUsername())));

        System.out.println("6666666666666666666666666666666");
    }

  /**
   * Helper Method to convert userPostDTO into a JSON string such that the input
   * can be processed
   * Input will look like this: {"name": "Test User", "username": "testUsername"}
   * 
   * @param object
   * @return string
   */
  private String asJsonString(final Object object) {
    try {
      return new ObjectMapper().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new ResponseStatusException(HttpStatus.CONFLICT,
          String.format("The request body could not be created.%s", e.toString()));
    }
  }
}
