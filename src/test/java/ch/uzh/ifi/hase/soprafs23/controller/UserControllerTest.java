package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.hamcrest.MatcherAssert.assertThat;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


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
    public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
        // given
        User user = new User();
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
                .andExpect(jsonPath("$[0].username", is(user.getUsername())));

    }

    @Test
    public void getSingleUser_notFound() throws Exception {
        MockHttpServletRequestBuilder getRequest = get("/users/100").contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(getRequest).andExpect(status().isNotFound());

    }

    @Test
    public void createUser_validInput_userCreated() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setUsername("testUsername");
        user.setToken("1");

        UserPostDTO userPostDTO = new UserPostDTO();
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
                .andExpect(jsonPath("$.username", is(user.getUsername())));
    }

    @Test
    public void createUser_invalidInput_usernameTaken() throws Exception {
        // given
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("testUsername");

        User createdUser = new User();
        createdUser.setId(1L);
        createdUser.setUsername("testUsername");
        createdUser.setToken("1");

        // given
        UserPostDTO duplicateUserPostDTO = new UserPostDTO();
        duplicateUserPostDTO.setUsername("testUsername");

        given(userService.createUser(Mockito.any())).willThrow(
                new ResponseStatusException(HttpStatus.CONFLICT, "The username already exists."));

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postDuplicateUserRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(duplicateUserPostDTO));

        // then
        mockMvc.perform(postDuplicateUserRequest)
                .andExpect(status().isConflict());
    }

    @Test
    public void getUser_validInput_userReturned() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setUsername("testUsername");
        user.setToken("1");

        given(userService.getUserById(Mockito.anyLong())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/users/1")
                .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.username", is(user.getUsername())));
    }

    @Test
    public void getUser_invalidInput_userNotFound() throws Exception {
        // given
        given(userService.getUserById(Mockito.anyLong())).willReturn(null);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/users/1")
                .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    public void addFriend_validInput_friendsAdded() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        List<User> friendList = new ArrayList<>();
        user.setFriends(friendList);

        User friend = new User();
        friend.setId(2L);
        friend.setUsername("testFriend");
        List<User> friendList2 = new ArrayList<>();
        friend.setFriends(friendList2);

        friendList.add(friend);

        given(userService.getUserById(user.getId())).willReturn(user);
        given(userService.getUserByUsername(friend.getUsername())).willReturn(friend);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/users/" + user.getId() + "/friends")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(friend.getUsername()));

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isConflict());

    }

    @Test
    void updateUser_validInput() throws Exception{
        User userOld = new User();
        userOld.setId(1L);
        userOld.setUsername("test");
        userOld.setImage("image.jpg");

        given(userService.getUserById(userOld.getId())).willReturn(userOld);

        MockMultipartFile imageFile = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test".getBytes());


        MockHttpServletRequestBuilder requestBuilder = put("/users/"+userOld.getId())
                .param("username", "test new")
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .content(imageFile.getBytes());

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());

    }

    @Test
    void updateUser_userNotFound() throws Exception{
        User userOld = new User();
        userOld.setId(1L);
        userOld.setUsername("test");
        userOld.setImage("image.jpg");

        given(userService.getUserById(anyLong())).willReturn(null);

        MockMultipartFile imageFile = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test".getBytes());

        MockHttpServletRequestBuilder requestBuilder = put("/users/"+userOld.getId())
                .param("username", "test new")
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .content(imageFile.getBytes());

        // Perform request and verify response
        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_userNameLengthGreaterThanNull() throws Exception{
        User userOld = new User();
        userOld.setId(1L);
        userOld.setUsername("123456890jgksjfksjfklsf");
        userOld.setImage("image.jpg");

        given(userService.getUserById(anyLong())).willReturn(userOld);

        MockMultipartFile imageFile = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test".getBytes());

        MockHttpServletRequestBuilder requestBuilder = put("/users/"+userOld.getId())
                .param("username", "123456890jgksjfksjfklsf")
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .content(imageFile.getBytes());

        // Perform request and verify response
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());

    }

    @Test
    void updateUser_getUserProfileImage_userIsNull() throws Exception{
        User userOld = new User();
        userOld.setId(1L);
        userOld.setUsername("123456890jgksjfksjfklsf");
        userOld.setImage("image.jpg");

        given(userService.getUserById(anyLong())).willReturn(null);

        MockMultipartFile imageFile = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test".getBytes());

        MockHttpServletRequestBuilder requestBuilder = get("/users/"+userOld.getId()+"/image")
                .param("username", "123456890jgksjfksjfklsf")
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .content(imageFile.getBytes());

        // Perform request and verify response
        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());

    }

    @Test
    void updateUser_getUserProfileImage_fileNameIsNull() throws Exception{
        User userOld = new User();
        userOld.setId(1L);
        userOld.setUsername("123456890jgksjfksjfklsf");
        userOld.setImage(null);

        given(userService.getUserById(anyLong())).willReturn(userOld);

        MockMultipartFile imageFile = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test".getBytes());

        MockHttpServletRequestBuilder requestBuilder = get("/users/"+userOld.getId()+"/image")
                .param("username", "123456890jgksjfksjfklsf")
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .content(imageFile.getBytes());

        // Perform request and verify response
        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());

    }

    @Test
    void updateUser_getUserProfileImage_blobIsNull() throws Exception{
        User userOld = new User();
        userOld.setId(1L);
        userOld.setUsername("123456890jgksjfksjfklsf");
        userOld.setImage("image.jpg");

        given(userService.getUserById(anyLong())).willReturn(userOld);

        MockMultipartFile imageFile = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test".getBytes());

        MockHttpServletRequestBuilder requestBuilder = get("/users/"+userOld.getId()+"/image")
                .param("username", "123456890jgksjfksjfklsf")
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .content(imageFile.getBytes());

        // Perform request and verify response
        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());

    }

    @Test
    void login_success() throws Exception{
        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");

        // 创建一个UserPostDTO对象
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("username");
        userPostDTO.setPassword("password");

        given(userService.UserLogin(any(User.class))).willReturn(user);


        MockHttpServletRequestBuilder requestBuilder = post("/login")
                .content(asJsonString(userPostDTO))
                .contentType(MediaType.APPLICATION_JSON);

        // Perform request and verify response
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());

    }


    @Test
    void getFriends_success() throws Exception{
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user 1");
        user1.setPassword("password");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user 2");
        user2.setPassword("password");

        List<User> friends_1 = new ArrayList<>();
        List<User> friends_2 = new ArrayList<>();

        friends_1.add(user2);
        friends_2.add(user1);

        user1.setFriends(friends_1);
        user2.setFriends(friends_2);

        given(userService.getUserById(anyLong())).willReturn(user1);

        MockHttpServletRequestBuilder requestBuilder = get("/users/1/friends")
                .contentType(MediaType.APPLICATION_JSON);

        // Perform request and verify response
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());

    }

    @Test
    void getWaitFriends_success() throws Exception{
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user 1");
        user1.setPassword("password");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user 2");
        user2.setPassword("password");

        List<User> friends_1 = new ArrayList<>();
        List<User> friends_2 = new ArrayList<>();

        friends_1.add(user2);
//        friends_2.add(user1);

        user1.setFriends(friends_1);
        user2.setFriends(friends_2);

        given(userService.getUserById(anyLong())).willReturn(user1);

        MockHttpServletRequestBuilder requestBuilder = get("/users/1/waitlist")
                .contentType(MediaType.APPLICATION_JSON);

        // Perform request and verify response
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());

    }

    @Test
    void addFriends_success() throws Exception{
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user 1");
        user1.setPassword("password");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user 2");
        user2.setPassword("password");

        List<User> friends_1 = new ArrayList<>();
        List<User> friends_2 = new ArrayList<>();

        friends_1.add(user2);
        friends_2.add(user1);

        user1.setFriends(friends_1);
        user2.setFriends(friends_2);

        given(userService.getUserById(anyLong())).willReturn(user1);
        given(userService.getUserByUsername(anyString())).willReturn(user2);

        MockHttpServletRequestBuilder requestBuilder = put("/users/1/friends")
                .contentType(MediaType.APPLICATION_JSON)
                .param("friendName", "user 1")
                .param("addFriendStatus", "1");


        // Perform request and verify response
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());

    }

    @Test
    void addFriends_user1IsNull() throws Exception{
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user 1");
        user1.setPassword("password");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user 2");
        user2.setPassword("password");

        List<User> friends_1 = new ArrayList<>();
        List<User> friends_2 = new ArrayList<>();

        friends_1.add(user2);
        friends_2.add(user1);

        user1.setFriends(friends_1);
        user2.setFriends(friends_2);

        given(userService.getUserById(anyLong())).willReturn(null);
        given(userService.getUserByUsername(anyString())).willReturn(user2);

        MockHttpServletRequestBuilder requestBuilder = put("/users/1/friends")
                .contentType(MediaType.APPLICATION_JSON)
                .param("friendName", "user 1")
                .param("addFriendStatus", "1");


        // Perform request and verify response
        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    void addFriends_user2IsNull() throws Exception{
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user 1");
        user1.setPassword("password");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user 2");
        user2.setPassword("password");

        List<User> friends_1 = new ArrayList<>();
        List<User> friends_2 = new ArrayList<>();

        friends_1.add(user2);
        friends_2.add(user1);

        user1.setFriends(friends_1);
        user2.setFriends(friends_2);

        given(userService.getUserById(anyLong())).willReturn(user1);
        given(userService.getUserByUsername(anyString())).willReturn(null);

        MockHttpServletRequestBuilder requestBuilder = put("/users/1/friends")
                .contentType(MediaType.APPLICATION_JSON)
                .param("friendName", "user 1")
                .param("addFriendStatus", "1");


        // Perform request and verify response
        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    void addFriends_conflict() throws Exception{
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user 1");
        user1.setPassword("password");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user 2");
        user2.setPassword("password");

        List<User> friends_1 = new ArrayList<>();
        List<User> friends_2 = new ArrayList<>();

        friends_1.add(user2);
        friends_2.add(user1);

        user1.setFriends(friends_1);
        user2.setFriends(friends_2);

        given(userService.getUserById(anyLong())).willReturn(user1);
        given(userService.getUserByUsername(anyString())).willReturn(user1);

        MockHttpServletRequestBuilder requestBuilder = put("/users/1/friends")
                .contentType(MediaType.APPLICATION_JSON)
                .param("friendName", "user 1")
                .param("addFriendStatus", "1");

        // Perform request and verify response
        mockMvc.perform(requestBuilder)
                .andExpect(status().isConflict());
    }


    @Test
    void validateInvitedUserName_success() throws Exception{
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user 1");
        user1.setPassword("password");

        Mockito.doNothing().when(userService).validateInvitedUserName(Mockito.anyString());


        MockHttpServletRequestBuilder requestBuilder = post("/users/user 1")
                .contentType(MediaType.APPLICATION_JSON);

        // Perform request and verify response
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    void getGlobalranking_success() throws Exception{
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user 1");
        user1.setPassword("password");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user 2");
        user2.setPassword("password");

        List<User> players = new ArrayList<>();

        players.add(user1);
        players.add(user2);

        given(userService.getUsers()).willReturn(players);

        MockHttpServletRequestBuilder requestBuilder = get("/globalranking")
                .contentType(MediaType.APPLICATION_JSON);

        // Perform request and verify response
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    void getCommunityranking_success() throws Exception{
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user 1");
        user1.setPassword("password");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user 2");
        user2.setPassword("password");

        List<User> friends_1 = new ArrayList<>();
        List<User> friends_2 = new ArrayList<>();

        friends_1.add(user2);
        friends_2.add(user1);

        user1.setFriends(friends_1);
        user2.setFriends(friends_2);

        given(userService.getUserById(anyLong())).willReturn(user1);

        MockHttpServletRequestBuilder requestBuilder = get("/communityranking/1")
                .contentType(MediaType.APPLICATION_JSON);

        // Perform request and verify response
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The request body could not be created.%s", e.toString()));
        }
    }

  
}
