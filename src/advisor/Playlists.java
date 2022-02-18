package advisor;

import java.util.*;

public class Playlists {
    static boolean isAuthorized = false;
    public static int PAGES = 5;
    Viewers current;
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while(true) {
            String command = scanner.nextLine().trim();
            if (command.equals("exit")) {
                System.out.println("---GOODBYE!---");
                System.exit(0);
            } else if (command.equals("auth")) {
                Authorization authorisation = new Authorization();
                authorisation.getAccessCode();
                authorisation.getAccessToken();
            } else if (!isAuthorized) {
                System.out.println("Please, provide access for application.");
            } else if (command.equals("categories")) {
                current = new Categories();
                current.printPlaylist();
            } else if (command.equals("featured")) {
                current = new Featured();
                current.printPlaylist();
            } else  if (command.equals("new")) {
                current = new NewClass();
                current.printPlaylist();
            } else if (command.contains("playlists ")) {
                String type = command.trim().substring(10);
                current = new ParticularPlaylist(type);
                current.printPlaylist();
            } else if (command.equals("prev")) {
                current.prev();
            } else if (command.equals("next")) {
                current.next();
            } else {
                System.out.println("Enter Valid Command.");
            }
        }
    }
}
