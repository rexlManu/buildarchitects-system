/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.application.player;

import com.grinderwolf.swm.api.world.SlimeWorld;
import de.rexlmanu.api.models.application.Application;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ApplicationPlayer {

    private UUID uuid;
    private Application application;
    private SlimeWorld world;
    private long joinDate;
    private boolean blockPhysics;

    public ApplicationPlayer(UUID uuid, Application application, SlimeWorld world) {
        this.uuid = uuid;
        this.application = application;
        this.world = world;
        this.blockPhysics = false;
    }
}
