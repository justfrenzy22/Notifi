package com.notify;

import android.widget.Button;

import java.util.Date;

public class NodeMCU {
    private final String _id;
    private final String userId;
    private final String name;
    private String authToken;
    private final String link;
    private final Date[] triggeredAt;
    private final Date createdAt;


    public NodeMCU(String _id, String userid, String name, String authToken, Date[] triggeredAt, Date createdAt) {
        this._id = _id;
        this.userId = userid;
        this.name = name;
        this.authToken = authToken;
        this.link = "http://87.227.174.139:8080/nodemcu/link";
        this.triggeredAt = triggeredAt;
        this.createdAt = createdAt;
    }

    public String getId () {
        return this._id;
    }


    public String getUserId () {
        return this.userId;
    }

    public String getName () {
        return this.name;
    }

    public String getAuthToken () {
        return this.authToken;
    }

    public void setAuthToken (String authToken)
    {
        this.authToken = authToken;
    }

    public String getLink () {
        return this.link;
    }





}
