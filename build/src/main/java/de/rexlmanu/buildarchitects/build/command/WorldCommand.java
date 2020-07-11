/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.buildarchitects.build.command;

import de.rexlmanu.buildarchitects.build.BuildPlugin;
import de.rexlmanu.buildarchitects.build.inventory.WorldInventory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public class WorldCommand implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!commandSender.hasPermission("buildarchitects.worlds.menu")) {
            commandSender.sendMessage(BuildPlugin.PERMISSION_REQUIRED);
            return true;
        }
        WorldInventory worldInventory = new WorldInventory((Player) commandSender);
        worldInventory.open();
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
