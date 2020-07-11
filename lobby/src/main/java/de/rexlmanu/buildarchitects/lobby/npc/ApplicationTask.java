/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.buildarchitects.lobby.npc;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.rexlmanu.api.handler.ApplicationHandler;
import de.rexlmanu.api.models.application.Application;
import de.rexlmanu.buildarchitects.lobby.LobbyPlugin;
import de.rexlmanu.buildarchitects.lobby.utility.title.ChatBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Data
public class ApplicationTask extends BukkitRunnable {

    public static Map<Player, ApplicationTask> taskMap = new HashMap<>();

    private Player player;
    private int creationTick, globalTick;
    private boolean over;
    private State state;
    private Application application;

    public ApplicationTask(Player player) {
        this.player = player;
        this.creationTick = 0;
        this.over = false;
        this.state = State.FETCHING_DATA;
        taskMap.put(player, this);

        Bukkit.getScheduler().runTaskAsynchronously(LobbyPlugin.getPlugin(), () -> {
            ApplicationHandler handler = LobbyPlugin.getPlugin().getRequest().getApplicationHandler();
            this.application = handler.get(player.getUniqueId());
            if (this.application == null) {
                this.application = handler.create(Application.create(player.getUniqueId(), player.getName()));
                this.state = State.CREATION;
                CloudAPI.getInstance().sendCustomSubServerMessage(
                        "application",
                        "create",
                        new Document().append("uuid", player.getUniqueId().toString())
                );
            } else {
                this.state = State.LOADING;
                CloudAPI.getInstance().sendCustomSubServerMessage(
                        "application",
                        "load",
                        new Document().append("uuid", player.getUniqueId().toString())
                );
            }
        });
    }

    @Override
    public void run() {
        switch (this.state) {
            case LOADING:
                switch (this.creationTick) {
                    case 0:
                        ChatBase.sendTitle(player, 0, 21, 0, "§bBuild§3Architects", "§7Welt wird geladen.");
                        break;
                    case 1:
                        ChatBase.sendTitle(player, 0, 21, 0, "§bBuild§3Architects", "§7Welt wird geladen..");
                        break;
                    case 2:
                        ChatBase.sendTitle(player, 0, 21, 0, "§bBuild§3Architects", "§7Welt wird geladen...");
                        this.creationTick = -1;
                        break;
                    default:
                        this.creationTick = -1;
                        break;
                }
                this.creationTick++;
                break;
            case FETCHING_DATA:
                ChatBase.sendTitle(player, 1, 21, 1, "§bBuild§3Architects", "§7Daten werden geladen");
                break;
            case CREATION:
                switch (this.creationTick) {
                    case 0:
                        ChatBase.sendTitle(player, 0, 21, 0, "§bBuild§3Architects", "§7Welt wird erstellt.");
                        break;
                    case 1:
                        ChatBase.sendTitle(player, 0, 21, 0, "§bBuild§3Architects", "§7Welt wird erstellt..");
                        break;
                    case 2:
                        ChatBase.sendTitle(player, 0, 21, 0, "§bBuild§3Architects", "§7Welt wird erstellt...");
                        this.creationTick = -1;
                        break;
                    default:
                        this.creationTick = -1;
                        break;
                }
                this.creationTick++;
                break;
            case LOADED:
                ChatBase.sendTitle(player, 1, 21, 1, "§bBuild§3Architects", "§7Die Welt wurde geladen.");
                this.over = true;
                this.player.playSound(player.getLocation(), Sound.LEVEL_UP, 1f, 1f);
                LobbyPlugin.getPlugin().sendToServer(player, LobbyPlugin.APPLICATION_SERVER_NAME);
                cancel();
                break;
            case CREATED:
                ChatBase.sendTitle(player, 1, 21, 1, "§bBuild§3Architects", "§7Die Welt wurde erstellt.");
                this.over = true;
                this.player.playSound(player.getLocation(), Sound.LEVEL_UP, 1f, 1f);
                LobbyPlugin.getPlugin().sendToServer(player, LobbyPlugin.APPLICATION_SERVER_NAME);
                cancel();
                break;
            case ERROR:
                ChatBase.sendTitle(player, 1, 21, 1, "§bBuild§3Architects", "§cEin Fehler ist aufgetreten.");
                this.over = true;
                this.player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 1f, 1f);
                cancel();
                break;
        }

        /*switch (globalTick) {
            case 20:
                this.creationTick = 5;
                ChatBase.sendTitle(player, 1, 21, 1, "§bBuild§3Architects", "§7Welt wurde erstellt.");
                break;
            case 21:
                ChatBase.sendTitle(player, 1, 21, 1, "§bBuild§3Architects", "§7Du wirst zur deiner Welt teleportiert.");
                this.player.playSound(player.getLocation(), Sound.FUSE, 1f, 1f);
                cancel();
                this.over = true;
                break;
            default:
                break;
        }*/

        this.globalTick++;
    }

    public enum State {

        FETCHING_DATA, CREATION, LOADING, CREATED, LOADED, ERROR

    }
}
