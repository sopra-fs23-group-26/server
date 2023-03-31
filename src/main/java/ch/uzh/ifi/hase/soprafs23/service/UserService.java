package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
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

import java.util.Date;
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

  public User getSingleUsername(String username){
      return this.userRepository.findByUsername(username);
  }

  public User getUserById(long id){
      Optional<User> byId = this.userRepository.findById(id);
      if(byId.isPresent()){
          return byId.get();
      }
      return null;
  }


  public User createUser(User newUser){
    newUser.setToken(UUID.randomUUID().toString());
    checkIfUserExists(newUser);
    newUser = userRepository.save(newUser);
    userRepository.flush();
    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }

  public User updateUser(User user){
      return this.userRepository.saveAndFlush(user);
  }

  private void checkIfUserExists(User userToBeCreated) {
    User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());

    if (userByUsername != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT,
          "The username provided is not unique. Therefore, the user could not be created!");
    } else if (isEmail(userToBeCreated.getEmail()) == false){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Error: please input a valid email");
    } else if ((userToBeCreated.getPassword()).compareTo(userToBeCreated.getRepeatPassword()) != 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
              "The repeat password is not equal to the password, input again!");
    }
  }

  private boolean isEmail(String email) {
    int pos1 = email.indexOf("@");
    if (pos1 == -1) {
      return false;
    } else {
      String sub_email = email.substring(pos1 + 1);
      int pos2 = sub_email.indexOf(".");
      if ( pos2 == -1)
        return false;
      else
        return true;
    }
  }

}
