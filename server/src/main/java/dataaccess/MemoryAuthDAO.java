package dataaccess;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import dataaccess.interfaces.AuthDAO;
import model.AuthData;

public class MemoryAuthDAO implements AuthDAO{

    private Map<String, AuthData> authTokens;

    public MemoryAuthDAO() {
        authTokens = new HashMap<String, AuthData>();
    }

    @Override
    public void createAuth(String username) throws DataAccessException {
        if (!authTokens.containsKey(username)){
            var id = UUID.randomUUID().toString();
            var data = new AuthData(id, username);
            authTokens.put(username, data);
            return;
        }
        throw new DataAccessException("Username already has an authToken");
    }

    @Override
    public AuthData getAuth(String username) throws DataAccessException {
        AuthData token = authTokens.get(username);
        if (token != null) {
            return token;
        }
        throw new DataAccessException("Username doesn't have an associated authToken");
    }

    @Override
    public void deleteAuth(String username) throws DataAccessException {
        if (authTokens.containsKey(username)){
            authTokens.remove(username);
            return;
        }
        throw new DataAccessException("Username doesn't have an associated authToken");
    }

}
