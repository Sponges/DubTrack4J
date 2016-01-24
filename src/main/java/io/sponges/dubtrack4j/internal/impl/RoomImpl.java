/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Joe Burnard
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.sponges.dubtrack4j.internal.impl;

import io.sponges.dubtrack4j.exception.InvalidUserException;
import io.sponges.dubtrack4j.framework.ProfileImage;
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

    private User creator;

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
    public User getUserByUsername(String username) throws IOException {
        for (User user : users.values()) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }

        String id = getUserIdFromUsername(username);
        return getUserById(id);
    }

    private String getUserIdFromUsername(String username) throws IOException {
        JSONObject json = new UserInfoRequest(dubtrack, username).request();
        JSONObject userInfo = json.getJSONObject("data").getJSONObject("userInfo");
        return userInfo.getString("userid");
    }

    @Override
    public User getUserById(String id) throws IOException {
        User user = users.get(id);

        if (user == null) {
            return loadUserData(id);
        } else {
            return user;
        }
    }

    @Override
    public Song getCurrentSong() {
        return current;
    }

    @Override
    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    // not interfaced to prevent confusion between changing the song & setting the instance
    public void setCurrent(Song current) {
        this.current = current;
    }

    @Override
    public void sendMessage(String message) throws IOException {
        new SendMessageRequest(id, message, dubtrack).request();
    }
    
    public User getOrLoadUser(String id) throws IOException {
        User user = getUserById(id);

        if (user == null) {
            loadUserData(id);
        }

        return user;
    }

    private User loadUserData(String id) throws IOException {
        JSONObject userInfo = new UserInfoRequest(dubtrack, id).request();
        JSONObject data = userInfo.getJSONObject("data");
        String username = data.getString("username");
        int status = data.getInt("status");
        int roleId = data.getInt("roleid");
        int dubs = data.getInt("dubs");
        long created = data.getLong("created");

        ProfileImage profileImage;
        {
            JSONObject image = data.getJSONObject("profileImage");
            String imageId = image.getString("public_id");
            int width = image.getInt("width");
            int height = image.getInt("height");
            String format = image.getString("format");
            int bytes = image.getInt("bytes");
            String url = image.getString("url");
            String secureUrl = image.getString("secure_url");

            profileImage = new ProfileImage(imageId, width, height, format, bytes, url, secureUrl);
        }

        User user = new UserImpl(id, username, profileImage);
        users.put(id, user);
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
