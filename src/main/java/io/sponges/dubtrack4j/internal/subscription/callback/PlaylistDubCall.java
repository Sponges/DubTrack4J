package io.sponges.dubtrack4j.internal.subscription.callback;

import io.sponges.dubtrack4j.DubtrackAPI;
import org.json.JSONObject;
import io.sponges.dubtrack4j.api.DubType;
import io.sponges.dubtrack4j.api.Room;
import io.sponges.dubtrack4j.api.Song;
import io.sponges.dubtrack4j.api.User;
import io.sponges.dubtrack4j.event.UserDubEvent;
import io.sponges.dubtrack4j.util.Logger;

public class PlaylistDubCall extends SubCallback {

    private DubtrackAPI dubtrack;

    public PlaylistDubCall(DubtrackAPI dubtrack) {
        this.dubtrack = dubtrack;
    }

    @Override
    public void run(JSONObject json) {
        Logger.debug(false, json.toString());

        String username = json.getJSONObject("user").getString("username");
        String userId = json.getJSONObject("user").getString("_id");
        String roomId = json.getJSONObject("playlist").getString("roomid");

        int currentUps = json.getJSONObject("playlist").getInt("updubs");
        int currentDowns = json.getJSONObject("playlist").getInt("downdubs");

        DubType type = DubType.valueOf(json.getString("dubtype").toUpperCase());

        Room room = dubtrack.loadRoom(roomId);
        User user = room.loadUser(userId, username);
        Song song = room.getCurrent();
        if (song == null) return;

        song.setUpdubs(currentUps);
        song.setDowndubs(currentDowns);

        //if (type == UserDubEvent.DubType.UPDUB) song.addUpdub();
        //else song.addDowndub();

        dubtrack.getEventManager().handle(new UserDubEvent(song, user, room, type));
    }

}
