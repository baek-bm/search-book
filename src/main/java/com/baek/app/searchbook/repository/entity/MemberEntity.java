package com.baek.app.searchbook.repository.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "member")
public class MemberEntity {
    @Id
    @Column(length = 50, nullable = false)
    private String email;

    @Column(length = 100, nullable = false)
    private String password;

    @Column(length = 50, nullable = false)
    private String userName;

    @Column(nullable = false)
    private Boolean isRoleAdmin = true;

    @Column(nullable = false)
    private Boolean isRoleMember = true;

    @Column(nullable = false)
    private Boolean isRoleMonitor = true;

    @Column(nullable = false)
    private LocalDateTime createdDate;

    @Column(nullable = false)
    private LocalDateTime modifiedDate;
}