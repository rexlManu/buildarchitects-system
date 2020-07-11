/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.application.player;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerCache {

    @Getter
    private List<ApplicationPlayer> players;

    public PlayerCache() {
        this.players = new ArrayList<>();
    }

    public void add(ApplicationPlayer player) {
        this.players.add(player);
    }

    public void remove(ApplicationPlayer player) {
        this.players.remove(player);
    }

    public ApplicationPlayer get(UUID uuid) {
        return this.players.stream().filter(player -> player.getUuid().equals(uuid)).findFirst().orElse(null);
    }
}
