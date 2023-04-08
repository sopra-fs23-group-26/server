package ch.uzh.ifi.hase.soprafs23.rest.dto;


import org.springframework.web.multipart.MultipartFile;

public class UserPostDTO {

    private Long id;
    private String username;
    private String password;
    private MultipartFile image;
    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
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

}
