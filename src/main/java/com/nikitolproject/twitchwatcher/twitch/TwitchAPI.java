package com.nikitolproject.twitchwatcher.twitch;

import com.nikitolproject.twitchwatcher.twitch.model.Streamer;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TwitchAPI {

    private final OkHttpClient httpClient = new OkHttpClient();
    private final String clientId;
    private final String clientSecret;
    private String accessToken;

    public TwitchAPI(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public void authenticate() throws IOException {
        RequestBody body = new FormBody.Builder()
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .add("grant_type", "client_credentials")
                .build();

        Request request = new Request.Builder()
                .url("https://id.twitch.tv/oauth2/token")
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            JSONObject json = new JSONObject(response.body().string());
            this.accessToken = json.getString("access_token");
        }
    }

    public Set<String> getLiveStreamers(List<Streamer> streamers) throws IOException {
        if (accessToken == null) {
            authenticate();
        }

        String url = "https://api.twitch.tv/helix/streams?" + streamers.stream()
                .map(s -> "user_login=" + s.getTwitchNickname())
                .collect(Collectors.joining("&"));

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Client-ID", clientId)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                if (response.code() == 401) { // Unauthorized, likely token expired
                    authenticate(); // Re-authenticate
                    return getLiveStreamers(streamers); // Retry the request
                }
                throw new IOException("Unexpected code " + response);
            }

            JSONObject json = new JSONObject(response.body().string());
            JSONArray data = json.getJSONArray("data");

            Set<String> liveStreamers = new HashSet<>();
            for (int i = 0; i < data.length(); i++) {
                JSONObject stream = data.getJSONObject(i);
                liveStreamers.add(stream.getString("user_login").toLowerCase());
            }
            return liveStreamers;
        }
    }
}