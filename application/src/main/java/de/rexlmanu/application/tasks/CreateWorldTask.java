/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.application.tasks;

import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.exceptions.WorldAlreadyExistsException;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.rexlmanu.api.models.application.Application;
import de.rexlmanu.application.ApplicationPlugin;
import de.rexlmanu.application.player.ApplicationPlayer;
import org.bukkit.*;

import java.io.IOException;
import java.util.UUID;

public class CreateWorldTask implements Task {

    private UUID uuid;
    private String serverName;
    private Application application;

    public CreateWorldTask(UUID uuid, String serverName) {
        this.uuid = uuid;
        this.serverName = serverName;
    }

    @Override
    public void run() {
        this.application = ApplicationPlugin.getPlugin().getRequest().getApplicationHandler().get(this.uuid);
        if (this.application == null) {
            CloudAPI.getInstance().sendCustomSubServerMessage("lobby", "error",
                    new Document().append("uuid", this.uuid.toString()).append("error", "Application not found"));
            return;
        }

        SlimePlugin slimePlugin = ApplicationPlugin.getPlugin().getSlimePlugin();
        SlimeLoader loader = slimePlugin.getLoader("mysql");
        SlimePropertyMap slimePropertyMap = new SlimePropertyMap();
        slimePropertyMap.setInt(SlimeProperties.SPAWN_X, 0);
        slimePropertyMap.setInt(SlimeProperties.SPAWN_Y, 64);
        slimePropertyMap.setInt(SlimeProperties.SPAWN_Z, 0);
        slimePropertyMap.setBoolean(SlimeProperties.ALLOW_ANIMALS, false);
        slimePropertyMap.setBoolean(SlimeProperties.ALLOW_MONSTERS, false);
        slimePropertyMap.setBoolean(SlimeProperties.PVP, false);
        slimePropertyMap.setString(SlimeProperties.WORLD_TYPE, "default");
        try {
            SlimeWorld world = slimePlugin.createEmptyWorld(loader, this.application.getWorldName(), false, slimePropertyMap);
            Bukkit.getScheduler().runTask(ApplicationPlugin.getPlugin(), () -> {
                slimePlugin.generateWorld(world);

                World bukkitWorld = Bukkit.getWorld(world.getName());
                for (int x = 0; x < 3; x++) {
                    for (int z = 0; z < 3; z++) {
                        Location location = new Location(bukkitWorld, x - 1, 63, z - 1);
                        location.getBlock().setType(Material.LAPIS_BLOCK);
                    }
                }

                WorldBorder worldBorder = bukkitWorld.getWorldBorder();
                worldBorder.setCenter(bukkitWorld.getSpawnLocation());
                worldBorder.setSize(100);

                ApplicationPlugin.getPlugin().getPlayerCache().add(new ApplicationPlayer(this.uuid, this.application, world));

                CloudAPI.getInstance().sendCustomSubServerMessage(
                        "lobby",
                        "created",
                        new Document().append("uuid", this.uuid.toString())
                );

            });
        } catch (WorldAlreadyExistsException | IOException e) {
            e.printStackTrace();
            CloudAPI.getInstance().sendCustomSubServerMessage("lobby", "error",
                    new Document().append("uuid", this.uuid.toString()).append("error", e.getMessage()));
        }
    }
}
