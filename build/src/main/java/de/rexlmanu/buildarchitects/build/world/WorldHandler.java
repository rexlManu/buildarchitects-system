/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.buildarchitects.build.world;

import de.rexlmanu.buildarchitects.build.BuildPlugin;
import de.rexlmanu.buildarchitects.build.database.DatabaseHandler;
import de.rexlmanu.buildarchitects.build.database.DatabaseManager;
import de.rexlmanu.buildarchitects.build.database.PreparedStatementBuilder;
import de.rexlmanu.buildarchitects.build.utility.StringList;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WorldHandler {

    public static CompletableFuture<List<World>> getWorlds() {
        CompletableFuture<List<World>> future = new CompletableFuture<>();
        databaseManager().execute("SELECT * FROM buildworlds", (resultSet, throwable) -> {
            List<World> worlds = new ArrayList<>();
            try {
                while (resultSet.next()) {
                    worlds.add(new World(
                            resultSet.getString("name"),
                            WorldState.valueOf(resultSet.getString("state")),
                            WorldType.valueOf(resultSet.getString("type")),
                            StringList.toList(resultSet.getString("builders"))
                    ));
                }
                future.complete(worlds);
            } catch (SQLException e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    public static void saveWorld(World world) {
        databaseManager().update(PreparedStatementBuilder.create("UPDATE buildworlds SET state = ?, type = ?, builders = ? WHERE name =?;")
                .bindString(world.getState().name()).bindString(world.getType().name())
                .bindString(StringList.toString(world.getBuilders())).bindString(world.getName()).build());
    }

    public static void createWorld(World world) {
        databaseManager().update(PreparedStatementBuilder.create("INSERT INTO buildworlds VALUES (?,?,?,?)")
                .bindString(world.getName()).bindString(world.getState().name()).bindString(world.getType().name()).bindString(StringList.toString(world.getBuilders())).build());
    }

    public static void deleteWorld(World world) {
        databaseManager().update(PreparedStatementBuilder.create("DELETE FROM buildworlds WHERE name = ?;").bindString(world.getName()).build());
    }

    private static DatabaseManager databaseManager() {
        return BuildPlugin.getPlugin().getDatabaseManager();
    }
}
