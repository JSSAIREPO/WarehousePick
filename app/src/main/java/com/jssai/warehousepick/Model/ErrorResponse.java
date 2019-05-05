package com.jssai.warehousepick.Model;

public class ErrorResponse {

    private String Type;

    private String Message;

    public ErrorResponse(String type, String message) {
        Type = type;
        Message = message;
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
}
