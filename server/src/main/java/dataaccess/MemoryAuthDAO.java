package dataaccess;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import dataaccess.interfaces.AuthDAO;
import model.AuthData;

public class MemoryAuthDAO implements AuthDAO{

    private Map<String, String> authTokens;
    private Map<String, String> usernameToAuth;

    public MemoryAuthDAO() {
        authTokens = new HashMap<>();
        usernameToAuth = new HashMap<>();
    }

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        // if (usernameToAuth.containsKey(username)){
        //     throw new DataAccessException("Error: Username already has an authToken");
        // }

        var id = UUID.randomUUID().toString();
        var data = new AuthData(id, username);

        authTokens.put(id, username);
        usernameToAuth.put(username, id);

        return data;
        
        
    }

    @Override
    public String getAuthToken(String authToken) throws DataAccessException {
        if (authTokens.containsKey(authToken)) {
            return authTokens.get(authToken);
        }
        throw new DataAccessException("Error: authToken is not valid");
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if (authTokens.containsKey(authToken)){
            var username = authTokens.remove(authToken);
            usernameToAuth.remove(username);
            return;
        }
        throw new DataAccessException("Error: Username doesn't have an associated authToken");
    }

    @Override
    public void clear(){
        authTokens = new HashMap<>();
    }

}
