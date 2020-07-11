/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.application.listener;

import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.bridge.CloudServer;
import de.dytanic.cloudnet.bridge.event.bukkit.BukkitPlayerUpdateEvent;
import de.dytanic.cloudnet.bridge.internal.util.ItemStackBuilder;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import de.rexlmanu.api.models.application.Application;
import de.rexlmanu.api.models.application.ApplicationState;
import de.rexlmanu.application.ApplicationPlugin;
import de.rexlmanu.application.inventory.ApplicationInventory;
import de.rexlmanu.application.player.ApplicationPlayer;
import de.rexlmanu.application.player.PlayerCache;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.PortalCreateEvent;

import java.util.Date;

public class ApplicationListener implements Listener {

    @EventHandler
    public void handle(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        Player player = event.getPlayer();
        ApplicationPlayer applicationPlayer = ApplicationPlugin.getPlugin().getPlayerCache().get(player.getUniqueId());
        if (applicationPlayer == null) {
            player.kickPlayer("");
            return;
        }
        SlimeWorld world = applicationPlayer.getWorld();
        World bukkitWorld = Bukkit.getWorld(world.getName());
        player.teleport(bukkitWorld.getSpawnLocation().clone().add(0.5, 0.2, 0.5));

        applicationPlayer.setJoinDate(new Date().getTime());

        player.setGameMode(GameMode.CREATIVE);
        player.getInventory().clear();
        player.getInventory().setItem(0, ItemStackBuilder.builder(Material.NETHER_STAR).displayName("§8» §bNavigator").build());

        Bukkit.getScheduler().runTaskLaterAsynchronously(ApplicationPlugin.getPlugin(), () -> CloudServer.getInstance().updateNameTags(player), 3L);
        Bukkit.getOnlinePlayers().forEach(o -> {
            if (player.equals(o)) return;
            player.hidePlayer(o);
            o.hidePlayer(player);
        });
    }

    @EventHandler
    public void handle(PlayerInteractEvent event) {
        event.setCancelled(this.cancelIfFinished(event.getPlayer().getLocation().getWorld()));
        if (event.getItem() == null) return;
        if (!event.getItem().hasItemMeta()) return;
        if (!event.getItem().getItemMeta().hasDisplayName()) return;
        if (!event.getAction().equals(Action.RIGHT_CLICK_AIR) && !event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            return;
        if (event.getItem().getItemMeta().getDisplayName().equals("§8» §bNavigator")) {
            ApplicationInventory applicationInventory = new ApplicationInventory(event.getPlayer());
            applicationInventory.open();
        }
    }

    @EventHandler
    public void handle(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Player player = event.getPlayer();
        ApplicationPlayer applicationPlayer = ApplicationPlugin.getPlugin().getPlayerCache().get(player.getUniqueId());
        if (applicationPlayer == null) {
            player.sendMessage("Deine Welt konnte nicht gespeichert werden.");
            return;
        }

        SlimeWorld world = applicationPlayer.getWorld();
        Application application = applicationPlayer.getApplication();
        ApplicationPlugin.getPlugin().getRequest().getApplicationHandler().update(application);
        World bukkitWorld = Bukkit.getWorld(world.getName());
        bukkitWorld.getPlayers().forEach(player1 -> player1.teleport(Bukkit.getWorlds().get(0).getSpawnLocation()));
        if (!Bukkit.unloadWorld(bukkitWorld, true)) {
            System.out.println("Failed unloaded world.");
        }
        ApplicationPlugin.getPlugin().getPlayerCache().remove(applicationPlayer);
    }

    @EventHandler
    public void handle(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void handle(BlockFromToEvent event) {
        event.setCancelled(this.cancelIfBlockPhysics(event.getBlock().getWorld()));
    }

    private boolean cancelIfBlockPhysics(World world) {
        ApplicationPlayer player = ApplicationPlugin.getPlugin().getPlayerByWorld(world.getName());
        if (player == null) return true;
        return !player.isBlockPhysics();
    }

    private boolean cancelIfFinished(World world) {
        ApplicationPlayer player = ApplicationPlugin.getPlugin().getPlayerByWorld(world.getName());
        if (player == null) return true;
        return player.getApplication().getState().equals(ApplicationState.FINISHED);
    }

    @EventHandler
    public void handle(EntityExplodeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void handle(BlockPhysicsEvent event) {
        event.setCancelled(this.cancelIfBlockPhysics(event.getBlock().getWorld()));
    }

    @EventHandler
    public void handle(LeavesDecayEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void handle(BlockSpreadEvent event) {
        event.setCancelled(this.cancelIfBlockPhysics(event.getBlock().getWorld()));
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
    public void handle(ProjectileLaunchEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void handle(PortalCreateEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void handle(FireworkExplodeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void handle(PotionSplashEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void handle(SpawnerSpawnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void handle(ItemSpawnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void handle(BlockDispenseEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void handle(PlayerFishEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void handle(PlayerEditBookEvent event) {
        event.setCancelled(true);
    }
}
