/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.application.tasks;

import de.rexlmanu.application.ApplicationPlugin;
import org.bukkit.Bukkit;

public class TaskRunner {

    public static void runAsync(Task task) {
        Bukkit.getScheduler().runTaskAsynchronously(ApplicationPlugin.getPlugin(), task);
    }

}
