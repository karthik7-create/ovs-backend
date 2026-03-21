package com.vote.ovs.Dto;

import lombok.Data;

@Data
public class AdminRegisterRequest {
    private String username;
    private String password;
    private String adminSecret;
}
