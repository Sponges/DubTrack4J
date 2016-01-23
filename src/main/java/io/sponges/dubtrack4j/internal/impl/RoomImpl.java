package io.sponges.dubtrack4j.internal.impl;

import io.sponges.dubtrack4j.exception.InvalidUserException;
import io.sponges.dubtrack4j.framework.Room;
import io.sponges.dubtrack4j.framework.Song;
import io.sponges.dubtrack4j.framework.User;
import io.sponges.dubtrack4j.internal.DubtrackAPIImpl;
import io.sponges.dubtrack4j.internal.request.*;
import io.sponges.dubtrack4j.util.Logger;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RoomImpl implements Room {

    private final Map<String, User> users = new HashMap<>();

    private final DubtrackAPIImpl dubtrack;
    private final String name, id;

    private volatile String playlistId = null;
    private volatile Song current = null;

    public RoomImpl(DubtrackAPIImpl dubtrack, String name, String id) {
        this.dubtrack = dubtrack;
        this.name = name;
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getPlaylistId() throws IOException {
        if (playlistId == null) {
            playlistId = new RoomPlaylistRequest(id, dubtrack).request().getJSONObject("data").getJSONObject("song").getString("_id");
        }

        return playlistId;
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }

    @Override
    public Map<String, User> getUsers() {
        return users;
    }

    @Override
    public User getUserByUsername(String username) {
        for (User user : users.values()) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }

        return null;
    }

    @Override
    public User getUserById(String id) {
        return users.get(id);
    }

    @Override
    public Song getCurrent() {
        return current;
    }

    // not interfaced to prevent confusion between changing the song & setting the instance
    public void setCurrent(Song current) {
        this.current = current;
    }

    @Override
    public void sendMessage(String message) throws IOException {
        new SendMessageRequest(id, message, dubtrack).request();
    }

    public User loadUser(String id, String username) {
        User user = getUserById(id);

        if (user == null) {
            getUsers().put(id, new UserImpl(id, username));
            user = getUserById(id);
        }

        return user;
    }
    
    public User loadUser(DubtrackAPIImpl dubtrack, String id) throws IOException {
        User user = getUserById(id);

        if (user == null) {
            JSONObject userInfo = new UserInfoRequest(dubtrack, id).request();
            String username = userInfo.getJSONObject("data").getString("username");
            getUsers().put(id, new UserImpl(id, username));
            user = getUserById(id);
        }

        return user;
    }

    @Override
    public void kickUser(String username) throws IOException, InvalidUserException {
        User user = getUserByUsername(username);

        if (user == null) {
            throw new InvalidUserException(username);
        }

        kickUser(user);
    }

    @Override
    public void kickUser(User user) throws IOException {
        JSONObject jsonObject = new KickUserRequest(dubtrack, dubtrack.getAccount(), id, name, user.getId()).request();
        Logger.debug(jsonObject.toString());
    }

    @Override
    public void banUser(String username) throws IOException, InvalidUserException {
        User user = getUserByUsername(username);

        if (user == null) {
            throw new InvalidUserException(username);
        }

        banUser(user, 0);
    }

    @Override
    public void banUser(String username, int length) throws IOException, InvalidUserException {
        User user = getUserByUsername(username);

        if (user == null) {
            throw new InvalidUserException(username);
        }

        banUser(user, length);
    }

    @Override
    public void banUser(User user) throws IOException {
        banUser(user, 0);
    }

    @Override
    public void banUser(User user, int length) throws IOException {
        JSONObject jsonObject = new BanUserRequest(dubtrack, dubtrack.getAccount(), id, name, user.getId(), length).request();
        Logger.debug(jsonObject.toString());
    }

    @Override
    public void skipSong() throws IOException {
        current.skip();
    }

}
