package com.jssai.warehousepick.Model;

public class LoginResponse {

    private String Key;

    private String User_ID;

    private String Password;

    private String Type;

    private String Message;

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getUser_ID() {
        return User_ID;
    }

    public void setUser_ID(String user_ID) {
        User_ID = user_ID;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "Key='" + Key + '\'' +
                ", User_ID='" + User_ID + '\'' +
                ", Password='" + Password + '\'' +
                ", Type='" + Type + '\'' +
                ", Message='" + Message + '\'' +
                '}';
    }
}
