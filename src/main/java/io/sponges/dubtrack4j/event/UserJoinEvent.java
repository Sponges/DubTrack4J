package io.sponges.dubtrack4j.event;

import io.sponges.dubtrack4j.api.Room;
import io.sponges.dubtrack4j.event.framework.Event;
import io.sponges.dubtrack4j.api.User;

public class UserJoinEvent implements Event {

    private final User user;
    private final Room room;

    public UserJoinEvent(User user, Room room) {
        this.user = user;
        this.room = room;
    }

    public User getUser() {
        return user;
    }

    public Room getRoom() {
        return room;
    }
}
