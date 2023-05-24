package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import javax.imageio.ImageIO;
import javax.sql.rowset.serial.SerialBlob;
import java.io.*;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;

  @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<User> getUsers() {
    return this.userRepository.findAll();
  }

  public User createUser(User newUser) throws IOException, SQLException {
    newUser.setToken(UUID.randomUUID().toString());
    newUser.setCommunityRanking(1);
    newUser.setScore(0);
    newUser.setGlobalRanking(1);
    checkIfUserExists(newUser);
    checkEmptyString(newUser.getPassword(),"password");
    checkEmptyString(newUser.getUsername(),"username");
    newUser = (User) userRepository.save(newUser);
    userRepository.flush();
    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }

  private void checkEmptyString(String string, String message) {
    if(string.trim().isEmpty()){
      throw new ResponseStatusException(HttpStatus.CONFLICT, message + " cannot be empty space.");
    } 
    
    if(message.equals("username") && string.length() >= 15) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, message + " should be reduced to within 14 characters.");
    }
  }

  public User UserLogin(User newUser){
    checkIfPasswordCorrects(newUser);
    return userRepository.findByUsername(newUser.getUsername());
  }

  private void checkIfPasswordCorrects(User userToBeChecked){
    User userByUsername = userRepository.findByUsername(userToBeChecked.getUsername());
    if(userByUsername==null)
    {throw new ResponseStatusException(HttpStatus.CONFLICT, "Unregistered User!");}
    else if(!userByUsername.getPassword().equals(userToBeChecked.getPassword())){
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Wrong Password!");
    }
  }

  private void checkIfUserExists(User userToBeCreated) {
    User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());

    if (userByUsername != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT,
          "The username provided is not unique. Therefore, the user could not be created!");
    }
  }

  public User getUserById(long id) {
    return userRepository.findById(id);
  }

  public void update(User userToBeUpdated, User updateUserInfo) {
    if(updateUserInfo.getUsername() != null){
      String updateUsername = updateUserInfo.getUsername();
      checkEmptyString(updateUsername, "username");
      if(userToBeUpdated.getUsername().compareTo(updateUserInfo.getUsername()) != 0) {
        checkIfUserExists(updateUserInfo);
      }
      userToBeUpdated.setUsername(updateUserInfo.getUsername());
    }
    if(updateUserInfo.getPassword() != null){
      String updatePassword = updateUserInfo.getPassword();
      checkEmptyString(updatePassword,"password");
    }
    if(updateUserInfo.getImage() != null){
      String updateImage = updateUserInfo.getImage();
      userToBeUpdated.setImage(updateUserInfo.getImage());
    }
    userRepository.save(userToBeUpdated);
  }

  public User getUserByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  public void addFriend(User user1, User user2, int addFriendStatus) {

    if(addFriendStatus == 1) { //addFriend
      if(user2.getFriends().contains(user1)) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "You have sended an adding-friend request to this friend!");
      }
      //when A send request to B, B will add friend A, when B accept request, A will also add friend B.
      //The above relationship is reversal to intuition for better operation
      user2.getFriends().add(user1);
      userRepository.save(user2);
    } else if(addFriendStatus == 2) {  //Accept friend
      if(user2.getFriends().contains(user1)) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "You have already added this friend! Please refresh the page");
      }
      user2.getFriends().add(user1);
      userRepository.save(user2);
    } else {  //reject friend
      if(user1.getFriends().contains(user2) == false) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "You have already rejected this friend request! Please refresh the page");
      }
      user1.getFriends().remove(user2);
      userRepository.save(user1);
    }
  }

  public long validateInvitedUserName(String username){
    User invitedUser = userRepository.findByUsername(username);
    System.out.println("inviting func");
    System.out.println(invitedUser);
    if(invitedUser==null){
      System.out.println("no invitedUser");
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no user called "+username);
    }

    if (invitedUser.getRoom()!=null){
      System.out.println("invitedUser in a room");
      throw new ResponseStatusException(HttpStatus.CONFLICT, username+" has been in a room");
    }
    return invitedUser.getId();

  }
}