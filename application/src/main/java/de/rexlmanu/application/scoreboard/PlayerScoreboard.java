/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.application.scoreboard;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.bridge.CloudServer;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import de.rexlmanu.api.models.application.Application;
import de.rexlmanu.api.models.application.ApplicationState;
import de.rexlmanu.application.ApplicationPlugin;
import de.rexlmanu.application.player.ApplicationPlayer;
import de.rexlmanu.application.utility.fastboard.FastBoard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class PlayerScoreboard extends BukkitRunnable {

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

    private ApplicationPlugin plugin;
    private int animationTick;
    private int socialmediaTick;
    private Map<Player, FastBoard> fastBoardMap;

    public PlayerScoreboard(ApplicationPlugin plugin) {
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

        ApplicationPlayer applicationPlayer = ApplicationPlugin.getPlugin().getPlayerCache().get(player.getUniqueId());
        long millis = 0;
        if (applicationPlayer != null) millis = new Date().getTime() - applicationPlayer.getJoinDate();

        board.updateLines(Lists.newArrayList(Iterables.concat(Arrays.asList(
                "                 §r",
                "§7Status",
                "§8» §b" + getStatus(player),
                "",
                "§7Spielzeit",
                "§8» §b" + this.format(millis),
                ""
        ), this.socialmedia())));
    }

    private String format(long millis) {
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    private String getStatus(Player player) {
        ApplicationPlayer applicationPlayer = ApplicationPlugin.getPlugin().getPlayerCache().get(player.getUniqueId());
        Application application = applicationPlayer.getApplication();
        if (application.getState().equals(ApplicationState.BUILDING)) return "Bauphase";
        switch (application.getStatus()) {
            case DENIED:
                return "Abgelehnt";
            case PENDING:
                return "Bearbeitung";
            case ACCEPTED:
                return "Akzeptiert";
        }
        return "Error";
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
