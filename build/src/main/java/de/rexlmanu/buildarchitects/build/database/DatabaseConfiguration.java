/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.buildarchitects.build.database;

import de.rexlmanu.buildarchitects.build.BuildPlugin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

@Data
public class DatabaseConfiguration {

    public static DatabaseConfiguration load() {
        File file = new File(BuildPlugin.getPlugin().getDataFolder(), "database.json");
        if (file.exists())
            try {
                return BuildPlugin.GSON.fromJson(new FileReader(file), BuildPlugin.DATABASE_CONFIGURATION_TYPE);
            } catch (FileNotFoundException ignored) {
            }
        else {
            try {
                DatabaseConfiguration configuration = new DatabaseConfiguration();
                Files.write(file.toPath(), BuildPlugin.GSON.toJson(configuration).getBytes(), StandardOpenOption.CREATE);
                return configuration;
            } catch (IOException ignored) {
            }
        }
        return null;
    }

    private DatabaseConnection connection;

    public DatabaseConfiguration() {
        this.connection = new DatabaseConnection("root", "test", "test", "test", 3306);
    }
}
