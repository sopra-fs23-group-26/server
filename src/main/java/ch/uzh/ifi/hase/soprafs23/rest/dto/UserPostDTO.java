package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;

import java.util.Date;

public class UserPostDTO {

    private Long id;
    private String email;
    private String username;
    private String password;
    private String repeatPassword;

    private int score;
    private int communityranking;
    private int globalranking;

    public UserPostDTO() {
        this.score = 0;
        this.communityranking = 0;
        this.globalranking = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getCommunityranking() {
        return communityranking;
    }

    public void setCommunityranking(int communityranking) {
        this.communityranking = communityranking;
    }

    public int getGlobalranking() {
        return globalranking;
    }

    public void setGlobalranking(int globalranking) {
        this.globalranking = globalranking;
    }
}
