package com.dazito.twitterfy.util;

import org.nibor.autolink.LinkExtractor;
import org.nibor.autolink.LinkSpan;
import org.nibor.autolink.LinkType;

import java.util.*;

/**
 * Created by daz on 17/04/2017.
 */
public final class StringUtil {

    private StringUtil() {}

    public static String removeNewLine(String str) {
        return str
                // Replace new line by a space - avoid words being glued together
                .replace("\n", " ")
                .replace("\r", " ")
                // Remove double spaces
                .replace("  ", " ");
    }

    public static List<String> fetchUrlFromTweet(String tweet) {
        LinkExtractor linkExtractor = LinkExtractor.builder()
                .linkTypes(EnumSet.of(LinkType.URL, LinkType.WWW))
                .build();

        final Iterable<LinkSpan> links = linkExtractor.extractLinks(tweet);
        final Iterator<LinkSpan> iterator = links.iterator();

        List<String> fetchedLinkList = new ArrayList<>();

        while (iterator.hasNext()) {
            final LinkSpan link = iterator.next();

            link.getType();
            link.getBeginIndex();
            link.getEndIndex();

            final String url = tweet.substring(link.getBeginIndex(), link.getEndIndex());

            fetchedLinkList.add(url);
        }

        return fetchedLinkList;
    }

    public static String stringArrayToString(String[] strArray, String separator) {
        final StringBuilder stringBuilder = new StringBuilder();

        for(int i = 0; i < strArray.length; i++) {
            stringBuilder.append(strArray[i]);

            if(i != strArray.length - 1) {
                stringBuilder.append(separator);
            }
        }

        return stringBuilder.toString();
    }

    public static String stringSetToString(Set<String> strSet, String separator) {
        final StringBuilder stringBuilder = new StringBuilder();

        final Iterator<String> setIterator = strSet.iterator();
        while(setIterator.hasNext()) {
            stringBuilder.append(setIterator.next());

            if(setIterator.hasNext()) {
                stringBuilder.append(separator);
            }
        }

        return stringBuilder.toString();
    }
}
