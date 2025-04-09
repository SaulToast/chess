package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import com.google.gson.Gson;

import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();
    Map<Integer, Set<String>> activePlayersByGame = new ConcurrentHashMap<>();

    public void add(String visitorName, Session session, int gameID) {
        var connection = new Connection(visitorName, session, gameID);
        connections.put(visitorName, connection);
    }

    public void remove(String visitorName) {
        connections.remove(visitorName);
    }

    public void broadcast(String excludeVisitorName, int targetGameID, ServerMessage message) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.visitorName.equals(excludeVisitorName) && c.gameID == targetGameID) {
                    c.send(new Gson().toJson(message));
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.visitorName);
        }
    }

    public void playerJoined(int gameID, String username) {
        activePlayersByGame.computeIfAbsent(gameID, 
            _ -> ConcurrentHashMap.newKeySet()).add(username);
    }

    public void playerLeft(int gameID, String username) {
        var players = activePlayersByGame.get(gameID);
        if (players != null) {
            players.remove(username);
            if (players.isEmpty()) {
                activePlayersByGame.remove(gameID);
            }
        }
    }

    public boolean hasActivePlayers(int gameID) {
        return activePlayersByGame.containsKey(gameID);
    }
}