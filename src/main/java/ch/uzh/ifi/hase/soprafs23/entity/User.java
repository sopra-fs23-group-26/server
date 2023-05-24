package ch.uzh.ifi.hase.soprafs23.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Internal User Representation
 * This class composes the internal representation of the user and defines how
 * the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes
 * the primary key
 */
@Entity
@Table(name = "USER")
public class User implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue
  private Long id;

  @Column
  private String image;

  @Column(nullable = false, unique = true)
  private String username;

  private String friendName;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false, unique = true)
  private String token;

  @Column(nullable = false)
  private int score;

  @Column(nullable = false)
  private int globalRanking;

  @Column(nullable = false)
  private int communityRanking;

  private String word;

  private boolean undercover;

  private boolean isVoted;

  private String description;

  @ManyToMany
  @JsonIgnore
  @JoinTable(name = "friends",
          joinColumns = @JoinColumn(name = "user_id"),
          inverseJoinColumns = @JoinColumn(name = "friend_id"))
  private List<User> friends;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "room_id")
  @JsonIgnore
  private Room room;

  private int votes;

  public Room getRoom() {
    return room;
  }

  public void setRoom(Room room) {
    this.room = room;
  }

  public List<User> getFriends() {
    return friends;
  }

  public void setFriends(List<User> friends) {
    this.friends = friends;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

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

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public int getScore() {
    return score;
  }

  public void setScore(int score) {
    this.score = score;
  }

  public int getGlobalRanking() {
    return globalRanking;
  }

  public void setGlobalRanking(int globalRanking) {
    this.globalRanking = globalRanking;
  }

  public int getCommunityRanking() {
    return communityRanking;
  }

  public void setCommunityRanking(int communityRanking) {
    this.communityRanking = communityRanking;
  }

  public String getWord() {
    return word;
  }

  public void setWord(String word) {
    this.word = word;
  }

  public boolean isUndercover() {
    return undercover;
  }

  public void setUndercover(boolean undercover) {
    this.undercover = undercover;
  }

  public boolean isVoted() {
    return isVoted;
  }

  public void setVoted(boolean voted) {
    isVoted = voted;
  }

  public String getFriendName() {
    return friendName;
  }

  public void setFriendName(String friendName) {
    this.friendName = friendName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public int getVotes() {
    return votes;
  }

  public void setVotes(int votes) {
    this.votes = votes;
  }
}