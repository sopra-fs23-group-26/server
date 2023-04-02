package ch.uzh.ifi.hase.soprafs23.rest.mapper;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPutDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import javax.sql.rowset.serial.SerialBlob;
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

  @Mapping(source = "email", target = "email")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "password", target = "password")
  @Mapping(source = "image", target = "image", qualifiedByName = "mapToBlob")
  User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);
  @Named("mapToBlob")
  public default Blob mapToBlob(String bytes) throws SQLException {
    if (bytes == null) {
      return null;
    }
    try {
      return new SerialBlob(bytes.getBytes());
    }
    catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }


  @Mapping(source = "email", target = "email")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "globalRanking", target = "globalRanking")
  @Mapping(source = "communityRanking", target = "communityRanking")
  @Mapping(source = "score", target = "score")
  @Mapping(source = "id", target = "id")
  @Mapping(source = "token", target = "token")
  @Mapping(source = "image", target = "image", qualifiedByName = "mapToString")
  UserGetDTO convertEntityToUserGetDTO(User user);
  @Named("mapToString")
  public default String mapToString(Blob bytes) throws SQLException {
    if (bytes == null) return null;
    else
      return bytes.toString();
  }

  @Mapping(source = "email", target = "email")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "password", target = "password")
  User convertUserPutDTOtoEntity(UserPutDTO userPutDTO);
}
