/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.buildarchitects.lobby.scoreboard;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.bridge.CloudServer;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import de.rexlmanu.buildarchitects.lobby.LobbyPlugin;
import de.rexlmanu.buildarchitects.lobby.utility.fastboard.FastBoard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class LobbyScoreboard extends BukkitRunnable {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private String[] animation = new String[]
            {
                    "§8▎ §8▎",
                    "§8▎ §bB §8▎",
                    "§8▎ §bBu §8▎",
                    "§8▎ §bBui §8▎",
                    "§8▎ §bBuil §8▎",
                    "§8▎ §bBuild §8▎",
                    "§8▎ §bBuild§3A §8▎",
                    "§8▎ §bBuild§3Ar §8▎",
                    "§8▎ §bBuild§3Arc §8▎",
                    "§8▎ §bBuild§3Arch §8▎",
                    "§8▎ §bBuild§3Archi §8▎",
                    "§8▎ §bBuild§3Archit §8▎",
                    "§8▎ §bBuild§3Archite §8▎",
                    "§8▎ §bBuild§3Architec §8▎",
                    "§8▎ §bBuild§3Architect §8▎",
                    "§8▎ §bBuild§3Architects §8▎",
                    "§8▎ §bBuild§3Architects §8▎",
                    "§8▎ §bBuild§3Architects §8▎",
                    "§8▎ §bBuild§3Architect §8▎",
                    "§8▎ §bBuild§3Architec §8▎",
                    "§8▎ §bBuild§3Archite §8▎",
                    "§8▎ §bBuild§3Archit §8▎",
                    "§8▎ §bBuild§3Archi §8▎",
                    "§8▎ §bBuild§3Arch §8▎",
                    "§8▎ §bBuild§3Arc §8▎",
                    "§8▎ §bBuild§3Ar §8▎",
                    "§8▎ §bBuild§3A §8▎",
                    "§8▎ §bBuild §8▎",
                    "§8▎ §bBuil §8▎",
                    "§8▎ §bBui §8▎",
                    "§8▎ §bBu §8▎",
                    "§8▎ §bB §8▎",
                    "§8▎ §8▎",
            };

    private LobbyPlugin plugin;
    private int animationTick;
    private int socialmediaTick;
    private Map<Player, FastBoard> fastBoardMap;

    public LobbyScoreboard(LobbyPlugin plugin) {
        this.plugin = plugin;
        this.animationTick = 0;
        this.socialmediaTick = 0;
        this.fastBoardMap = new HashMap<>();
        this.runTaskTimerAsynchronously(plugin, 1, 7);

        Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, () -> {
            if (this.socialmediaTick >= 2) socialmediaTick = 0;
            this.socialmediaTick++;
        }, 1, 20 * 3);
    }

    public void create(Player player) {
        this.fastBoardMap.put(player, new FastBoard(player));
    }

    public List<String> socialmedia() {
        switch (this.socialmediaTick) {
            case 0:
                return Arrays.asList("§7TeamSpeak", "§8» §bbuild§3architects.team", "");
            case 1:
                return Arrays.asList("§7Discord", "§8» §bCJQEPUv", "");
            case 2:
                return Arrays.asList("§7Twitter", "§8» §b@Build§3ArchiTeam", "");
        }
        return new ArrayList<>();
    }

    public void update(Player player) {
        FastBoard board = this.fastBoardMap.get(player);
        final PermissionGroup permissionGroup = CloudServer.getInstance().getCachedPlayer(player.getUniqueId()).getPermissionEntity().getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool());
        String color = "§a" + ChatColor.translateAlternateColorCodes('&', permissionGroup.getColor());
        board.updateTitle(animation[animationTick]);

        board.updateLines(Lists.newArrayList(Iterables.concat(Arrays.asList(
                "                 §r",
                "§7Willkommen",
                "§8» " + color + player.getName(),
                "",
                "§7Uhrzeit",
                "§8» §b" + FORMATTER.format(LocalDateTime.now(ZoneId.of("Europe/Paris"))),
                ""
        ), this.socialmedia())));
    }

    @Override
    public void run() {
        if (animationTick >= animation.length) animationTick = 0;

        Bukkit.getOnlinePlayers().forEach(player -> {
            if (!this.fastBoardMap.containsKey(player)) this.create(player);
            this.update(player);
        });

        this.animationTick++;
    }

}
