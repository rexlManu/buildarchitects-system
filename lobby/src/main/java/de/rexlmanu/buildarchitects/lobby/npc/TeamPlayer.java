/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.buildarchitects.lobby.npc;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.jitse.npclib.api.skin.Skin;
import net.jitse.npclib.api.state.NPCState;
import org.bukkit.Location;

import java.util.UUID;

@Data
@AllArgsConstructor
public class TeamPlayer{

    @SerializedName("uuid")
    private UUID uuid;
    @SerializedName("emote")
    private int emote;
    //@SerializedName("location")
    //private Location location;
    @SerializedName("skin")
    private Skin skin;
    @SerializedName("isState")
    private boolean isState;
    @SerializedName("npcState")
    private NPCState npcState;

}
