package ch.uzh.ifi.hase.soprafs23.rest.mapper;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPostDTO;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-03-31T17:08:35+0200",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 17.0.6 (Private Build)"
)
public class DTOMapperImpl implements DTOMapper {

    @Override
    public User convertUserPostDTOtoEntity(UserPostDTO userPostDTO) {
        if ( userPostDTO == null ) {
            return null;
        }

        User user = new User();

        user.setPassword( userPostDTO.getPassword() );
        user.setRepeatPassword( userPostDTO.getRepeatPassword() );
        user.setEmail( userPostDTO.getEmail() );
        user.setUsername( userPostDTO.getUsername() );
        user.setId( userPostDTO.getId() );
        user.setScore( userPostDTO.getScore() );
        user.setCommunityranking( userPostDTO.getCommunityranking() );
        user.setGlobalranking( userPostDTO.getGlobalranking() );

        return user;
    }

    @Override
    public UserGetDTO convertEntityToUserGetDTO(User user) {
        if ( user == null ) {
            return null;
        }

        UserGetDTO userGetDTO = new UserGetDTO();

        userGetDTO.setPassword( user.getPassword() );
        userGetDTO.setRepeatPassword( user.getRepeatPassword() );
        userGetDTO.setEmail( user.getEmail() );
        userGetDTO.setUsername( user.getUsername() );
        userGetDTO.setId( user.getId() );
        userGetDTO.setScore( user.getScore() );
        userGetDTO.setCommunityranking( user.getCommunityranking() );
        userGetDTO.setGlobalranking( user.getGlobalranking() );

        return userGetDTO;
    }
}
