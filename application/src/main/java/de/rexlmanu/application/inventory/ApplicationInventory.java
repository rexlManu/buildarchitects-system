/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.application.inventory;

import de.dytanic.cloudnet.bridge.internal.util.ItemStackBuilder;
import de.rexlmanu.api.models.application.Application;
import de.rexlmanu.api.models.application.ApplicationState;
import de.rexlmanu.application.ApplicationPlugin;
import de.rexlmanu.application.player.ApplicationPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ApplicationInventory implements Listener {

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
    private ApplicationPlayer applicationPlayer;

    public ApplicationInventory(Player player) {
        this.player = player;
        this.category = Category.SELECTION;
        this.inventory = Bukkit.createInventory(null, 3 * 9, "§8» §b§lVerwaltung");
        this.greenSwitch = false;
        this.applicationPlayer = ApplicationPlugin.getPlugin().getPlayerCache().get(player.getUniqueId());

        Bukkit.getPluginManager().registerEvents(this, ApplicationPlugin.getPlugin());
    }

    @EventHandler
    public void handle(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (!event.getClickedInventory().equals(this.inventory)) return;
        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();
        String displayName = item.getItemMeta().getDisplayName();

        switch (this.category) {
            case SELECTION:
                switch (displayName) {
                    case "§8» §aBlockPhysics":
                        if (this.applicationPlayer.getApplication().getState().equals(ApplicationState.FINISHED)) break;
                        this.applicationPlayer.setBlockPhysics(!this.applicationPlayer.isBlockPhysics());
                        if (this.applicationPlayer.isBlockPhysics()) this.addGlow(item);
                        else this.removeGlow(item);
                        this.inventory.setItem(event.getSlot(), item);
                        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1f, 1f);
                        break;
                    case "§8» §bAbgeben":
                        this.category = Category.FINISH;
                        this.openCategory();
                        break;
                }
            case FINISH:
                switch (displayName) {
                    case "§8» §cAbbrechen":
                        this.category = Category.SELECTION;
                        this.openCategory();
                        break;
                    case "§8» §aAbgeben":
                        if (this.applicationPlayer.getApplication().getState().equals(ApplicationState.FINISHED)) break;
                        this.applicationPlayer.getApplication().setState(ApplicationState.FINISHED);
                        this.applicationPlayer.setBlockPhysics(false);
                        ApplicationPlugin.getPlugin().getRequest().getApplicationHandler().update(this.applicationPlayer.getApplication());
                        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1f, 1f);
                        player.sendMessage(ApplicationPlugin.PREFIX + "§7Du hast deine Welt §berfolgreich§7 abgegeben.");
                        this.category = Category.SELECTION;
                        this.openCategory();
                        break;
                    case "§8» §bInformation":
                        break;
                }
        }
    }

    public void open() {
        this.designSides();
        this.openCategory();
        this.player.openInventory(this.inventory);
    }

    private void openCategory() {
        this.designSides();
        switch (this.category) {
            case FINISH:
                this.inventory.setItem(SLOT_THIRD + LINE_SECOND,
                        ItemStackBuilder.builder(Material.INK_SACK, 1, 1).displayName("§8» §cAbbrechen").build());

                this.inventory.setItem(SLOT_FIFTH + LINE_SECOND,
                        ItemStackBuilder.builder(Material.PAPER).displayName("§8» §bInformation").lore(
                                "§7Hiermit kannst du die Welt abgeben, damit das BuildArchitects sie bewertet."
                        ).build());
                this.inventory.setItem(SLOT_SEVENTH + LINE_SECOND,
                        ItemStackBuilder.builder(Material.INK_SACK, 1, 10).displayName("§8» §aAbgeben").lore(
                                "§7Nach der Abgabe der Welt, kannst du §bnichts§7 mehr ändern."
                        ).build());
                break;
            case SELECTION:
                if (!this.applicationPlayer.getApplication().getState().equals(ApplicationState.FINISHED))
                    this.inventory.setItem(SLOT_THIRD + LINE_SECOND, ItemStackBuilder.builder(Material.CHEST).displayName("§8» §bAbgeben").build());
                //this.inventory.setItem(SLOT_FIFTH * LINE_SECOND, ItemStackBuilder.builder(Material.BOOKSHELF).displayName("§8» §aInformation").build());
                ItemStack itemStack = ItemStackBuilder.builder(Material.WORKBENCH).displayName("§8» §aBlockPhysics").build();
                if (this.applicationPlayer.isBlockPhysics()) this.addGlow(itemStack);
                else this.removeGlow(itemStack);
                this.inventory.setItem(SLOT_FIFTH + LINE_SECOND, itemStack);
                break;
        }
    }

    @EventHandler
    public void handle(InventoryCloseEvent event) {
        HandlerList.unregisterAll(this);
    }

    public enum Category {
        SELECTION, FINISH
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

    private void addGlow(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        itemStack.setItemMeta(itemMeta);
    }

    private void removeGlow(ItemStack itemStack) {
        itemStack.removeEnchantment(Enchantment.DURABILITY);
    }
}
