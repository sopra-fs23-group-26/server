package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.entity.User;

import java.util.Set;

public class RoomGetDTO {
    private Long id;
    private String name;
    private Set<User> players;
    private String gameName;

    private Long ownerId;

    public Set<User> getPlayers() {
        return players;
    }

    public void setPlayers(Set<User> players) {
        this.players = players;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }


    public void setOwnerId(Long ownerId){ this.ownerId=ownerId;}

    public Long getOwnerId(){ return ownerId;}
}
