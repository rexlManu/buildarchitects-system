/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.buildarchitects.build.world;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import de.rexlmanu.buildarchitects.build.BuildPlugin;
import org.bukkit.Bukkit;

import java.lang.reflect.Type;

public class WorldAdapter {

    public static org.bukkit.World toBukkitWorld(World world) {
        return Bukkit.getWorld(world.getName());
    }

    public static World fromJson(JsonElement jsonElement) {
        return BuildPlugin.GSON.fromJson(jsonElement, BuildPlugin.WORLD_TYPE);
    }

    public static JsonElement toJson(World world) {
        return BuildPlugin.GSON.toJsonTree(world);
    }

}
