package com.nanugi.api.model.dto;

import lombok.Data;

@Data
public class UserResponse {
    private String name;
    private String uid;

    public UserResponse(String _name, String _uid){
        name = _name;
        uid = _uid;
    }
}