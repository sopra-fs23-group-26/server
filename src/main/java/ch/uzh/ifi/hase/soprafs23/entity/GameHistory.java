package ch.uzh.ifi.hase.soprafs23.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalTime;

@Entity
@Table(name = "GameHistory")
public class GameHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name="gameHistory_seq", sequenceName = "HISTORY_SEQUENCE", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="history_seq")
    private Long id;

    @Column
    private LocalTime time;

    @Column
    private String username;

    @Column
    private String gameName;

    @Column
    private String earnedPoint;

    @Column
    private String winOrLose;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getEarnedPoint() {
        return earnedPoint;
    }

    public void setEarnedPoint(String earnedPoint) {
        this.earnedPoint = earnedPoint;
    }

    public String getWinOrLose() {
        return winOrLose;
    }

    public void setWinOrLose(String winOrLose) {
        this.winOrLose = winOrLose;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }
}
