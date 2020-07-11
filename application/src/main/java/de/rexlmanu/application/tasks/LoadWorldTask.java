/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.application.tasks;

import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.exceptions.CorruptedWorldException;
import com.grinderwolf.swm.api.exceptions.NewerFormatException;
import com.grinderwolf.swm.api.exceptions.UnknownWorldException;
import com.grinderwolf.swm.api.exceptions.WorldInUseException;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.rexlmanu.api.models.application.Application;
import de.rexlmanu.application.ApplicationPlugin;
import de.rexlmanu.application.player.ApplicationPlayer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;

import java.io.IOException;
import java.util.UUID;

public class LoadWorldTask implements Task {

    private UUID uuid;
    private String serverName;
    private Application application;

    public LoadWorldTask(UUID uuid, String serverName) {
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
            SlimeWorld slimeWorld = slimePlugin.loadWorld(loader, this.application.getWorldName(), false, slimePropertyMap);
            Bukkit.getScheduler().runTask(ApplicationPlugin.getPlugin(), () -> {
                slimePlugin.generateWorld(slimeWorld);

                ApplicationPlugin.getPlugin().getPlayerCache().add(new ApplicationPlayer(this.uuid, this.application, slimeWorld));

                World bukkitWorld = Bukkit.getWorld(slimeWorld.getName());
                WorldBorder worldBorder = bukkitWorld.getWorldBorder();
                worldBorder.setCenter(bukkitWorld.getSpawnLocation());
                worldBorder.setSize(100);

                CloudAPI.getInstance().sendCustomSubServerMessage(
                        "lobby",
                        "loaded",
                        new Document().append("uuid", this.uuid.toString())
                );
            });
        } catch (UnknownWorldException | WorldInUseException | IOException | NewerFormatException | CorruptedWorldException e) {
            CloudAPI.getInstance().sendCustomSubServerMessage("lobby", "error",
                    new Document().append("uuid", this.uuid.toString()).append("error", e.getMessage()));
        }
    }
}
