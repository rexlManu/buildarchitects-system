/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.application;

import com.grinderwolf.swm.api.SlimePlugin;
import de.rexlmanu.api.Request;
import de.rexlmanu.application.listener.ApplicationListener;
import de.rexlmanu.application.listener.CloudMessageListener;
import de.rexlmanu.application.player.ApplicationPlayer;
import de.rexlmanu.application.player.PlayerCache;
import de.rexlmanu.application.scoreboard.PlayerScoreboard;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class ApplicationPlugin extends JavaPlugin {

    public static final String PREFIX = "§8▎ §2Application §8» §7";
    public static final String PERMISSION_REQUIRED = PREFIX + "Dir fehlen die Rechte um diese Aktion auszuführen!";

    @Getter
    private static ApplicationPlugin plugin;

    private SlimePlugin slimePlugin;
    private Request request;
    private PlayerCache playerCache;
    private PlayerScoreboard playerScoreboard;

    @Override
    public void onEnable() {
        plugin = this;

        this.slimePlugin = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
        this.request = new Request("x391Rhm6XNIS8QhLtN3QPeWnxFQ2F1PY1gQHyQsBblcRqqMp2GuNx17tlY9BU");
        this.playerCache = new PlayerCache();
        this.playerScoreboard = new PlayerScoreboard(this);

        Bukkit.getPluginManager().registerEvents(new ApplicationListener(), this);
        Bukkit.getPluginManager().registerEvents(new CloudMessageListener(), this);
    }

    public ApplicationPlayer getPlayerByWorld(String worldName) {
        return this.playerCache.getPlayers().stream().filter(player -> player.getWorld().getName().equals(worldName)).findFirst().orElse(null);
    }
}
