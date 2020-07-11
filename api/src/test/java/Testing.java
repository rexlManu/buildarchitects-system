/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */

import de.rexlmanu.api.Request;
import de.rexlmanu.api.models.application.Application;
import de.rexlmanu.api.models.application.ApplicationStatus;

import java.util.UUID;

public class Testing {

    public static void main(String[] args) {
        Request request = new Request("x391Rhm6XNIS8QhLtN3QPeWnxFQ2F1PY1gQHyQsBblcRqqMp2GuNx17tlY9BU");

        Application application = request.getApplicationHandler().get(UUID.randomUUID());
        System.out.println(application.getUuid());
    }

}
