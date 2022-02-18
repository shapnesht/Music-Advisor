package advisor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParticularPlaylist implements Viewers {
    static Map<String, String> categoryId;
    static String str;
    static List<String> songName;
    static List<String> songLink;
    static int pages;
    static int currentStartPage;
    static int prevStartPage;
    static int nextStartPage;
    public ParticularPlaylist(String type) {
        str = type;
        categoryId = new HashMap<>();
        songName = new ArrayList<>();
        songLink = new ArrayList<>();
        pages = Playlists.PAGES;
        currentStartPage = 0;
        prevStartPage = -pages;
        nextStartPage = pages;
    }

    public void next() {
        if (nextStartPage >= songName.size()) {
            System.out.println("No more pages.");
        } else {
            prevStartPage = currentStartPage;
            currentStartPage = nextStartPage;
            nextStartPage = Math.min(nextStartPage + pages, songLink.size());
            for (int i = currentStartPage; i < nextStartPage; i++) {
                if (i >= songName.size()) break;
                System.out.println(songName.get(i));
                System.out.println(songLink.get(i) + "\n");
            }
            System.out.println("---PAGE " + (currentStartPage/pages + 1) + " OF " + songName.size() / pages + "---");
        }
    }
    public void prev() {
        if (prevStartPage < 0) {
            System.out.println("No more pages.");
        } else {
            nextStartPage = currentStartPage;
            currentStartPage = prevStartPage;
            prevStartPage = prevStartPage - pages;
            for (int i = currentStartPage; i < nextStartPage; i++) {
                if (i >= songName.size()) break;
                System.out.println(songName.get(i));
                System.out.println(songLink.get(i) + "\n");
            }
            System.out.println("---PAGE " + (currentStartPage/pages + 1) + " OF " + songName.size() / pages + "---");
        }
    }

    public void printPlaylist() {
        getCategories();
        getPlaylist();
        for (int i = currentStartPage; i < nextStartPage; i++) {
            if (i >= songName.size()) break;
            System.out.println(songName.get(i));
            System.out.println(songLink.get(i) + "\n");
        }
        System.out.println("---PAGE " + (currentStartPage/pages + 1) + " OF " + songName.size() / pages + "---");
    }

    private void getPlaylist() {
        if (categoryId.containsKey(str)) {
            String path = Authorization.API_SERVER_PATH + "/v1/browse/categories/" + categoryId.get(str) + "/playlists";

            HttpRequest request = HttpRequest.newBuilder()
                    .header("Authorization", "Bearer " + Authorization.ACCESS_TOKEN)
                    .uri(URI.create(path))
                    .GET()
                    .build();

            try {
                HttpClient client = HttpClient.newBuilder().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                String jsonString = response.body();
                JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();

                JsonObject playlists = json.getAsJsonObject("playlists");
                JsonElement items = playlists.getAsJsonArray("items");

                for (JsonElement element : items.getAsJsonArray()) {
                    songName.add(element.getAsJsonObject().get("name").getAsString());
                    songLink.add(element.getAsJsonObject().get("external_urls").getAsJsonObject().get("spotify").getAsString());
                }
            } catch (IOException | InterruptedException | NullPointerException e) {
                System.out.println("Test unpredictable error message");
            }
        } else {
            System.out.println("Specified id doesn't exist");
        }
    }

    private void getCategories() {
        String path = Authorization.API_SERVER_PATH + "/v1/browse/categories";

        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + Authorization.ACCESS_TOKEN)
                .uri(URI.create(path))
                .GET()
                .build();

        try {
            HttpClient client = HttpClient.newBuilder().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String jsonString = response.body();

            JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
            JsonObject categories = json.getAsJsonObject("categories");
            JsonElement items = categories.getAsJsonArray("items");

            for (JsonElement element : items.getAsJsonArray()) {
                if (element.isJsonObject()) {
                    String playlistName = element.getAsJsonObject().get("name").getAsString();
                    String playlistID = element.getAsJsonObject().get("id").getAsString();
                    categoryId.put(playlistName, playlistID);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
