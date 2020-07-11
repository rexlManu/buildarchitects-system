/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.buildarchitects.lobby.events;

import de.dytanic.cloudnet.bridge.event.bukkit.BukkitCustomChannelMessageReceiveEvent;
import de.dytanic.cloudnet.bridge.event.bukkit.BukkitSubChannelMessageEvent;
import de.rexlmanu.buildarchitects.lobby.LobbyPlugin;
import de.rexlmanu.buildarchitects.lobby.npc.ApplicationTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class CloudListener implements Listener {

    @EventHandler
    public void handle(BukkitSubChannelMessageEvent event) {
        if (!event.getChannel().equals("lobby")) return;
        Player player = Bukkit.getPlayer(UUID.fromString(event.getDocument().getString("uuid")));
        ApplicationTask task = ApplicationTask.taskMap.get(player);
        switch (event.getMessage()) {
            case "created":
                task.setState(ApplicationTask.State.CREATED);
                break;
            case "loaded":
                task.setState(ApplicationTask.State.LOADED);
                break;
            case "error":
                task.setState(ApplicationTask.State.ERROR);
                System.out.println(event.getDocument().getString("error"));
                break;
        }
    }

}
