/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.buildarchitects.lobby.utility;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.jitse.npclib.api.skin.MineSkinFetcher;
import net.jitse.npclib.api.skin.Skin;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class MineToolsFetcher {

    private static final String MINESKIN_API = "https://api.minetools.eu/profile/";
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    public static void fetchSkinFromUuidAsync(UUID uuid, Consumer<Skin> skinConsumer) {
        EXECUTOR.execute(() -> {
            try {
                StringBuilder builder = new StringBuilder();
                HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(MINESKIN_API + uuid.toString().replace("-", "")).openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                Scanner scanner = new Scanner(httpURLConnection.getInputStream());
                while (scanner.hasNextLine()) {
                    builder.append(scanner.nextLine());
                }

                scanner.close();
                httpURLConnection.disconnect();

                JsonObject jsonObject = new JsonParser().parse(builder.toString()).getAsJsonObject();
                JsonObject raw = jsonObject.getAsJsonObject("raw");
                JsonObject textures = raw.getAsJsonArray("properties").get(0).getAsJsonObject();
                Skin skin = new Skin(textures.get("value").getAsString(), textures.get("signature").getAsString());
                skinConsumer.accept(skin);
            } catch (IOException exception) {
                Bukkit.getLogger().severe("Could not fetch skin! (Id: " + uuid.toString() + "). Message: " + exception.getMessage());
                exception.printStackTrace();
            }
        });
    }

}
