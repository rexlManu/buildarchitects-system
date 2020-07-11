/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.buildarchitects.lobby.location;

import de.rexlmanu.buildarchitects.lobby.LobbyPlugin;
import de.rexlmanu.buildarchitects.lobby.npc.TeamManager;
import de.rexlmanu.buildarchitects.lobby.npc.TeamPlayer;
import de.rexlmanu.buildarchitects.lobby.utility.MineToolsFetcher;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.jitse.npclib.api.state.NPCState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class LocationCommand implements CommandExecutor {

    private LocationFile locationFile;

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] arguments) {
        if (!commandSender.hasPermission("buldarchitects.lobby")) {
            commandSender.sendMessage(LobbyPlugin.PERMISSION_REQUIRED);
            return true;
        }
        Player player = (Player) commandSender;
        switch (arguments.length) {
            case 0:
                commandSender.sendMessage(LobbyPlugin.PREFIX + "§7Hilfeübersicht");
                commandSender.sendMessage("§8» §7/buildlobby set <Name>");
                commandSender.sendMessage("§8» §7/buildlobby createnpc <UUID> <EmoteId>");
                commandSender.sendMessage("§8» §7/buildlobby createnpc <UUID> <EmoteId> <State>");
                break;
            case 2:
                switch (arguments[0].toLowerCase()) {
                    case "set":
                        this.locationFile.getConfiguration().set(arguments[1].toLowerCase(), player.getLocation());
                        this.locationFile.save();
                        player.sendMessage(LobbyPlugin.PREFIX + String.format("§7Du hast erfolgreich §a%s §7gesetzt.", arguments[1]));
                        break;
                }
                break;
            case 3:
                switch (arguments[0].toLowerCase()) {
                    /*case "createnpc":
                        MineToolsFetcher.fetchSkinFromUuidAsync(UUID.fromString(arguments[1]), skin -> {
                            TeamPlayer teamPlayer = new TeamPlayer(UUID.fromString(arguments[1]), Integer.parseInt(arguments[2]),
                                    player.getLocation(), skin, false, NPCState.CROUCHED);
                            locationFile.getConfiguration().set(UUID.fromString(arguments[1]).toString(), player.getLocation());
                            locationFile.save();
                            teamManager.addPlayer(teamPlayer);
                            player.sendMessage(LobbyPlugin.PREFIX + "§7Der NPC wurde erfolgreich erstellt und gespeichert.");
                        });
                        break;*/
                }
                break;
            case 4:
                switch (arguments[0].toLowerCase()) {
                    /*case "createnpc":
                        MineToolsFetcher.fetchSkinFromUuidAsync(UUID.fromString(arguments[1]), skin -> {
                            TeamPlayer teamPlayer = new TeamPlayer(UUID.fromString(arguments[1]), Integer.parseInt(arguments[2]),
                                    player.getLocation(), skin, true, NPCState.valueOf(arguments[3]));
                            locationFile.getConfiguration().set(UUID.fromString(arguments[1]).toString(), player.getLocation());
                            locationFile.save();
                            teamManager.addPlayer(teamPlayer);
                            player.sendMessage(LobbyPlugin.PREFIX + "§7Der NPC wurde erfolgreich erstellt und gespeichert.");
                        });
                        break;*/
                }
                break;
        }

        return false;
    }
}
