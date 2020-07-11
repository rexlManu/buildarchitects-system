/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.buildarchitects.lobby.npc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import de.rexlmanu.buildarchitects.lobby.LobbyPlugin;
import de.rexlmanu.buildarchitects.lobby.location.LocationFile;
import net.jitse.npclib.NPCLib;
import net.jitse.npclib.api.NPC;
import net.jitse.npclib.api.events.NPCInteractEvent;
import net.jitse.npclib.api.skin.Skin;
import net.jitse.npclib.api.state.NPCState;
import net.jitse.npclib.internal.NPCBase;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public class TeamManager implements Listener {

    private static final Gson GSON = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
    private static final Type TEAM_PLAYER_TYPE = new TypeToken<TeamPlayer>() {
    }.getType();
    private static final Type LIST_TEAM_PLAYER_TYPE = new TypeToken<ArrayList<TeamPlayer>>() {
    }.getType();
    private static final JsonParser JSON_PARSER = new JsonParser();
    private static final Random RANDOM = new Random();

    private LobbyPlugin plugin;
    private List<TeamPlayer> teamPlayers;
    private File file;
    private NPCLib npcLib;
    private Map<TeamPlayer, NPC> playerNPCMap;

    public TeamManager(LobbyPlugin plugin) {
        this.plugin = plugin;
        this.teamPlayers = new ArrayList<>();
        this.file = new File(plugin.getDataFolder(), "teamPlayers.json");
        this.playerNPCMap = new HashMap<>();

        this.npcLib = new NPCLib(plugin);

        Bukkit.getPluginManager().registerEvents(this, plugin);
        try {
            if (this.file.exists()) {
                this.teamPlayers = GSON.fromJson(JSON_PARSER.parse(new String(Files.readAllBytes(this.file.toPath()))).getAsJsonArray(), LIST_TEAM_PLAYER_TYPE);
            }
            else {
                return;
            }
            ;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createNpcs() {
        this.teamPlayers.forEach(this::createNPC);
    }

    private void createNPC(TeamPlayer teamPlayer) {
        OfflinePlayer player = CloudAPI.getInstance().getOfflinePlayer(teamPlayer.getUuid());
        PermissionGroup group = player.getPermissionEntity().getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool());
        String groupColor = group.getColor().replace("&", "§");
        NPC npc = this.npcLib.createNPC(Arrays.asList(groupColor + group.getName(), groupColor + player.getName()));
        npc.setSkin(teamPlayer.getSkin());
        npc.setLocation((Location) plugin.getLocationFile().getConfiguration().get(teamPlayer.getUuid().toString()));
        npc.create();
        if (teamPlayer.isState()) {
            npc.toggleState(teamPlayer.getNpcState());
        }
        this.playerNPCMap.put(teamPlayer, npc);
    }

    @EventHandler
    public void handle(PlayerJoinEvent event) {
        this.teamPlayers.forEach(teamPlayer -> {
            this.playerNPCMap.get(teamPlayer).show(event.getPlayer());
        });
    }

    @EventHandler
    public void handle(NPCInteractEvent event) {
        Player player = event.getWhoClicked();
        if (event.getClickType().equals(NPCInteractEvent.ClickType.RIGHT_CLICK)) {
            NPC npc = event.getNPC();
            TeamPlayer teamPlayer = getKeysByValue(this.playerNPCMap, npc).get(0);
            npc.forceLabyModEmote(player, teamPlayer.getEmote());
        }
    }

    public static <T, E> List<T> getKeysByValue(Map<T, E> map, E value) {
        return map.entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getValue(), value))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public void addPlayer(TeamPlayer player) {
        this.teamPlayers.add(player);
        this.saveList();
        this.createNPC(player);

        Bukkit.getOnlinePlayers().forEach(o -> playerNPCMap.get(player).show(o));
    }

    private void saveList() {
        try {
            Files.write(this.file.toPath(), GSON.toJson(this.teamPlayers).getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
