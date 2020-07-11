/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.buildarchitects.lobby.task;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class BootsTask extends BukkitRunnable {

    private static final Color FAR = Color.TEAL;
    private static final Color CLOSE = Color.AQUA;

    private double ratio;
    private boolean ratioSwitch;

    public BootsTask() {
        this.ratio = 0;
        this.ratioSwitch = false;
    }

    @Override
    public void run() {
        if (this.ratio >= 1.0D) this.ratioSwitch = true;
        if (this.ratio <= 0D) this.ratioSwitch = false;
        Bukkit.getOnlinePlayers().forEach(player -> {
            ItemStack boots = player.getInventory().getBoots();
            if (boots == null) return;
            LeatherArmorMeta armorMeta = (LeatherArmorMeta) boots.getItemMeta();
            armorMeta.setColor(colorTransition());
            boots.setItemMeta(armorMeta);
            player.getInventory().setBoots(boots);
        });

        if (!this.ratioSwitch) this.ratio += 0.05D;
        else this.ratio -= 0.05D;
    }

    private Color colorTransition() {
        int red = (int) Math.abs((ratio * FAR.getRed()) + ((1 - ratio) * CLOSE.getRed()));
        int green = (int) Math.abs((ratio * FAR.getGreen()) + ((1 - ratio) * CLOSE.getGreen()));
        int blue = (int) Math.abs((ratio * FAR.getBlue()) + ((1 - ratio) * CLOSE.getBlue()));
        return Color.fromRGB(red, green, blue);
    }
}
