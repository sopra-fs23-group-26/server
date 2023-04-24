package ch.uzh.ifi.hase.soprafs23.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ROOM")
public class Room implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name="room_seq", sequenceName = "ROOM_SEQUENCE", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="room_seq")
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String gameName;

    @Column(nullable = false)
    private Long ownerId;

    @OneToMany(mappedBy = "room", cascade = CascadeType.PERSIST, orphanRemoval = false, fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<User> players = new HashSet<>();





    public Set<User> getPlayers() {
        return players;
    }

    public void setPlayers(Set<User> players) {
        this.players = players;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOwnerId() { return ownerId;}

    public void setOwnerId(Long ownerId){ this.ownerId=ownerId;}

}
