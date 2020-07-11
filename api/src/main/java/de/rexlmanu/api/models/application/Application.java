/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.api.models.application;

import de.rexlmanu.api.models.Model;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class Application extends Model {

    public static Application create(UUID uuid, String worldName) {
        return new Application(0, null, null, uuid, ApplicationState.BUILDING, null, ApplicationStatus.PENDING, worldName);
    }

    private UUID uuid;
    private ApplicationState state;
    private LocalDateTime finishedAt;
    private ApplicationStatus status;
    private String worldName;

    public Application(int id, LocalDateTime createdAt, LocalDateTime updatedAt, UUID uuid, ApplicationState state, LocalDateTime finishedAt, ApplicationStatus status, String worldName) {
        super(id, createdAt, updatedAt);
        this.uuid = uuid;
        this.state = state;
        this.finishedAt = finishedAt;
        this.status = status;
        this.worldName = worldName;
    }


}
