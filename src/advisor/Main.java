package advisor;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<String, String> inputs = new HashMap<>();
        for (int i = 0; i < args.length; i += 2) inputs.put(args[i], args[i+1]);
        if (inputs.containsKey("-access")) {
            Authorization.SERVER_PATH = inputs.get("-access");
        }
        if (inputs.containsKey("-resource")) {
            Authorization.API_SERVER_PATH = inputs.get("-resource");
        }
        if (inputs.containsKey("-page")) {
            Playlists.PAGES = Integer.parseInt(inputs.get("-page"));
        }
        Playlists playlists = new Playlists();
        playlists.run();
    }
}
