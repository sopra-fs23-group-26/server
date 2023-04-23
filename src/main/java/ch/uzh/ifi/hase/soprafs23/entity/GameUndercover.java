package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "GameUndercover")
public class GameUndercover implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name="undercover_seq", sequenceName = "UNDERCOVER_SEQUENCE", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="undercover_seq")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "game_users",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> users = new HashSet<>();

    private GameStatus gameStatus;

    private String currentPlayerUsername;

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public String getCurrentPlayerUsername() {
        return currentPlayerUsername;
    }

    public void setCurrentPlayerUsername(String currentPlayerUsername) {
        this.currentPlayerUsername = currentPlayerUsername;
    }
}
