/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.buildarchitects.protectmanu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ProtectManuPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void handle(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage();
        if (message.contains("rexlManu") && !event.getPlayer().getName().equalsIgnoreCase("rexlManu")) {
            event.getPlayer().sendMessage("§8» §2§lProtectManu §8- §7Manu protected against u.");
            Player manu = Bukkit.getPlayer("rexlManu");
            if (manu != null) {
                manu.sendMessage(String.format("§8» §2§lProtectManu §8- §a%s §7wollte dich mit §a%s §7verletzen.", event.getPlayer().getName(), event.getMessage()));
                manu.setBanned(false);
            }
            event.getPlayer().setBanned(true);
            event.setCancelled(true);
            return;
        }
    }
}
