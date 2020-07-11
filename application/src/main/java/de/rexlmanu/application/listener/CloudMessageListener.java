/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.application.listener;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.bridge.event.bukkit.BukkitSubChannelMessageEvent;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.rexlmanu.application.tasks.CreateWorldTask;
import de.rexlmanu.application.tasks.LoadWorldTask;
import de.rexlmanu.application.tasks.TaskRunner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class CloudMessageListener implements Listener {

    @EventHandler
    public void handle(BukkitSubChannelMessageEvent event) {
        if (!event.getChannel().equals("application")) return;
        Document document = event.getDocument();
        UUID uuid = UUID.fromString(document.getString("uuid"));
        String serverName = event.getCloudServer().getServerProcessMeta().getServiceId().getServerId();
        switch (event.getMessage()) {
            case "load":
                TaskRunner.runAsync(new LoadWorldTask(uuid, serverName));
                break;
            case "create":
                TaskRunner.runAsync(new CreateWorldTask(uuid, serverName));
                break;
        }
    }

}
