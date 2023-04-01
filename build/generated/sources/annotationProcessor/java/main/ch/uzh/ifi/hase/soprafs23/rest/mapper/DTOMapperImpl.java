package ch.uzh.ifi.hase.soprafs23.rest.mapper;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPutDTO;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-04-01T22:00:06+0200",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 17.0.6 (Eclipse Adoptium)"
)
public class DTOMapperImpl implements DTOMapper {

    @Override
    public User convertUserPostDTOtoEntity(UserPostDTO userPostDTO) {
        if ( userPostDTO == null ) {
            return null;
        }

        User user = new User();

        user.setPassword( userPostDTO.getPassword() );
        user.setEmail( userPostDTO.getEmail() );
        user.setUsername( userPostDTO.getUsername() );
        user.setId( userPostDTO.getId() );

        return user;
    }

    @Override
    public UserGetDTO convertEntityToUserGetDTO(User user) {
        if ( user == null ) {
            return null;
        }

        UserGetDTO userGetDTO = new UserGetDTO();

        userGetDTO.setGlobalRanking( user.getGlobalRanking() );
        userGetDTO.setScore( user.getScore() );
        userGetDTO.setCommunityRanking( user.getCommunityRanking() );
        userGetDTO.setId( user.getId() );
        userGetDTO.setEmail( user.getEmail() );
        userGetDTO.setUsername( user.getUsername() );
        userGetDTO.setToken( user.getToken() );

        return userGetDTO;
    }

    @Override
    public User convertUserPutDTOtoEntity(UserPutDTO userPutDTO) {
        if ( userPutDTO == null ) {
            return null;
        }

        User user = new User();

        user.setPassword( userPutDTO.getPassword() );
        user.setEmail( userPutDTO.getEmail() );
        user.setUsername( userPutDTO.getUsername() );
        user.setId( userPutDTO.getId() );

        return user;
    }
}
