package ch.uzh.ifi.hase.soprafs23.rest.mapper;

import ch.uzh.ifi.hase.soprafs23.entity.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.RoomGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.RoomPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.RoomPutDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPutDTO;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-04-17T22:23:59+0200",
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
        userGetDTO.setImage( user.getImage() );
        userGetDTO.setCommunityRanking( user.getCommunityRanking() );
        userGetDTO.setId( user.getId() );
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
        user.setUsername( userPutDTO.getUsername() );
        user.setId( userPutDTO.getId() );

        return user;
    }

    @Override
    public Room convertRoomPostDTOtoEntity(RoomPostDTO roomPostDTO) {
        if ( roomPostDTO == null ) {
            return null;
        }

        Room room = new Room();

        room.setGameName( roomPostDTO.getGameName() );
        room.setOwnerId( roomPostDTO.getOwnerId() );
        Set<User> set = roomPostDTO.getPlayers();
        if ( set != null ) {
            room.setPlayers( new HashSet<User>( set ) );
        }
        room.setName( roomPostDTO.getName() );
        room.setId( roomPostDTO.getId() );

        return room;
    }

    @Override
    public RoomGetDTO convertEntityToRoomGetDTO(Room room) {
        if ( room == null ) {
            return null;
        }

        RoomGetDTO roomGetDTO = new RoomGetDTO();

        roomGetDTO.setName( room.getName() );
        roomGetDTO.setId( room.getId() );
        roomGetDTO.setGameName( room.getGameName() );
        Set<User> set = room.getPlayers();
        if ( set != null ) {
            roomGetDTO.setPlayers( new HashSet<User>( set ) );
        }

        return roomGetDTO;
    }

    @Override
    public Room convertRoomPutDTOtoEntity(RoomPutDTO roomPutDTO) {
        if ( roomPutDTO == null ) {
            return null;
        }

        Room room = new Room();

        Set<User> set = roomPutDTO.getPlayers();
        if ( set != null ) {
            room.setPlayers( new HashSet<User>( set ) );
        }
        room.setId( roomPutDTO.getId() );

        return room;
    }

    @Override
    public RoomPostDTO convertEntityToRoomPostDTO(Room room) {
        if ( room == null ) {
            return null;
        }

        RoomPostDTO roomPostDTO = new RoomPostDTO();

        roomPostDTO.setName( room.getName() );
        roomPostDTO.setId( room.getId() );
        roomPostDTO.setGameName( room.getGameName() );
        Set<User> set = room.getPlayers();
        if ( set != null ) {
            roomPostDTO.setPlayers( new HashSet<User>( set ) );
        }
        roomPostDTO.setOwnerId( room.getOwnerId() );

        return roomPostDTO;
    }
}
