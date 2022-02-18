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

public class Categories implements Viewers {
    static HashMap<Object, Object> categoryId;
    static List<String> categoriesList;
    static int pages;
    static int currentStartPage;
    static int prevStartPage;
    static int nextStartPage;

    public Categories() {
        categoryId = new HashMap<>();
        categoriesList = new ArrayList<>();
        pages = Playlists.PAGES;
        currentStartPage = 0;
        prevStartPage = -pages;
        nextStartPage = pages;
    }

    public void next() {
        if (nextStartPage >= categoriesList.size()) {
            System.out.println("No more pages.");
        } else {
            prevStartPage = currentStartPage;
            currentStartPage = nextStartPage;
            nextStartPage = Math.min(nextStartPage + pages, categoriesList.size());
            for (int i = currentStartPage; i < nextStartPage; i++) {
                if (i >= categoriesList.size()) break;
                System.out.println(categoriesList.get(i));
            }
            System.out.println("---PAGE " + (currentStartPage/pages + 1) + " OF " + categoriesList.size() / pages + "---");
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
                if (i >= categoriesList.size()) break;
                System.out.println(categoriesList.get(i));
            }
            System.out.println("---PAGE " + (currentStartPage/pages + 1) + " OF " + categoriesList.size() / pages + "---");
        }
    }

    public void printPlaylist() {
        getAllCategories();
        for (int i = currentStartPage; i < nextStartPage; i++) {
            if (i >= categoriesList.size()) break;
            System.out.println(categoriesList.get(i));
        }
        System.out.println("---PAGE " + (currentStartPage/pages + 1) + " OF " + categoriesList.size() / pages + "---");
    }


    static void getAllCategories() {
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
                    categoriesList.add(playlistName);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
