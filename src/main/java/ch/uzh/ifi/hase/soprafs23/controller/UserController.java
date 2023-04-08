package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.core.io.Resource;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

  @GetMapping("/users/{id}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO getUser(@PathVariable("id") long id) {
    User user = userService.getUserById(id);
    if(user==null){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Can't find user!");
    }
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
  }
//
//  @PutMapping("/users/{id}")
//  @ResponseStatus(HttpStatus.NO_CONTENT)
//  @ResponseBody
//  public void updateUser(@PathVariable("id") long id, @RequestBody UserPutDTO userPutDTO) {
//    User userToBeUpdated = userService.getUserById(id);
//    User updateUserInfo = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);
//    if(userToBeUpdated == null){
//      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Can't find user!");
//    }
//    userService.update(userToBeUpdated, updateUserInfo);
//  }

  @PutMapping("/users/{id}")
  @ResponseBody
  public void updateUser(@PathVariable Long id, @RequestParam(required = false) String username, @RequestParam(required = false) MultipartFile image) throws IOException {
    User userToBeUpdated = userService.getUserById(id);
    User updateUserInfo = new User();
    if(userToBeUpdated == null){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Can't find user!");
    }
    if (username != null) {
      updateUserInfo.setUsername(username);
    }
    if (image != null) {
      String fileName = StringUtils.cleanPath(image.getOriginalFilename());
      String uploadDir = "user-photos/" + updateUserInfo.getId();
      File uploadDirPath = new File(uploadDir);
      if (!uploadDirPath.exists()) {
        uploadDirPath.mkdirs();
      }
      Path path = Paths.get(uploadDir + "/" + fileName);
      Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
      updateUserInfo.setImage(fileName);
    }
    userService.update(userToBeUpdated,updateUserInfo);
  }

  @GetMapping("/users/{id}/image")
  @ResponseBody
  public ResponseEntity<Resource> getUserProfileImage(@PathVariable Long id) {
    User user = userService.getUserById(id);
    if (user == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Can't find user!");
    }

    String fileName = user.getImage();
    if (fileName == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User has no profile image!");
    }

    String uploadDir = "user-photos/" + user.getId();
    Path path = Paths.get(uploadDir + "/" + fileName);
    Resource resource;
    try {
      resource = new UrlResource(path.toUri());
    } catch (MalformedURLException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile image not found!");
    }
    return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_JPEG)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + ((UrlResource) resource).getFilename() + "\"")
            .body(resource);
  }


  @PostMapping("/users")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public UserGetDTO createUser(@RequestBody UserPostDTO userPostDTO) throws IOException, SQLException {
    // convert API user to internal representation
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
    // create user
    User createdUser = userService.createUser(userInput);
    // convert internal representation of user back to API
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
  }

  @PostMapping("/login")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO login(@RequestBody UserPostDTO userPostDTO) {
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(userService.UserLogin(userInput));
  }


  @GetMapping("/users/{userId}/friends")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<User> getFriends(@PathVariable long userId) {
    User user = userService.getUserById(userId);
    if(user == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Can't find user!");
    }
    return user.getFriends();
  }

  @PutMapping("/users/{userId}/friends")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void addFriend(@PathVariable("userId") long userId, @RequestBody String username) {
    User user = userService.getUserById(userId);
    if(user == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Can't find user!");
    }

    userService.addFriend(user, username);
  }



}