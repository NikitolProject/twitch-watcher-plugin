package com.nikitolproject.twitchwatcher.twitch.model;

public class Streamer {

    private final String twitchNickname;
    private final String minecraftNickname;

    public Streamer(String twitchNickname, String minecraftNickname) {
        this.twitchNickname = twitchNickname;
        this.minecraftNickname = minecraftNickname;
    }

    public String getTwitchNickname() {
        return twitchNickname;
    }

    public String getMinecraftNickname() {
        return minecraftNickname;
    }
}
