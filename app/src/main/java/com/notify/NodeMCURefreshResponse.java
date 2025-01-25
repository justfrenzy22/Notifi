package com.notify;

public class NodeMCURefreshResponse {
    private final String msg;
    private final String authToken;

    public NodeMCURefreshResponse(String msg, String authToken) {
        this.msg = msg;
        this.authToken = authToken;
    }

    public String getMsg() {return this.msg; }
    public String getAuthToken() {return this.authToken; }
}
