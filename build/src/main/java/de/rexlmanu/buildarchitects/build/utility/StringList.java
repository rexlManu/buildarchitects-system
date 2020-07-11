/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.buildarchitects.build.utility;

import java.util.Arrays;
import java.util.List;

public class StringList {

    public static List<String> toList(String raw) {
        return Arrays.asList(raw.split(","));
    }

    public static String toString(List<String> list) {
        return list.toString().replace("[", "").replace("]", "");
    }

}
