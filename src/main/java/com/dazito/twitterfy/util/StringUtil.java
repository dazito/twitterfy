package com.dazito.twitterfy.util;

/**
 * Created by daz on 17/04/2017.
 */
public final class StringUtil {

    private StringUtil() {}

    public static String remoteNewLine(String str) {
        return str
                // Replace new line by a space - avoid words being glued together
                .replace("\n", " ")
                .replace("\r", " ")
                // Remove double spaces
                .replace("  ", " ");
    }
}
