package com.example.SpringBootDemoApplication.models.auth;

public class RefreshTokenRequest {

    private String refreshToken;
    private String username;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}
