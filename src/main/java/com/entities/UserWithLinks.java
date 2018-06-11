package com.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

public class UserWithLinks extends ResourceSupport {

    private User user;

    public UserWithLinks() {};

    @JsonCreator
    public UserWithLinks(@JsonProperty("content") User user) {
        this.user = user;
    };

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
