package com.milkvender.procurement.entitty;

import javax.persistence.*;

@Entity
@Table(name = "USERS")
public class UserEntity {

    @Id
    @Column(name = "USERID")
    private Long userID;


    @Column(name = "DETAILS")
    private String details;

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
