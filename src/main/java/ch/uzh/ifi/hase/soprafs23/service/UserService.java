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
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import javax.sql.rowset.serial.SerialBlob;
import java.io.*;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
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
    InputStream inputStream = getClass().getResourceAsStream("/default.png");
    Blob blob = new javax.sql.rowset.serial.SerialBlob(inputStream.readAllBytes());
    newUser.setImage(blob);
    checkIfUserExists(newUser);
    checkEmptyString(newUser.getPassword(),"password");
    checkEmptyString(newUser.getUsername(),"username");
    newUser = userRepository.save(newUser);
    userRepository.flush();
    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }

  private void checkEmptyString(String string, String message) {
    if(string.trim().isEmpty()){
      throw new ResponseStatusException(HttpStatus.CONFLICT, message + " cannot be empty space.");
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
       checkIfUserExists(updateUserInfo);
       userToBeUpdated.setUsername(updateUserInfo.getUsername());
     }
    if(updateUserInfo.getPassword() != null){
      String updatePassword = updateUserInfo.getPassword();
      checkEmptyString(updatePassword,"password");
    }
    userRepository.save(userToBeUpdated);
  }

  public User getUserByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  public void addFriend(User user, String username) {

    User friend = userRepository.findByUsername(username);

    if(friend == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Can't find your friend's username!");
    }

    user.getFriends().add(friend);
    friend.getFriends().add(user);

    userRepository.save(user);
    userRepository.save(friend);
  }
}
