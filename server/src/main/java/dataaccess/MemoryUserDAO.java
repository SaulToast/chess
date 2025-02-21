package dataaccess;

import java.util.HashMap;
import java.util.Map;

import dataaccess.interfaces.UserDAO;
import model.UserData;

public class MemoryUserDAO implements UserDAO{

    private Map<String, UserData> users;

    public MemoryUserDAO () {
        users = new HashMap<String, UserData>();
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        UserData data = users.get(username);
        if (data != null) {
            return data;
        }
        throw new DataAccessException("The requested user doesn't exist");
    }

    @Override
    public void addUserData(UserData data) throws DataAccessException {
        if (!users.containsKey(data.username())) {
            users.put(data.username(), data);
        } else {
            throw new DataAccessException("Username already exists");
        }
    }

    @Override
    public void clear(){
        users = new HashMap<>();
    }

}
