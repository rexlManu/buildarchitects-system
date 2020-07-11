/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.buildarchitects.lobby.events;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.bridge.CloudServer;
import de.dytanic.cloudnet.bridge.event.bukkit.BukkitPlayerUpdateEvent;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import de.rexlmanu.buildarchitects.lobby.LobbyPlugin;
import de.rexlmanu.buildarchitects.lobby.location.LocationFile;
import de.rexlmanu.buildarchitects.lobby.npc.ApplicationTask;
import lombok.AllArgsConstructor;
import net.labymod.serverapi.Permission;
import net.labymod.serverapi.bukkit.LabyModPlugin;
import net.labymod.serverapi.bukkit.event.PermissionsSendEvent;
import net.labymod.serverapi.bukkit.utils.PacketUtils;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Calendar;

@AllArgsConstructor
public class LobbyListener implements Listener {

    private LocationFile locationFile;
    private LobbyPlugin plugin;

    @EventHandler
    public void handle(EntityDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void handle(PlayerInteractEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void handle(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void handle(BlockFromToEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void handle(EntityExplodeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void handle(BlockPhysicsEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void handle(LeavesDecayEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void handle(BlockSpreadEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void handle(BlockBurnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void handle(BlockGrowEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void handle(BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void handle(BlockPlaceEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void handle(PlayerPickupItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void handle(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void handle(PlayerAchievementAwardedEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void handle(BlockRedstoneEvent event) {
        event.setNewCurrent(0);
    }

    @EventHandler
    public void handle(EntitySpawnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void handle(PlayerArmorStandManipulateEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void handle(PlayerJoinEvent event) {
        event.setJoinMessage(null);

        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();
        inventory.clear();

        player.setGameMode(GameMode.ADVENTURE);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1f, 1f);
        player.setHealthScale(1);
        player.setHealth(player.getMaxHealth());
        player.setLevel(Calendar.getInstance().getWeekYear());
        player.setExp(0);
        player.setFoodLevel(20);

        if (this.locationFile.getConfiguration().get("lobby") != null) {
            player.teleport((Location) this.locationFile.getConfiguration().get("lobby"));
        }

        ItemStack itemStack = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta itemMeta = (LeatherArmorMeta) itemStack.getItemMeta();
        itemMeta.setColor(Color.fromRGB(17, 237, 28));
        itemMeta.setDisplayName("§8» §b@§lBuild§3§lArchiTeam");
        itemStack.setItemMeta(itemMeta);
        inventory.setBoots(itemStack);
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> CloudServer.getInstance().updateNameTags(player), 3L);
        PacketUtils packetUtils = LobbyPlugin.getPlugin().getPacketUtils();
        byte[] bytes = {0};
        packetUtils.sendPacket(player, packetUtils.getPluginMessagePacket("DAMAGEINDICATOR", bytes));

    }

    @EventHandler
    public void handle(PermissionsSendEvent event) {
        Player player = event.getPlayer();
    }

    @EventHandler
    public void handle(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        if (ApplicationTask.taskMap.containsKey(event.getPlayer())) {
            ApplicationTask.taskMap.get(event.getPlayer()).cancel();
            ApplicationTask.taskMap.remove(event.getPlayer());
        }
    }

    @EventHandler
    public void handle(final AsyncPlayerChatEvent e) {
        final PermissionGroup permissionGroup = CloudServer.getInstance().getCachedPlayer(e.getPlayer().getUniqueId()).getPermissionEntity().getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool());
        if (permissionGroup == null) {
            return;
        }
        e.setFormat(ChatColor.translateAlternateColorCodes('&', "%display%%player% §8» §7%message%".replace("%display%", ChatColor.translateAlternateColorCodes('&', permissionGroup.getDisplay())).replace("%prefix%", ChatColor.translateAlternateColorCodes('&', permissionGroup.getPrefix())).replace("%suffix%", ChatColor.translateAlternateColorCodes('&', permissionGroup.getSuffix())).replace("%group%", permissionGroup.getName()).replace("%player%", e.getPlayer().getName()).replace("%message%", e.getPlayer().hasPermission("cloudnet.chat.color") ? ChatColor.translateAlternateColorCodes('&', e.getMessage().replace("%", "%%")) : ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', e.getMessage().replace("%", "%%"))))));
    }


    @EventHandler
    public void handleUpdate(final BukkitPlayerUpdateEvent e) {
        if (Bukkit.getPlayer(e.getCloudPlayer().getUniqueId()) != null && e.getCloudPlayer().getServer() != null && e.getCloudPlayer().getServer().equalsIgnoreCase(CloudAPI.getInstance().getServerId())) {
            CloudServer.getInstance().updateNameTags(Bukkit.getPlayer(e.getCloudPlayer().getUniqueId()));
        }
    }


    @EventHandler
    public void handle(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void handle(InventoryClickEvent event) {
        event.setCancelled(true);
    }

}
