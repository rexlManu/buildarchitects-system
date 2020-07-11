/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.buildarchitects.lobby.npc;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.dytanic.cloudnet.bridge.internal.util.ItemStackBuilder;
import de.rexlmanu.buildarchitects.lobby.LobbyPlugin;
import de.rexlmanu.buildarchitects.lobby.utility.title.ChatBase;
import net.jitse.npclib.api.NPC;
import net.jitse.npclib.api.events.NPCInteractEvent;
import net.jitse.npclib.api.skin.Skin;
import net.jitse.npclib.api.state.NPCSlot;
import net.jitse.npclib.internal.NPCBase;
import net.labymod.serverapi.bukkit.LabyModPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ApplicationNPC extends BukkitRunnable implements Listener {

    private String[] animation = new String[]
            {
                    "§bVorbauen?",
                    "§b§3Vo§brbauen?",
                    "§bVo§3rb§bauen?",
                    "§bVorb§3au§ben?",
                    "§bVorbau§3en§b?",
                    "§bVorbauen§3?",
            };

    private int animationTick;
    private NPC npc;
    private Map<Player, ApplicationTask> applicationTaskMap;

    public ApplicationNPC() {
        this.animationTick = 0;
        this.npc = LobbyPlugin.getPlugin().getNpcLibrary().createNPC(this.getText());
        this.npc.setLocation((Location) LobbyPlugin.getPlugin().getLocationFile().getConfiguration().get("vorbauen"));
        this.npc.setItem(NPCSlot.MAINHAND, ItemStackBuilder.builder(Material.DIAMOND_PICKAXE).build());
        this.npc.setSkin(new Skin("eyJ0aW1lc3RhbXAiOjE1ODY3Njk3NDIzNzUsInByb2ZpbGVJZCI6Ijc3NmEwOGM3MDFkNzQ2Mzc4OWI3MDAyNmEwNDMzMWFiIiwicHJvZmlsZU5hbWUiOiJyZXhsTWFudSIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjIxMjUxNTM5YmJkOTY2ZjJiZDYyMmM1YmJhZGQ1Mzg5YzAxMDkzYzNjMTQ1Njk3NzA3NWJmM2JkYTVmYzgwNiIsIm1ldGFkYXRhIjp7Im1vZGVsIjoic2xpbSJ9fX19", "D+BH1H4UjDizh/rvHPTiUYFoE6MC23mCnu7RGXFvhxv+d5yJs119Q/Zee5lOQUocvB8n8nFtoPPTruSW7K2+HrqLY4MvufbqkvivEZJ+dYPajMNjCEo9tk8fN3gcVjrqA2cKlKmLDvJrakAor0eohhnko+1Ub2WvseBfxZNEVLS4oCiJ1x1VCspRA0vGUKeoXgR0+O3YP/FRPtzerJv2lb9ItffY8YYHxfBuCgnZH1FaY/bBYKbNaPhXkiNEDdN1C6OcrJmWikfvXtD4cHbTl32CYwA0SrHDodSU3Vtwo6MQJfvqs5GSlFkc7BSZ1SaBqmlj+iat3kWNLaljLOmasJJ49r0CUr/ngSjqMH/RpF/IDB3fMveHuCrQ7xzggi1RUTmFpN3+4sUr6UjWo8rU7Otl3ze2SUCiqoZNFuUqWfezr7jeqeN6WhR9ACADKGrNZ/Ra3ByYterKG8T1H4FARyJNVd5j4evFlavTPs3gZR0GNp8S63hqze6GzmTx5UvMptcGuVgX+4OC3lvAkShPsP6RZs21FfL3W2W7huIXwJkjkdrIiVFv/LAf7i5qCgrxp+yvpLv4fokAl9ABxi0Dg3dL9uL25I80h2ZqbX4ZIfXa9NYubxRPl7nfq7BE9abMJUrqv29USd+XLeGGZRVGZGTyHs/jdg4ABCBul0b/znM="));
        this.npc.create();
        this.applicationTaskMap = new HashMap<>();

        Bukkit.getPluginManager().registerEvents(this, LobbyPlugin.getPlugin());
    }

    @EventHandler
    public void handle(NPCInteractEvent event) {
        if (!event.getNPC().getId().equals(this.npc.getId())) return;
        Player player = event.getWhoClicked();
        if (event.getClickType().equals(NPCInteractEvent.ClickType.RIGHT_CLICK)) {
            if (this.applicationTaskMap.containsKey(player) && !this.applicationTaskMap.get(player).isOver()) return;
            this.applicationTaskMap.remove(player);
            ApplicationTask applicationTask = new ApplicationTask(player);
            this.applicationTaskMap.put(player, applicationTask);
            applicationTask.runTaskTimerAsynchronously(LobbyPlugin.getPlugin(), 1, 20);
        } else {
            this.npc.forceLabyModEmote(player, 20);
        }
    }

    public void forceEmote(Player receiver, UUID npcUUID, int emoteId ) {
        JsonArray array = new JsonArray();
        JsonObject forcedEmote = new JsonObject();
        forcedEmote.addProperty( "uuid", npcUUID.toString() );
        forcedEmote.addProperty( "emote_id", emoteId );
        array.add(forcedEmote);
        LabyModPlugin.getInstance().sendServerMessage( receiver, "emote_api", array );
    }

    @EventHandler
    public void handle(PlayerJoinEvent event) {
        this.npc.show(event.getPlayer());
    }

    @Override
    public void run() {
        if (animationTick >= animation.length) animationTick = 0;

        this.npc.setText(this.getText());

        this.animationTick++;
    }

    private List<String> getText() {
        return Arrays.asList(
                this.animation[this.animationTick],
                "§7Du willst ein Teil von §bBuild§3Architects §7sein?",
                "§r"
        );
    }
}
