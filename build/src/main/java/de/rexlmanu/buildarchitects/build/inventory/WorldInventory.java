/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.buildarchitects.build.inventory;

import de.dytanic.cloudnet.bridge.internal.util.ItemStackBuilder;
import de.rexlmanu.buildarchitects.build.BuildPlugin;
import de.rexlmanu.buildarchitects.build.world.World;
import de.rexlmanu.buildarchitects.build.world.WorldHandler;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class WorldInventory implements Listener {

    private static final int LINE_FIRST = 0;
    private static final int LINE_SECOND = 9;
    private static final int LINE_THIRD = 2 * 9;
    private static final int LINE_FOURTH = 3 * 9;
    private static final int LINE_FIFTH = 4 * 9;
    private static final int LINE_SIXTH = 5 * 9;

    private static final int SLOT_FIRST = 0;
    private static final int SLOT_SECOND = 1;
    private static final int SLOT_THIRD = 2;
    private static final int SLOT_FOURTH = 3;
    private static final int SLOT_FIFTH = 4;
    private static final int SLOT_SIXTH = 5;
    private static final int SLOT_SEVENTH = 6;
    private static final int SLOT_EIGHTH = 7;
    private static final int SLOT_NINTH = 8;

    private Player player;
    private Category category;
    private Inventory inventory;
    private boolean greenSwitch;
    private List<World> worlds;

    public WorldInventory(Player player) {
        this.player = player;
        this.category = Category.SELECTION;
        this.inventory = Bukkit.createInventory(null, 6 * 9, "§8» §2§lWORLDS");
        this.greenSwitch = false;

        Bukkit.getPluginManager().registerEvents(this, BuildPlugin.getPlugin());
    }

    @EventHandler
    public void handle(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (!event.getClickedInventory().equals(this.inventory)) return;
        event.setCancelled(true);

        String displayName = event.getCurrentItem().getItemMeta().getDisplayName();

        switch (this.category) {
            case SELECTION:
                switch (displayName) {
                    case "§8» §aWelten":
                        this.category = Category.WORLD_LIST;
                        WorldHandler.getWorlds().whenComplete((loadedWorlds, throwable) -> {
                            if (throwable != null) {
                                player.sendMessage(BuildPlugin.PREFIX + "§7Es ist ein Fehler augetreten: §c" + throwable.getMessage());
                                return;
                            }
                            this.worlds = loadedWorlds;
                            this.openCategory();
                        });
                        break;
                    case "§8» §aTemplates":
                        this.category = Category.TEMPLATE_LIST;
                        WorldHandler.getWorlds().whenComplete((loadedWorlds, throwable) -> {
                            if (throwable != null) {
                                player.sendMessage(BuildPlugin.PREFIX + "§7Es ist ein Fehler augetreten: §c" + throwable.getMessage());
                                return;
                            }
                            this.worlds = loadedWorlds;
                            this.openCategory();
                        });
                        break;
                    case "§8» §aEinstellungen":
                        this.category = Category.SETTINGS;
                        this.openCategory();
                        break;
                }
            case WORLD_LIST:
                break;
        }
    }

    public void open() {
        this.designSides();
        this.inventory.setItem(SLOT_THIRD * LINE_SECOND, ItemStackBuilder.builder(Material.CHEST).displayName("§8» §aWelten").build());
        this.inventory.setItem(SLOT_FIFTH * LINE_SECOND, ItemStackBuilder.builder(Material.BOOKSHELF).displayName("§8» §aTemplates").build());
        this.inventory.setItem(SLOT_SEVENTH * LINE_SECOND, ItemStackBuilder.builder(Material.WORKBENCH).displayName("§8» §aEinstellungen").build());
        this.player.openInventory(this.inventory);
    }

    private void openCategory() {
        this.designSides();
        switch (this.category) {
            case WORLD_LIST:
                this.inventory.setItem(SLOT_FIFTH * LINE_FIRST, ItemStackBuilder.builder(Material.WOOD).displayName("§8» §aWelt erstellen").build());
                for (int i = 0; i < this.worlds.size(); i++) {
                    this.inventory.setItem(2 + i + (i % 5 == 0 ? 9 : 0), ItemStackBuilder.builder(Material.WORKBENCH).displayName("§8» §a" + this.worlds.get(i)).build());
                }
                break;
        }
    }

    @EventHandler
    public void handle(InventoryCloseEvent event) {
        HandlerList.unregisterAll(this);
    }

    public enum Category {
        SELECTION, WORLD_LIST, WORLD_CREATE, WORLD_SETTINGS, TEMPLATE_LIST, SETTINGS
    }

    public void designSides() {
        ItemStack limeGlass = ItemStackBuilder.builder(Material.STAINED_GLASS_PANE, 1, 5).displayName("§r").build();
        ItemStack greenGlass = ItemStackBuilder.builder(Material.STAINED_GLASS_PANE, 1, 13).displayName("§r").build();
        ItemStack blackGlass = ItemStackBuilder.builder(Material.STAINED_GLASS_PANE, 1, 15).displayName("§r").build();
        for (int i = 0; i < this.inventory.getSize(); i++)
            this.inventory.setItem(i, blackGlass);

        for (int i = 0; i < (this.inventory.getSize() / 9); i++) {
            if (greenSwitch) {
                this.inventory.setItem(i * 9, limeGlass);
                this.inventory.setItem(8 + (i * 9), greenGlass);
            } else {
                this.inventory.setItem(i * 9, greenGlass);
                this.inventory.setItem(8 + (i * 9), limeGlass);
            }
            this.greenSwitch = !this.greenSwitch;
        }
    }
}
