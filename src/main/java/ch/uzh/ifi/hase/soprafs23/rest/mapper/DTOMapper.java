package ch.uzh.ifi.hase.soprafs23.rest.mapper;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.Room;
import ch.uzh.ifi.hase.soprafs23.rest.dto.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.sql.SQLException;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically
 * transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g.,
 * UserGetDTO for getting, UserPostDTO for creating, UserPutDTO for updating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for
 * creating information (POST).
 */
@Mapper
public interface DTOMapper {

  DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

  @Mapping(source = "username", target = "username")
  @Mapping(source = "password", target = "password")
  @Mapping(target = "image", ignore = true)
  User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);



  @Mapping(source = "username", target = "username")
  @Mapping(source = "globalRanking", target = "globalRanking")
  @Mapping(source = "communityRanking", target = "communityRanking")
  @Mapping(source = "score", target = "score")
  @Mapping(source = "id", target = "id")
  @Mapping(source = "token", target = "token")
  @Mapping(source = "image", target = "image")
  UserGetDTO convertEntityToUserGetDTO(User user);


  @Mapping(source = "username", target = "username")
  @Mapping(source = "password", target = "password")
  User convertUserPutDTOtoEntity(UserPutDTO userPutDTO);


  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "gameName", target = "gameName")
  @Mapping(source = "players", target = "players")
  Room convertRoomPostDTOtoEntity(RoomPostDTO roomPostDTO);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "gameName", target = "gameName")
  @Mapping(source = "players", target = "players")
  RoomGetDTO convertEntityToRoomGetDTO(Room room);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "players", target = "players")
  Room convertRoomPutDTOtoEntity(RoomPutDTO roomPutDTO);

}
