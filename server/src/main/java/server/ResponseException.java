package server;

import java.util.Map;

import com.google.gson.Gson;

public class ResponseException extends Exception {

    final private int statusCode;

    public ResponseException(int statusCode, String message){
        super(message);
        this.statusCode = statusCode;
    }

    public String toJson() {
        return new Gson().toJson(Map.of("message", getMessage(), "status", statusCode));
    }

    public int statusCode() {
        return statusCode;
    }
}
