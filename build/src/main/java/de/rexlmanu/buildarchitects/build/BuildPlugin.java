/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.buildarchitects.build;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.grinderwolf.swm.api.SlimePlugin;
import de.rexlmanu.buildarchitects.build.command.WorldCommand;
import de.rexlmanu.buildarchitects.build.database.DatabaseConfiguration;
import de.rexlmanu.buildarchitects.build.database.DatabaseConnection;
import de.rexlmanu.buildarchitects.build.database.DatabaseManager;
import de.rexlmanu.buildarchitects.build.world.World;
import de.rexlmanu.buildarchitects.build.world.WorldHandler;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Type;

@Getter
public class BuildPlugin extends JavaPlugin {

    public static final String PREFIX = "§8▎ §2System §8» §7";
    public static final String PERMISSION_REQUIRED = PREFIX + "Dir fehlen die Rechte um diese Aktion auszuführen!";

    public static final JsonParser JSON_PARSER = new JsonParser();
    public static final Type WORLD_TYPE = new TypeToken<World>() {
    }.getType();
    public static final Type DATABASE_CONFIGURATION_TYPE = new TypeToken<DatabaseConfiguration>() {
    }.getType();
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    @Getter
    private static BuildPlugin plugin;
    private WorldHandler worldHandler;
    private DatabaseConfiguration databaseConfiguration;
    private DatabaseManager databaseManager;
    private SlimePlugin slimePlugin;

    @Override
    public void onEnable() {
        plugin = this;
        this.getDataFolder().mkdir();

        this.worldHandler = new WorldHandler();
        this.databaseConfiguration = DatabaseConfiguration.load();
        this.databaseManager = new DatabaseManager(this.databaseConfiguration.getConnection());
        this.slimePlugin = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");

        PluginCommand pluginCommand = this.getCommand("worlds");
        WorldCommand worldCommand = new WorldCommand();
        pluginCommand.setExecutor(worldCommand);
        pluginCommand.setTabCompleter(worldCommand);
    }
}
