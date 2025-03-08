package dataaccess.memoryDAOs;

import java.util.HashMap;
import java.util.Map;

import dataaccess.DataAccessException;
import dataaccess.interfaces.UserDAO;
import model.UserData;

public class MemoryUserDAO implements UserDAO{

    private Map<String, UserData> users;

    public MemoryUserDAO() {
        users = new HashMap<String, UserData>();
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        UserData data = users.get(username);
        if (data != null) {
            return data;
        }
        throw new DataAccessException("Error: The requested user doesn't exist");
    }

    @Override
    public void addUserData(UserData data) throws DataAccessException {
        if (!users.containsKey(data.username())) {
            users.put(data.username(), data);
        } else {
            throw new DataAccessException("Error: Username already exists");
        }
    }

    @Override
    public void clear(){
        users = new HashMap<>();
    }

    public Map<String, UserData> getAllUserData() {
        return users;
    }

}
