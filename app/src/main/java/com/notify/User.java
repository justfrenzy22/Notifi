package com.notify;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class User {
    private final String firstName;
    private final String lastName;
    private final String email;
    private final Boolean isAlarmOn;
    private NodeMCU[] nodeMCUArray;

    public User(String firstName, String lastName, String email, Boolean isAlarmOn, NodeMCU[] nodeMCUArray) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.isAlarmOn = isAlarmOn;
        this.nodeMCUArray = nodeMCUArray;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getEmail() {
        return this.email;
    }

    public Boolean getIsAlarmOn() {
        return this.isAlarmOn;
    }

    public NodeMCU[] getNodeMCUArray() {
        return this.nodeMCUArray;
    }
}
