package advisor;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class NewClass implements Viewers{
    static List<String> songName;
    static List<String> songLink;
    static List<ArrayList<String>> artistsName;
    static int pages;
    static int currentStartPage;
    static int prevStartPage;
    static int nextStartPage;

    public NewClass() {
        songName = new ArrayList<>();
        songLink = new ArrayList<>();
        artistsName = new ArrayList<>();
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
                System.out.println(artistsName.get(i));
                System.out.println(songLink.get(i) + "\n");
            }
            System.out.println("---PAGE " + (currentStartPage/pages + 1) + " OF " + songName.size() / pages + "---");
        }
    }
    @Override
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
                System.out.println(artistsName.get(i));
                System.out.println(songLink.get(i) + "\n");
            }
            System.out.println("---PAGE " + (currentStartPage/pages + 1) + " OF " + songName.size() / pages + "---");
        }
    }

    public void printPlaylist() {
        getNewReleases();
        for (int i = currentStartPage; i < nextStartPage; i++) {
            if (i >= songName.size()) break;
            System.out.println(songName.get(i));
            System.out.println(artistsName.get(i));
            System.out.println(songLink.get(i) + "\n");
        }
        System.out.println("---PAGE " + (currentStartPage/pages + 1) + " OF " + songName.size() / pages + "---");
    }

    static void getNewReleases() {
        String path = Authorization.API_SERVER_PATH + "/v1/browse/new-releases";

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
            JsonObject albums = json.getAsJsonObject("albums");
            JsonElement items = albums.getAsJsonArray("items");

            for (JsonElement element : items.getAsJsonArray()) {

                if (element.isJsonObject()) {
                    songName.add(element.getAsJsonObject().get("name").getAsString());

                    JsonArray elementArray = element.getAsJsonObject().getAsJsonArray("artists");

                    ArrayList<String> artistsList = new ArrayList<>();

                    for (JsonElement el : elementArray) {
                        artistsList.add(el.getAsJsonObject().get("name").getAsString());
                    }
                    artistsName.add(artistsList);
                    songLink.add(element.getAsJsonObject().get("external_urls")
                            .getAsJsonObject().get("spotify").getAsString());
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
