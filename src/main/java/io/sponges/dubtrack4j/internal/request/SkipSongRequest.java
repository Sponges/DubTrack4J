package io.sponges.dubtrack4j.internal.request;

import io.sponges.dubtrack4j.DubAccount;
import io.sponges.dubtrack4j.util.URL;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

public class SkipSongRequest implements Request {

    private String room, song;
    private DubAccount account;

    public SkipSongRequest(String room, String song, DubAccount account) throws IOException {
        this.room = room;
        this.song = song;
        this.account = account;

        request();
    }

    public Connection.Response request() throws IOException {
        String url = String.format(URL.SKIP_SONG.toString(), room, song);

        Connection.Response response = Jsoup.connect(url)
                .ignoreContentType(true)
                .userAgent("Mozilla/5.0 DubTrack4J")
                .cookie("connect.sid", account.getToken())
                .data("realTimeChannel", "dubtrackfm-" + room)
                .method(Connection.Method.POST)
                .execute();

        return response;
    }

}
