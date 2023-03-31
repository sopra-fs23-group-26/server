package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.ResDto;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class UserController {

  private final UserService userService;

  UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/users")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<UserGetDTO> getAllUsers() {
    // fetch all users in the internal representation
    List<User> users = userService.getUsers();
    List<UserGetDTO> userGetDTOs = new ArrayList<>();

    // convert each user to the API representation
    for (User user : users) {
      userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
    }
    return userGetDTOs;   //print userGetDTO objects in the page
  }

  @GetMapping("/user/{username}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO getSingleUsername(@PathVariable String username) {
    User singleUsername = userService.getSingleUsername(username);
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(singleUsername);
  }

  @GetMapping("/score/{username}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public int getUserScore(@PathVariable String username) {
    User singleUsername = userService.getSingleUsername(username);
    return singleUsername.getScore();
  }

  @GetMapping("/communityranking/{username}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public int getUserCommunityRanking(@PathVariable String username) {
    User singleUsername = userService.getSingleUsername(username);
    return singleUsername.getCommunityranking();
  }

  @GetMapping("/globalranking/{username}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public int getUserGlobalRanking(@PathVariable String username) {
    User singleUsername = userService.getSingleUsername(username);
    return singleUsername.getGlobalranking();
  }

  @PostMapping("/logout/{username}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public Object userLogout(@PathVariable String username) {
    return "ok";
  }

  @PostMapping("/users")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public UserGetDTO createUser(@RequestBody UserPostDTO userPostDTO) throws ParseException {
    // convert API user to internal representation
    String username = userPostDTO.getUsername();
    UserGetDTO singleUsername = this.getSingleUsername(username);
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
    // create user
    User createdUser = userService.createUser(userInput);
    // convert internal representation of user back to API
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
  }


  @PostMapping("/user")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public ResDto updateUser(@RequestBody UserPostDTO userPostDTO) throws ParseException {
    Long id = userPostDTO.getId();
    User userById = this.userService.getUserById(id);
    if(userById == null){
      return ResDto.err("User ID does not exist");
    }
    UserGetDTO singleUsername = this.getSingleUsername(userPostDTO.getUsername());
    if(!Objects.isNull(singleUsername) && !Objects.equals(singleUsername.getId(), userPostDTO.getId())){
      return ResDto.err("User name already exists");
    }
    userById.setUsername(userPostDTO.getUsername());
    this.userService.updateUser(userById);

    return ResDto.ok();
  }

  @PostMapping("/login")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO login(@RequestBody UserPostDTO userPostDTO) {
    String username = userPostDTO.getUsername();
    User singleUsername = this.userService.getSingleUsername(username);
    if(singleUsername == null || !Objects.equals(singleUsername.getPassword(), userPostDTO.getPassword())){
      return null;
    }
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(singleUsername);
  }
}