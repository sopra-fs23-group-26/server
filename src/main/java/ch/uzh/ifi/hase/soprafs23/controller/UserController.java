package ch.uzh.ifi.hase.soprafs23.controller;
import com.google.cloud.storage.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.UserService;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

  @PutMapping("/users/{id}")
  @ResponseBody
  public void updateUser(@PathVariable Long id, @RequestParam(required = false) String username, @RequestParam(required = false) MultipartFile image) throws IOException {
    User userToBeUpdated = userService.getUserById(id);
    User updateUserInfo = new User();
    if(userToBeUpdated == null){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Can't find user!");
    }
    if (username == null || username.compareTo("null") == 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username cannot be null!");
    } else if (username.length() >= 15) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username should be less than 15 characters!");
    } else {
      updateUserInfo.setUsername(username);
    }
    if (image != null) {
      String fileName = StringUtils.cleanPath(image.getOriginalFilename());
      String bucketName = "sopra26_user_profile_image";
      Storage storage = StorageOptions.getDefaultInstance().getService();
      String blobName = "user-photos/" + id + "/" + fileName;
      BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(bucketName, blobName))
              .setContentType(image.getContentType())
              .build();
      Blob newBlob = storage.create(blobInfo, image.getBytes());
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

    String bucketName = "sopra26_user_profile_image";
    Storage storage = StorageOptions.getDefaultInstance().getService();
    Blob blob = storage.get(bucketName, "user-photos/" + user.getId() + "/" + fileName);
    if (blob == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile image not found!");
    }

    BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(bucketName, "user-photos/" + user.getId() + "/" + fileName))
            .setContentType(MediaType.IMAGE_JPEG.toString())
            .build();
    Blob newBlob = storage.create(blobInfo, blob.getContent());

    byte[] content = newBlob.getContent();
    ByteArrayResource resource = new ByteArrayResource(content);

    return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_JPEG)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
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

  @GetMapping("/users/{id}/friends")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<UserGetDTO> getFriends(@PathVariable long id) {
    User user = userService.getUserById(id);
    List<User> allFriends = user.getFriends();
    List<UserGetDTO> realFriends = new ArrayList<>();
    for(int i = 0; i < allFriends.size(); i++) {
      User realFriend = allFriends.get(i);
      if(realFriend.getFriends().contains(user)) {
        UserGetDTO userGetDTO = new UserGetDTO();
        userGetDTO.setId(realFriend.getId());;
        userGetDTO.setUsername(realFriend.getUsername());
        userGetDTO.setImage(realFriend.getImage());
        realFriends.add(userGetDTO);
      }
    }
    return realFriends;
  }

  @GetMapping("/users/{id}/waitlist")
  @ResponseBody
  public List<UserGetDTO> getWaitFriend(@PathVariable Long id) {
    User user = userService.getUserById(id);
    List<User> allFriends = user.getFriends();
    List<UserGetDTO> waitFriends = new ArrayList<>();
    for(int i = 0; i < allFriends.size(); i++) {
      User waitFriend = allFriends.get(i);
      if(waitFriend.getFriends().contains(user) == false) {
        UserGetDTO userGetDTO = new UserGetDTO();
        userGetDTO.setId(waitFriend.getId());;
        userGetDTO.setUsername(waitFriend.getUsername());
        waitFriends.add(userGetDTO);
      }
    }
    return waitFriends;
  }

  @PutMapping("/users/{id}/friends")
  @ResponseBody
  public void addFriend(@PathVariable long id, @RequestParam(required = false) String friendName, @RequestParam(required = false) int addFriendStatus) {
    User user1 = userService.getUserById(id);
    if(user1 == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Can't find user!");
    }
    User user2 = userService.getUserByUsername(friendName);
    if(user2 == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Can't find user!");
    }
    if(user1.getUsername().compareTo(user2.getUsername()) == 0) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Can not add yourself as your friend!");
    }
    userService.addFriend(user1, user2, addFriendStatus); //addFriendStatus = 1(add friend), 2(accept friend), 3(reject friend)
  }



  @PostMapping("/users/{username}")
  @ResponseBody
  public void validateInvitedUserName(@PathVariable String username){
    System.out.println("inviting");
    System.out.println(username);
    userService.validateInvitedUserName(username);

  }

  @GetMapping("/globalranking")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<UserGetDTO> getGlobalranking() {
    List<User> users = userService.getUsers();
    Collections.sort(users, Comparator.comparing(User::getScore).reversed());
    List<UserGetDTO> userGetDTOs = new ArrayList<>();
    int rank = 1;
    for (User user : users) {
      user.setGlobalRanking(rank);
      rank++;
      userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
    }
    return userGetDTOs;   //print userGetDTO objects in the page
  }

  @GetMapping("/communityranking/{id}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<UserGetDTO> getCommunityranking(@PathVariable("id") long id) {
    User user = userService.getUserById(id);
    List<User> community = user.getFriends();
    community.add(user);
    Collections.sort(community, Comparator.comparing(User::getScore).reversed());
    List<UserGetDTO> userGetDTOs = new ArrayList<>();
    int rank = 1;
    for (User fellow : community) {
      fellow.setGlobalRanking(rank);
      rank++;
      userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(fellow));
    }
    return userGetDTOs;   //print userGetDTO objects in the page
  }

}


