/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.buildarchitects.lobby;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.rexlmanu.api.Request;
import de.rexlmanu.buildarchitects.lobby.events.CloudListener;
import de.rexlmanu.buildarchitects.lobby.events.LobbyListener;
import de.rexlmanu.buildarchitects.lobby.location.LocationCommand;
import de.rexlmanu.buildarchitects.lobby.location.LocationFile;
import de.rexlmanu.buildarchitects.lobby.npc.ApplicationNPC;
import de.rexlmanu.buildarchitects.lobby.npc.TeamManager;
import de.rexlmanu.buildarchitects.lobby.scoreboard.LobbyScoreboard;
import de.rexlmanu.buildarchitects.lobby.task.BootsTask;
import lombok.Getter;
import net.jitse.npclib.NPCLib;
import net.labymod.serverapi.bukkit.utils.PacketUtils;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@Getter
public class LobbyPlugin extends JavaPlugin {

    @Getter
    private static LobbyPlugin plugin;

    public static final String PREFIX = "§8▎ §2System §8» §7";
    public static final String PERMISSION_REQUIRED = PREFIX + "Dir fehlen die Rechte um diese Aktion auszuführen!",
            APPLICATION_SERVER_NAME = "Application-1";

    @Getter
    private LocationFile locationFile;
    private LobbyScoreboard scoreboard;
    private NPCLib npcLibrary;
    private PacketUtils packetUtils;
    private Request request;

    @Override
    public void onEnable() {
        plugin = this;
        this.locationFile = new LocationFile(new File(getDataFolder(), "location.yml"));
        this.scoreboard = new LobbyScoreboard(this);
        this.npcLibrary = new NPCLib(this);
        this.packetUtils = new PacketUtils();
        this.request = new Request("x391Rhm6XNIS8QhLtN3QPeWnxFQ2F1PY1gQHyQsBblcRqqMp2GuNx17tlY9BU");

        Bukkit.getPluginManager().registerEvents(new LobbyListener(this.locationFile, this), this);
        Bukkit.getPluginManager().registerEvents(new CloudListener(), this);
        this.getCommand("buildlobby").setExecutor(new LocationCommand(this.locationFile));

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            Bukkit.getWorlds().forEach(world -> {
                world.setTime(1000);
                world.setDifficulty(Difficulty.PEACEFUL);
                world.setThundering(false);
            });
        }, 1, 1);


        new BootsTask().runTaskTimer(this, 0, 5);
        if (this.locationFile.getConfiguration().get("vorbauen") != null) {
            new ApplicationNPC().runTaskTimerAsynchronously(plugin, 0, 5);
        }

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "DAMAGEINDICATOR");
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    public void sendToServer(Player player, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
    }
}
