package ch.uzh.ifi.hase.soprafs23.rest.dto;

import java.sql.Blob;

public class UserGetDTO {

  private Long id;
  private String username;
  private int score;
  private int globalRanking;
  private int communityRanking;
  private String token;
  private String image;
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

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
