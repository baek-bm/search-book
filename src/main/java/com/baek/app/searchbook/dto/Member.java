package com.baek.app.searchbook.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
public class Member {
    @Email
    @NotEmpty
    private String email;
    @NotEmpty
    private String password;
    @NotEmpty
    private String userName;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public Member(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.userName = name;
    }
}
