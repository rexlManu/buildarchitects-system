/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.api.handler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.rexlmanu.api.Request;
import de.rexlmanu.api.models.application.Application;
import de.rexlmanu.api.models.application.ApplicationState;
import de.rexlmanu.api.models.application.ApplicationStatus;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ApplicationHandler {

    private Request request;

    public ApplicationHandler(Request request) {
        this.request = request;
    }

    public List<Application> applications() {
        List<Application> applications = new ArrayList<>();
        this.request.get("/applications").getAsJsonArray().forEach(jsonElement ->
                applications.add(this.parse(jsonElement.getAsJsonObject())));
        return applications;
    }

    public Application get(UUID uuid) {
        JsonElement jsonElement = this.request.get("/applications/" + uuid.toString());
        if (jsonElement.isJsonNull()) return null;
        return this.parse(jsonElement.getAsJsonObject());
    }

    public boolean delete(UUID uuid) {
        return !this.request.get("/applications/" + uuid.toString() + "/delete").getAsBoolean();
    }

    public Application create(Application application) {
        HashMap<String, String> parameters = new HashMap<>();
        if (application.getWorldName() != null)
            parameters.put("world_name", application.getWorldName());
        parameters.put("uuid", application.getUuid().toString());
        JsonElement post = this.request.post("/applications/create", parameters);
        if (post.isJsonNull()) return null;
        JsonObject object = post.getAsJsonObject();
        object.addProperty("state", "BUILDING");
        object.addProperty("status", "PENDING");
        return this.parse(object);
    }

    public boolean update(Application application) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("world_name", application.getWorldName());
        parameters.put("state", application.getState().name());
        parameters.put("status", application.getStatus().name());
        parameters.put("created_at", this.formatToSql(application.getCreatedAt()));
        parameters.put("updated_at", this.formatToSql(application.getUpdatedAt()));
        parameters.put("finished_at", this.formatToSql(application.getFinishedAt()));
        return !this.request.post("/applications/" + application.getUuid().toString() + "/update", parameters).getAsBoolean();
    }

    private String formatToSql(LocalDateTime time) {
        if (time == null) return null;
        return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private Application parse(JsonObject object) {
        if (object.isJsonNull()) return null;
        return new Application(
                object.get("id").getAsInt(),
                this.parse(object, "created_at"),
                this.parse(object, "updated_at"),
                UUID.fromString(object.get("uuid").getAsString()),
                ApplicationState.valueOf(object.get("state").getAsString()),
                this.parse(object, "finished_at"),
                ApplicationStatus.valueOf(object.get("status").getAsString()),
                object.get("world_name").getAsString()
        );
    }

    private LocalDateTime parse(JsonObject object, String key) {
        if (!object.has(key) || object.get(key) == null || object.get(key).isJsonNull()) {
            return null;
        }
        return Timestamp.valueOf(object.get(key).getAsString()).toLocalDateTime();
    }
}
